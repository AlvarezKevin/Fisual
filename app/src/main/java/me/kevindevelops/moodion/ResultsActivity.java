package me.kevindevelops.moodion;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import me.kevindevelops.moodion.models.EmotionResults;

public class ResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EmotionResults>>{

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private static final int EMOTIONS_LOADER = 201;

    private ImageView mPreviewIV;
    private TextView mScoresTV;

    private Bitmap mImageBitmap;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreviewIV = (ImageView)findViewById(R.id.iv_results_preview);
        mScoresTV = (TextView)findViewById(R.id.tv_scores);

        if(getIntent().getData() != null) {
            mImageUri = getIntent().getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
            } catch (IOException e) {
                Toast.makeText(this,"Could not get image",Toast.LENGTH_SHORT).show();
            }
            mPreviewIV.setImageBitmap(mImageBitmap);
        }

        getSupportLoaderManager().initLoader(EMOTIONS_LOADER,null,this);
    }

    @Override
    public Loader<List<EmotionResults>> onCreateLoader(int id, Bundle args) {
        return new GetEmotionsLoader(this,mImageBitmap);
    }

    @Override
    public void onLoadFinished(Loader<List<EmotionResults>> loader, List<EmotionResults> data) {
        mScoresTV.append("Your results\n");
        for(EmotionResults scores : data) {
            mScoresTV.append("Anger : " + scores.getAnger() + "\n");
            mScoresTV.append("Contempt : " + scores.getContempt() + "\n");
            mScoresTV.append("Disgust : " + scores.getDisgust() + "\n");
            mScoresTV.append("Fear : " + scores.getFear() + "\n");
            mScoresTV.append("Happiness : " + scores.getHappiness() + "\n");
            mScoresTV.append("Neutral : " + scores.getNeutral() + "\n");
            mScoresTV.append("Sadness : " + scores.getSadness() + "\n");
            mScoresTV.append("Surprise : " + scores.getSurprise() + "\n");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EmotionResults>> loader) {

    }
}
