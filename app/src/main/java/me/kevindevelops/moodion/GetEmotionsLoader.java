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

    // Makes sure load in background starts
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // Fetches the emotions based on the image and organizes it
    @Override
    public List<EmotionResults> loadInBackground() {
        HttpClient httpClient = new DefaultHttpClient();
        String json = null;
        try {

            // Create http post call with headers passing the Microsoft Emotion API key and the content type
            HttpPost httpPost = new HttpPost("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");
            httpPost.setHeader("Content-Type", "application/octet-stream");
            httpPost.setHeader("Ocp-Apim-Subscription-Key", APIKEY.KEY);

            // Converts the image from bitmap to ByteArrayInputStream so it can be passed in the http post call
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            // Passes Entity including the byte array input stream of the image
            ByteArrayEntity entity = new ByteArrayEntity(IOUtils.toByteArray(inputStream));
            httpPost.setEntity(entity);

            // Executes http post
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();

            // Attempts to read and collect json
            if (httpEntity != null) {
                json = EntityUtils.toString(httpEntity);
            }
        } catch (Exception e) {
            Log.v(LOG_TAG, "Couldn't get emotions", e);
        }

        // Attempts to parse json to organize emotions of image
        try {
            return getScoresFromJson(json);
        } catch (JSONException e) {
            Log.v(LOG_TAG, "Couldn't parse json");
            return null;
        }
    }


    public List<EmotionResults> getScoresFromJson(String json) throws JSONException {

        // Converts json string to a json array object
        List<EmotionResults> list = new ArrayList<EmotionResults>();
        JSONArray jsonArray = new JSONArray(json);

        // Loops through json array object and parses it
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject scores = jsonObject.getJSONObject("scores");

            // Creates an EmotionResults object and passes results into it and adds it to a list
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
