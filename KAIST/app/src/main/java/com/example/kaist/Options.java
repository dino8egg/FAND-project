package com.example.kaist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Options extends AppCompatActivity {
    private Button btn_update;
    private Button btn_record;
    private Button btn_train;
    private Button btn_test;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        this.btn_update = (Button) findViewById(R.id.btn_Update);
        this.btn_record = (Button) findViewById(R.id.btn_Record);
        this.btn_train = (Button) findViewById(R.id.btn_Train);
        this.btn_test = (Button) findViewById(R.id.btn_Test);
        this.textView = (TextView) findViewById(R.id.textView2);
        final GlobalVars vars = (GlobalVars)getApplicationContext();
        textView.setText(update_text(update()));

        this.btn_update.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                textView.setText(update_text(update()));
            }
        });
        this.btn_record.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                vars.set_op(true);
                Intent intent = new Intent(Options.this, Records.class);
                startActivity(intent);
            }
        });
        this.btn_train.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int up = update();
                textView.setText(update_text(up));
                if((up/10)%10 >= 2){
                    TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
                    t.request("Train", 1000, 1000);
                }
            }
        });
        this.btn_test.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int up = update();
                textView.setText(update_text(up));
                if(up % 10 == 2) {
                    vars.set_op(false);
                    Intent intent = new Intent(Options.this, Records.class);
                    startActivity(intent);
                }
            }
        });
    }

    public int update(){
        GlobalVars vars = (GlobalVars)getApplicationContext();
        TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
        String res = t.request("Update", 1000, 1000);
        return Integer.parseInt(res);
    }
    public String update_text(int input){
        String ret = "";
        int voice = input/100;
        int train = (input/10)%10;
        int test = input%10;

        ret += ("Num of People: "+Integer.toString(voice)+"\n");
        if(train == 0) ret += ("Train: No train data\n");
        if(train == 1) ret += ("Train: Is training\n");
        if(train == 2) ret += ("Train: Train possible\n");
        if(train == 3) ret += ("Train: Already trained\n");
        if(test == 0) ret += ("Test: No model\n");
        if(test == 1) ret += ("Test: Is training\n");
        if(test == 2) ret += ("Test: Test possible\n");
        return ret;
    }
}
