package com.example.kaist;

import android.app.Application;

public class GlobalVars extends Application {
    private String ML_ip;
    private int ML_port;
    private String PI_ip;
    private int PI_port;
    private Boolean istrain;

    public String get_ML_ip(){
        return this.ML_ip;
    }
    public int get_ML_port(){
        return this.ML_port;
    }
    public String get_PI_ip(){
        return this.PI_ip;
    }
    public int get_PI_port(){
        return this.PI_port;
    }
    public Boolean get_op() { return this.istrain; }
    public void set_ML_ip(String ip){
        this.ML_ip = String.copyValueOf(ip.toCharArray(), 0, ip.length());
    }
    public void set_ML_port(int port){
        this.ML_port = port;
    }
    public void set_PI_ip(String ip){
        this.PI_ip = String.copyValueOf(ip.toCharArray(), 0, ip.length());
    }
    public void set_PI_port(int port){
        this.PI_port = port;
    }
    public void set_op(Boolean input) { this.istrain = input; }
}