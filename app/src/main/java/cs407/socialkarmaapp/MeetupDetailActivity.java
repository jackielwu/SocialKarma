package cs407.socialkarmaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Adapters.MeetupDelegate;
import cs407.socialkarmaapp.Adapters.MeetupDetailAdapter;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetupDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private String meetupId;
    private String meetupTitle;

    private RecyclerView detailRecyclerView;
    private MeetupDetailAdapter meetupDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup_detail);

        Intent intent = getIntent();
        meetupId = intent.getStringExtra(MeetupActivity.EXTRA_MEETUP);
        meetupTitle = intent.getStringExtra(MeetupActivity.EXTRA_MEETUP_TITLE);

        setupToolbar();
        setupViews();
        setupDetails();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_meetup_detail);
        toolbar.setTitle(meetupTitle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViews() {
        detailRecyclerView = findViewById(R.id.recyclerView_meetup_detail);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        meetupDetailAdapter = new MeetupDetailAdapter(null, new MeetupDelegate() {
            @Override
            public void rsvpButtonClicked(String meetupId) {
                APIClient.INSTANCE.postRsvpMeetup(meetupId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(MeetupDetailActivity.this, "Failed to RSVP to meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        setupDetails();
                    }
                });
            }
        });
        detailRecyclerView.setAdapter(meetupDetailAdapter);
    }

    private void setupDetails() {
        APIClient.INSTANCE.getMeetupDetail(this.meetupId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MeetupDetailActivity.this, "Failed to show this meetup.", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Gson gson = new GsonBuilder().create();

                final Meetup meetup = gson.fromJson(body, Meetup.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meetupDetailAdapter.setMeetup(meetup);
                    }
                });
            }
        });
    }
}
