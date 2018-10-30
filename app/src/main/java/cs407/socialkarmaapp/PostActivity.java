package cs407.socialkarmaapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs407.socialkarmaapp.Adapters.CommentAdapterDelegate;
import cs407.socialkarmaapp.Adapters.MeetupAdapter;
import cs407.socialkarmaapp.Adapters.MeetupDelegate;
import cs407.socialkarmaapp.Adapters.MeetupDetailAdapter;
import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.PostDetailAdapter;
import cs407.socialkarmaapp.Adapters.PostHeaderDelegate;
import cs407.socialkarmaapp.Adapters.SortBy;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Comment;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PostActivity extends AppCompatActivity {
    private static class CommentSortByDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.title_sort_by)
                    .setItems(R.array.sortByArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "" + which, Toast.LENGTH_SHORT).show();
                        }
                    });
            return builder.create();
        }

    }
    private Toolbar toolbar;
    private Post post;

    private RecyclerView detailRecyclerView;
    private PostDetailAdapter postDetailAdapter;
    private CommentSortByDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(PostsFragment.EXTRA_POST_OBJ);

        setupToolbar();
        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupDetails();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_comment) {
            Intent intent = new Intent(this, CreateCommentActivity.class);
            intent.putExtra(PostsFragment.EXTRA_POST_OBJ, post);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_post_detail);
        toolbar.setTitle(post.getName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViews() {
        detailRecyclerView = findViewById(R.id.recyclerView_post_detail);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dialog = new CommentSortByDialog();
        postDetailAdapter = new PostDetailAdapter(post, new ArrayList<Comment>(), new PostAdapterDelegate() {
            @Override
            public void upVoteButtonClicked(@NotNull String postId) {

            }

            @Override
            public void downVoteButtonClicked(@NotNull String postId) {

            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(SortBy sortBy) {
                dialog.show(getSupportFragmentManager(), "commentDialog");
            }
        }, new CommentAdapterDelegate() {
            @Override
            public void upVoteButtonClicked(String postId) {

            }

            @Override
            public void downVoteButtonClicked(String postId) {

            }
        });
        detailRecyclerView.setAdapter(postDetailAdapter);
    }

    private void setupDetails() {
        APIClient.INSTANCE.getComments(this.post.getPostId(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity.this, "Failed to show this post.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() >= 400) {
                    return;
                }
                String body = response.body().string();
                Gson gson = new GsonBuilder().create();

                final Comment[] comments = gson.fromJson(body, Comment[].class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postDetailAdapter.setComments(new ArrayList<Comment>(Arrays.asList(comments)));
                    }
                });
            }
        });
    }
}
