package cs407.socialkarmaapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs407.socialkarmaapp.Adapters.ChatsAdapter;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Chat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MessagesFragment extends Fragment {
    public static final String EXTRA_MESSAGE_PARTNER = "cs407.socialkarmaapp.MESSAGE_PARTNER";
    public static final String EXTRA_MESSAGE_PARTNER_NAME = "cs407.socialkarmaapp.MESSAGE_PARTNER_NAME";
    public static final String EXTRA_CHAT_ID = "cs407.socialkarmaapp.EXTRA_CHAT_ID";
    public static final String EXTRA_CHAT_OBJECT = "cs407.socialkarmaapp.EXTRA_CHAT_OBJECT";

    private TextView mTextMessage;

    //the listview
    RecyclerView recyclerView;
    ChatsAdapter chatsAdapter;
    SwipeRefreshLayout refreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, parent, false);

        recyclerView = view.findViewById(R.id.recyclerView_chats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatsAdapter = new ChatsAdapter(new ArrayList<Chat>(), getActivity());
        recyclerView.setAdapter(chatsAdapter);
        refreshLayout = view.findViewById(R.id.swipeRefreshLayout_messages);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getChats();
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getChats();
    }

    private void getChats() {
        APIClient.INSTANCE.getChats(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Failed to retrieve conversations.", Toast.LENGTH_SHORT).show();
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

                Chat[] chatsArray = gson.fromJson(body, Chat[].class);
                final List<Chat> chats = new ArrayList<>(Arrays.asList(chatsArray));
                int index = 0;
                for (final Chat chat: chats) {
                    final int tempIndex = index;
                    FirebaseDatabase.getInstance().getReference("users").child(chat.getPartnerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            chat.setPartner(dataSnapshot.getValue(User.class));
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatsAdapter.setChat(tempIndex);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    index++;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatsAdapter.setChats(chats);
                    }
                });
            }
        });
    }
}

