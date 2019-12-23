package com.example.kaist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConnectPI extends AppCompatActivity {
    private Button btn_start;
    private EditText input_ip;
    private EditText input_port;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_pi);

        this.btn_start = (Button) findViewById(R.id.connect);
        this.input_ip = (EditText) findViewById(R.id.PI_ip);
        this.input_port = (EditText) findViewById(R.id.PI_port);
        this.textView = (TextView) findViewById(R.id.textView1);

        this.btn_start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String ip = input_ip.getText().toString();
                String tmp = input_port.getText().toString();
                try{
                    int port = Integer.parseInt(tmp);
                    TCP t = new TCP(ip, port);
                    String res = t.request("Connect", 1000, 1000);
                    textView.setText(res);
                    if(res.equals("Connected")){
                        GlobalVars vars = (GlobalVars)getApplicationContext();
                        vars.set_PI_ip(ip);
                        vars.set_PI_port(port);
                        Intent intent = new Intent(ConnectPI.this, Options.class);
                        startActivity(intent);
                    }
                }
                catch(Exception e){
                    textView.setText(e.toString());
                }
            }
        });
    }
}

