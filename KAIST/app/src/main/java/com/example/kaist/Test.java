package com.example.kaist;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Test extends AppCompatActivity {
    private Button btn_test;
    private Button btn_end;
    private TextView name;
    private Boolean isdone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        this.btn_test = (Button) findViewById(R.id.btn_test);
        this.btn_end = (Button) findViewById(R.id.btn_end);
        this.name = (TextView) findViewById(R.id.test_name);
        this.isdone = false;
        final GlobalVars vars = (GlobalVars)getApplicationContext();

        this.btn_test.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
                String res = t.request("Test", 20000, 1000);
                int result;
                try{
                    result = Integer.parseInt(res);
                }catch(Exception e){result = -1; }
                if(result==-1){
                    name.setText("Error");
                }
                else if(result==0){
                    name.setText("Not found");
                }
                else{
                    try{
                        FileInputStream fis = openFileInput("FAND-p"+result+".txt");
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
                        String str = buffer.readLine();

                        String person_name = str.split("\\+\\+")[0];
                        final String[] apps = str.split("\\+\\+")[1].split("\\+");
                        name.setText(person_name);

                        AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
                        builder.setTitle(person_name + "'s App List");
                        builder.setItems(apps, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = getPackageManager().getLaunchIntentForPackage(apps[i]);
                                startActivity(intent);
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    catch(Exception e){
                        Toast.makeText(Test.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                isdone = true;
            }
        });
        this.btn_end.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(isdone){
                    Intent intent = new Intent(Test.this, Options.class);
                    startActivity(intent);
                }
            }
        });
    }
}
