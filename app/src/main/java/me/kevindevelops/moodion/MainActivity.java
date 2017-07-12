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

    public static final String EXTRA_TOKEN = "TOKEN_EXTRA";

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

        //Checks to make sure user is logged into Spotify
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(APIKEY.CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        final AuthenticationRequest request = builder.build();


        mButtonChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launches Intent to choose an image
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

        //If user has not logged in yet it will prompt them to
        if (ACCESS_TOKEN == null) {
            AuthenticationClient.openLoginActivity(this, RC_LOG_IN, request);
        }

        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Makes sure an image is chosen and user is indeed logged into Spotify
                if (imgBitmap != null && ACCESS_TOKEN != null) {
                    // If conditions are met user, ResultActivity is launched
                    // Intent passes image uri, and users Acess Token
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    intent.setData(mImageUri);
                    intent.putExtra(EXTRA_TOKEN, ACCESS_TOKEN);
                    startActivity(intent);
                } else if (ACCESS_TOKEN == null) {
                    Toast.makeText(MainActivity.this, "You have to be logged into Spotify First", Toast.LENGTH_SHORT).show();
                    if (ACCESS_TOKEN == null) {
                        AuthenticationClient.openLoginActivity(MainActivity.this, RC_LOG_IN, request);
                    }
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
            // Gets photo in Bitmap and Uri if it was taken from camera
            // Displays photo into the preview ImageView
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
                        // Get chosen image from the uri and sets image view to display it
                        imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        mImageUri = data.getData();
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
                        // Handle successful response by setting the Acess Token to a global variable
                        ACCESS_TOKEN = response.getAccessToken();
                        break;
                    // Auth flow returned an error
                    case ERROR:
                        // Handle error Toast
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

        // Creates file name as 'Moodion_' + current time as a jpg
        String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorage.getPath() + File.separator + "Moodion_" + currentTime + ".jpg");
    }
}
