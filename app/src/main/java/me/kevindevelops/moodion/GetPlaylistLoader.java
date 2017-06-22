package me.kevindevelops.moodion;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import me.kevindevelops.moodion.models.MoodPlaylist;

/**
 * Created by Kevin on 6/19/2017.
 */

public class GetPlaylistLoader extends AsyncTaskLoader<MoodPlaylist> {

    private static final String LOG_TAG = GetPlaylistLoader.class.getSimpleName();
    private String ACCESS_TOKEN;
    private String searchTerm;

    public GetPlaylistLoader(Context context,String accessToken,String searchTerm) {
        super(context);
        ACCESS_TOKEN = accessToken;
        this.searchTerm = searchTerm;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public MoodPlaylist loadInBackground() {
        getJsonFromUrl(buildUrl());
        return null;
    }

    private String getJsonFromUrl(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        String json;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Authorization","Bearer " + ACCESS_TOKEN );


            HttpResponse httpResponse = httpClient.execute(httpGet);
            json = EntityUtils.toString(httpResponse.getEntity());
            Log.v(LOG_TAG, json);

            return json;
        } catch (Exception e) {
            Log.v(LOG_TAG, "Couldn't get playlist", e);
        }
        return null;
    }

    private String buildUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.spotify.com")
                .appendPath("v1")
                .appendPath("search")
                .appendQueryParameter("query",searchTerm)
                .appendQueryParameter("type","playlist")
                .appendQueryParameter("market","US")
                .appendQueryParameter("offset","0")
                .appendQueryParameter("limit","10");
        return builder.build().toString();
    }
}
