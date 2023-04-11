package com.example.messengerreal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextPasswordRegistration;
    private EditText editTextEmailRegistration;
    private EditText editTextNameRegistration;
    private Button buttonRegistration;

    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        observeViewModel();
        setUpClickListeners();
    }

    private void setUpClickListeners() {
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailRegistration.getText().toString().trim();
                String password = editTextPasswordRegistration.getText().toString().trim();
                String name = editTextNameRegistration.getText().toString().trim();
                viewModel.singUp(email, password, name);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null){
                    Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getUserLD().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    startActivity(UsersActivity.newIntent(RegistrationActivity.this, firebaseUser.getUid()));
                    finish();
                }
            }
        });
    }

    private void initViews() {
        editTextPasswordRegistration = findViewById(R.id.editTextPasswordRegistration);
        editTextEmailRegistration = findViewById(R.id.editTextEmailRegistration);
        editTextNameRegistration = findViewById(R.id.editTextNameRegistration);
        buttonRegistration = findViewById(R.id.buttonRegistration);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }
}