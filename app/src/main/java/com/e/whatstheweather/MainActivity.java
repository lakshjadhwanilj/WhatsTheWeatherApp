package com.e.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView weatherText;

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls){

            String result = "";

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while ( data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();

                return "Failed!";

            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String msg = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Content: ", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                weatherText = (TextView) findViewById(R.id.weatherText);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Log.i("main", jsonPart.getString("main"));

                    Log.i("description", jsonPart.getString("description"));

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (main != "" && description != "") {

                        msg += main + ": " + description + "\r\n";
                    }

                }

                if (msg != "") {

                    weatherText.setText(msg);

                } else {

                    Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();

            }

        }
    }

    public void findWeather(View view) {

        DownloadTask task = new DownloadTask();

        EditText cityName = (EditText) findViewById(R.id.cityName);

        try {

            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=539f186cd45678de46f3750367103b48");

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG).show();

        }
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
