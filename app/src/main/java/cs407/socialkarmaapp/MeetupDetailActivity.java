package cs407.socialkarmaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.io.IOException;

import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetupDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String meetupId;
    private String meetupTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup_detail);

        Intent intent = getIntent();
        meetupId = intent.getStringExtra(MeetupActivity.EXTRA_MEETUP);
        meetupTitle = intent.getStringExtra(MeetupActivity.EXTRA_MEETUP_TITLE);

        setupToolbar();
        setupDetails();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_meetup_detail);
        toolbar.setTitle(meetupTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                finish();
            }
        });
    }

    private void setupDetails() {
        APIClient.INSTANCE.getMeetupDetail(this.meetupId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                finish();
                Toast.makeText(MeetupDetailActivity.this, "Failed to show this meetup.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
