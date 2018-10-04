package cs407.socialkarmaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;

import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MeetupActivity extends AppCompatActivity {
    private RecyclerView meetupRecyclerView;

    private int lastStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        meetupRecyclerView = findViewById(R.id.recyclerView_meetup);
        meetupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        meetupRecyclerView.setAdapter(new MeetupAdapter());
    }

    private void getMeetups() {
        APIClient.INSTANCE.getMeetups(lastStartTime, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Could not get meetups.");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}