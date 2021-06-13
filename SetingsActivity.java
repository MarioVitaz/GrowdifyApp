package com.example.mymqttapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class SetingsActivity extends AppCompatActivity {
    public static ImageButton buttonBack;
    Button timeBtnMin, timeBtnMinLight;
    Button timeBtnMax, timeBtnMaxLight;
    CheckBox checkTime;
    CheckBox checkAuto;
    CheckBox checkLight;
    NumberPicker numberPicker;
    int hourMax, hourMin, hourMaxLight, hourMinLight;
    int minuteMax, minuteMin, minuteMaxLight, minuteMinLight;
    String clientID;
    private MqttAndroidClient client;
    private static final String TAG = "MyTag";
    GlobalClass globalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);
        timeBtnMaxLight = findViewById(R.id.selectMaxTimeLight);
        timeBtnMinLight = findViewById(R.id.selectMinTimeLight);
        timeBtnMax = findViewById(R.id.selectMaxTime);
        timeBtnMin = findViewById(R.id.selectMinTime);
        checkAuto = findViewById(R.id.checkBoxAto);
        checkTime = findViewById(R.id.checkBoxTime);
        checkLight = findViewById(R.id.checkBoxTimeLight);
        numberPicker = findViewById(R.id.numberPicker);

        clientID = "xxx";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.241.245:1883",
                        clientID);

        connectY();
//
//        if(globalClass.isSetPicker() == false){
//            numberPicker.setValue(globalClass.getNumPickTime());
//        }else{
//            //globalClass.setSetPicker(true);
//            numberPicker.setValue(1);
//        }





        numberPicker.setMaxValue(999);
        numberPicker.setMinValue(1);

        globalClass = (GlobalClass) getApplicationContext();

        if(globalClass.isFirst() == true){
            globalClass.setCheckAuto(true);
            globalClass.setCheckTime(false);
            globalClass.setCheckLight(false);
            numberPicker.setValue(10);
            globalClass.setFirst(false);
        }else {
            checkAuto.setChecked(globalClass.isCheckAuto());
            checkTime.setChecked(globalClass.isCheckTime());
            checkLight.setChecked(globalClass.isCheckLight());
            numberPicker.setValue(globalClass.getNumPickTime());
        }
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                globalClass.setNumPickTime(newVal);
                setWaitingTime(String.valueOf(newVal));
            }
        });

        checkAuto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(checkAuto.isChecked()){
                    globalClass.setCheckAuto(true);
                  automat("on");

                }else if(!checkAuto.isChecked()) {
                    globalClass.setCheckAuto(false);
                    automat("off");

                }
            }
        });

        checkTime.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(checkTime.isChecked()){
                    globalClass.setCheckTime(true);
                    automatTime("on");
                    minTime(globalClass.getHourMinString()+":"+globalClass.getMinuteMinString());
                    maxTime(globalClass.getHourMaxString()+":"+globalClass.getMinuteMaxString());

                }else if(!checkTime.isChecked()) {
                    globalClass.setCheckTime(false);
                    automatTime("off");
                    minTime("99:99");
                    maxTime("00:00");
                }
            }
        });

        checkLight.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if(checkLight.isChecked()){
                    globalClass.setCheckLight(true);
                    lightAutomat("on");
                    minLightTime(globalClass.getHourMinLightString()+":"+globalClass.getMinuteMinLightString());
                    maxLightTime(globalClass.getHourMaxLightString() + ":"+ globalClass.getMinuteMaxLightString());
                }else if(!checkLight.isChecked()) {
                    globalClass.setCheckLight(false);
                    turnLight("of/f");
                    globalClass.setLightOn(false);
                    lightAutomat("off");
                    minLightTime("99:99");
                    maxLightTime("00:00");
                }
            }
        });




        timeBtnMin.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMin(),globalClass.getMinuteMin()));
        timeBtnMax.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMax(),globalClass.getMinuteMax()));

        timeBtnMinLight.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMinLight(),globalClass.getMinuteMinLight()));
        timeBtnMaxLight.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMaxLight(),globalClass.getMinuteMaxLight()));



    }



    public void selectMinTime(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectHour, int selectMinute) {

                    globalClass.setHourMin(selectHour);
                    globalClass.setHourMinString(String.valueOf(selectHour));
                    if(selectHour <10 ){
                        globalClass.setHourMinString("0"+String.valueOf(selectHour));

                    }
                    globalClass.setMinuteMin(selectMinute);
                globalClass.setMinuteMinString(String.valueOf(selectMinute));
                    if(selectMinute <10 ){
                        globalClass.setMinuteMinString("0"+String.valueOf(selectMinute));
                    }
                    timeBtnMin.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMin(),globalClass.getMinuteMin()));
                    minTime(globalClass.getHourMinString()+":"+globalClass.getMinuteMinString());
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hourMin, minuteMin, true);

        timePickerDialog.setTitle("Polievanie od: ");
        timePickerDialog.show();
    }
    public void selectMaxTime(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectHour, int selectMinute) {

                globalClass.setHourMax(selectHour);
                if(selectHour < 10){
                    globalClass.setHourMaxString("0"+String.valueOf(selectHour));
                }else{
                    globalClass.setHourMaxString(String.valueOf(selectHour));
                }
                globalClass.setMinuteMax(selectMinute);
                globalClass.setMinuteMaxString(String.valueOf(selectMinute));
                if (selectMinute < 10) {
                    globalClass.setMinuteMaxString("0"+selectMinute);
                }
                timeBtnMax.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMax(),globalClass.getMinuteMax()));
                maxTime(globalClass.getHourMaxString()+":"+globalClass.getMinuteMaxString());
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hourMax, minuteMax, true);

        timePickerDialog.setTitle("Polievanie do: ");
        timePickerDialog.show();
    }

    public void selectMinTimeLight(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectHour, int selectMinute) {
                String hour = null,min = null;
                globalClass.setHourMinLight(selectHour);
                if(selectHour < 10 ){
                    globalClass.setHourMinLightString("0"+String.valueOf(selectHour));
                }else {
                    globalClass.setHourMinLightString(String.valueOf(selectHour));
                }
                globalClass.setMinuteMinLight(selectMinute);
                if(selectMinute < 10 ){
                    globalClass.setMinuteMinLightString("0"+String.valueOf(selectMinute));
                }else {
                    globalClass.setMinuteMinLightString(String.valueOf(selectMinute));
                }

                timeBtnMinLight.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMinLight(),globalClass.getMinuteMinLight()));
                minLightTime(globalClass.getHourMinLightString() + ":"+ globalClass.getMinuteMinLightString());
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hourMinLight, minuteMinLight, true);

        timePickerDialog.setTitle("Osvetlenie od: ");
        timePickerDialog.show();
    }

    public void selectMaxTimeLight(View view) {

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectHour, int selectMinute) {

                globalClass.setHourMaxLight(selectHour);
                if(selectHour < 10 ){
                    globalClass.setHourMaxLightString("0"+String.valueOf(selectHour));
                }else {
                    globalClass.setHourMaxLightString(String.valueOf(selectHour));
                }
                globalClass.setMinuteMaxLight(selectMinute);
                if(selectMinute < 10 ){
                    globalClass.setMinuteMaxLightString("0"+String.valueOf(selectMinute));
                }else {
                    globalClass.setMinuteMaxLightString(String.valueOf(selectMinute));
                }
                timeBtnMaxLight.setText(String.format(Locale.getDefault(), "%02d:%02d", globalClass.getHourMaxLight(),globalClass.getMinuteMaxLight()));


                maxLightTime(globalClass.getHourMaxLightString() + ":"+ globalClass.getMinuteMaxLightString());

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, /*style,*/ onTimeSetListener, hourMaxLight, minuteMaxLight, true);

        timePickerDialog.setTitle("Osvetlenie do: ");
        timePickerDialog.show();
    }

    private void connectY(){
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");

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


    private void lightAutomat(String val){
        String topic = "esp32/lightAutomat";
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

    private void setWaitingTime(String val){
        String topic = "esp32/setWaitingTime";
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

    private void automat(String val){
        String topic = "esp32/isAutomat";
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
    private void automatTime(String val){
        String topic = "esp32/isTimeAuto";
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

    private void minLightTime(String val){
        String topic = "esp32/lightTimeFromAppMin";
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

    private void maxLightTime(String val){
        String topic = "esp32/lightTimeFromAppMax";
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

    private void maxTime(String val){
        String topic = "esp32/timeFromAppMax";
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

    private void minTime(String val){
        String topic = "esp32/timeFromAppMin";
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
}