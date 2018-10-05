package cs407.socialkarmaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    List<list_item> list;
    List<meetup_item> meetupList;
    List<list_item> commentList;
    List<message_item> messageList;
    //the listview
    ListView listView;


    private TextView forgotPasswordLink;
    EditText e1, e2;
    FirebaseAuth auth;
    private GoogleMap mMap;
    LatLng myPosition;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
                    openMain();
                    return true;
                case R.id.navigation_profile:
//                    mTextMessage.setText(R.string.title_profile);
                    openProfile();
                    return true;
                case R.id.navigation_map:
//                    mTextMessage.setText(R.string.title_map);
                    openMap();
                    return true;
                case R.id.navigation_meetup:
//                    mTextMessage.setText(R.string.title_meetup);
                    openMeetup();
                    return true;
                case R.id.navigation_message:
//                    mTextMessage.setText(R.string.title_message);
                    openChatList();
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
                
                String email = e1.getText().toString();
                String password = e2.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please enter valid password", Toast.LENGTH_LONG).show();
                    return;
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

                // Code here executes on main thread after user presses button
                //openMain();
                //openProfile();
                //openMeetup();
                //openMap();
                //openPostIndividual();
                //openChat();
                //openChatList();

            }
        });

    }

    public void signUpButton(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void openMain() {
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
                openPostIndividual();
            }
        });

    }

    public void openProfile() {
        setContentView(R.layout.activity_profile);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        Button delete_btn = (Button)findViewById(R.id.btn_DeleteAccount);
        delete_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               FirebaseUser tempUser = FirebaseAuth.getInstance().getCurrentUser();
               tempUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           setContentView(R.layout.activity_login);
                           Toast.makeText(getApplicationContext(), "Account deleted", Toast.LENGTH_LONG).show();
                       }
                   }
               });
            }
        });

    }

    public void openMeetup() {

        setContentView(R.layout.activity_meetup);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //initializing objects
//        meetupList = new ArrayList<>();
//        listView = (ListView) findViewById(R.id.meetup_list);
//
//        //adding some values to our list
//        meetupList.add(new meetup_item("Name","Title","context"));
//        meetupList.add(new meetup_item("Name","Title","context"));
//        meetupList.add(new meetup_item("Name","Title","context"));
//        meetupList.add(new meetup_item("Name","Title","context"));
//
//        //creating the adapter
//        MyMeetupAdapter adapter = new MyMeetupAdapter(this, R.layout.meetup_item, meetupList);
//
//        //attaching adapter to the listview
//        listView.setAdapter(adapter);
    }

    public void openMap() {
        setContentView(R.layout.activity_map);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    public void openPostIndividual() {
        setContentView(R.layout.activity_individual_post);

        Button back = (Button)findViewById(R.id.post_back);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMain();
            }
        });
        //list
        list = new ArrayList<>();
        commentList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.posted_item);
        //listView1 = (ListView) findViewById(R.id.comment_list);
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,commentList.size(), "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));


        MyListAdapter adapter = new MyListAdapter(this, R.layout.comment_item, commentList);

        listView.setAdapter(adapter);

        TextView textViewName = (TextView) findViewById(R.id.name);
        TextView textViewContext = (TextView) findViewById(R.id.context);
        TextView textViewVote = (TextView) findViewById(R.id.upvote_num);
        TextView textViewComment = (TextView) findViewById(R.id.commentButton);

        textViewName.setText(list.get(0).getName());
        textViewContext.setText(list.get(0).getContext());
        textViewVote.setText(list.get(0).getVote_num_string());
        textViewComment.setText(list.get(0).getComment_num_String());


    }
    public void openChatList() {
        setContentView(R.layout.activity_chat);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView)findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        messageList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.message_list);
        messageList.add(new message_item("12:30","Ryan Java", "A message with a sender name"));

        MyMessageAdapter adapter = new MyMessageAdapter(this, R.layout.chat_item, messageList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                openChat();
            }
        });
    }


    public void openChat(){
        setContentView(R.layout.activity_individual_chat);
        Button back = (Button)findViewById(R.id.chat_back);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChatList();
            }
        });
        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
        chatView.addMessage(new ChatMessage("A message with a sender name",
                System.currentTimeMillis(), ChatMessage.Type.RECEIVED, "Ryan Java"));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                //need to implement storing to database

                return true;
            }
        });

        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {

            }

            @Override
            public void userStoppedTyping() {

            }
        });
    }
}
