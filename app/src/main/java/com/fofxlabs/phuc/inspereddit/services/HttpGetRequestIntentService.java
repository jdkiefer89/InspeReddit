package com.fofxlabs.phuc.inspereddit.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.fofxlabs.phuc.inspereddit.activites.MainActivity;
import com.fofxlabs.phuc.inspereddit.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpGetRequestIntentService extends IntentService {
    private static final String ACTION_GET_JSON = "com.fofxlabs.phuc.inspereddit.services.action.GET_JSON";

    private static final String EXTRA_URL = "com.fofxlabs.phuc.inspereddit.services.extra.URL";

    public static final String REQUEST_TYPE_GET = "GET";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    public static final int TIMEOUT = 10000;

    public HttpGetRequestIntentService() {
        super("HttpGetRequestIntentService");
    }

    public static void getJsonFromUrl(Context context, String url) {
        Intent intent = new Intent(context, HttpGetRequestIntentService.class);
        intent.setAction(ACTION_GET_JSON);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_JSON.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionGetJson(url);
            }
        }
    }

    private void handleActionGetJson(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_TYPE_GET);
            connection.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String serverResponse = readStream(connection.getInputStream());

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_REFRESH_CONTENT);
                broadcastIntent.putExtra(Constants.SERVER_RESPONSE, serverResponse);
                sendBroadcast(broadcastIntent);
            }
            else {
                showFetchError();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showFetchError();
        }
    }

    private String readStream(InputStream is) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
            for (String line = r.readLine(); line != null; line =r.readLine()){
                sb.append(line);
            }
            is.close();
            return sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void showFetchError() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_SHOW_URL_FETCH_ERROR);
        sendBroadcast(broadcastIntent);
    }

}
