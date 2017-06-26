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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.kevindevelops.moodion.adapter.EmotionsAdapter;
import me.kevindevelops.moodion.adapter.PlaylistAdapter;
import me.kevindevelops.moodion.models.EmotionResults;
import me.kevindevelops.moodion.models.MoodPlaylist;

public class ResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private static final int EMOTIONS_LOADER = 201;
    private static final int PLAYLIST_LOADER = 202;

    private LinearLayout mTopLinearLayout;
    private ImageView mStrongestEmotionIV;
    private ImageView mPlaylistIV;
    private TextView mPlaylistNameTV;
    private TextView mPlaylistOwnerTV;
    private TextView mAmountofTracksTV;
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
        mPlaylistIV = (ImageView) findViewById(R.id.iv_playlist_image);
        mEmotionsRV = (RecyclerView)findViewById(R.id.rv_emotions);
        mPlaylistRV = (RecyclerView)findViewById(R.id.rv_playlist);
        mPlaylistNameTV = (TextView) findViewById(R.id.tv_playlist_name);
        mPlaylistOwnerTV = (TextView) findViewById(R.id.tv_owner);
        mAmountofTracksTV = (TextView)findViewById(R.id.tv_number_of_tracks);

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
        mPlaylistRV.setLayoutManager(new LinearLayoutManager(this));
        mPlaylistRV.setAdapter(null);

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
            if(data.getImageUrl() != null) {
                Glide.with(ResultsActivity.this)
                        .load(data.getImageUrl())
                        .into(mPlaylistIV);
            }

            mPlaylistNameTV.setText(data.getName());
            mPlaylistOwnerTV.setText(data.getOwnerId());
            mAmountofTracksTV.setText(data.getAmountOfTracks() + " Tracks");

            mPlaylistRV.setAdapter(new PlaylistAdapter(ResultsActivity.this,data));
            ViewGroup.LayoutParams layoutParams = mPlaylistRV.getLayoutParams();
            layoutParams.height = data.getAmountOfTracks() * 204;
            mPlaylistRV.setLayoutParams(layoutParams);
        }

        @Override
        public void onLoaderReset(Loader<MoodPlaylist> loader) {
            mPlaylistRV.setAdapter(null);
        }
    };
}
