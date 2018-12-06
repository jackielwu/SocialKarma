package cs407.socialkarmaapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    private static final String TAG = MapFragment.class.getSimpleName();
    private MapView mapView;
    Criteria criteria;
    LocationManager locationManager;
    List<Post> posts;
//    Location location;
//    LatLng myLatLng;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, parent, false);
        Log.e("","hello");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("","permission granted");
            mMap.setMyLocationEnabled(true);
        }
        locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));

        Log.e("","location granted");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e("",latitude+" "+longitude);
        LatLng myLatLng = new LatLng(
                latitude,longitude);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,15));

        getPosts();
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
    }

    private void getPosts() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            APIClient.INSTANCE.getGeolocation(location, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("Could not get a geolocation.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    Gson gson = new GsonBuilder().create();

                    Map<String, String> map = new HashMap<String, String>();
                    map = (Map<String, String>)gson.fromJson(body, map.getClass());
                    String geolocation = map.get("geo");

                    if (geolocation != null) {
                        APIClient.INSTANCE.getPosts(geolocation, 0, null, true, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                System.out.println("Could not get posts.");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body().string();
                                Gson gson = new GsonBuilder().create();

                                Post[] postsArray = gson.fromJson(body, Post[].class);
                                final List<Post> posts= new ArrayList<Post>(Arrays.asList(postsArray));
                                MapFragment.this.posts = posts;
                            }
                        });
                    }
                }
            });
        }

    }
}
