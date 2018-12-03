package cs407.socialkarmaapp;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class UserProfileActivity extends Activity {
    private Toolbar toolbar;
    private TextView usernameTextView;
    private TextView karmaPointsTextView;
    private RadioRealButtonGroup buttonGroup;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
    }

}
