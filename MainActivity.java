package com.example.mymqttapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.yuan.waveview.WaveView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Random;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity {
//MqttAndroidClient client;
    private String appName = "Growdify";
    private Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> seriesHum2;
    private BarGraphSeries<DataPoint> seriesWater;
    private LineGraphSeries<DataPoint> seriesHumAir;
    private LineGraphSeries<DataPoint> seriesTempAir;
    private LineGraphSeries<DataPoint> seriesTempSoil;

    private double lastXPoint;
    private double lastXPoint2 = 1;
    private double lastXPoint3 = 1;
    private double lastXPoint4;

    private Random random = new Random();
    private boolean hum1Notif = false;
    private boolean hum2Notif = false;

    float airTemperature;
    float soilTemperature;

    private Button btn;
    private static final String TAG = "MyTag";
    private String topicHum1,topicHum2,topicOptimalHum, topicWaterLevel, clientID,topicTempSoil, topicTempHum, topicTempAir, topicHumAir;
    private MqttAndroidClient client;
    TextView textView;
    TextView textOptimalHumidity;
    TextView textView3;
    TextView textOptimalHum;
    private float optimalHumidity = 50;
    private float hum1 = 0;
    private float hum2 = 0;
    private float humAir = 0;
    float waterLevel = 0;
    boolean waterLevelNotif = false;
    //private RelativeLayout mainLayout;
    //private LineChart mchart;
    GraphView graph;
    GraphView graphTemp;
    GraphView graphWater;
    LineChart lineChart;
    Gauge gauge;
    SeekBar customSeekBar;
    WaveView waveView;
    int wave;
    java.util.Date date;
    boolean isOpHum = true;

    Button pour1;
    //Button pour2;

    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pour1 = findViewById(R.id.buttonPour1);
        //pour2 = findViewById(R.id.buttonPour2);

        setContentView(R.layout.activity_main);
//        init();

        findViewById(R.id.buttonSetings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        SetingsActivity.class));
            }
        });
        globalClass = (GlobalClass) getApplicationContext();
        globalClass.doIt();
        customSeekBar =(SeekBar)findViewById(R.id.seekBar);
        textView=(TextView)findViewById(R.id.textView);
        textOptimalHumidity=(TextView)findViewById(R.id.textOptimalHumidity);
        textView3=(TextView)findViewById(R.id.textView3);
        textOptimalHum = (TextView)findViewById(R.id.textOptimalHum);

        textOptimalHum.setText(globalClass.getOptimalHumidity() + "%");
        gaugeHum1();
        gaugeHum2();


        lastXPoint = globalClass.getLastPoint();
        lastXPoint4 = globalClass.getLastPoint4();




        customSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                textOptimalHum.setText(Integer.toString(progressChangedValue) + "%");
                setOptimalHum(progressChangedValue);

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "Select humidity:" + progressChangedValue + "%",
                        Toast.LENGTH_SHORT).show();
                wave = progressChangedValue;
                setOptimalHum(progressChangedValue);
                //wave();

            }
        });
        addNextValue();
        addNextValueAirTemp();
        addNextValueHum2();
        addNextValueHumAir();
        addNextValueSoilTemp();
        //mchart = (LineChart) findViewById(R.id.linechart1);
        //mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
   /*    mchart = new LineChart(this);
//        mainLayout.addView(mchart);

        mchart.setTouchEnabled(true);
        mchart.setDragEnabled(true);
        mchart.setScaleEnabled(true);
        mchart.setDrawGridBackground(true);

        mchart.setPinchZoom(true);
        mchart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mchart.setData(data);

        Legend l = mchart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mchart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = mchart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setDrawGridLines(true);
        yl.setAxisMaxValue(120f);

        YAxis yl2 = mchart.getAxisRight();
        yl2.setEnabled(false);
*/

//        lineChart = findViewById(R.id.lineChart);
//        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(),"data set");
//        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
//        iLineDataSets.add(lineDataSet);
//
//        LineData lineData = new LineData(iLineDataSets);
//        lineChart.setData(lineData);
//        lineChart.invalidate();


        clientID = "xxx";
        topicHum1 = "esp32/soilHum1";
        topicHum2 = "esp32/soilHum2";
        topicOptimalHum = "esp32/getOptSoilHum";
        topicHumAir = "esp32/airHumidity";
        topicTempAir = "esp32/airTemperature";
        topicTempSoil = "esp32/soilTemperature";
        topicWaterLevel = "esp32/waterLevel";


        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.241.245:1883",
                        clientID);

        connectX();


//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //connectX();
//
//            }
//        });
        graphTemp();
        graphHum1();
        //graphHum2();
        //addRandomDataPoint();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notificaion", "My notificaion", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notificaion1", "My notificaion1", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My notificaion3", "My notificaion3", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

//        pour1.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                //pub();
//            }
//        });

//        pour2.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v) {
//                //puboff();
//            }
//        });




    }
    public void pour1(View view){
           pour1();
    }
    public void pour2(View view){
        pour2();
    }

    public void  refresh(View view){
        refresh();
    }

    public void light(View view){
        if(globalClass.isLightOn()) {
            turnLight("off");
            globalClass.setLightOn(false);
        }else {
            turnLight("on");
            globalClass.setLightOn(true);
        }

    }


    private void turnLight(String val){
        String topic = "esp32/light";
        String payload = val;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    private void refresh(){
        String topic = "esp32/getMsg";
        String payload = "ok";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    private void setOptimalHum(int hum){
        String topic = "esp32/setOptimalSoilHumidity";
        String payload = String.valueOf(hum);
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private void pour1(){
        String topic = "esp32/waterPump1On";
        String payload = "off";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    private void pour2(){
        String topic = "esp32/waterPump2On";
        String payload = "off";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }





    private void getTime(){

        date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(date));
    }


    private void gaugeHum1(){

        gauge = (Gauge) findViewById(R.id.gauge);

        gauge.moveToValue(globalClass.getHum1());
        gauge.setLowerText(Float. toString(globalClass.getHum1()) + "%");
    }

    private void gaugeHum2() {
        gauge = (Gauge) findViewById(R.id.gaugehum2);

        gauge.moveToValue(globalClass.getHum2());
        gauge.setLowerText(Float.toString(globalClass.getHum2()) + "%");
    }

//    private void graphWater(){
//        graphWater= (GraphView) findViewById(R.id.graphhum5);
//        seriesWater = new BarGraphSeries<>(new DataPoint[]{
//                //new DataPoint(0, 1),
//                new DataPoint(1, 1)
//
//        });
//        graphWater.addSeries(seriesWater);
//        graphWater.getViewport().setYAxisBoundsManual(true);
//        graphWater.getViewport().setXAxisBoundsManual(true);
//        graphWater.getViewport().setMinX(1);
//        graphWater.getViewport().setMaxX(1);
//        graphWater.getViewport().setMinY(0);
//        graphWater.getViewport().setMaxY(1);
//    }

    private void graphTemp(){
        graphTemp = (GraphView) findViewById(R.id.grapTemerature);
        seriesTempAir = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
        });
        graphTemp.addSeries(seriesTempAir);

        seriesTempSoil = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
        });
        graphTemp.addSeries(seriesTempSoil);

        graphTemp.getViewport().setMinX(0);
        graphTemp.getViewport().setMaxX(lastXPoint4 + 3);
        graphTemp.getViewport().setXAxisBoundsManual(false);
        graphTemp.getViewport().setScalable(true);

        seriesTempSoil.setTitle("Random Curve 1");
        seriesTempSoil.setDataPointsRadius(10);
        seriesTempSoil.setThickness(8);
        seriesTempSoil.setDrawDataPoints(true);
        seriesTempSoil.setColor(Color.RED);
        seriesTempSoil.setTitle("Pôda");
        seriesTempSoil.setAnimated(true);

        seriesTempAir.setAnimated(true);
        seriesTempAir.setTitle("Vzduch");
        seriesTempAir.setColor(Color.BLUE);
        seriesTempAir.setDrawDataPoints(true);
        seriesTempAir.setDrawAsPath(true);
        seriesTempAir.setDrawBackground(true);

        graphTemp.getLegendRenderer().setVisible(true);
        graphTemp.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }
    private void graphHum1(){


        graph = (GraphView) findViewById(R.id.graphhum1);

        series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
        });
        graph.addSeries(series);


        seriesHum2 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
        });
        graph.addSeries(seriesHum2);

        seriesHumAir = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
        });
        graph.addSeries(seriesHumAir);




        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(lastXPoint + 3);
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setScalable(true);


        series.setTitle("Random Curve 1");
        series.setDataPointsRadius(10);
        series.setThickness(8);
        series.setDrawDataPoints(true);
        series.setColor(Color.RED);
        series.setTitle("Senzor 1");
        series.setAnimated(true);
        seriesHum2.setAnimated(true);
        seriesHum2.setTitle("Senzor 2");
        seriesHum2.setColor(Color.BLUE);
        seriesHum2.setDrawDataPoints(true);
        seriesHum2.setDrawAsPath(true);
        seriesHum2.setDrawBackground(true);

        seriesHumAir.setAnimated(true);
        seriesHumAir.setTitle("Air Sensor");
        seriesHumAir.setColor(Color.YELLOW);
        seriesHumAir.setDrawDataPoints(true);
        seriesHumAir.setDrawAsPath(true);
        seriesHumAir.setDrawBackground(true);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }



    private void setView(){
        graph.getViewport().setMinX(lastXPoint - 3);
        graph.getViewport().setMaxX(lastXPoint + 3);
    }
    private void setViewTemp(){
        graphTemp.getViewport().setMinX(lastXPoint4 - 3);
        graphTemp.getViewport().setMaxX(lastXPoint4 + 3);
    }

    private void addNextValue(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint++;
                globalClass.setLastPoint((int) lastXPoint);
                series.appendData(new DataPoint(globalClass.getLastPoint(), globalClass.getHum1()), false, 1000);
                globalClass.setSeries(series);

                setView();
            }
        }, 1000);
    }

    private void addNextValueHum2(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint2++;
                seriesHum2.appendData(new DataPoint(lastXPoint, globalClass.getHum2()), false, 1000);

            }
        }, 1000);
    }

    private void addNextValueHumAir(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint3++;
                seriesHumAir.appendData(new DataPoint(lastXPoint+1, globalClass.getHumAir()), false, 1000);

            }
        }, 1000);
    }

    private void addNextValueAirTemp(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint4++;
                seriesTempAir.appendData(new DataPoint(lastXPoint4, globalClass.getAirTemperature()), false, 1000);

            }
        }, 1000);
    }

    private void addNextValueSoilTemp(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //lastXPoint5++;
                seriesTempSoil.appendData(new DataPoint(lastXPoint4, globalClass.getSoilTemperature()), false, 1000);
                setViewTemp();
            }
        }, 1000);
    }



//    private void init(){
//        btn = findViewById(R.id.btn_sub);
//
//    }


    private void connectX(){
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    sub();
                    //pub();
                   // subHum2();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });


        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    private void pub(){
        String topic = "esp32/light";
        String payload = "on";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }

    private void puboff(){
        String topic = "esp32/light";
        String payload = "off";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    private void sub(){
        try {
            client.subscribe(topicHum1,0);
            client.subscribe(topicHum2,0);
            client.subscribe(topicOptimalHum,0);
            client.subscribe(topicHumAir,0);
            client.subscribe(topicWaterLevel,0);
            client.subscribe(topicTempAir,0);
            client.subscribe(topicTempSoil,0);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                    if (topic.equals(topicHum1)) {
                        Log.d(TAG, "topic: " + topic);
                        Log.d(TAG, "message: " + new String(message.getPayload()));
                        textView.setText("Humidity 1: " + new String(message.getPayload()) + "%");

                        gaugeHum1();

                        hum1 = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setHum1(hum1);
                        addNextValue();

                        if (hum1 < optimalHumidity && hum1Notif == false) {
                            //textView3.setText("Mada Faka");
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion");
                            builder.setContentTitle(appName);
                            builder.setContentText("Senzor 1. Poda je príliš suchá. " + hum1 + "%");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(1, builder.build());
                            hum1Notif = true;
                        } else if (hum1 >= optimalHumidity && hum1Notif == true) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion");
                            builder.setContentTitle(appName);
                            builder.setContentText("Senzor 1. Poda bola zaliata. Aktuálna vlhkosť:. " + hum1 + "%");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(1, builder.build());
                            hum1Notif = false;
                        }
                    }
                    if (topic.equals(topicHum2)) {
                        Log.d(TAG, "topic: " + topicHum2);
                        Log.d(TAG, "message: " + new String(message.getPayload()));

                        //graphWater();
                        gaugeHum2();

                        hum2 = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setHum2(hum2);
                        addNextValueHum2();
                        //textView2.setText("Oprimla humidity: "+ optimalHumidity + "%");
                        textView3.setText("Humidity 2: " + new String(message.getPayload()) + "%");
                        if (hum2 < optimalHumidity && hum2Notif == false) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion1");
                            builder.setContentTitle(appName);
                            builder.setContentText("Senzor 2. Poda je príliš suchá. " + hum2 + "%");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(2, builder.build());
                            hum2Notif = true;
                        } else if (hum2 >= optimalHumidity && hum2Notif == true) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion1");
                            builder.setContentTitle(appName);
                            builder.setContentText("Senzor 2. Poda bola zaliata. Aktuálna vlhkosť:. " + hum2 + "%");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(2, builder.build());
                            hum2Notif = false;
                        }

                    }
                    if (topic.equals(topicOptimalHum)) {
                        optimalHumidity = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setOptimalHumidity(optimalHumidity);
                        textOptimalHumidity.setText("Požadovaná vlhkosť: " + optimalHumidity + "%");
                        if (isOpHum == true) {
                            textOptimalHum.setText(optimalHumidity + "%");
                            customSeekBar.setProgress((int) optimalHumidity);
                            isOpHum = false;
                        }
                    }

                    if (topic.equals(topicTempAir)) {
                        airTemperature = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setAirTemperature(airTemperature);
                        addNextValueAirTemp();
                    }

                    if (topic.equals(topicTempSoil)) {
                        soilTemperature = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setSoilTemperature(soilTemperature);
                        addNextValueSoilTemp();
                    }

                    if (topic.equals(topicHumAir)) {
                        Log.d(TAG, "topic: " + topicHumAir);
                        Log.d(TAG, "message: " + new String(message.getPayload()));
                        //textView.setText(topic +" : " + new String(message.getPayload()));

                        //optimalHumidity =  new String(message.getPayload());

                        humAir = Float.parseFloat(new String(message.getPayload()));
                        globalClass.setHumAir(humAir);
                        addNextValueHumAir();
                        //textView2.setText("Oprimla humidity: "+ optimalHumidity + "%");
//                        textView3.setText("Humidity 2: " + new String(message.getPayload()) + "%");
//                        if(hum2 < optimalHumidity && hum2Notif == false){
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My notificaion1");
//                            builder.setContentTitle(appName);
//                            builder.setContentText("Senzor 2. Poda je príliš suchá. " + hum2 + "%");
//                            builder.setSmallIcon(R.drawable.ic_launcher_background);
//                            builder.setAutoCancel(true);
//
//                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
//                            managerCompat.notify(2, builder.build());
//                            hum2Notif = true;
//                        }else if(hum2 >= optimalHumidity && hum2Notif == true){
//
//                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"My notificaion1");
//                            builder.setContentTitle(appName);
//                            builder.setContentText("Senzor 2. Poda bola zaliata. Aktuálna vlhkos:. " + hum2 + "%");
//                            builder.setSmallIcon(R.drawable.ic_launcher_background);
//                            builder.setAutoCancel(true);
//
//                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
//                            managerCompat.notify(2, builder.build());
//                            hum2Notif = false;
//                        }

                    }

                    if (topic.equals(topicWaterLevel)) {
                        Log.d(TAG, "topic: " + topicWaterLevel);
                        Log.d(TAG, "message: " + new String(message.getPayload()));

                        waterLevel = Float.parseFloat(new String(message.getPayload()));
                        if (waterLevel == 1.0 && waterLevelNotif == false) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion3");
                            builder.setContentTitle(appName);
                            builder.setContentText("Dostatok vody v nádržke");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(3, builder.build());
                            waterLevelNotif = true;
                        }else  if (waterLevel != 1.0 && waterLevelNotif == true) {

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My notificaion3");
                            builder.setContentTitle(appName);
                            builder.setContentText("Málo vody v nádržke");
                            builder.setSmallIcon(R.drawable.ic_launcher_background);
                            builder.setAutoCancel(true);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
                            managerCompat.notify(3, builder.build());
                            waterLevelNotif = false;
                        }


                    }
                }


                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }


        catch (MqttException e){

        }
    }

}