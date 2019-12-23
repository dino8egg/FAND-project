package com.example.kaist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.scichart.charting.visuals.SciChartSurface;

public class MainActivity extends AppCompatActivity {
    private Button btn_start;
    private Button btn_admin;
    private EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String license = "";

        try{
            SciChartSurface.setRuntimeLicenseKey(license);
        } catch(Exception e){
            pwd.setText("error");
        }

        this.btn_start = (Button) findViewById(R.id.button);
        this.btn_admin = (Button) findViewById(R.id.button_admin);
        this.pwd = (EditText) findViewById(R.id.pwd);
        this.btn_start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConnectML.class);
                startActivity(intent);
            }
        });
        this.btn_admin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String password = pwd.getText().toString();
                if(password.equals("pwd")) {
                    Intent intent = new Intent(MainActivity.this, Admin.class);
                    startActivity(intent);
                }
            }
        });
    }
}
