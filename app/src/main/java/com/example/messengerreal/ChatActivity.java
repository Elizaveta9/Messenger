package com.example.messengerreal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_USER_ID = "currentUser";
    private static final String EXTRA_OTHER_USER_ID = "otherUser";

    private TextView textViewName;
    private View isOnlineChat;
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private ImageView imageViewSend;
    private MessageAdapter messageAdapter;
    private String currentUserId;
    private String otherUserId;

    private ChatViewModel viewModel;
    private ChatViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);
        viewModelFactory = new ChatViewModelFactory(currentUserId, otherUserId);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChatViewModel.class);
        messageAdapter = new MessageAdapter(currentUserId);
        recyclerViewMessages.setAdapter(messageAdapter);
        observeViewModel();
        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(
                        editTextMessage.getText().toString(),
                        currentUserId,
                        otherUserId
                );
                if (!message.getText().equals("")) {
                    viewModel.sendMessage(message);
                }
            }
        });
    }

    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
        isOnlineChat = findViewById(R.id.isOnlineChat);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSend = findViewById(R.id.imageViewSend);
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messageAdapter.setMessages(messages);
            }
        });
        viewModel.getIsSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSent) {
                if (isSent) {
                    editTextMessage.setText("");
                }
            }
        });
        viewModel.getOtherUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                textViewName.setText(user.getName());
                int resId;
                if (user.isOnline()) {
                    resId = R.drawable.circle_green;
                } else {
                    resId = R.drawable.circle_red;
                }
                Drawable drawable = ContextCompat.getDrawable(ChatActivity.this, resId);
                isOnlineChat.setBackground(drawable);
            }
        });
    }

    static Intent newIntent(Context context, String currentUserId, String otherUserId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId);
        return intent;
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setOnlineStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setOnlineStatus(false);
    }
}