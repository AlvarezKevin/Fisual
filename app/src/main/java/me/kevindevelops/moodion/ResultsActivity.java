package me.kevindevelops.moodion;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.kevindevelops.moodion.adapter.EmotionsAdapter;
import me.kevindevelops.moodion.models.EmotionResults;
import me.kevindevelops.moodion.models.MoodPlaylist;

public class ResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private static final int EMOTIONS_LOADER = 201;
    private static final int PLAYLIST_LOADER = 202;

    private LinearLayout mTopLinearLayout;
    private ImageView mStrongestEmotionIV;
    private RecyclerView mEmotionsRV;
    private RecyclerView mPlaylistRV;

    private Bitmap mImageBitmap;
    private Uri mImageUri;
    private String ACCESS_TOKEN;
    private Map.Entry<Double,String> mStrongestEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTopLinearLayout = (LinearLayout)findViewById(R.id.top_linear_layout);
        mStrongestEmotionIV = (ImageView)findViewById(R.id.iv_strongest_emotion);
        mEmotionsRV = (RecyclerView)findViewById(R.id.rv_emotions);
        //mPlaylistRV = (RecyclerView)findViewById(R.id.rv_playlist);

        ACCESS_TOKEN = getIntent().getStringExtra(MainActivity.EXTRA_TOKEN);
        if (getIntent().getData() != null) {
            mImageUri = getIntent().getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            } catch (IOException e) {
                Toast.makeText(this, "Could not get image", Toast.LENGTH_SHORT).show();
            }
        }

        mTopLinearLayout.setBackground(new BitmapDrawable(mImageBitmap));
        mEmotionsRV.setLayoutManager(new LinearLayoutManager(this));
        mEmotionsRV.setAdapter(null);

        getSupportLoaderManager().initLoader(EMOTIONS_LOADER, null, emotionLoaderListener);
    }

    private LoaderManager.LoaderCallbacks<List<EmotionResults>> emotionLoaderListener = new LoaderManager.LoaderCallbacks<List<EmotionResults>>() {
        @Override
        public Loader<List<EmotionResults>> onCreateLoader(int id, Bundle args) {
            return new GetEmotionsLoader(ResultsActivity.this,mImageBitmap);
        }

        @Override
        public void onLoadFinished(Loader<List<EmotionResults>> loader, List<EmotionResults> data) {
            mEmotionsRV.setAdapter(new EmotionsAdapter(ResultsActivity.this, data));

            for (EmotionResults scores : data) {

                Map.Entry<Double,String> temp = scores.getMaxEmotion();
                if(mStrongestEmotion == null) {
                    mStrongestEmotion = temp;
                }else if(mStrongestEmotion.getKey().compareTo(temp.getKey()) > 0 ) {
                    mStrongestEmotion = temp;
                }
            }

            if(Utilities.getEmotionDrawable(mStrongestEmotion.getValue()) != 0) {
                mStrongestEmotionIV.setImageResource(Utilities.getEmotionDrawable(mStrongestEmotion.getValue()));
            }


            getSupportLoaderManager().initLoader(PLAYLIST_LOADER, null, playlistLoaderListener);
        }

        @Override
        public void onLoaderReset(Loader<List<EmotionResults>> loader) {
            mEmotionsRV.setAdapter(null);
        }
    };

    private LoaderManager.LoaderCallbacks<MoodPlaylist> playlistLoaderListener = new LoaderManager.LoaderCallbacks<MoodPlaylist>() {
        @Override
        public Loader<MoodPlaylist> onCreateLoader(int id, Bundle args) {
            return new GetPlaylistLoader(ResultsActivity.this,ACCESS_TOKEN,mStrongestEmotion.getValue());
        }

        @Override
        public void onLoadFinished(Loader<MoodPlaylist> loader, MoodPlaylist data) {
        }

        @Override
        public void onLoaderReset(Loader<MoodPlaylist> loader) {

        }
    };
}
