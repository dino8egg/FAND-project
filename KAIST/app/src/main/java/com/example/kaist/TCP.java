package com.example.kaist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCP {

    private String ip;
    private int port;
    private String input;
    private Socket socket;
    private int timeout1;
    private int timeout2;

    public TCP(String ip, int port){
        this.ip = ip;
        this.port = port;
        this.socket = new Socket();
    }

    public String request(String input, int timeout1, int timeout2){
        this.input = input;
        this.timeout1 = timeout1;
        this.timeout2 = timeout2;

        try{
            Request req = new Request();
            Thread thread = new Thread(req);
            thread.start();
            thread.join();
            return req.getValue();
        }catch (Exception e){
            return "Error: thread join";
        }
    }

    public class Request implements Runnable {
        private String req;
        @Override
        public void run(){
            try {
                socket.setSoTimeout(timeout1);
                socket.connect(new InetSocketAddress(ip, port), timeout2);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream();

                out.write(input.getBytes());
                req = in.readLine();
            } catch(SocketTimeoutException e){
                req = "Error: Socket timeout";
            }
            catch(Exception e) {
                req = "Error: Socket";
            } finally {
                try{
                    socket.close();
                }catch(Exception e){}
            }
        }

        public String getValue() {
            return req;
        }
    }
}
