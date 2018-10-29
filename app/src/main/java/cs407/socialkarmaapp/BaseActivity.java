package cs407.socialkarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FrameLayout frameContainer;

    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (selected) {
            case R.id.navigation_home:
                getMenuInflater().inflate(R.menu.posts_menu, menu);
                break;
            case R.id.navigation_message:
                getMenuInflater().inflate(R.menu.messages_menu, menu);
                break;
            case R.id.navigation_meetup:
                getMenuInflater().inflate(R.menu.meetups_menu, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.default_menu, menu);
                break;
        }
        return true;
    }

    private void setBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar_meetup_detail);
        setSupportActionBar(toolbar);
        selected = R.id.navigation_home;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new PostsFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Social Karma");
                        selected = R.id.navigation_home;
                        invalidateOptionsMenu();
                        break;

                    case R.id.navigation_profile:
                        fragment = new ProfileFragment();
                        loadFragment(fragment);
                        toolbar.setTitle(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        selected = R.id.navigation_profile;
                        invalidateOptionsMenu();
                        break;

                    case R.id.navigation_meetup:
                        fragment = new MeetupActivity();
                        loadFragment(fragment);
                        toolbar.setTitle("Meetups");
                        selected = R.id.navigation_meetup;
                        invalidateOptionsMenu();

                        break;

                    case R.id.navigation_map:
                        fragment = new MapFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Map");
                        selected = R.id.navigation_map;
                        invalidateOptionsMenu();
                        break;

                    case R.id.navigation_message:
                        fragment = new MessagesFragment();
                        loadFragment(fragment);
                        toolbar.setTitle("Messages");
                        selected = R.id.navigation_message;
                        invalidateOptionsMenu();
                        break;
                }
                return true;
            }
        });


        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_meetup) {
            Intent intent = new Intent(this, AddMeetupActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_add_post) {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
