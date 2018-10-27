package cs407.socialkarmaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    private TextView mTextMessage;

    List<list_item> list;
    ListView listView;

    EditText e1, e2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, parent, false);
        mTextMessage = (TextView) view.findViewById(R.id.message);

        //initializing objects
        list = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.list_item);

        //adding some values to our list
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));
        list.add(new list_item(0,0, "Lorem Ipsum", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. "));

        //creating the adapter
        MyListAdapter adapter = new MyListAdapter(getActivity(), R.layout.list_item, list);

        //attaching adapter to the listview
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
//                openPostIndividual();
            }
        });
        return view;
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
