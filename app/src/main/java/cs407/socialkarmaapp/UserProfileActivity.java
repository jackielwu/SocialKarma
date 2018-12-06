package cs407.socialkarmaapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import co.intentservice.chatui.models.ChatMessage;
import cs407.socialkarmaapp.Adapters.CommentAdapterDelegate;
import cs407.socialkarmaapp.Adapters.CommentsAdapter;
import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.PostHeaderDelegate;
import cs407.socialkarmaapp.Adapters.PostsAdapter;
import cs407.socialkarmaapp.Adapters.SortBy;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Comment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserProfileActivity extends AppCompatActivity {
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

    public static final String EXTRA_USER_PROFILE_ID = "cs407.socialkarmaapp.USER_PROFILE_ID";

    FirebaseDatabase database;
    List<Post> list;
    List<cs407.socialkarmaapp.Models.Comment> commentList;
    FirebaseUser currentFirebaseUser;
    String uid;
    Query querypost, querycomment, queryKarma;
    PostsAdapter postsAdapter;
    CommentsAdapter commentAdapter;
    int groupPosition = 0;
    SortByDialog dialog;
    TextView usernameTextView;
    TextView karmaTextView;
    RecyclerView recyclerView;
    Toolbar toolbar;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        recyclerView = findViewById(R.id.recyclerView_user_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        karmaTextView = (TextView)findViewById(R.id.textView_user_profile_karmaPoints);
        usernameTextView = (TextView)findViewById(R.id.textView_user_profile_username);
        toolbar = findViewById(R.id.toolbar_user_profile);

        Intent intent = getIntent();
        uid = intent.getStringExtra(EXTRA_USER_PROFILE_ID);
        database = FirebaseDatabase.getInstance();

        setupToolbar();

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
                currentUser.uid = uid;
                if (currentUser.chatMembers == null) {
                    currentUser.chatMembers = new HashMap<>();
                }
                System.out.println(currentUser.username);
                usernameTextView.setText(currentUser.username);
                toolbar.setTitle(currentUser.username);
                karmaTextView.setText(("Karma: " + currentUser.karma));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to get user data: " + databaseError.getCode());
            }
        });

        list = new ArrayList<>();
        commentList = new ArrayList<>();

        commentAdapter = new CommentsAdapter(commentList, this, new CommentAdapterDelegate() {
            @Override
            public void deleteComment(@NotNull Comment comment, int atIndex) {
                final Comment c = comment;
                APIClient.INSTANCE.postDeleteComment(c.getPostCommentId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to delete this comment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO: delete comment locally?
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public void upVoteButtonClicked(cs407.socialkarmaapp.Models.Comment comment) {
                final cs407.socialkarmaapp.Models.Comment c = comment;
                APIClient.INSTANCE.postPostCommentVote(c.getPostCommentId(), 1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to upvote this comment.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setVotes(c.getVotes() + 1);
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public void downVoteButtonClicked(cs407.socialkarmaapp.Models.Comment comment) {
                final cs407.socialkarmaapp.Models.Comment c = comment;
                APIClient.INSTANCE.postPostCommentVote(c.getPostCommentId(), -1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to downvote this comment.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                c.setVotes(c.getVotes() - 1);
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                dialog.show(getSupportFragmentManager(), "commentDialog");
            }
        }, false);
        postsAdapter = new PostsAdapter(list, this, new PostAdapterDelegate() {
            @Override
            public void deletePost(@NotNull Post post, int atIndex) {
                final Post p = post;
                APIClient.INSTANCE.postDeletePost(p.getPostId(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to delete this post", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO: delete post locally?
                                postsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            @Override
            public void upVoteButtonClicked(Post post) {
                final Post p = post;
                APIClient.INSTANCE.postPostVote(p.getPostId(), 1, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to upvote this post.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p.setVotes(p.getVotes() + 1);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserProfileActivity.this, "Failed to downvote this post.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                p.setVotes(p.getVotes() - 1);
                                postsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                dialog.show(getSupportFragmentManager(), "postsDialog");
            }
        }, false, false);

        querypost = database.getReference("posts").orderByChild("author").equalTo(uid);

        querycomment = database.getReference("postComments").orderByChild("author").equalTo(uid);


        final RadioRealButton button1 = (RadioRealButton) findViewById(R.id.user_profile_btn_profile_posts);
        final RadioRealButton button2 = (RadioRealButton) findViewById(R.id.user_profile_btn_profile_comments);

        RadioRealButtonGroup group = (RadioRealButtonGroup) findViewById(R.id.user_profile_button_group);
        group.setPosition(0);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if(position == 0 ) {
                    recyclerView.setAdapter(postsAdapter);
                    querypost.addListenerForSingleValueEvent(valueEventListener);
                } else if(position == 1) {
                    recyclerView.setAdapter(commentAdapter);
                    querycomment.addListenerForSingleValueEvent(valueEventListener1);
                }
                groupPosition = position;
//                Toast.makeText(getActivity(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onResume() {
        super.onResume();

        if(groupPosition == 0 ) {
            recyclerView.setAdapter(postsAdapter);
            querypost.addListenerForSingleValueEvent(valueEventListener);
        } else if(groupPosition == 1) {
            recyclerView.setAdapter(commentAdapter);
            querycomment.addListenerForSingleValueEvent(valueEventListener1);
        }

        if (queryKarma != null) {
            queryKarma.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User newUser = dataSnapshot.getValue(User.class);
                    if (newUser.chatMembers != null) {
                        currentUser.chatMembers = newUser.chatMembers;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Failed to get user data: " + databaseError.getCode());
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_message_user) {
            Intent intent = new Intent(this, ChatMessagesActivity.class);
            intent.putExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER, uid);
            intent.putExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER_NAME, currentUser.username);
            intent.putExtra(ChatMessagesActivity.EXTRA_CHAT_MESSAGE_PARTNER, currentUser);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    list.add(post);

                }
                runOnUiThread(new Runnable() {
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
                    cs407.socialkarmaapp.Models.Comment comment = snapshot.getValue(Comment.class);
                    comment.setPostCommentId(snapshot.getKey());
                    commentList.add(comment);

                }
                runOnUiThread(new Runnable() {
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
