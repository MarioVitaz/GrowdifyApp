package com.example.mymqttapp;

import android.app.Application;
import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.logging.ConsoleHandler;

import de.nitri.gauge.Gauge;

public class GlobalClass extends Application {


    int hourMax, hourMin, hourMaxLight, hourMinLight;
    int numPickTime;
    int minuteMax, minuteMin, minuteMaxLight, minuteMinLight;
    float airTemperature, soilTemperature;
    int lastPoint;
    int lastPoint4;
    String clientID;
    String hourMinLightString, minuteMinLightString, hourMaxLightString, minuteMaxLightString, hourMaxString, minuteMaxString;
String hourMinString,minuteMinString;

    public String getHourMinString() {
        return hourMinString;
    }

    public void setHourMinString(String hourMinString) {
        this.hourMinString = hourMinString;
    }

    public String getMinuteMinString() {
        return minuteMinString;
    }

    public void setMinuteMinString(String minuteMinString) {
        this.minuteMinString = minuteMinString;
    }

    public String getHourMaxString() {
        return hourMaxString;
    }

    public void setHourMaxString(String hourMaxString) {
        this.hourMaxString = hourMaxString;
    }

    public String getMinuteMaxString() {
        return minuteMaxString;
    }

    public void setMinuteMaxString(String minuteMaxString) {
        this.minuteMaxString = minuteMaxString;
    }

    public String getHourMaxLightString() {
        return hourMaxLightString;
    }

    public void setHourMaxLightString(String hourMaxLightString) {
        this.hourMaxLightString = hourMaxLightString;
    }

    public String getMinuteMaxLightString() {
        return minuteMaxLightString;
    }

    public void setMinuteMaxLightString(String minuteMaxLightString) {
        this.minuteMaxLightString = minuteMaxLightString;
    }

    public String getHourMinLightString() {
        return hourMinLightString;
    }

    public void setHourMinLightString(String hourMinLightString) {
        this.hourMinLightString = hourMinLightString;
    }

    public String getMinuteMinLightString() {
        return minuteMinLightString;
    }

    public void setMinuteMinLightString(String minuteMinLightString) {
        this.minuteMinLightString = minuteMinLightString;
    }

    private MqttAndroidClient client;
    private static final String TAG = "MyTag";

    public int getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(int lastPoint) {
        this.lastPoint = lastPoint;
    }

    public int getLastPoint4() {
        return lastPoint4;
    }

    public void setLastPoint4(int lastPoint4) {
        this.lastPoint4 = lastPoint4;
    }

    public float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(float airTemperature) {
        this.airTemperature = airTemperature;
    }

    public float getSoilTemperature() {
        return soilTemperature;
    }

    public void setSoilTemperature(float soilTemperature) {
        this.soilTemperature = soilTemperature;
    }

    boolean isFirst = true;
    boolean isFirstMain = true;
    boolean isLightOn = false;
    boolean isCheckAuto, isCheckTime, isCheckLight;
    boolean isSetPicker = true;

    float hum1, hum2, optimalHumidity, humAir;

    public boolean isLightOn() {
        return isLightOn;
    }

    public void setLightOn(boolean lightOn) {
        isLightOn = lightOn;
    }

    public boolean isFirstMain() {
        return isFirstMain;
    }

    public void setFirstMain(boolean firstMain) {
        isFirstMain = firstMain;
    }

    public boolean isCheckLight() {
        return isCheckLight;
    }

    public void setCheckLight(boolean checkLight) {
        isCheckLight = checkLight;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isSetPicker() {
        return isSetPicker;
    }

    public void setSetPicker(boolean setPicker) {
        isSetPicker = setPicker;
    }

    public int getNumPickTime() {
        return numPickTime;
    }

    public void setNumPickTime(int numPickTime) {
        this.numPickTime = numPickTime;
    }

    public int getHourMaxLight() {
        return hourMaxLight;
    }

    public void setHourMaxLight(int hourMaxLight) {
        this.hourMaxLight = hourMaxLight;
    }

    public int getHourMinLight() {
        return hourMinLight;
    }

    public void setHourMinLight(int hourMinLight) {
        this.hourMinLight = hourMinLight;
    }

    public int getMinuteMaxLight() {
        return minuteMaxLight;
    }

    public void setMinuteMaxLight(int minuteMaxLight) {
        this.minuteMaxLight = minuteMaxLight;
    }

    public int getMinuteMinLight() {
        return minuteMinLight;
    }

    public void setMinuteMinLight(int minuteMinLight) {
        this.minuteMinLight = minuteMinLight;
    }

    public float getHumAir() {
        return humAir;
    }

    public void setHumAir(float humAir) {
        this.humAir = humAir;
    }

    public float getOptimalHumidity() {
        return optimalHumidity;
    }

    public void setOptimalHumidity(float optimalHumidity) {
        this.optimalHumidity = optimalHumidity;
    }

    private LineGraphSeries<DataPoint> series;

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    public void setSeries(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }

    public float getHum2() {
        return hum2;
    }

    public void setHum2(float hum2) {
        this.hum2 = hum2;
    }

    public float getHum1() {
        return hum1;
    }

    public void setHum1(float hum1) {
        this.hum1 = hum1;
    }

    public boolean isCheckAuto() {
        return isCheckAuto;
    }

    public void setCheckAuto(boolean checkAuto) {
        isCheckAuto = checkAuto;
    }

    public boolean isCheckTime() {
        return isCheckTime;
    }

    public void setCheckTime(boolean checkTime) {
        isCheckTime = checkTime;
    }

    public int getHourMax() {
        return hourMax;
    }

    public void setHourMax(int hourMax) {
        this.hourMax = hourMax;
    }

    public int getHourMin() {
        return hourMin;
    }

    public void setHourMin(int hourMin) {
        this.hourMin = hourMin;
    }

    public int getMinuteMax() {
        return minuteMax;
    }

    public void setMinuteMax(int minuteMax) {
        this.minuteMax = minuteMax;
    }

    public int getMinuteMin() {
        return minuteMin;
    }

    public void setMinuteMin(int minuteMin) {
        this.minuteMin = minuteMin;
    }

    public void doIt(){
        clientID = "xxx";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.241.245:1883",
                        clientID);

        connectY();

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

}
