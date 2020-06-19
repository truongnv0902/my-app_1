//Test Filter
// 08/05/2019
// Orientation : done
// Move triangle:
// Sync Java Filter: done
// First QR scanning: done
// Exit QR scanning mode:
// QR ref: done
// Scan wifi often:
// Standard condition ( threshold) : done

package com.example.ti_da.countingfootsteps;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.Permissions;
import java.text.DecimalFormat;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity<Rssi> extends AppCompatActivity implements SensorEventListener {

    WifiManager mainWifi;
    //WifiReceiver wifiReceiver;
    List<ScanResult> wifiList;

    private TextView textView;
    private SensorManager sensorManager;
    private Sensor gyro;
    private Sensor accel;
    private Sensor compass;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps = 0;
    Button BtnStop, BtnStart, BtnReset, BtnTest;
    TextView TvNameL, TvNameN, TvNameD;
    TextView TvSteps, TvLength, TvDistance;
    TextView timens, tvRssi;
    Canvas canvas;
    Paint mPaint;
    Paint mPaint1;
    ImageView imv;
    ImageView arr;
    Paint paint;
    // add
    boolean isReset = false;
    public static final String TAG = MainActivity.class.getSimpleName();


    private static final int ACCEL_RING_SIZE = 50;
//    private static final int ACCEL_RING_SIZE = 5;
    private static final int VEL_RING_SIZE = 20;
    private static final int FILTER_ORDER_SIZE = 5;
    private static final float STEP_THRESHOLD = 16.5f;
    private static final int STEP_DELAY_NS = 450000000;
    private int accelRingCounter = 0;
    private float[] accelRingX = new float[ACCEL_RING_SIZE];
    private float[] accelRingY = new float[ACCEL_RING_SIZE];
    private float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private float[] new_accelRing = new float[FILTER_ORDER_SIZE];
    private float[] y_normACC = new float[FILTER_ORDER_SIZE - 1];

    private float[] new_accelRingX = new float[FILTER_ORDER_SIZE];
    private float[] new_accelRingY = new float[FILTER_ORDER_SIZE];
    private float[] new_accelRingZ = new float[FILTER_ORDER_SIZE];
    private int velRingCounter = 0;
    private float[] velRing = new float[VEL_RING_SIZE];
    private long lastStepTimeNs = 0;
    private int screenWidth;
    private int screenHeight;
    public int cuong_do = 0;

    Double Fs = 40.0;
    Double Ts = 1 / Fs;
    String Step0 = "0.0 m";
    String Step1 = "0.6 m";
    String Step2 = "0.7 m";
    String M = "m";
    String file_name = "1_";
    String Ssum1;
    String java_yn = ""; // Check y(n)
    String Norm_acc = "";// Check x(n)
    int time = 0;
    int time_prev = 0;
    float y_normACC_prev = 1;
    Double delta_t = 0.5;
    DecimalFormat df = new DecimalFormat("#0.0");
    int a = 0;
    int Count_L = 0;
    int Count_R = 0;
    // L1 = 5 L2=6
    int L1 = 8;
    int L2 = 9;
    float lastcurrentH = 350;
    float lastcurrentV = 300;
    //p205
    float currentH_p205 = 390;
    float currentV_p205 = 308;

    //p204
    float currentH_p204 = 390;
    float currentV_p204 = 347;

    // P203
    float currentH_p203 = 400;
    float currentV_p203 = 253;
    //P202
    float currentH_p202 = 390;
    float currentV_p202 = 175;

    //P201
    float currentH_p201 = 390;
    float currentV_p201 = 220;
    //p1
    float currentH_p1 = 290;
    float currentV_p1 = 173;

    //p2
    float currentH_p2 = 30;
    float currentV_p2 = 75;

    //p3
    float currentH_p3 = 30;
    float currentV_p3 = 310;


    int TIMECOUNTING = 10, SAMPLEINTERVAL = 100;
//    int Wifi_LevelBMTTVT;
//    int Wifi_LevelBMHTVT;
    int check_first = 0;
    float A1 = 0.5f;
    float A2 = 2f;
    double Distance = 0;
    float mdegree;
    float degree2;
    float currentDegree = 0f;
    float y_normACC_b = 0;
    float[] y_normACC_check = new float[3];
    RotateAnimation ra;
    TranslateAnimation translateAnimation;
    TranslateAnimation rr;
    Animation ani;

    Handler handler;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRssi = (TextView) findViewById(R.id.Rssi);


        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what != 0) {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo info = wifiManager.getConnectionInfo();
                    cuong_do = info.getRssi();
                    tvRssi.setText(String.valueOf(cuong_do));
                    if(cuong_do >= -55) {
                        timens.setText("Match: Test point");
                        lastcurrentH = currentH_p1;
                        lastcurrentV = currentV_p1;

                        canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
                    }
                }
                else {

                }
            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 100, 100);

        //Runtime Permission

//        protected void onCreate((Bundle) savedInstanceState){
            int Permission_All = 1;
            String[] Permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//            if (!hasPermission(this, java.security.Permission)) {
//                ActivityCompat.requestPermissions(this, Permissions, Permission_All);
//            }
//        }

//        Get wifiList for the first display
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert mainWifi != null;
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();


//        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imv = (ImageView) findViewById(R.id.map);
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(106, 110, 177));
        mPaint.setStrokeWidth(3);


        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setColor(Color.rgb(180, 10, 110));
        mPaint1.setStrokeWidth(3);

        configDraw();
        /**/
        // p204(357,350)
        // canvas.drawCircle(lastcurrentH, lastcurrentV, 4, mPaint); // Tọa độ đầu tiên


        TvSteps = (TextView) findViewById(R.id.tv_steps);
        TvLength = (TextView) findViewById(R.id.tv_length);
        TvSteps = (TextView) findViewById(R.id.tv_steps);
        TvLength = (TextView) findViewById(R.id.tv_length);
        TvDistance = (TextView) findViewById(R.id.tv_Distance);
        TvNameL = (TextView) findViewById(R.id.tv_A);
        TvNameN = (TextView) findViewById(R.id.tv_B);
        TvNameD = (TextView) findViewById(R.id.tv_C);
        tvRssi = (TextView) findViewById(R.id.Rssi);

        timens = (TextView) findViewById(R.id.time);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        BtnReset = (Button) findViewById(R.id.btn_Reset);
//        BtnTest = (Button) findViewById(R.id.btn_Test);
        arr = findViewById(R.id.Arrow);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        screenWidth = size.x;
        /*arr.setX(20.0f);
        arr.setY(20.0f);*/

        //CUONG DO WIFI



        //TvLength.setText(String.valueOf(Step1));


        //start();
//        QRcode_Launch();
        timens.setText("Match: p204");
        lastcurrentH = currentH_p204;
        lastcurrentV = currentV_p204;

        canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);


//        BtnTest.setOnClickListener (new View.OnClickListener(){
//            @Override
//            public void onClick(View arg0) {
//
//                    timens.setText("Match: Test");
//                    lastcurrentH = currentH_p1;
//                    lastcurrentV = currentV_p1;
//
//                    canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
//
//            }
//        });

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                numSteps = 0;
                   /* for (int i = 0; i < wifiList.size(); i++) {
                        if (wifiList.get(i).BSSID.equals("30:b5:c2:a9:83:08") && wifiList.get(i).level> -56 )
                        {
                            lastcurrentH = 400;
                            lastcurrentV = 360;
                            Toast.makeText(MainActivity.this, wifiList.get(i).SSID + " " + wifiList.get(i).level+" "+ wifiList.get(i).level + "\n", Toast.LENGTH_LONG).show();
                        }
                    }*/
                sensorManager.registerListener(MainActivity.this, accel, 25000);
                sensorManager.registerListener(MainActivity.this, gyro, 50000000);
                sensorManager.registerListener(MainActivity.this, compass, SensorManager.SENSOR_DELAY_GAME);
                BtnStart.setTextSize(25);
                BtnStop.setTextSize(20);
                BtnReset.setTextSize(20);
            }
        });

        BtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(MainActivity.this);
                timens.setText("Direction");
                BtnStop.setTextSize(25);
                BtnStart.setTextSize(20);
                numSteps = 0;
                configDraw();
                canvas.drawCircle(lastcurrentH, lastcurrentV, 3, mPaint);
                TvSteps.setText(String.valueOf(numSteps));


                // TvDistance.setText("Distance");

                  /*  translateAnimation = new TranslateAnimation(0,150,0,150);
                    translateAnimation.setDuration(1000);
                    translateAnimation.setFillAfter(true);
                    //translateAnimation.setRepeatCount(0);
                    arr.startAnimation(translateAnimation);*/


                writeTofile(file_name + "NORM_ACC", Ssum1);
                TvDistance.setText(String.valueOf(df.format(Distance)).replace(",", ".") + " " + M);
                writeTofile("java_processed_data", java_yn);
                writeTofile("java_raw_data ", Norm_acc);


            }
        });
        BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                MainActivity.super.recreate();

            }

        });
    }

//    public void QRcode_Launch()
////    {
////        final IntentIntegrator integrator = new IntentIntegrator(this);
////        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
////        integrator.setPrompt("Check in action");
////        integrator.setCameraId(0);
////        integrator.setOrientationLocked(false);
////        integrator.setBeepEnabled(false);
////        integrator.initiateScan();

// sửa code
    //
    //
    //
    //

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null)
        {


            String contents = result.getContents();
            timens.setText(contents+"Fail");
            String p203 = "P203";


            if( contents.equals("p201"))
            {
                timens.setText("Match: p201");
                lastcurrentH = currentH_p201;
                lastcurrentV = currentV_p201;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p202"))
            {
                timens.setText("Match: p202");
                lastcurrentH = currentH_p202;
                lastcurrentV = currentV_p202;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p203"))
            {
                timens.setText("Match: p203");
                lastcurrentH = currentH_p203;
                lastcurrentV = currentV_p203;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p204"))
            {
                timens.setText("Match: p204");
                lastcurrentH = currentH_p204;
                lastcurrentV = currentV_p204;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p205"))
            {
                timens.setText("Match: p205");
                lastcurrentH = currentH_p205;
                lastcurrentV = currentV_p205;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p1"))
            {
                timens.setText("Match: p1");
                lastcurrentH = currentH_p1;
                lastcurrentV = currentV_p1;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p2"))
            {
                timens.setText("Match: p2");
                lastcurrentH = currentH_p2;
                lastcurrentV = currentV_p2;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }
            if( contents.equals("p3"))
            {
                timens.setText("Match: p3");
                lastcurrentH = currentH_p3;
                lastcurrentV = currentV_p3;

                canvas.drawCircle(lastcurrentH, lastcurrentV, 7, mPaint1);
            }

            Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
        }
        else {

            super.onActivityResult(requestCode, resultCode, intent);
        }
    }


    public void drawTriangle(Canvas canvas, Paint paint, int x, int y, int height, float degree)
    {
        int halfWidth = height / 2;

        Path path = new Path();
        path.moveTo(x, y - height*2/3); // Top
        path.lineTo(x - height/2, y + height/3); // Bottom left
        path.lineTo(x + height/2, y + height/3); // Bottom right
        path.lineTo(x, y - height*2/3); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
        canvas.rotate(degree);
    }
    public void drawTriangle2(Canvas canvas, Paint paint, int x, int y, int height, float angle)
    {
        int halfWidth = height / 2;

        Path path = new Path();
        path.moveTo(x, (float) (y - height*2/3*Math.sin(angle))); // Top
        path.lineTo((float) (x - height/3*Math.sin(angle) - 2*height*Math.cos(angle)/Math.sqrt(3)), (float) (y + height/3*Math.cos(angle)-2*height*Math.sin(angle)/Math.sqrt(3))); // Bottom left
        path.lineTo((float) (x +  height/3*Math.sin(angle) + 2*height*Math.cos(angle)/Math.sqrt(3)), (float) (y + height/3*Math.cos(angle)+2*height*Math.sin(angle)/Math.sqrt(3))); // Bottom right
        path.lineTo(x, (float) (y -  height*2/3*Math.sin(angle))); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
//    huong la ban:
        @Override
        public void onSensorChanged(SensorEvent event)
        {

        switch (event.sensor.getType()) {

            case Sensor.TYPE_ORIENTATION:
                mdegree=Math.round(event.values[0]-7);
                degree2 = (float)Math.toRadians(mdegree);



                timens.setBackgroundColor(Color.rgb(34,30,56));
                timens.setTextColor(Color.rgb(250,250,250));
                // timens.setText("cos("+Float.toString(mdegree)+")=" + (lastcurrentH-(float)(L1*Math.sin(degree2))) + " ");
                // timens.setText(" " + Float.toString(degree) + " ");
                break;


            case Sensor.TYPE_ACCELEROMETER:
                time++;
                Log.e(TAG, Integer.toString(time) + ":\n");
              //  timens.setText("North: "+Float.toString(mdegree) +" degree");

                ra =new RotateAnimation(currentDegree, mdegree-105, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                ra.setDuration(1000);
                ra.setFillAfter(true);
                ra.setRepeatCount(0);
                arr.startAnimation(ra);

                currentDegree=mdegree;


                //    event.timestamp, event.values[0], event.values[1], event.values[2]);

                float[] currentAccel = new float[3];
                currentAccel[0] = event.values[0];
                currentAccel[1] = event.values[1];
                currentAccel[2] = event.values[2];
                float normACC = SensorFilter.norm(currentAccel);
                Norm_acc += Float.toString(normACC)+"\n";

                // Trọng số bộ lọc
                float coff_A0  = 1.0000f;
                float[] coff_A  = new float[4];
                coff_A [0] = -3.6856f;
                coff_A [1] = 5.1840f;
                coff_A [2] = -3.2970f;
                coff_A [3] = 0.8008f;

                float[] coff_B  = new float[5];
                coff_B [0] = 0.0055f;
                coff_B [1] = 0f;
                coff_B [2] = -0.0111f;
                coff_B [3] = 0f;
                coff_B [4] = 0.0055f;

                if( check_first == 0 )
                    for ( int i=0 ; i<new_accelRing.length; i++) new_accelRing[i] = 0;


                new_accelRing[0]=new_accelRing[1];
                new_accelRing[1]=new_accelRing[2];
                new_accelRing[2]=new_accelRing[3];
                new_accelRing[3]=new_accelRing[4];
                new_accelRing[4] = normACC;

                accelRingCounter++;
                float y_normACC_p;

                float y_normACC_0 = 0;
                if( check_first != 0 ) y_normACC_0 = y_normACC_b;

                if ( check_first == 0)
                   for (int i=0; i< y_normACC.length; i++) y_normACC[i] = 0;

               /*
                 present yn : y_normACC[0] y_normACC_0
                 y(n-1)     : y_normACC[1]                */

                y_normACC[3]=y_normACC[2];
                y_normACC[2]=y_normACC[1];
                y_normACC[1]=y_normACC[0];
                y_normACC[0]=y_normACC_0 ;
                y_normACC_0= (SensorFilter.dot(coff_B,new_accelRing)-SensorFilter.dot(coff_A,y_normACC))/coff_A0;
                //y_normACC_0= y_normACC_0 - 1.5f; // Khử DC
                y_normACC_b = y_normACC_0;

                y_normACC_check[0] = y_normACC_check[1];
                y_normACC_check[1] = y_normACC_check[2];
                y_normACC_check[2] = y_normACC_0;


                java_yn+= Float.toString(y_normACC_check[1])+"\n";
                if( check_first != 0 )
                {
                    if( ( Math.abs(y_normACC_check[1]) > Math.abs(y_normACC_check[0])) && (Math.abs(y_normACC_check[1])>Math.abs(y_normACC_check[2]) ))
                        if(     ((Ts*time - Ts*time_prev > delta_t)&&((y_normACC_check[1])*(y_normACC_prev) >0)) || ((y_normACC_check[1])*(y_normACC_prev)<0) )
                        {
                            if( time >= 50004) time = 0; // reset time avoid huge value
                            time_prev = time;
                            y_normACC_prev = y_normACC_check[1];

                            if( y_normACC_check[1] >= A1 && y_normACC_check[1] <= A2 ) draw(); // Ngưỡng 1

                            else if(y_normACC_check[1] >= A2) draw();// Nguong 2
                        }
                }
                check_first = 1;

                /*
                // __________________________________________________ Old version_____________________________________________
                velRingCounter++;

                velRing[velRingCounter % VEL_RING_SIZE] = y_normACC[0]-9.7f;

                double Sum1;
                if (event.timestamp - lastStepTimeNs > STEP_DELAY_NS && velRing.length == VEL_RING_SIZE &&velRing[velRing.length-1] !=0) {

                    double Sum = 0;
                    double Mean;
                    double Temp = 0;


                    for (int i = 0; i < velRing.length; i++) {
                        Sum += velRing[i];
                        }
                    Mean = Sum / velRing.length;

                    for (int i = 0; i < velRing.length; i++)
                    {
                        Temp += (velRing[i] - Mean) * (velRing[i] - Mean);
                        }
                    Sum1 = Temp / velRing.length-1;
                  //  TvLength.setText(String.valueOf(Sum1));
                    Ssum1+= Double.toString(Sum1) + "\n";
                    if (Sum1 > A2) {
                        if(a%3==0) start2();
                        a = ++numSteps;
                        TvSteps.setText(String.valueOf(a));
                        float currentH = lastcurrentH + (float)(L1*Math.sin(degree2)); //
                        float currentV = lastcurrentV - L1*(float)(Math.cos(degree2)); // North
                        Distance = Distance + 0.6;
                        canvas.drawCircle( currentH, currentV, 3, mPaint);
                        lastcurrentV = currentV;
                        lastcurrentH = currentH;
                        imv.invalidate();
                        TvLength.setText(String.valueOf(Step1));
                    }

                    TvDistance.setText(String.valueOf(df.format(Distance)).replace(",", ".")+" "+M);
                    lastStepTimeNs = event.timestamp;
                }
*/

        }
    }



    public void configDraw(){
        Bitmap b = Bitmap.createBitmap(420, 420, Bitmap.Config.ARGB_8888);
        b = b.copy(b.getConfig(), true);
        canvas = new Canvas(b);
        imv.setImageBitmap(b);
        imv.setBackgroundResource(R.drawable.map_g2_new_bigger);
        imv.invalidate();
    }

    private boolean hasPermission(MainActivity mainActivity, String[] permissions) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && mainActivity != null && permissions != null){
            for (String permission:permissions){
                if (ActivityCompat.checkSelfPermission(mainActivity, permission) !=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
    // Viet ham scan and check wifi
    // BMTTVT 30:b5:c2:a9:83:08
    // Chinh  04:25:c5:eb:e7:5c


    private  void start2(){
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();
        for (int i = 0; i < wifiList.size(); i++) {
            timens.setText( " "+wifiList.get(i).level+" ");
           /* if (wifiList.get(i).BSSID.equals("04:25:c5:eb:e7:5c") && wifiList.get(i).level< -50 )
            {
                lastcurrentH = 400;
                lastcurrentV = 360;
                Toast.makeText(MainActivity.this, wifiList.get(i).SSID + " " + wifiList.get(i).level+ "\n", Toast.LENGTH_LONG).show();

            }*/
        }



    }
    private  void draw()
    {
        if(a%3==0) start2();
        a = ++numSteps;
        TvSteps.setText(String.valueOf(a));
        float currentH = lastcurrentH + (float)(L1*Math.sin(degree2)); //
        float currentV = lastcurrentV - L1*(float)(Math.cos(degree2)); // North
        Distance = Distance + 0.6;
        canvas.drawCircle( currentH, currentV, 3, mPaint);



        lastcurrentV = currentV;
        lastcurrentH = currentH;
        imv.invalidate();
        TvLength.setText(String.valueOf(Step1));


    }
    private  void draw2()
    {
        if(a%3==0) start2();
        a = ++numSteps;
        TvSteps.setText(String.valueOf(a));
        float currentH = lastcurrentH + (float)(L1*Math.sin(degree2)); //
        float currentV = lastcurrentV - L1*(float)(Math.cos(degree2)); // North
        Distance = Distance + 0.6;
        canvas.drawCircle( currentH, currentV, 3, mPaint1);
        lastcurrentV = currentV;
        lastcurrentH = currentH;
        imv.invalidate();
        TvLength.setText(String.valueOf(Step1));
    }


//scanwifi
    private void start(){
        CountDownTimer countDownTimer = new CountDownTimer(TIMECOUNTING * 1000, SAMPLEINTERVAL) {
            //int i = TIMECOUNTING - 1;
            @Override
            public void onTick(long l) {
                Date date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                mainWifi.startScan();
                wifiList = mainWifi.getScanResults();
                for (int i = 0; i < wifiList.size(); i++) {
                    timens.setText( timestamp.toString()+" "+wifiList.get(i).level+" ");
                    if (wifiList.get(i).BSSID.equals("30:b5:c2:a9:83:09") && wifiList.get(i).level> -89 )
                    {
                        lastcurrentH = 400;
                        lastcurrentV = 360;
                     //   Toast.makeText(MainActivity.this, wifiList.get(i).SSID + " " + wifiList.get(i).level+" "+ wifiList.get(i).level + "\n", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFinish() {
            mainWifi.startScan();
            wifiList = mainWifi.getScanResults();
            }


        };
        countDownTimer.start();

    }


    public void writeTofile(String fileName, String data)
    {
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/SensorData/" );

        //Toast.makeText(getApplicationContext(),"File 1:" +path.toString(),Toast.LENGTH_LONG).show();
        if(!path.exists())
        {
            path.mkdirs();
        }
        final File file = new File(path + fileName + ".txt");
        //Toast.makeText(getApplicationContext(),"File 2:" +file.toString(),Toast.LENGTH_LONG).show();
        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            Toast.makeText(getApplicationContext(),"ok :" + file.toString(),Toast.LENGTH_LONG).show();
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            //Toast.makeText(getApplicationContext(),"Notice: " + e.toString(),Toast.LENGTH_LONG).show();
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}