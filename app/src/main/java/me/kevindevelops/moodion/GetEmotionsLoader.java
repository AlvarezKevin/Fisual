package me.kevindevelops.moodion;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
        String json = null;
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
                json = EntityUtils.toString(httpEntity);
                Log.v(LOG_TAG, json);
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, "Couldn't get emotions", e);
        }

        try{
            return getScoresFromJson(json);
        }catch (JSONException e) {
            Log.v(LOG_TAG,"Couldn't parse json");
            return null;
        }
    }

    public List<EmotionResults> getScoresFromJson(String json) throws JSONException{
        List<EmotionResults> list = new ArrayList<EmotionResults>();
        JSONArray jsonArray = new JSONArray(json);
        for(int i = 0;i < jsonArray.length();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject scores = jsonObject.getJSONObject("scores");

            EmotionResults tempEmotions = new EmotionResults();

            tempEmotions.setAnger(scores.getDouble("anger"));
            tempEmotions.setContempt(scores.getDouble("contempt"));
            tempEmotions.setDisgust(scores.getDouble("disgust"));
            tempEmotions.setFear(scores.getDouble("fear"));
            tempEmotions.setHappiness(scores.getDouble("happiness"));
            tempEmotions.setNeutral(scores.getDouble("neutral"));
            tempEmotions.setSadness(scores.getDouble("sadness"));
            tempEmotions.setSurprise(scores.getDouble("surprise"));

            list.add(tempEmotions);
        }
        return list;
    }

}
