package com.example.kaist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.scichart.charting.visuals.SciChartSurface;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Queue;
import java.util.LinkedList;
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries;
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.extensions.builders.SciChartBuilder;
import com.scichart.charting.model.dataSeries.XyDataSeries;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.drawing.utility.ColorUtil;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class Records extends AppCompatActivity {


    private int port_index;
    private Button btn_record;
    private Button btn_next;
    private TextView textView_keyword;
    private TextView textView_msg;
    private EditText time;

    private Boolean istrain;
    private String[] keywords;
    private Boolean[] chk_record;
    private int index;

    private LinearLayout vol_graph1;
    private LinearLayout vol_graph2;
    private LinearLayout fft_graph1;
    private LinearLayout fft_graph2;

    private boolean[] record_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        this.btn_record = (Button) findViewById(R.id.btn_records);
        this.btn_next = (Button) findViewById(R.id.btn_nexts);
        this.textView_keyword = (TextView) findViewById(R.id.textView3s);
        this.textView_msg = (TextView) findViewById(R.id.textView4s);
        this.time = (EditText) findViewById(R.id.times);
        this.vol_graph1 = (LinearLayout) findViewById(R.id.chart_layout1s);
        this.vol_graph2 = (LinearLayout) findViewById(R.id.chart_layout2s);
        this.fft_graph1 = (LinearLayout) findViewById(R.id.chart_layout3s);
        this.fft_graph2 = (LinearLayout) findViewById(R.id.chart_layout4s);

        final GlobalVars vars = (GlobalVars)getApplicationContext();
        this.istrain = vars.get_op();
        TCP t = new TCP(vars.get_ML_ip(), vars.get_ML_port());
        String key;
        if(this.istrain) key = t.request("trainkey", 1000, 1000);
        else key = t.request("testkey", 1000, 1000);
        if(key.toLowerCase().contains("error")){
            Toast.makeText(Records.this, "Failed to get Keywords.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Records.this, Options.class);
            startActivity(intent);
        }
        this.keywords = key.split("\\+");
        this.chk_record = new Boolean[keywords.length];
        for(int i=0; i<chk_record.length; i++)
            chk_record[i] = false;
        this.index = 0;
        this.textView_keyword.setText(this.keywords[this.index]);
        this.textView_msg.setText("Need Record");
        vol_graph1.removeAllViews();
        vol_graph2.removeAllViews();
        fft_graph1.removeAllViews();
        fft_graph2.removeAllViews();

        final SciChartSurface vol1 = new SciChartSurface(Records.this);
        final SciChartSurface vol2 = new SciChartSurface(Records.this);
        final SciChartSurface fft1 = new SciChartSurface(Records.this);
        final SciChartSurface fft2 = new SciChartSurface(Records.this);

        vol_graph1.addView(vol1);
        vol_graph2.addView(vol2);
        fft_graph1.addView(fft1);
        fft_graph2.addView(fft2);

        SciChartBuilder.init(Records.this);
        final SciChartBuilder sciChartBuilder = SciChartBuilder.instance();

        final IAxis xAxis_v1 = sciChartBuilder.newNumericAxis().withAxisTitle("Frames").build();
        xAxis_v1.setDrawLabels(false);      xAxis_v1.setDrawMajorBands(false);      xAxis_v1.setDrawMajorGridLines(false);
        xAxis_v1.setDrawMajorTicks(false);  xAxis_v1.setDrawMinorGridLines(false);  xAxis_v1.setDrawMinorTicks(false);
        final IAxis yAxis_v1 = sciChartBuilder.newNumericAxis().withAxisTitle("Voltage").build();
        yAxis_v1.setDrawLabels(false);      yAxis_v1.setDrawMajorBands(false);      yAxis_v1.setDrawMajorGridLines(false);
        yAxis_v1.setDrawMajorTicks(false);  yAxis_v1.setDrawMinorGridLines(false);  yAxis_v1.setDrawMinorTicks(false);
        Collections.addAll(vol1.getXAxes(), xAxis_v1);
        Collections.addAll(vol1.getYAxes(), yAxis_v1);
        final IAxis xAxis_v2 = sciChartBuilder.newNumericAxis().withAxisTitle("Frames").build();
        xAxis_v2.setDrawLabels(false);      xAxis_v2.setDrawMajorBands(false);      xAxis_v2.setDrawMajorGridLines(false);
        xAxis_v2.setDrawMajorTicks(false);  xAxis_v2.setDrawMinorGridLines(false);  xAxis_v2.setDrawMinorTicks(false);
        final IAxis yAxis_v2 = sciChartBuilder.newNumericAxis().withAxisTitle("Voltage").build();
        yAxis_v2.setDrawLabels(false);      yAxis_v2.setDrawMajorBands(false);      yAxis_v2.setDrawMajorGridLines(false);
        yAxis_v2.setDrawMajorTicks(false);  yAxis_v2.setDrawMinorGridLines(false);  yAxis_v2.setDrawMinorTicks(false);
        Collections.addAll(vol2.getXAxes(), xAxis_v2);
        Collections.addAll(vol2.getYAxes(), yAxis_v2);

        final IAxis xAxis_f1 = sciChartBuilder.newNumericAxis().withVisibleRange(0, 256).withAxisTitle("Frequency").build();
        xAxis_f1.setDrawLabels(false);      xAxis_f1.setDrawMajorBands(false);      xAxis_f1.setDrawMajorGridLines(false);
        xAxis_f1.setDrawMajorTicks(false);  xAxis_f1.setDrawMinorGridLines(false);  xAxis_f1.setDrawMinorTicks(false);
        final IAxis yAxis_f1 = sciChartBuilder.newNumericAxis().withVisibleRange(-60.0, 40.0).withAxisTitle("FFT Volume").build();
        Collections.addAll(fft1.getXAxes(), xAxis_f1);
        Collections.addAll(fft1.getYAxes(), yAxis_f1);
        final IAxis xAxis_f2 = sciChartBuilder.newNumericAxis().withVisibleRange(0, 256).withAxisTitle("Frequency").build();
        xAxis_f2.setDrawLabels(false);      xAxis_f2.setDrawMajorBands(false);      xAxis_f2.setDrawMajorGridLines(false);
        xAxis_f2.setDrawMajorTicks(false);  xAxis_f2.setDrawMinorGridLines(false);  xAxis_f2.setDrawMinorTicks(false);
        final IAxis yAxis_f2 = sciChartBuilder.newNumericAxis().withVisibleRange(-60.0, 40.0).withAxisTitle("FFT Volume").build();
        Collections.addAll(fft2.getXAxes(), xAxis_f2);
        Collections.addAll(fft2.getYAxes(), yAxis_f2);

        final XyDataSeries lineData_v1 = sciChartBuilder.newXyDataSeries(Integer.class, Float.class).build();
        final XyDataSeries lineData_v2 = sciChartBuilder.newXyDataSeries(Integer.class, Float.class).build();
        final XyDataSeries lineData_f1 = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withFifoCapacity(512).build();
        final XyDataSeries lineData_f2 = sciChartBuilder.newXyDataSeries(Integer.class, Double.class).withFifoCapacity(512).build();
        final FastLineRenderableSeries lineSeries_v1 = sciChartBuilder.newLineSeries().withDataSeries(lineData_v1)
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true).build();
        final FastLineRenderableSeries lineSeries_v2 = sciChartBuilder.newLineSeries().withDataSeries(lineData_v2)
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true).build();
        final FastColumnRenderableSeries lineSeries_f1 = sciChartBuilder.newColumnSeries().withDataSeries(lineData_f1)
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true).build();
        final FastColumnRenderableSeries lineSeries_f2 = sciChartBuilder.newColumnSeries().withDataSeries(lineData_f2)
                .withStrokeStyle(ColorUtil.LightBlue, 2f, true).build();
        lineSeries_f1.setZeroLineY(-60.0);
        lineSeries_f2.setZeroLineY(-60.0);

        final Queue<float[]> data1 = new LinkedList<>();
        final Queue<float[]> data2 = new LinkedList<>();
        final Queue<Integer> data_length1 = new LinkedList<Integer>();
        final Queue<Integer> data_length2 = new LinkedList<Integer>();

        final LimitedQueue fft_data1 = new LimitedQueue(1024);
        final float[] fft_input1 = new float[1024];
        final double[] fft_output1 = new double[512];
        final LimitedQueue fft_data2 = new LimitedQueue(1024);
        final float[] fft_input2 = new float[1024];
        final double[] fft_output2 = new double[512];

        record_end = new boolean[2];
        this.port_index = 0;

        btn_record.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                chk_record[index] = true;
                textView_msg.setText("Recording....");
                int tmp;
                try{
                    tmp = Integer.parseInt(time.getText().toString());
                }catch(Exception e){
                    tmp = 0;
                    textView_msg.setText("Not natural number.");
                }
                final int record_time = tmp;
                if(record_time > 0){
                    try{
                        String string;
                        if(vars.get_op())
                            string = "record+trainrecv+"+record_time+"+"+keywords[index];
                        else
                            string = "record+testrecv+"+record_time+"+"+keywords[index];
                        TCP t = new TCP(vars.get_PI_ip(), vars.get_PI_port());
                        String res = t.request(string, 1000, 1000);

                        final int port1 = Integer.parseInt(res.split("\\+")[0]);
                        final int port2 = Integer.parseInt(res.split("\\+")[1]);

                        try{ Thread.sleep(500); }catch(Exception e){}

                        lineData_v1.clear();    lineData_v2.clear();    lineData_f1.clear();    lineData_f2.clear();
                        record_end[0] = record_end[1] = false;

                        port_index += 1;
                        recv_data r1 = new recv_data(data1, data_length1, vars.get_PI_ip(), port1, 0);
                        Thread tr1 = new Thread(r1);
                        tr1.start();
                        port_index += 1;
                        recv_data r2 = new recv_data(data2, data_length2, vars.get_PI_ip(), port2, 0);
                        Thread tr2 = new Thread(r2);
                        tr2.start();
                        TimerTask updateDataTask1 = new TimerTask() {
                            @Override
                            public void run() {
                                if(data1.size() > 0){
                                    final float[] sample = data1.poll();
                                    final int sample_length = data_length1.poll();
                                    UpdateSuspender.using(vol1, new Runnable() {
                                        @Override
                                        public void run() {
                                            int x = lineData_v1.getCount();
                                            for(int i=0; i<sample_length; i++) {
                                                fft_data1.add(sample[i]);
                                                if (i % 21 == 0) {
                                                    lineData_v1.append(x + (i / 21), sample[i]);
                                                    vol1.zoomExtents();
                                                }
                                            }
                                        }
                                    });
                                    UpdateSuspender.using(fft1, new Runnable() {
                                        @Override
                                        public void run() {
                                            lineData_f1.clear();
                                            fft_data1.get_array(fft_input1);
                                            FastFT.run(fft_input1, fft_output1);
                                            for(int i=0; i<512; i++)
                                                lineData_f1.append(i, fft_output1[i]);
                                        }
                                    });
                                }
                            }
                        };
                        TimerTask updateDataTask2 = new TimerTask() {
                            @Override
                            public void run() {
                                if(data2.size() > 0){
                                    final float[] sample = data2.poll();
                                    final int sample_length = data_length2.poll();
                                    UpdateSuspender.using(vol2, new Runnable() {
                                        @Override
                                        public void run() {
                                            int x = lineData_v2.getCount();
                                            for(int i=0; i<sample_length; i++) {
                                                fft_data2.add(sample[i]);
                                                if (i % 21 == 0) {
                                                    lineData_v2.append(x + (i / 21), sample[i]);
                                                    vol2.zoomExtents();
                                                }
                                            }
                                        }
                                    });
                                    UpdateSuspender.using(fft2, new Runnable() {
                                        @Override
                                        public void run() {
                                            lineData_f2.clear();
                                            fft_data2.get_array(fft_input2);
                                            FastFT.run(fft_input2, fft_output2);
                                            for(int i=0; i<512; i++)
                                                lineData_f2.append(i, fft_output2[i]);
                                        }
                                    });
                                }
                            }
                        };
                        Timer timer1 = new Timer();
                        Timer timer2 = new Timer();
                        timer1.schedule(updateDataTask1, 0, 10);
                        timer2.schedule(updateDataTask2, 0, 10);

                        cancel c1 = new cancel(timer1, updateDataTask1, data1, data_length1, record_time, 0);
                        Thread tc1 = new Thread(c1);
                        cancel c2 = new cancel(timer2, updateDataTask2, data2, data_length2, record_time, 0);
                        Thread tc2 = new Thread(c2);

                        vol1.getRenderableSeries().add(lineSeries_v1);
                        vol2.getRenderableSeries().add(lineSeries_v2);
                        fft1.getRenderableSeries().add(lineSeries_f1);
                        fft2.getRenderableSeries().add(lineSeries_f2);

                        tc1.start();
                        tc2.start();
                        tc1.join();
                        tc2.join();
                    }catch(Exception e){};
                    textView_msg.setText("Record Done");
                }
            }
        });
        btn_next.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(chk_record[index]){
                    if(index+1 == keywords.length){
                        if(istrain){
                            lineData_v1.clear();    lineData_v2.clear();    lineData_f1.clear();    lineData_f2.clear();
                            vol_graph1.removeAllViews();    vol_graph1.clearAnimation();    vol_graph1.removeAllViewsInLayout();
                            vol_graph2.removeAllViews();    vol_graph2.clearAnimation();    vol_graph2.removeAllViewsInLayout();
                            fft_graph1.removeAllViews();    fft_graph1.clearAnimation();    fft_graph1.removeAllViewsInLayout();
                            fft_graph2.removeAllViews();    fft_graph2.clearAnimation();    fft_graph2.removeAllViewsInLayout();

                            Intent intent = new Intent(Records.this, Permission.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(Records.this, Test.class);
                            startActivity(intent);
                        }
                    }
                    else{
                        lineData_v1.clear();    lineData_v2.clear();    lineData_f1.clear();    lineData_f2.clear();
                        index++;
                        textView_keyword.setText(keywords[index]);
                        textView_msg.setText("Need Record");
                    }
                }
                else textView_msg.setText("Need Record");
            }
        });
    }

    public  class cancel extends Thread {
        private Timer t;    private TimerTask tt;   private int time;   private Queue<float[]> data;    private Queue<Integer> data_length; private int index;
        public cancel(Timer t, TimerTask tt, Queue<float[]> data, Queue<Integer> data_length, int time, int index){
            this.t = t; this.tt = tt;   this.time = time;   this.data = data;   this.data_length = data_length; this.index = index;
        }
        public void run() {
            try{ Thread.sleep(time); }catch(Exception e){}
            while((data.size() > 0) || (data_length.size() > 0) || (record_end[index] == false));
            tt.cancel();    t.cancel();
        }
    }

    public class recv_data extends Thread {
        private String ip;
        private int port;
        private int index;
        private Queue<float[]> data;
        private Queue<Integer> data_length;

        public recv_data(Queue<float[]> data, Queue<Integer> data_length, String ip, int port, int index){
            this.ip = ip;   this.port = port;   this.data = data;   this.data_length = data_length; this.index = index;
        }
        public void run(){
            try{
                Socket socket = new Socket(ip, port);
                byte[] buf = new byte[1024 << 4];
                InputStream input = socket.getInputStream();
                int cnt;
                do{
                    cnt = input.read(buf);
                    if(cnt==-1)
                        break;
                    data_length.offer(cnt >> 2);
                    FloatBuffer floatBuffer = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
                    float[] array = new float[(cnt >> 2)];
                    floatBuffer.get(array);
                    data.offer(array);
                }while(true);
                record_end[index] = true;
            }
            catch(Exception e){
                textView_msg.setText(e.toString());
            }
        }
    }
}
