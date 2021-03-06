package com.ablethon.woongsang.gestcapturex.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ablethon.woongsang.gestcapturex.API.DownloadTask;
import com.ablethon.woongsang.gestcapturex.API.TouchInterface;
import com.ablethon.woongsang.gestcapturex.Parser.CurrentWeatherParser;
import com.ablethon.woongsang.gestcapturex.Parser.ThreeDayWeatherParser;
import com.ablethon.woongsang.gestcapturex.Parser.TodaysWeatherParser;
import com.ablethon.woongsang.gestcapturex.ProcessGesture.ProcessWeatherGesture;
import com.ablethon.woongsang.gestcapturex.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class WeatherActivity extends Activity implements TextToSpeech.OnInitListener {

    public static TextToSpeech myTTS;

    public static ArrayList<String> options = new ArrayList<String>();
    ListView listview;
    public static int selector;
    Context context = this;
    private static final String appid = "&appid=1c07e40d403816de4991116b22488b29";

    static DownloadTask task = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        options.clear();
        options.add("현재 날씨");
        options.add("오늘의 날씨");
        options.add("3일 날씨");
        selector = 0;
        myTTS = new TextToSpeech(this, this);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.bigfont_item, options);
        listview = (ListView) findViewById(R.id.WeatherListView);
        listview.setAdapter(adapter);

        listview.setOnTouchListener(scrollChecker);
    }


    AdapterView.OnTouchListener scrollChecker = new  AdapterView.OnTouchListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant

                return false;
            }

            return TI.gestureInterface(event);
        }

        ProcessWeatherGesture pg= new ProcessWeatherGesture();                               //to prcessing gesture
        TouchInterface TI = new TouchInterface((Activity) context,context,pg);       //to prcessing gesture
    };

    public static String getNextOption(int operator){
        if(operator==1){
            if(selector < options.size()-1 ) {
                selector++;
            }else {
                selector = 0;
            }
        }else{
            if(selector > 0 ) {
                selector--;
            }else {
                if(selector==-1){
                    selector=0;
                }else {
                    selector = options.size() - 1;
                }
            }
        }
        return options.get(selector);
    }

    @Override
    public void onInit(int status) {

    }

    public static void getWeather(double latitude, double longitude , String selected_option){

        String url = "http://api.openweathermap.org/data/2.5/";

        if (selected_option.equals(options.get(0)) ){ // current weather
            task = new CurrentWeatherParser();
            url += "weather?units=metric&lang=kr&lat=" + latitude + "&lon=" + longitude;
        }
        else if (selected_option.equals(options.get(1))){ // today's weather
            task = new TodaysWeatherParser();
            url += "forecast?units=metric&lang=kr&lat=" + latitude + "&lon=" + longitude + "&cnt=8";
        }
        else if (selected_option.equals(options.get(2))) { // 3 day weather
            task = new ThreeDayWeatherParser();
            url += "forecast?units=metric&lang=kr&lat=" + latitude + "&lon=" + longitude + "&cnt=24";
        }
        task.execute(url + appid);
    }

    @Override
    protected void onDestroy() {


        //Close the Text to Speech Library
        if(myTTS != null) {

            myTTS.stop();
            myTTS.shutdown();
            Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
    }
}
