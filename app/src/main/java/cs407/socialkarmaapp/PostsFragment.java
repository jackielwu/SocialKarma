package cs407.socialkarmaapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs407.socialkarmaapp.Adapters.PostAdapterDelegate;
import cs407.socialkarmaapp.Adapters.PostHeaderDelegate;
import cs407.socialkarmaapp.Adapters.PostsAdapter;
import cs407.socialkarmaapp.Adapters.SortBy;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Meetup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

interface SortByDelegate {
    public void sortByClicked(int which);
}

public class PostsFragment extends Fragment implements SortByDelegate {

    private static class PostSortByDialog extends DialogFragment {
        private int selected = 0;
        private SortByDelegate delegate;

        public PostSortByDialog(SortByDelegate delegate) {
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
    public static final String EXTRA_POST = "cs407.socialkarmaapp.POST";
    public static final String EXTRA_POST_TITLE = "cs407.socialkarmaapp.POST_TITLE";
    public static final String EXTRA_POST_OBJ = "cs407.socialkarmaapp.POST_OBJ";

    List<Post> list;
    RecyclerView recyclerView;
    PostsAdapter adapter;
    PostSortByDialog dialog;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, parent, false);

        //initializing objects
        list = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //creating the adapter
        adapter = new PostsAdapter(list, getActivity(), new PostAdapterDelegate() {
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
                                adapter.notifyDataSetChanged();
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
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }, new PostHeaderDelegate() {
            @Override
            public void sortByButtonClicked(@NotNull SortBy sortBy) {
                dialog.show(getFragmentManager(), "showSortByDialog");
            }
        });

        //attaching adapter to the listview
        recyclerView.setAdapter(adapter);
        dialog = new PostSortByDialog(this);
        mFusedLocationClient = getFusedLocationProviderClient(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setSortby(this.dialog.getSelected());
        getPosts();
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void sortByClicked(int which) {
        adapter.sortPosts(which);
    }

    private void getPosts() {
        checkPermissions();
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    APIClient.INSTANCE.getGeolocation(location, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("Could not get a geolocation.");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String body = response.body().string();
                            Gson gson = new GsonBuilder().create();

                            Map<String, String> map = new HashMap<String, String>();
                            map = (Map<String, String>)gson.fromJson(body, map.getClass());
                            String geolocation = map.get("geo");

                            if (geolocation != null) {
                                APIClient.INSTANCE.getPosts(geolocation, dialog.getSelected(), null, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        System.out.println("Could not get posts.");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String body = response.body().string();
                                        Gson gson = new GsonBuilder().create();

                                        Post[] postsArray = gson.fromJson(body, Post[].class);
                                        final List<Post> posts= new ArrayList<Post>(Arrays.asList(postsArray));
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.setPosts(posts);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


//    public void openPostIndividual() {
//        setContentView(R.layout.activity_individual_post);
//
//        Button back = (Button)findViewById(R.id.post_back);
//
//        back.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                openMain();
//            }
//        });
//        //list
//        list = new ArrayList<>();
//        commentList = new ArrayList<>();
//        listView = (ListView) findViewById(R.id.posted_item);
//        //listView1 = (ListView) findViewById(R.id.comment_list);
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        commentList.add(new list_item(0, "Comment", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//        list.add(new list_item(0,commentList.size(), "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
//
//
//        MyListAdapter adapter = new MyListAdapter(this, R.layout.comment_item, commentList);
//
//        listView.setAdapter(adapter);
//
//        TextView textViewName = (TextView) findViewById(R.id.name);
//        TextView textViewContext = (TextView) findViewById(R.id.context);
//        TextView textViewVote = (TextView) findViewById(R.id.upvote_num);
//        TextView textViewComment = (TextView) findViewById(R.id.commentButton);
//
//        textViewName.setText(list.get(0).getName());
//        textViewContext.setText(list.get(0).getContext());
//        textViewVote.setText(list.get(0).getVote_num_string());
//        textViewComment.setText(list.get(0).getComment_num_String());
//
//
//    }
}
