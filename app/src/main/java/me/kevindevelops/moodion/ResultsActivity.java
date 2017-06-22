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
import me.kevindevelops.moodion.models.MoodPlaylist;

public class ResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private static final int EMOTIONS_LOADER = 201;
    private static final int PLAYLIST_LOADER = 202;

    private ImageView mPreviewIV;
    private TextView mScoresTV;

    private Bitmap mImageBitmap;
    private Uri mImageUri;
    private String ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreviewIV = (ImageView) findViewById(R.id.iv_results_preview);
        mScoresTV = (TextView) findViewById(R.id.tv_scores);

        ACCESS_TOKEN = getIntent().getStringExtra(MainActivity.EXTRA_TOKEN);
        if (getIntent().getData() != null) {
            mImageUri = getIntent().getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            } catch (IOException e) {
                Toast.makeText(this, "Could not get image", Toast.LENGTH_SHORT).show();
            }
            mPreviewIV.setImageBitmap(mImageBitmap);
        }

        getSupportLoaderManager().initLoader(EMOTIONS_LOADER, null, emotionLoaderListener);
        getSupportLoaderManager().initLoader(PLAYLIST_LOADER, null, playlistLoaderListener);
    }

    private LoaderManager.LoaderCallbacks<List<EmotionResults>> emotionLoaderListener = new LoaderManager.LoaderCallbacks<List<EmotionResults>>() {
        @Override
        public Loader<List<EmotionResults>> onCreateLoader(int id, Bundle args) {
            return new GetEmotionsLoader(ResultsActivity.this,mImageBitmap);
        }

        @Override
        public void onLoadFinished(Loader<List<EmotionResults>> loader, List<EmotionResults> data) {
            mScoresTV.append("Your results\n\n");
            for (EmotionResults scores : data) {
                mScoresTV.append("Anger : " + scores.getAnger() + "\n");
                mScoresTV.append("Contempt : " + scores.getContempt() + "\n");
                mScoresTV.append("Disgust : " + scores.getDisgust() + "\n");
                mScoresTV.append("Fear : " + scores.getFear() + "\n");
                mScoresTV.append("Happiness : " + scores.getHappiness() + "\n");
                mScoresTV.append("Neutral : " + scores.getNeutral() + "\n");
                mScoresTV.append("Sadness : " + scores.getSadness() + "\n");
                mScoresTV.append("Surprise : " + scores.getSurprise() + "\n\n");
            }
        }

        @Override
        public void onLoaderReset(Loader<List<EmotionResults>> loader) {
            mScoresTV.setText("");
        }
    };

    private LoaderManager.LoaderCallbacks<MoodPlaylist> playlistLoaderListener = new LoaderManager.LoaderCallbacks<MoodPlaylist>() {
        @Override
        public Loader<MoodPlaylist> onCreateLoader(int id, Bundle args) {
            return new GetPlaylistLoader(ResultsActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<MoodPlaylist> loader, MoodPlaylist data) {

        }

        @Override
        public void onLoaderReset(Loader<MoodPlaylist> loader) {

        }
    };
}
