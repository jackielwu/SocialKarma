package cs407.socialkarmaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetupActivity extends AppCompatActivity {
    private RecyclerView meetupRecyclerView;
    private MeetupAdapter meetupAdapter;

    private Integer lastStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        meetupRecyclerView = findViewById(R.id.recyclerView_meetup);
        meetupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        meetupAdapter = new MeetupAdapter(new ArrayList<Meetup>());
        meetupRecyclerView.setAdapter(meetupAdapter);

        getMeetups();
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
                        meetupAdapter.addToMeetups(meetups);
                    }
                });
            }
        });
    }
}