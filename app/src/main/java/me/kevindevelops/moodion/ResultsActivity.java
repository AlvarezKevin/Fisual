package me.kevindevelops.moodion;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class ResultsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();

    private ImageView mPreviewIV;

    private Bitmap mImageBitmap;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreviewIV = (ImageView)findViewById(R.id.iv_results_preview);

        if(getIntent().getData() != null) {
            mImageUri = getIntent().getData();
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
            } catch (IOException e) {
                Toast.makeText(this,"Could not get image",Toast.LENGTH_SHORT).show();
            }
            mPreviewIV.setImageBitmap(mImageBitmap);
        }
    }
}
