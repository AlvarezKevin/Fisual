package me.kevindevelops.moodion;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import me.kevindevelops.moodion.models.EmotionResults;

/**
 * Created by Kevin on 6/14/2017.
 */

public class GetEmotionsLoader extends AsyncTaskLoader<List<EmotionResults>> {

    private static final String LOG_TAG = GetEmotionsLoader.class.getSimpleName();

    private Bitmap imageBitmap;

    public GetEmotionsLoader(Context context, Bitmap bitmap) {
        super(context);
        this.imageBitmap = bitmap;

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<EmotionResults> loadInBackground() {
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpPost httpPost = new HttpPost("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");
            httpPost.setHeader("Content-Type", "application/octet-stream");
            httpPost.setHeader("Ocp-Apim-Subscription-Key", APIKEY.KEY);


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,0,outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            ByteArrayEntity entity = new ByteArrayEntity(IOUtils.toByteArray(inputStream));

            httpPost.setEntity(entity);

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            if (httpClient != null) {
                Log.v(LOG_TAG, EntityUtils.toString(httpEntity));
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, "Couldn't get emotions", e);
        }

        return null;
    }

}
