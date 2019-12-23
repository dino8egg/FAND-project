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
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
    private Button app;
    private Button back;
    private EditText pid;
    private EditText name;
    private int id;

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
        setContentView(R.layout.activity_admin);

        this.app = (Button) findViewById(R.id.admin_App);
        this.back = (Button) findViewById(R.id.admin_end);
        this.pid = (EditText) findViewById(R.id.admin_id);
        this.name = (EditText) findViewById(R.id.admin_name);
        this.app_checked = new ArrayList<String>();

        this.app.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                try{
                    id = Integer.parseInt(pid.getText().toString());
                }catch(Exception e){
                    id = -1;
                }
                if(id == -1)
                    return;

                final String[] app_names = update_apps();
                String[] simple_app_names = new String[app_names.length];
                for(int i=0; i<app_names.length; i++){
                    String[] tmp = app_names[i].split("\\.");
                    simple_app_names[i] = tmp[tmp.length - 1];
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
                builder.setTitle("Select Apps: " + app_names.length);

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
                        Toast.makeText(Admin.this, Integer.toString(app_checked.size()) + " Apps selected.", Toast.LENGTH_LONG).show();
                        try{
                            FileOutputStream fos = openFileOutput("FAND-p"+id+".txt", Permission.MODE_PRIVATE);
                            String str = name.getText().toString() + "+";
                            for(String app:app_checked)
                                str+=("+" + app);
                            fos.write(str.getBytes());
                            fos.close();
                        }
                        catch(Exception e){
                            Toast.makeText(Admin.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        this.back.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Admin.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
