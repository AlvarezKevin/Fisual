package me.kevindevelops.moodion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Button mButtonChoosePhoto;
    private Button mButtonTakePhoto;
    private Button mButtonSubmit;
    private ImageView mImageViewPreview;

    private static final int RC_CAPTURE_IMAGE = 0;
    private static final int RC_SELECT_IMAGE = 1;
    private static final int RP_CAMERA_WRITE = 10;

    private Uri mImageUri;
    private Bitmap imgBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChoosePhoto = (Button) findViewById(R.id.button_choose_gallery);
        mButtonTakePhoto = (Button) findViewById(R.id.button_take_pic);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mImageViewPreview = (ImageView) findViewById(R.id.iv_preview);


        mButtonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose a picture"), RC_SELECT_IMAGE);
            }
        });

        mButtonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checks if user has granted app permission to access camera
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RP_CAMERA_WRITE);
                } else {
                    //Starts intent to capture images and gets uri from image
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    mImageUri = FileProvider.getUriForFile(MainActivity.this,"me.kevindevelops.moodion.fileProvider",getOutputImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
                    startActivityForResult(intent,RC_CAPTURE_IMAGE);
                }
            }
        });

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgBitmap != null) {
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    intent.setData(mImageUri);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Gets photo in Bitmap and Uri if it was taken from camera
            //Displays photo into the preview ImageView
            if (requestCode == RC_CAPTURE_IMAGE) {
                if (data != null) {
                    try {
                        imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mImageUri);
                    } catch (IOException e) {
                        Toast.makeText(this,"Could not get captured image",Toast.LENGTH_SHORT).show();
                    }
                    mImageViewPreview.setImageBitmap(imgBitmap);
                }
            }
            if (requestCode == RC_SELECT_IMAGE) {
                if (data != null) {
                    try {
                        imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImageUri = data.getData();
                        Log.v(LOG_TAG, mImageUri.toString());
                        mImageViewPreview.setImageBitmap(imgBitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Could not get photo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    //Creates a directory for images to be saved
    private static File getOutputImageFile() {
        File mediaStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Moodion");

        if(!mediaStorage.exists()) {
            if(!mediaStorage.mkdir()) {
                return null;
            }
        }

        String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorage.getPath() + File.separator + "Moodion_" + currentTime + ".jpg");
    }
}
