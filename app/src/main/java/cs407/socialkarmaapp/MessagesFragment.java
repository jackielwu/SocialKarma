package cs407.socialkarmaapp;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class MessagesFragment extends Fragment {

    private TextView mTextMessage;

    List<message_item> messageList;
    //the listview
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, parent, false);
        mTextMessage = (TextView) view.findViewById(R.id.message);

        messageList = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.message_list);
        messageList.add(new message_item("12:30","Ryan Java", "A message with a sender name"));

        MyMessageAdapter adapter = new MyMessageAdapter(getActivity(), R.layout.chat_item, messageList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
//                openChat();
            }
        });
        return view;
    }


//    public void openChat(){
//        setContentView(R.layout.activity_individual_chat);
//        Button back = (Button)findViewById(R.id.chat_back);
//
//        back.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                openChatList();
//            }
//        });
//        ChatView chatView = (ChatView) findViewById(R.id.chat_view);
//        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
//        chatView.addMessage(new ChatMessage("A message with a sender name",
//                System.currentTimeMillis(), ChatMessage.Type.RECEIVED, "Ryan Java"));
//        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
//            @Override
//            public boolean sendMessage(ChatMessage chatMessage) {
//                //need to implement storing to database
//
//                return true;
//            }
//        });
//
//        chatView.setTypingListener(new ChatView.TypingListener() {
//            @Override
//            public void userStartedTyping() {
//
//            }
//
//            @Override
//            public void userStoppedTyping() {
//
//            }
//        });
//    }
}
