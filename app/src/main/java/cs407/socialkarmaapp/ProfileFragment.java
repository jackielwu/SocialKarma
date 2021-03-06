package cs407.socialkarmaapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import cs407.socialkarmaapp.Adapters.CommentAdapterDelegate;
import cs407.socialkarmaapp.Adapters.CommentsAdapter;
import cs407.socialkarmaapp.Adapters.EmptyContentViewHolder;
import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.PostHeaderDelegate;
import cs407.socialkarmaapp.Adapters.PostsAdapter;
import cs407.socialkarmaapp.Adapters.SortBy;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Comment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {
    public static class SortByDialog extends DialogFragment {
        private int selected = 0;
        private SortByDelegate delegate;

        public SortByDialog(SortByDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_sort_by)
                    .setItems(R.array.sortByArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            selected = which;
                            delegate.sortByClicked(which);
                        }
                    });
            return builder.create();
        }

        public int getSelected() {
            return selected;
        }
    }

    private TextView mTextMessage;
    FirebaseDatabase database;
    List<Post> list;
    List<Comment> commentList;
    RecyclerView listView;
    FirebaseUser currentFirebaseUser;
    String uid;
    Query querypost, querycomment, queryKarma;
    PostsAdapter postsAdapter;
    CommentsAdapter commentAdapter;
    TextView karma, username;
    Button changePasswordButton;
    User currentUser;

    int groupPosition = 0;
    SortByDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, parent, false);
        listView = view.findViewById(R.id.recyclerView_profile);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        karma = (TextView)view.findViewById(R.id.textView_profile_karmaPoints);
        username = (TextView)view.findViewById(R.id.textView_profile_username);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentFirebaseUser.getUid();
        database = FirebaseDatabase.getInstance();


        dialog = new SortByDialog(new SortByDelegate() {
            @Override
            public void sortByClicked(int which) {
                if (groupPosition == 0) {
                    postsAdapter.sortPosts(which);
                } else if (groupPosition == 1) {
                    commentAdapter.sortPosts(which);
                }
            }
        });

        queryKarma = database.getReference("users/" + uid);

        queryKarma.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                System.out.println(currentUser.username);
                karma.setText(("Karma: " + currentUser.karma));
                username.setText(currentUser.username);

                if (currentUser.votes == null) {
                    currentUser.votes = new HashMap<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        changePasswordButton = view.findViewById(R.id.btn_Changepw);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Please check your email for password reset", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to send password reset link. Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        list = new ArrayList<>();
        commentList = new ArrayList<>();

        commentAdapter = new CommentsAdapter(commentList, getActivity(), new CommentAdapterDelegate() {
            @Override
            public void deleteComment(@NotNull Comment comment, final int atIndex) {
                final Comment c = comment;
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you would like to delete this comment?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                APIClient.INSTANCE.postDeleteComment(c.getPostCommentId(), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Failed to delete this comment", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.code() >= 400) {
                                            return;
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                commentAdapter.removeComment(atIndex);
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                // Create the AlertDialog object and return it
                Dialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void upVoteButtonClicked(Comment comment) {
                final Comment c = comment;
                APIClient.INSTANCE.postPostCommentVote(c.getPostCommentId(), 1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Failed to upvote this comment.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setVotes(c.getVotes() + 1);
                                c.setVoted(c.getVoted() + 1);
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public void downVoteButtonClicked(Comment comment) {
                final Comment c = comment;
                APIClient.INSTANCE.postPostCommentVote(c.getPostCommentId(), -1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Failed to downvote this comment.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setVotes(c.getVotes() - 1);
                                c.setVoted(c.getVoted() - 1);
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                dialog.show(getFragmentManager(), "commentDialog");
            }
        }, true);
        postsAdapter = new PostsAdapter(list, getActivity(),new PostAdapterDelegate() {

            @Override
            public void deletePost(@NotNull Post post, final int atIndex) {
                final Post p = post;
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you would like to delete this post?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                APIClient.INSTANCE.postDeletePost(p.getPostId(), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Failed to delete this post", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        if (response.code() >= 400) {
                                            return;
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                postsAdapter.removePost(atIndex);
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                // Create the AlertDialog object and return it
                Dialog dialog = builder.create();

                dialog.show();
            }

            @Override
            public void upVoteButtonClicked(Post post) {
                final Post p = post;
                APIClient.INSTANCE.postPostVote(p.getPostId(), 1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Failed to upvote this post.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p.setVotes(p.getVotes() + 1);
                                p.setVoted(p.getVoted() + 1);
                                postsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public void downVoteButtonClicked(Post post) {
                final Post p = post;
                APIClient.INSTANCE.postPostVote(p.getPostId(), -1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Failed to downvote this post.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p.setVotes(p.getVotes() - 1);
                                p.setVoted(p.getVoted() - 1);
                                postsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                dialog.show(getFragmentManager(), "postsDialog");
            }
        }, false, true, EmptyContentViewHolder.EmptyContentType.NOTEMPTY);

        querypost = database.getReference("posts").orderByChild("author").equalTo(uid);

        querycomment = database.getReference("postComments").orderByChild("author").equalTo(uid);


        mTextMessage = (TextView) view.findViewById(R.id.message);

        final RadioRealButton button1 = (RadioRealButton) view.findViewById(R.id.btn_profile_posts);
        final RadioRealButton button2 = (RadioRealButton) view.findViewById(R.id.btn_profile_comments);

        RadioRealButtonGroup group = (RadioRealButtonGroup) view.findViewById(R.id.profile_button_group);
        group.setPosition(0);

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
                groupPosition = position;
//                Toast.makeText(getActivity(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you would like to delete this account?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseUser tempUser = FirebaseAuth.getInstance().getCurrentUser();

                                tempUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference("users/" + uid).removeValue();
                                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG).show();
                                            BaseActivity.launchMainActivity(getActivity());
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                Dialog deleteDialog = builder.create();
                deleteDialog.show();
            }
        });
        return view;
    }

    public void onResume() {
        super.onResume();

        if(groupPosition == 0 ) {
            listView.setAdapter(postsAdapter);
            querypost.addListenerForSingleValueEvent(valueEventListener);
        }else if(groupPosition == 1) {
            listView.setAdapter(commentAdapter);
            querycomment.addListenerForSingleValueEvent(valueEventListener1);
        }
    }

    //for post
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    post.setPostId(snapshot.getKey());
//                    if (currentUser.votes != null && currentUser.votes.get("posts") != null && currentUser.votes.get("posts").get(post.getPostId()) != null) {
//                        post.setVoted(currentUser.votes.get("posts").get(post.getPostId()));
//                    }
                    list.add(post);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postsAdapter.notifyDataSetChanged();
                    }
                });
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
                    comment.setPostCommentId(snapshot.getKey());
                    if (currentUser.votes != null) {
//                        Map<String, Integer> map = currentUser.votes.get("postComments");
//
//                        if (map != null && map.get(comment.getPostCommentId()) != null) {
//                            comment.setVoted(currentUser.votes.get("postComments").get(comment.getPostCommentId()));
//                        }
                    }
                    commentList.add(comment);

                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
