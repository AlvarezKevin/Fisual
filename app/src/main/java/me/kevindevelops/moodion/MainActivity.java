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

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

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
    private static final int RC_LOG_IN = 1000;

    private static final String REDIRECT_URI = "moodion://callback";
    private static String ACCESS_TOKEN = null;


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
                    mImageUri = FileProvider.getUriForFile(MainActivity.this, "me.kevindevelops.moodion.fileProvider", getOutputImageFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(intent, RC_CAPTURE_IMAGE);
                }
            }
        });

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(APIKEY.CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        if (ACCESS_TOKEN == null) {
            AuthenticationClient.openLoginActivity(this, RC_LOG_IN, request);
        }

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgBitmap != null && ACCESS_TOKEN != null) {
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    intent.setData(mImageUri);
                    startActivity(intent);
                } else if (ACCESS_TOKEN == null) {
                    Toast.makeText(MainActivity.this, "You have to be logged into Spotify First", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "Select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.v(LOG_TAG, "ACCESS TOKEN: " + ACCESS_TOKEN);
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
                        imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    } catch (IOException e) {
                        Toast.makeText(this, "Could not get captured image", Toast.LENGTH_SHORT).show();
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
            if (requestCode == RC_LOG_IN) {
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

                switch (response.getType()) {
                    // Response was successful and contains auth token
                    case TOKEN:
                        // Handle successful response
                        ACCESS_TOKEN = response.getAccessToken();
                        Log.v(LOG_TAG, "TOKEN IS: " + ACCESS_TOKEN);
                        break;
                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        Toast.makeText(this, "Error singing into Spotify", Toast.LENGTH_SHORT).show();
                        break;

                    // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                        Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Creates a directory for images to be saved
    private static File getOutputImageFile() {
        File mediaStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Moodion");

        if (!mediaStorage.exists()) {
            if (!mediaStorage.mkdir()) {
                return null;
            }
        }

        String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorage.getPath() + File.separator + "Moodion_" + currentTime + ".jpg");
    }
}
