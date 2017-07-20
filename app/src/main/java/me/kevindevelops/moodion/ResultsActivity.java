package me.kevindevelops.moodion;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class ResultsActivity extends AppCompatActivity implements PlaylistAdapter.PlaylistAdapterOnClickHandler {

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private static final String PLAYLIST_SAVED = "PLAYLIST";
    private static final String EMOTIONS_SAVED = "EMOTIONS";

    private static final int EMOTIONS_LOADER = 201;
    private static final int PLAYLIST_LOADER = 202;

    // Layout Views
    private LinearLayout mTopLinearLayout;
    private LinearLayout mPlaylistLinearLayout;
    private ImageView mStrongestEmotionIV;
    private ImageView mPlaylistIV;
    private TextView mPlaylistNameTV;
    private TextView mPlaylistOwnerTV;
    private TextView mAmountofTracksTV;
    private RecyclerView mEmotionsRV;
    private RecyclerView mPlaylistRV;
    private ProgressDialog mProgressDialog;

    // Adapters
    private EmotionsAdapter mEmotionsAdapter;
    private PlaylistAdapter mPlaylistAdapter;

    private static Bundle mRecyclerViewStateBundle;

    // Important information
    private Bitmap mImageBitmap;
    private Uri mImageUri;
    private String ACCESS_TOKEN;
    private Map.Entry<Double, String> mStrongestEmotion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTopLinearLayout = (LinearLayout) findViewById(R.id.top_linear_layout);
        mPlaylistLinearLayout = (LinearLayout) findViewById(R.id.playlist_info_linear_layout);
        mStrongestEmotionIV = (ImageView) findViewById(R.id.iv_strongest_emotion);
        mPlaylistIV = (ImageView) findViewById(R.id.iv_playlist_image);
        mEmotionsRV = (RecyclerView) findViewById(R.id.rv_emotions);
        mPlaylistRV = (RecyclerView) findViewById(R.id.rv_playlist);
        mPlaylistNameTV = (TextView) findViewById(R.id.tv_playlist_name);
        mPlaylistOwnerTV = (TextView) findViewById(R.id.tv_owner);
        mAmountofTracksTV = (TextView) findViewById(R.id.tv_number_of_tracks);
        mProgressDialog = new ProgressDialog(ResultsActivity.this);

        // Gets info from intent such as Acess Token, and image.
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

        mProgressDialog.setMessage("Loading your results and playlist...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        // Launches A sync task loader to fetch the emotions which will then fetch the playlist.
        getSupportLoaderManager().initLoader(EMOTIONS_LOADER, null, emotionLoaderListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Saves the State of both the emotions and playlist so they can be restored on phone rotation
        mRecyclerViewStateBundle = new Bundle();
        mRecyclerViewStateBundle.putParcelable(EMOTIONS_SAVED, mEmotionsRV.getLayoutManager().onSaveInstanceState());
        mRecyclerViewStateBundle.putParcelable(PLAYLIST_SAVED, mPlaylistRV.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restores state of emotions and playlist recycler view after phone rotation
        if (mRecyclerViewStateBundle != null) {

            Parcelable emotionsState = mRecyclerViewStateBundle.getParcelable(EMOTIONS_SAVED);
            Parcelable playlistState = mRecyclerViewStateBundle.getParcelable(PLAYLIST_SAVED);

            mEmotionsRV.getLayoutManager().onRestoreInstanceState(emotionsState);
            mPlaylistRV.getLayoutManager().onRestoreInstanceState(playlistState);
        }
    }

    // Function used for when a song is clicked to launch it on Spotify
    @Override
    public void playlistOnClick(String uri) {
        launchAppFromURI(uri);
    }

    // Loader to fetch emotions and display them
    private LoaderManager.LoaderCallbacks<List<EmotionResults>> emotionLoaderListener = new LoaderManager.LoaderCallbacks<List<EmotionResults>>() {
        @Override
        public Loader<List<EmotionResults>> onCreateLoader(int id, Bundle args) {
            return new GetEmotionsLoader(ResultsActivity.this, mImageBitmap);
        }

        @Override
        public void onLoadFinished(Loader<List<EmotionResults>> loader, List<EmotionResults> data) {
            // Checks to make sure no errors occurred
            if (data.isEmpty()) {
                // This is in case of an error
                // Dismisses progress dialog, sets layouts to display there is no info and an error has occurred.
                // Including the adapters of emotions recycler view
                mProgressDialog.dismiss();

                mPlaylistIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_circle));
                mPlaylistNameTV.setText(R.string.no_playlist);
                mPlaylistOwnerTV.setText(R.string.error);
                mAmountofTracksTV.setText(R.string.no_tracks);
                mStrongestEmotionIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_circle));

                mEmotionsAdapter = new EmotionsAdapter(ResultsActivity.this, null);

                // Changes the layout height of emotions recycler view so other views come up in the layout
                ViewGroup.LayoutParams layoutParams = mEmotionsRV.getLayoutParams();
                layoutParams.height = 0;
                mEmotionsRV.setLayoutParams(layoutParams);

                Toast.makeText(ResultsActivity.this, "Error in getting results", Toast.LENGTH_SHORT).show();
                return;
            }

            // If no error occurred the data is passed into the adapter
            mEmotionsAdapter = new EmotionsAdapter(ResultsActivity.this, data);
            mEmotionsRV.setAdapter(mEmotionsAdapter);

            // Looks for the strongest emotion result
            for (EmotionResults scores : data) {

                Map.Entry<Double, String> temp = scores.getMaxEmotion();
                if (mStrongestEmotion == null) {
                    mStrongestEmotion = temp;
                } else if (mStrongestEmotion.getKey().compareTo(temp.getKey()) < 0) {
                    mStrongestEmotion = temp;
                }
            }

            // Displays strongest emotion at the top, gets appropriate icon according to emotion
            if (Utilities.getEmotionDrawable(mStrongestEmotion.getValue()) != 0) {
                mStrongestEmotionIV.setImageResource(Utilities.getEmotionDrawable(mStrongestEmotion.getValue()));
            }

            // Starts the loader to fetch playlist
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
            return new GetPlaylistLoader(ResultsActivity.this, ACCESS_TOKEN, mStrongestEmotion.getValue());
        }

        @Override
        public void onLoadFinished(Loader<MoodPlaylist> loader, final MoodPlaylist data) {

            // Error checking
            if (data == null) {
                mPlaylistIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_circle));
                mPlaylistNameTV.setText(R.string.no_playlist);
                mPlaylistOwnerTV.setText(R.string.error);
                mAmountofTracksTV.setText(R.string.no_tracks);
                return;
            }

            if (data.getImageUrl() == null) {
                mPlaylistIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_error_circle));
            }

            // Uses the data passed to set the appropriate views the correct info
            // Such as image, playlist name, owner id, and amount of tracks.
            Glide.with(ResultsActivity.this)
                    .load(data.getImageUrl())
                    .into(mPlaylistIV);

            mPlaylistNameTV.setText(data.getName());
            mPlaylistOwnerTV.setText(data.getOwnerId());
            mAmountofTracksTV.setText(data.getAmountOfTracks() + " Tracks");

            // Passes data to playlist adapter to set up recycler view
            mPlaylistAdapter = new PlaylistAdapter(ResultsActivity.this, data, ResultsActivity.this);
            mPlaylistRV.setAdapter(mPlaylistAdapter);

            // Makes sure recycler view height is enough to fit all the songs
            // Needed to do since there are multiple recycler views in the Scroll View
            ViewGroup.LayoutParams layoutParams = mPlaylistRV.getLayoutParams();
            layoutParams.height = data.getAmountOfTracks() * 200;
            mPlaylistRV.setLayoutParams(layoutParams);

            mProgressDialog.dismiss();

            // On Click listener for the playlist information that will launch the playlist on Spotify when clicked
            mPlaylistLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAppFromURI(data.getUri());
                }
            });

        }

        @Override
        public void onLoaderReset(Loader<MoodPlaylist> loader) {
            mPlaylistRV.setAdapter(null);
        }
    };

    //Helper function to launch an app from the Uri, useful for launching Spotify
    public void launchAppFromURI(String uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Could not launch", Toast.LENGTH_SHORT).show();
        }
    }
}
