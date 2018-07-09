package com.example.masato.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.text);

        HttpGetData atClass = new HttpGetData();


        // AsyncTaskの実行
        atClass.execute();

    }

    class HttpGetData extends AsyncTask<Void, Void, JSONObject> {

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

                EditText edittext1 = (EditText) findViewById(R.id.editText);
                String text = edittext1.getText().toString();

                StringBuilder urlStrBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?origin=大阪&destination="+text+"&key=AIzaSyCxDuj0SIOyIdwR3H05HrG0u5AkKaHKM9Y");
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
                String distance = status.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                textView1.setText(distance);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }
}