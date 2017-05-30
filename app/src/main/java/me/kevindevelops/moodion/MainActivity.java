package me.kevindevelops.moodion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Button mButtonChoosePhoto;
    private Button mButtonTakePhoto;
    private ImageView mImageViewPreview;

    private static final int RC_CAPTURE_IMAGE = 0;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChoosePhoto = (Button) findViewById(R.id.button_choose_gallery);
        mButtonTakePhoto = (Button) findViewById(R.id.button_take_pic);
        mImageViewPreview = (ImageView)findViewById(R.id.iv_preview);

        mButtonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,RC_CAPTURE_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == RC_CAPTURE_IMAGE) {
                Bitmap imgBitmap = (Bitmap)data.getExtras().get("data");
                mImageUri = data.getData();
                mImageViewPreview.setImageBitmap(imgBitmap);
            }
        }
    }
}
