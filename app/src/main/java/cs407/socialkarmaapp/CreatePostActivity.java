package cs407.socialkarmaapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CreatePostActivity extends AppCompatActivity {
    private EditText postTitleEditText;
    private EditText postDescriptionEditText;
    private Button submitPostButton;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setupToolbar();

        mFusedLocationProviderClient = getFusedLocationProviderClient(this);

        postTitleEditText = findViewById(R.id.editText_post_title);
        postDescriptionEditText = findViewById(R.id.editText_post_description);
        submitPostButton = findViewById(R.id.button_submit_post);
        setupViews();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }


    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_add_post);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViews() {
        checkPermissions();
        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(CreatePostActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            APIClient.INSTANCE.postNewPost(location, postTitleEditText.getText().toString(), postDescriptionEditText.getText().toString(), new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Toast.makeText(CreatePostActivity.this, "Failed to create a new post. Please try again later.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.code() >= 400) {
                                        Toast.makeText(CreatePostActivity.this, "Failed to create a new post. Please try again later.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
