package com.example.messengerreal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_USER_ID = "currentUser";
    private UsersViewModel viewModel;
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initViews();
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        observeViewModel();
        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onClick(User user) {
                startActivity(ChatActivity.newIntent(UsersActivity.this, currentUserId, user.getId()));
            }
        });

    }

    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        userAdapter = new UserAdapter();
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void observeViewModel() {
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null){
                    startActivity(MainActivity.newIntent(UsersActivity.this));
                    finish();
                }
            }
        });
        viewModel.getUsersListLD().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userAdapter.setUsers(users);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout){
            viewModel.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context, String currentUserId) {
        Intent intent = new Intent(context, UsersActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
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