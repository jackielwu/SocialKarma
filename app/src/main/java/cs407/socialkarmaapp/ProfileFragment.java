package cs407.socialkarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.PostHeaderDelegate;
import cs407.socialkarmaapp.Adapters.PostsAdapter;
import cs407.socialkarmaapp.Adapters.SortBy;
import cs407.socialkarmaapp.Helpers.APIClient;

public class ProfileFragment extends Fragment {
    private TextView mTextMessage;
    FirebaseDatabase database;
    List<Post> list;
    List<Comment> commentList;
    RecyclerView listView;
    FirebaseUser currentFirebaseUser;
    String uid;
    Query querypost, querycomment, queryKarma;
    PostsAdapter postsAdapter;
    CommentAdapter commentAdapter;
    TextView karma, username;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, parent, false);
        listView = view.findViewById(R.id.profile_list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        karma = (TextView)view.findViewById(R.id.karmaPoints);
        username = (TextView)view.findViewById(R.id.profile_username);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        username.setText(currentFirebaseUser.getEmail());

        queryKarma = database.getReference("users").orderByChild("username").equalTo(currentFirebaseUser.getEmail());

        queryKarma.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                System.out.println(user.username);
                karma.setText(("Karma point : " + user.Karma));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        list = new ArrayList<>();
        commentList = new ArrayList<>();

        commentAdapter = new CommentAdapter(getActivity(), commentList);
        postsAdapter = new PostsAdapter(list, getActivity(),new PostAdapterDelegate() {
            @Override
            public void upVoteButtonClicked(@NotNull String postId) {
                Log.e("","up");
            }

            @Override
            public void downVoteButtonClicked(@NotNull String postId) {
                Log.e("","down");
            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                Log.e("","sort");
            }
        });

        querypost = database.getReference("posts").orderByChild("author").equalTo(uid);

        querycomment = database.getReference("postComments").orderByChild("author").equalTo(uid);


        mTextMessage = (TextView) view.findViewById(R.id.message);

        final RadioRealButton button1 = (RadioRealButton) view.findViewById(R.id.btn_profile_posts);
        final RadioRealButton button2 = (RadioRealButton) view.findViewById(R.id.btn_profile_comments);

        RadioRealButtonGroup group = (RadioRealButtonGroup) view.findViewById(R.id.button_group);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if(position == 0 ) {
                    listView.setAdapter(postsAdapter);
                    querypost.addListenerForSingleValueEvent(valueEventListener);
                }else if(position == 1) {
                    listView.setAdapter(commentAdapter);
                    querycomment.addListenerForSingleValueEvent(valueEventListener1);
                }
                Toast.makeText(getActivity(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });


        //list
        //list = new ArrayList<>();
        //listView = (ListView) view.findViewById(R.id.profile_list);

        //creating the adapter
//        MyListAdapter adapter = new MyListAdapter(getActivity(), R.layout.list_item, list);

        //attaching adapter to the listview

        Button delete_btn = (Button)view.findViewById(R.id.btn_DeleteAccount);
        delete_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FirebaseUser tempUser = FirebaseAuth.getInstance().getCurrentUser();
                tempUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        return view;
    }

    //for post
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    list.add(post);

                }
                postsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    //for comment
    ValueEventListener valueEventListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            commentList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);

                }
                postsAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
