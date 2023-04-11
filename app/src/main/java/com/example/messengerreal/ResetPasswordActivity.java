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

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String EXTRA_TAG_EMAIL = "email";
    private EditText editTextEmailResetPassword;
    private Button buttonResetPassword;

    private ResetPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        observeViewModel();
        String email = getIntent().getStringExtra(EXTRA_TAG_EMAIL);
        editTextEmailResetPassword.setText(email);
        setUpClickListeners();
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null){
                    Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if (success){
                    Toast.makeText(ResetPasswordActivity.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUpClickListeners() {
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailResetPassword.getText().toString().trim();
                viewModel.resetPassword(email);
            }
        });
    }

    private void initViews() {
        editTextEmailResetPassword = findViewById(R.id.editTextEmailResetPassword);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
    }

    public static Intent newIntent(Context context, String email) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra(EXTRA_TAG_EMAIL, email);
        return intent;
    }
}