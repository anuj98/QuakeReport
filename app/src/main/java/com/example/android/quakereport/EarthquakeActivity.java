package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.AsyncTaskLoader;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>>{

    ListView l;
    TextView t;
    View pb;
    EarthquakeAdapter ea;
    private static final String USGS_RESPONSE_STRING = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    public static final String TAG = "My_QUAKE_REPORT";
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String magnitude = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));
        Uri baseUri = Uri.parse(USGS_RESPONSE_STRING);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", magnitude);
        uriBuilder.appendQueryParameter("orderby", "time");
        return new EarthquakeAsyncTask(this, uriBuilder.toString());

    }
    protected void onCreate(Bundle savedInstanceState){
        Log.i(TAG, "TEST: Earthquake Activity onCreate() called");
        try{
            Thread.sleep(2000);
        }catch(Exception a){
            Log.e(TAG,"",a);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        pb = findViewById(R.id.progress);
        l = (ListView) findViewById(R.id.list);
        t = (TextView) findViewById(R.id.text);
        l.setEmptyView(t);
//        EarthquakeAsynTask eq = new EarthquakeAsynTask(); ///for AsyncTask
//        eq.execute(USGS_RESPONSE_STRING);

        ea = new EarthquakeAdapter(this,new ArrayList<Earthquake>());
        l.setAdapter(ea);

        LoaderManager loaderManager = getLoaderManager();
        Log.i(TAG,"TEST:calling initLoader()...");

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isconnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
        if(isconnected == true)
            loaderManager.initLoader(1, null, this);
        else
        {
            pb.setVisibility(View.GONE);
            t.setText(R.string.no_connection);
        }
        l.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterview,View v, int position, long l){
                Earthquake earth = ea.getItem(position);
                Uri earthquakeUri = Uri.parse(earth.geturl());
                Intent browser = new Intent(Intent.ACTION_VIEW,earthquakeUri);
                startActivity(browser);
            }
        });
    }


    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes){
        Log.i(TAG,"TEST: Earthquake Activity onLoadFinished() called");
        ea.clear();
        if(earthquakes != null && !earthquakes.isEmpty())
            ea.addAll(earthquakes);
        else
            t.setText(R.string.no_earthquake);
        pb.setVisibility(View.GONE);
    }

    public void onLoaderReset(Loader<List<Earthquake>> loader){
        Log.i(TAG,"TEST: Earthquake Activity onLoaderReset() called");
        ea.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class EarthquakeAsyncTask extends AsyncTaskLoader<List<Earthquake>>{

        /*******Uisng AsyncTask****/
//        @Override
//        protected ArrayList<Earthquake> doInBackground(String... strings) {
//            if(strings.length<1 || strings[0] == null)
//                return null;
//            ArrayList<Earthquake> eqs = QueryUtils.extractEarthquakes(strings[0]);
//            return eqs;
//        }

//        @Override
//        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {
//            ea.clear();
//            ea = new EarthquakeAdapter(this,new ArrayList<Earthquake>());
//            l.setAdapter(ea);
//            if(earthquakes !=null || !earthquakes.isEmpty())
//                ea.addAll(earthquakes);
//        }

                /*****Using AsyncTaskLoader OR LOADER******/
        public String mUrl;
        public EarthquakeAsyncTask(Context context, String url){
            super(context);
            mUrl = url;
        }

        public void onStartLoading(){
            Log.i(TAG,"TEST: EarthquakeAsyncTask Activity onStartLoading() called");
            forceLoad();
        }
        @Override
        public List<Earthquake> loadInBackground() {
            Log.i(TAG,"TEST: EarthquakeAsyncTask Activity loadInBackground() called");
            if(mUrl==null)
                return null;
            List<Earthquake> eqs = QueryUtils.extractEarthquakes(mUrl);
            return eqs;
        }
    }


}
