package com.example.kaist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Permission extends AppCompatActivity {
    private TextView train_name;
    private TextView train_id;
    private Button btn_app;
    private Button btn_back;
    private int pid;

    private List<String> app_checked;

    public String[] update_apps(){
        ArrayList<String> s = new ArrayList<String>();
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
            if (intent!=null)
                s.add(packageInfo.packageName);
        }
        return s.toArray(new String[s.size()]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        this.app_checked = new ArrayList<String>();
        this.train_name = (TextView) findViewById(R.id.Train_name);
        this.train_id = (TextView) findViewById(R.id.Train_id);
        this.btn_app = (Button) findViewById(R.id.btn_App);
        this.btn_back = (Button) findViewById(R.id.btn_Back);
        final GlobalVars vars = (GlobalVars)getApplicationContext();

        TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
        String res = t.request("New", 1000, 1000);
        train_id.setText("Person ID: " + res);
        this.pid = Integer.parseInt(res);

        this.btn_app.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final String[] app_names = update_apps();
                String[] simple_app_names = new String[app_names.length];
                for(int i=0; i<app_names.length; i++){
                    String[] tmp = app_names[i].split("\\.");
                    simple_app_names[i] = tmp[tmp.length - 1];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Permission.this);
                builder.setTitle("Select Apps: " + Integer.toString(app_names.length));

                boolean[] b = new boolean[app_names.length];
                for (int i=0; i<app_names.length; i++){
                    if(app_checked.contains(app_names[i])) b[i] = true;
                    else b[i] = false;
                }
                builder.setMultiChoiceItems(simple_app_names, b, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean ischecked) {
                        if(ischecked) app_checked.add(app_names[i]);
                        else app_checked.remove(app_names[i]);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Permission.this, Integer.toString(app_checked.size()) + " Apps selected.", Toast.LENGTH_LONG).show();
                        try{
                            FileOutputStream fos = openFileOutput("FAND-p"+pid+".txt", Permission.MODE_PRIVATE);
                            String str = train_name.getText().toString() + "+";
                            for(String app:app_checked)
                                str+=("+" + app);
                            fos.write(str.getBytes());
                            fos.close();
                            TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
                            String res = t.request("End", 1000, 1000);
                        }
                        catch(Exception e){
                            Toast.makeText(Permission.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        this.btn_back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Permission.this, Options.class);
                startActivity(intent);
            }
        });

    }
}
