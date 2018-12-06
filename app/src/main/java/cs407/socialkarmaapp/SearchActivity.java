package cs407.socialkarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.SearchResultsAdapter;
import cs407.socialkarmaapp.Adapters.SearchResultsType;
import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends Activity {
    public static final String EXTRA_RESULT_TYPE = "cs407.socialkarmaapp.RESULT_TYPE";

    private Button backButton;
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SearchResultsAdapter searchResultsAdapter;

    int resultType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        resultType = intent.getIntExtra(EXTRA_RESULT_TYPE, 0);

        setupViews();
    }

    private void setupViews() {
        backButton = findViewById(R.id.button_search_back);
        searchEditText = findViewById(R.id.editText_search);

        recyclerView = findViewById(R.id.recyclerView_search_results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        switch(resultType) {
            case 0:
                searchResultsAdapter = new SearchResultsAdapter(SearchResultsType.POST, new ArrayList<Object>(), this, new PostAdapterDelegate() {
                    @Override
                    public void deletePost(@NotNull Post post, int atIndex) {}

                    @Override
                    public void upVoteButtonClicked(Post post) {
                        final Post p = post;
                        APIClient.INSTANCE.postPostVote(p.getPostId(), 1, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SearchActivity.this, "Failed to upvote this post.", Toast.LENGTH_SHORT).show();
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
                                        searchResultsAdapter.notifyDataSetChanged();
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
                                        Toast.makeText(SearchActivity.this, "Failed to downvote this post.", Toast.LENGTH_SHORT).show();
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
                                        searchResultsAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                    }
                });
                recyclerView.setAdapter(searchResultsAdapter);
                break;
            case 1:
                searchResultsAdapter = new SearchResultsAdapter(SearchResultsType.USER, new ArrayList<Object>(), this, null);
                recyclerView.setAdapter(searchResultsAdapter);
                break;
            default:
                break;
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (resultType) {
                    case 0:
                        break;
                    case 1:
                        String newText = searchEditText.getText().toString();
                        if (newText != null && !newText.isEmpty()) {
                            FirebaseDatabase.getInstance().getReference("users").orderByChild("username").startAt(newText).endAt(newText + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Object> newUsers = new ArrayList<>();
                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                        User newUser = snap.getValue(User.class);
                                        newUser.uid = snap.getKey();
                                        newUsers.add(newUser);
                                    }
                                    final List<Object> newUsersList = newUsers;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchResultsAdapter.setResults(newUsersList);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(SearchActivity.this, "Failed to retrieve search results.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
