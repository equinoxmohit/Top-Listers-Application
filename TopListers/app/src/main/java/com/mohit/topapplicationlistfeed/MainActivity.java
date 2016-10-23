package com.mohit.topapplicationlistfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private String feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit=10;
    public   String  URL_CHACHED="INVALIDATE";
    public static final String FEED_URL="feedUrl";
    public static final String FEED_LIMIT="feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.xmlListView);
        if(savedInstanceState!=null)
        {
            feedUrl=savedInstanceState.getString(FEED_URL);
            feedLimit=savedInstanceState.getInt(FEED_LIMIT);
        }

        downloadUrl(String.format(feedUrl,feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        if(feedLimit==10)
        {
            menu.findItem(R.id.mnu10).setChecked(true);
        }else
        {
            menu.findItem(R.id.mnu25).setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnuFreeApp:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaidApps:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuTopSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRestart:
                URL_CHACHED="INVALIDATE";
                break;
               default:
             return super.onOptionsItemSelected(item);
        }

        downloadUrl(String.format(feedUrl,feedLimit));
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FEED_URL,feedUrl);
        outState.putInt(FEED_LIMIT,feedLimit);
        super.onSaveInstanceState(outState);

    }

    private void downloadUrl(String feedUrl) {
        if(!feedUrl.equals(URL_CHACHED)) {

            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            URL_CHACHED=feedUrl;
            Log.d(TAG, "downloadUrl: Download done");
        }else
        {
            Log.d(TAG, "downloadUrl: not downloaded again");
        }

    }


    public class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: The data downloaded is " + s);
            ParseXmlData parseXmlData = new ParseXmlData();
            parseXmlData.parse(s);
            //ArrayAdapter adapter=new ArrayAdapter(MainActivity.this,R.layout.list_item,parseXmlData.getApplications());
            FeedAdapter adapter = new FeedAdapter(MainActivity.this, R.layout.list_adapter, parseXmlData.getApplications());
            listView.setAdapter(adapter);

        }

        @Override
        protected String doInBackground(String... params) {
            String rssFeed = downloadXml(params[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Nothing downloaded");
            }
            return rssFeed;
        }
    }

    public String downloadXml(String urlPath) {
        StringBuilder xmlContent = new StringBuilder();
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "downloadXml: The response code is" + responseCode);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            int charsRead;

            char[] inputData = new char[500];
            while (true) {
                charsRead = reader.read(inputData);
                if (charsRead < 0) {
                    break;
                }
                if (charsRead > 0) {
                    xmlContent.append(String.copyValueOf(inputData, 0, charsRead));
                }
            }
            reader.close();
            return xmlContent.toString();

        } catch (IOException | SecurityException ex) {
            Log.e(TAG, "downloadXml:\t" + ex.getLocalizedMessage());
        }
        return null;
    }
}
