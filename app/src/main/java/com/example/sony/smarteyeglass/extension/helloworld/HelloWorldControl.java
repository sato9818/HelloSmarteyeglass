/*
Copyright (c) 2011, Sony Mobile Communications Inc.
Copyright (c) 2014, Sony Corporation

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Mobile Communications Inc.
 nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.example.sony.smarteyeglass.extension.helloworld;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.sony.smarteyeglass.SmartEyeglassControl;
import com.sony.smarteyeglass.extension.util.SmartEyeglassControlUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import android.os.Handler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Demonstrates how to communicate between an Android activity and its
 * corresponding SmartEyeglass app.
 *
 */
public final class HelloWorldControl extends ControlExtension {
    private Handler handler = null;
    private Runnable r = null;
    private String distance = "";
    private String duration = "";
    private String instruction1 = "";
    private String instruction2 = "";
    private String dura_inst = "";
    private String address = "";
    /** Instance of the SmartEyeglass Control Utility class. */
    private final SmartEyeglassControlUtils utils;

    /** The SmartEyeglass API version that this app uses */
    private static final int SMARTEYEGLASS_API_VERSION = 1;

    /**
     * Shows a simple layout on the SmartEyeglass display and sets
     * the text content dynamically at startup.
     * Tap on the device controller touch pad to start the Android activity
     * for this app on the phone.
     * Tap the Android activity button to run the SmartEyeglass app.
     *
     * @param context            The context.
     * @param hostAppPackageName Package name of SmartEyeglass host application.
     */
    public HelloWorldControl(final Context context,
            final String hostAppPackageName, final String message) {
        super(context, hostAppPackageName);
        utils = new SmartEyeglassControlUtils(hostAppPackageName, null);
        utils.setRequiredApiVersion(SMARTEYEGLASS_API_VERSION);
        utils.activate(context);



        /*
         * Set reference back to this Control object
         * in ExtensionService class to allow access to SmartEyeglass Control
         */
        HelloWorldExtensionService.Object.SmartEyeglassControl = this;

        /*
         * Show the message that was set Iif any) when this Control started
         */
        if (message != null) {
            //sendText(R.id.btn_update_this, message);
            showToast(message);
        } else {
            updateLayout();
        }
    }

    /**
     * Provides a public method for ExtensionService and Activity to call in
     * order to request start.
     */
    public void requestExtensionStart() {
        startRequest();
    }

    // Update the SmartEyeglass display when app becomes visible
    @Override
    public void onResume() {
        updateLayout();
        //startlocation();
        super.onResume();
    }

    // Clean up data structures on termination.
    @Override
    public void onDestroy() {
        Log.d(Constants.LOG_TAG, "onDestroy: HelloWorldControl");
        utils.deactivate();
    };

    @Override
    public void onPause() {
        handler.removeCallbacks(r);
    }
    /**
     * Process Touch events.
     * This starts the Android Activity for the app, passing a startup message.
     */
    @Override
    public void onTouch(final ControlTouchEvent event) {
        super.onTouch(event);
        HttpGetData atClass = new HttpGetData();
        // AsyncTaskの実行
        atClass.execute();
        sendText(R.id.btn_update_this2, "距離：" + distance + ", " + "時間：" + duration);
        sendText(R.id.btn_update_this1, "住所：" + address);
        sendText(R.id.btn_update_this3,  instruction1 + "。 " + dura_inst + "先" + instruction2 + "。");
        //sendText(R.id.btn_update_this3,  "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        //sendText(R.id.btn_update_this4,  dura_inst + "先" + instruction2);
        //HelloWorldExtensionService.Object.sendMessageToActivity("Hello Activity!!!!!!!");
        //sendText(R.id.btn_update_this1, HelloWorldActivity.str1);
        //sendText(R.id.btn_update_this2, HelloWorldActivity.str2);
    }

    public void startlocation(){
        handler = new Handler();
        r = new Runnable() {
            //int count = 0;
            @Override
            public void run() {
                // UIスレッド
                /*
                String s1 = String.valueOf(count);
                sendText(R.id.btn_update_this1, s1);
                count++;
                */
                //１秒毎に表示している。
                sendText(R.id.btn_update_this1, "距離：" + distance);
                sendText(R.id.btn_update_this1, "時間：" + duration);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(r);
    }
//add1
    /**
     *  Update the display with the dynamic message text.
     */
    public void updateLayout() {
        showLayout(R.layout.layout, null);
        //sendText(R.id.btn_update_this, "Hello");
    }

    /**
     * Timeout dialog messages are similar to Toast messages on
     * Android Activities
     * This shows a timeout dialog with the specified message.
     */
    public void showToast(final String message) {
        Log.d(Constants.LOG_TAG, "Timeout Dialog : HelloWorldControl");
        utils.showDialogMessage(message,
                SmartEyeglassControl.Intents.DIALOG_MODE_TIMEOUT);
    }
    class HttpGetData extends AsyncTask<Void, Void, JSONObject> {

        public String StrSpi(String str){
            if(str.equals("徒歩")){
                return "walking";
            }else if(str.equals("自転車")){
                return "driving";
            }else if(str.equals("車")){
                return "car";
            }else{
                return "walking";
            }

        }
        // doInBackgroundの事前準備処理（UIスレッド）


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(Void... param) {
            Log.d("A", "AAAAAAA");
            JSONObject datas = new JSONObject();

            try {

//            StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
//            urlStrBuilder.append("?location=" + latitude + "," + longtitude);
//            urlStrBuilder.append("&sensor=true&language=ja&keyword=ファミレス&radius=" + radius+"&key=AIzaSyAiFtizLKssWRexnD8r9PQkVWN7U8OQqbM");
                String str1 = "takadanobaba";
                String str2 = "waseda";

                String str3 = "https://maps.googleapis.com/maps/api/directions/json?origin="+LocationActivity.lati+","+LocationActivity.longti+"&destination="+LocationActivity.destination+"&mode="+StrSpi(LocationActivity.str_spi)+"&language=ja&key=AIzaSyCxDuj0SIOyIdwR3H05HrG0u5AkKaHKM9Y";
                StringBuilder urlStrBuilder = new StringBuilder(str3/*"https://maps.googleapis.com/maps/api/directions/json?origin=takadanobaba&destination=waseda&key=AIzaSyCxDuj0SIOyIdwR3H05HrG0u5AkKaHKM9Y"*/);
                URL u = new URL(urlStrBuilder.toString());

                // HTTP request
                HttpURLConnection con = (HttpURLConnection) u.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                BufferedInputStream is = new BufferedInputStream(con.getInputStream());


                int bytesRead = -1;
                byte[] buffer = new byte[1024];
                String jsonResult = "";
                while ((bytesRead = is.read(buffer)) != -1) {
                    String buf = new String(buffer, 0, bytesRead);
                    jsonResult += buf;
                }

                is.close();

                datas = new JSONObject(jsonResult);
//            datas = jsonObject.getJSONArray("results");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return datas;
        }


        @Override
        protected void onPostExecute(JSONObject status) {
            super.onPostExecute(status);
            Log.d("test", status.toString());
            try {
                distance = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                duration = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
                String buf1 = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(0).getString("html_instructions");
                String buf2 = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(1).getString("html_instructions");
                dura_inst = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(0).getJSONObject("distance").getString("text");
                address = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getString("end_address");
                String str1 = "";
                String str2 = "";
                int a=0;
                for(int i = 0;i<buf1.length();i++){
                    char ch = buf1.charAt(i);
                    if(a == 0) {
                        if (!((ch <= '\u007e') || (ch == '\u00a5') || (ch == '\u203e'))) {
                            str1 += ch;
                            a = 1;
                        }
                    }else{
                        if(ch == '\u003c'){
                            a=0;
                            continue;
                        }
                        str1 += ch;
                    }
                }
                a = 0;
                for(int i = 0;i<buf2.length();i++){
                    char ch = buf2.charAt(i);
                    if(a == 0) {
                        if (!((ch <= '\u007e') || (ch == '\u00a5') || (ch == '\u203e'))) {
                            str2 += ch;
                            a = 1;
                        }
                    }else{
                        if(ch == '\u003c'){
                            a = 0;
                            continue;
                        }
                        str2 += ch;
                    }
                }
                instruction1 = str1;
                instruction2 = str2;

            } catch (Exception e) {
                e.printStackTrace();
            }


        }



    }
}
