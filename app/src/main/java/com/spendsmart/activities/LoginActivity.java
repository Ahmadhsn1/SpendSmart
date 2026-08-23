package com.spendsmart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.spendsmart.R;

// OOP: Inherits AppCompatActivity (Inheritance)
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout   tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton    btnLogin;
    private TextView          tvSignUp;
    private ProgressBar       progressBar;
    private FirebaseAuth      mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mAuth       = FirebaseAuth.getInstance();
        tilEmail    = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvSignUp    = findViewById(R.id.tvSignUp);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        // OOP: Lambda expressions
        btnLogin.setOnClickListener(v -> loginUser());

        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void loginUser() {
        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // OOP: Exception handling through validation
        if (!validateInputs(email, password)) return;

        setLoading(true);

        // OOP: Background task - Firestore async
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                setLoading(false);
                Toast.makeText(this, "Welcome back! 👋", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            })
            .addOnFailureListener(e -> {
                setLoading(false);
                Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private boolean validateInputs(String email, String password) {
        boolean valid = true;

        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.error_empty_email));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.error_empty_password));
            valid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.error_short_password));
            valid = false;
        }

        return valid;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Logging in..." : getString(R.string.login));
    }
}
