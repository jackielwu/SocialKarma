package cs407.socialkarmaapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class MeetupActivity extends Fragment {
    public static final String EXTRA_MEETUP = "cs407.socialkarmaapp.MEETUP";
    public static final String EXTRA_MEETUP_TITLE = "cs407.socialkarmaapp.MEETUP_TITLE";

    private RecyclerView meetupRecyclerView;
    private MeetupAdapter meetupAdapter;

    private Integer lastStartTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_meetup, parent, false);
        meetupRecyclerView = view.findViewById(R.id.recyclerView_meetup);
        meetupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        meetupAdapter = new MeetupAdapter(new ArrayList<Meetup>(), getActivity(), new MeetupDelegate() {
            @Override
            public void rsvpButtonClicked(String meetupId) {
                APIClient.INSTANCE.postRsvpMeetup(meetupId, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(), "Failed to RSVP to meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        getMeetups();
                    }
                });
            }
        });
        meetupRecyclerView.setAdapter(meetupAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meetupAdapter.setMeetups(meetups);
                    }
                });
            }
        });
    }
}