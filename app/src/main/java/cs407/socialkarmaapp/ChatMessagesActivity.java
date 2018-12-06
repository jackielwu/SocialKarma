package cs407.socialkarmaapp;

import cs407.socialkarmaapp.Adapters.ChatMessagesAdapter;
import cs407.socialkarmaapp.Helpers.APIClient;
import cs407.socialkarmaapp.Models.Chat;
import cs407.socialkarmaapp.Models.Message;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatMessagesActivity extends Activity {
    public static final String EXTRA_CHAT_MESSAGE_PARTNER = "cs407.socialkarmaapp.EXTRA_CHAT_MESSAGE_PARTNER";

    private Toolbar toolbar;
    private User partner;
    private String partnerId;
    private String partnerName;
    private String chatId;
    private Chat chat;

    EditText messageEditText;
    Button sendButton;

    RecyclerView recyclerView;
    ChatMessagesAdapter chatMessagesAdapter;

    private HashMap<DatabaseReference, ChildEventListener> listenerHashMap = new HashMap<>();
    private DatabaseReference chatRef;
    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        Intent intent = getIntent();
        partnerId = intent.getStringExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER);
        partnerName = intent.getStringExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER_NAME);
        chatId = intent.getStringExtra(MessagesFragment.EXTRA_CHAT_ID);
        chat = (Chat)intent.getSerializableExtra(MessagesFragment.EXTRA_CHAT_OBJECT);
        partner = (User)intent.getSerializableExtra(ChatMessagesActivity.EXTRA_CHAT_MESSAGE_PARTNER);

        setupToolbar();
        setupViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currUser != null) {
            if (chatId != null) {
                setupChatMessages();
            } else if (partner != null && partner.chatMembers.get(currUser.getUid()) != null) {
                chatId = partner.chatMembers.get(currUser.getUid());
                setupChatMessages();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        removeValueEventListeners(listenerHashMap);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_chat_messages);
        if (partnerName != null) {
            toolbar.setTitle(partnerName);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupViews() {
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int nHeight = 0;
        if (resourceId > 0) {
            nHeight = resources.getDimensionPixelSize(resourceId);
        }
        final int navHeight = nHeight;
        final View rootView = getWindow().getDecorView().getRootView();
        final ConstraintLayout constraintLayout = findViewById(R.id.activity_layout_chat_messages);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);

                int screenHeight = rootView.getHeight();
                int keyboardHeight = screenHeight - (rect.bottom - rect.top);
                keyboardHeight -= navHeight;
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.constraintLayout_chat_messages_edit, ConstraintSet.BOTTOM, R.id.activity_layout_chat_messages, ConstraintSet.BOTTOM, keyboardHeight);
                constraintSet.applyTo(constraintLayout);
            }
        });
        recyclerView = findViewById(R.id.recyclerView_chat_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatMessagesAdapter = new ChatMessagesAdapter(new ArrayList<Message>(), chat, this);
        recyclerView.setAdapter(chatMessagesAdapter);
        messageEditText = findViewById(R.id.editText_chat_messages);
        sendButton = findViewById(R.id.button_chat_messages_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currUser != null) {
                    if (partner.chatMembers.get(currUser.getUid()) != null) {
                        APIClient.INSTANCE.postMessage(chatId, messageEditText.getText().toString(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(ChatMessagesActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() >= 400) {
                                    Toast.makeText(ChatMessagesActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageEditText.setText("");
                                    }
                                });
                            }
                        });
                    } else {
                        APIClient.INSTANCE.postChat(partnerId, messageEditText.getText().toString(), new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Toast.makeText(ChatMessagesActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.code() >= 400) {
                                    Toast.makeText(ChatMessagesActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                                }

                                String body = response.body().string();
                                Gson gson = new GsonBuilder().create();

                                Chat newChat = gson.fromJson(body, Chat.class);
                                chat = newChat;
                                chatId = newChat.getChatId();
                                partner.chatMembers.put(currUser.getUid(), chatId);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        messageEditText.setText("");
                                        setupChatMessages();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    private void setupChatMessages() {
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId);
        messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(chatId);

        if (chat == null) {
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chat = dataSnapshot.getValue(Chat.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        ChildEventListener chatsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals("readReceipt")) {
                    boolean newRead = dataSnapshot.getValue(Boolean.class);
                    chat.setReadReceipt(newRead);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMessagesAdapter.setChat(chat);
                            recyclerView.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
                        }
                    });
                } else if (dataSnapshot.getKey().equals("lastSentUser")) {
                    String newUser = dataSnapshot.getValue(String.class);
                    if (newUser != null) {
                        chat.setLastSentUser(newUser);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                chatRef.removeEventListener(this);
            }
        };
        chatRef.addChildEventListener(chatsChildEventListener);
        listenerHashMap.put(chatRef, chatsChildEventListener);

        ChildEventListener messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null && !newMessage.getUserId().equals(currentUser.getUid())) {
                                setChatReadReceipt();
                            }
                            chatMessagesAdapter.addMessage(newMessage);
                            recyclerView.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                chatRef.removeEventListener(this);
            }

        };
        messagesRef.addChildEventListener(messagesChildEventListener);
        listenerHashMap.put(messagesRef, messagesChildEventListener);
    }

    private void setChatReadReceipt() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && !chat.getLastSentUser().equals(currentUser.getUid())) {
            chatRef.child("readReceipt").setValue(true);
        }
    }

    private static void removeValueEventListeners(HashMap<DatabaseReference, ChildEventListener> map) {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry: map.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ChildEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
    }
}
