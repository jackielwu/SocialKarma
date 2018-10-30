package cs407.socialkarmaapp;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import cs407.socialkarmaapp.Helpers.APIClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateCommentActivity extends AppCompatActivity {
    private EditText commentEditText;
    private Button submitButton;

    private Toolbar toolbar;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        Intent intent = getIntent();
        post = (Post)intent.getSerializableExtra(PostsFragment.EXTRA_POST_OBJ);

        setupViews();
        setupToolbar();
    }

    private void setupViews() {
        commentEditText = findViewById(R.id.editText_comment_description);
        submitButton = findViewById(R.id.button_submit_comment);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIClient.INSTANCE.postNewComment(post.getPostId(), commentEditText.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateCommentActivity.this, "Failed to create a new comment. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() >= 400) {
                            Toast.makeText(CreateCommentActivity.this, "Failed to create a new comment. Please try again later.", Toast.LENGTH_SHORT).show();
                        } else {
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_add_comment);
        toolbar.setTitle("Create Comment");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
