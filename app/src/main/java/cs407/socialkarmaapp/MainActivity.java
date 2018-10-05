package cs407.socialkarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    List<list_item> list;
    List<meetup_item> meetupList;
    List<list_item> commentList;

    //the listview
    ListView listView;
    ListView listView1;


    private TextView forgotPasswordLink;
    EditText e1, e2;
    FirebaseAuth auth;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        Button login_btn = (Button)findViewById(R.id.button2);

        e1 = (EditText)findViewById(R.id.editText2);
        e2 = (EditText)findViewById(R.id.editText);
        auth = FirebaseAuth.getInstance();
        forgotPasswordLink = (TextView)findViewById(R.id.textView5);
        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = e1.getText().toString();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(), "Email is blank, enter a valid email above", Toast.LENGTH_LONG).show();
                }
                else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Please check your email for password reset", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                String email = e1.getText().toString();
                String password = e2.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please enter valid password", Toast.LENGTH_LONG).show();
                }
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            openMain();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Email/Password is invalid", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
                */
                // Code here executes on main thread after user presses button
                //openMain();
                //openProfile();
                //openMeetup();
                //openMap();
                openPostIndividual();
            }
        });
//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void signUpButton(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void openMain() {
        setContentView(R.layout.activity_main);
        //initializing objects
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_item);

        //adding some values to our list
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));

        //creating the adapter
        MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, list);

        //attaching adapter to the listview
        listView.setAdapter(adapter);

    }

    public void openProfile() {
        setContentView(R.layout.activity_profile);


        final RadioRealButton button1 = (RadioRealButton) findViewById(R.id.btn_profile_posts);
        final RadioRealButton button2 = (RadioRealButton) findViewById(R.id.btn_profile_comments);

        RadioRealButtonGroup group = (RadioRealButtonGroup) findViewById(R.id.button_group);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                Toast.makeText(MainActivity.this, "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });





        //list
        list = new ArrayList<>();
        listView = (ListView) findViewById(R.id.profile_list);

        //adding some values to our list
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));

        //creating the adapter
        MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, list);

        //attaching adapter to the listview
        listView.setAdapter(adapter);

    }

    public void openMeetup() {

        setContentView(R.layout.activity_meetup);
        //initializing objects
        meetupList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.meetup_list);

        //adding some values to our list
        meetupList.add(new meetup_item("Name","Title","context"));
        meetupList.add(new meetup_item("Name","Title","context"));
        meetupList.add(new meetup_item("Name","Title","context"));
        meetupList.add(new meetup_item("Name","Title","context"));

        //creating the adapter
        MyMeetupAdapter adapter = new MyMeetupAdapter(this, R.layout.meetup_item, meetupList);

        //attaching adapter to the listview
        listView.setAdapter(adapter);
    }

    public void openMap() {
        setContentView(R.layout.activity_map);

    }

    public void openPostIndividual() {
        setContentView(R.layout.activity_individual_post);
        //list
        list = new ArrayList<>();
        commentList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.posted_item);
        //listView1 = (ListView) findViewById(R.id.comment_list);
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));

        MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, list);

        MyListAdapter adapter1 = new MyListAdapter(this, R.layout.comment_item, commentList);

        listView.setAdapter(adapter);
        listView.setAdapter(adapter1);

    }
}
