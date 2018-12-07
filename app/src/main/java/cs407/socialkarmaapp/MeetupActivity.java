package cs407.socialkarmaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs407.socialkarmaapp.Adapters.EmptyContentViewHolder;
import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Adapters.MeetupDelegate;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MeetupActivity extends Fragment {
    public static final String EXTRA_MEETUP = "cs407.socialkarmaapp.MEETUP";
    public static final String EXTRA_MEETUP_TITLE = "cs407.socialkarmaapp.MEETUP_TITLE";
    public static final String EXTRA_MEETUP_GEOLOCATION = "cs407.socialkarmaapp.MEETUP_GEOLOCATION";

    private RecyclerView meetupRecyclerView;
    private MeetupAdapter meetupAdapter;

    private Integer lastStartTime;
    private SwipeRefreshLayout refreshLayout;
    private FusedLocationProviderClient mFusedLocationClient;
    private String geolocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_meetup, parent, false);
        meetupRecyclerView = view.findViewById(R.id.recyclerView_meetup);
        meetupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        meetupAdapter = new MeetupAdapter(new ArrayList<Meetup>(), EmptyContentViewHolder.EmptyContentType.NOTEMPTY, getActivity(), new MeetupDelegate() {
            @Override
            public void rsvpButtonClicked(String meetupId) {
                APIClient.INSTANCE.postRsvpMeetup(meetupId, geolocation, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Failed to RSVP to meetup. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        getMeetups();
                    }
                });
            }
        });
        meetupRecyclerView.setAdapter(meetupAdapter);
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());

        refreshLayout = view.findViewById(R.id.swipeRefreshLayout_meetups);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeetups();
                refreshLayout.setRefreshing(false);
            }
        });

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

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void getMeetups() {
        checkPermissions();
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    APIClient.INSTANCE.getGeolocation(location, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("Could not get a geolocation.");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.EMPTY);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.code() >= 400) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        progressBar.setVisibility(View.GONE);
                                        meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.ERROR);
                                    }
                                });
                                return;
                            }
                            String body = response.body().string();
                            Gson gson = new GsonBuilder().create();

                            Map<String, String> map = new HashMap<String, String>();
                            map = (Map<String, String>) gson.fromJson(body, map.getClass());
                            String geolocation = map.get("geo");

                            MeetupActivity.this.geolocation = geolocation;
                            if (geolocation != null) {
                                APIClient.INSTANCE.getMeetups(geolocation, lastStartTime, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.ERROR);
                                                Toast.makeText(getActivity(), "Failed to retrieve meetups.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.code() >= 400) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.ERROR);
                                                }
                                            });
                                            return;
                                        }
                                        String body = response.body().string();
                                        Gson gson = new GsonBuilder().create();

                                        Meetup[] meetupsArray = gson.fromJson(body, Meetup[].class);
                                        final List<Meetup> meetups = new ArrayList<Meetup>(Arrays.asList(meetupsArray));
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (meetups.size() > 0) {
                                                    meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.NOTEMPTY);
                                                    meetupAdapter.setMeetups(meetups);
                                                } else {
                                                    meetupAdapter.setEmptyType(EmptyContentViewHolder.EmptyContentType.EMPTY);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}