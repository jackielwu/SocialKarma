package cs407.socialkarmaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;

public class ProfileFragment extends Fragment {
    private TextView mTextMessage;

    List<list_item> list;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, parent, false);

        mTextMessage = (TextView) view.findViewById(R.id.message);

        final RadioRealButton button1 = (RadioRealButton) view.findViewById(R.id.btn_profile_posts);
        final RadioRealButton button2 = (RadioRealButton) view.findViewById(R.id.btn_profile_comments);

        RadioRealButtonGroup group = (RadioRealButtonGroup) view.findViewById(R.id.button_group);

        // onClickButton listener detects any click performed on buttons by touch
        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                Toast.makeText(getActivity(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });


        //list
        list = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.profile_list);

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
}
