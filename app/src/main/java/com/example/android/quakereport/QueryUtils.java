package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.String;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

class Earthquake {
    private String location,url;
    private long time;
    private double magnitude;

    public Earthquake(Double x, String y, long z, String url) {
        this.magnitude=x;
        this.location=y;
        this.time=z;
        this.url = url;
    }

    public double getMag(){return (magnitude);}
    public String getLoc(){
        return location;
    }
    public long getDate(){return time;}
    public String geturl(){return url;}

}

public final class QueryUtils {

   public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private QueryUtils() {
    }

    public static URL createurl(String create){
        URL url = null;
        try{
            url = new URL(create);
        }catch(MalformedURLException excep) {
            Log.e(LOG_TAG, "",excep);
        }
        return url;
    }

    public static ArrayList<Earthquake> extractEarthquakes(String U){

        Log.i("Extracting","extractEarthquakes() called");
        String jsonres = "";
        try{
            URL url = createurl(U);
            jsonres = makeHttpRequest(url);
        }catch(IOException e){
            Log.e(LOG_TAG,"Error closing input stream",e);
        }

        if(TextUtils.isEmpty(jsonres))
            return null;

        ArrayList<Earthquake> earthquake = new ArrayList<>();

        try{
            JSONObject json = new JSONObject(jsonres);
            JSONArray features = json.getJSONArray("features");
            for(int i=0; i<features.length(); i++) {
                JSONObject j = features.getJSONObject(i);
                JSONObject prop = j.getJSONObject("properties");
                Double mag = prop.getDouble("mag");
                String loc = prop.getString("place");
                long time = prop.getLong("time");
                String u = prop.getString("url");

                Earthquake eq = new Earthquake(mag,loc,time,u);
                earthquake.add(eq);
            }
        }catch(JSONException e1){
            Log.e(LOG_TAG,"",e1);
        }
        return earthquake;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jresponse = "";
        if(url == null){
            return jresponse;
        }

        HttpURLConnection urlConnect  = null;
        InputStream inputStream = null;

        try{
            urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setReadTimeout(10000);
            urlConnect.setConnectTimeout(15000);
            urlConnect.setRequestMethod("GET");
            urlConnect.connect();

            if (urlConnect.getResponseCode() == 200) {
                inputStream = urlConnect.getInputStream();
                jresponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnect.getResponseCode());
            }
        }catch(Exception e){
            Log.e(LOG_TAG,"", e);
        }finally{
            if(urlConnect!=null)
                urlConnect.disconnect();
            if(inputStream!=null)
                inputStream.close();
        }

        return jresponse;
    }

    private static String readFromStream(InputStream input){
        StringBuilder out = new StringBuilder();
        try{
            if(input!=null){
                InputStreamReader inputreader = new InputStreamReader(input, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(inputreader);
                String line = br.readLine();
                while (line != null) {
                    out.append(line);
                    line = br.readLine();
                }
            }
        }catch(IOException e){
            Log.e(LOG_TAG,"",e);
        }

        return out.toString();
    }
}
