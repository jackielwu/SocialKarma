package cs407.socialkarmaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Adapters.MeetupDelegate;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetupActivity extends AppCompatActivity {
    public static final String EXTRA_MEETUP = "cs407.socialkarmaapp.MEETUP";
    public static final String EXTRA_MEETUP_TITLE = "cs407.socialkarmaapp.MEETUP_TITLE";

    private RecyclerView meetupRecyclerView;
    private Toolbar toolbar;
    private MeetupAdapter meetupAdapter;

    private Integer lastStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        meetupRecyclerView = findViewById(R.id.recyclerView_meetup);
        meetupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        meetupAdapter = new MeetupAdapter(new ArrayList<Meetup>(), this, new MeetupDelegate() {
            @Override
            public void rsvpButtonClicked(String meetupId) {
                APIClient.INSTANCE.postRsvpMeetup(meetupId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(MeetupActivity.this, "Failed to RSVP to meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        getMeetups();
                    }
                });
            }
        });
        meetupRecyclerView.setAdapter(meetupAdapter);

        toolbar = findViewById(R.id.toolbar_meetup_detail);
        setSupportActionBar(toolbar);
        getMeetups();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMeetups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meetups_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_meetup) {
            Intent intent = new Intent(this, AddMeetupActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMeetups() {
        APIClient.INSTANCE.getMeetups(lastStartTime, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Could not get meetups.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Gson gson = new GsonBuilder().create();


                Meetup[] meetupsArray = gson.fromJson(body, Meetup[].class);
                final List<Meetup> meetups = new ArrayList<Meetup>(Arrays.asList(meetupsArray));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meetupAdapter.setMeetups(meetups);
                    }
                });
            }
        });
    }
}