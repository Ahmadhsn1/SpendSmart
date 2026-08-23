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
import com.google.firebase.firestore.FirebaseFirestore;
import com.spendsmart.R;

import java.util.HashMap;
import java.util.Map;

// OOP: Inherits AppCompatActivity (Inheritance)
public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout   tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton    btnSignUp;
    private TextView          tvLogin;
    private ProgressBar       progressBar;
    private FirebaseAuth      mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mAuth              = FirebaseAuth.getInstance();
        db                 = FirebaseFirestore.getInstance();
        tilName            = findViewById(R.id.tilName);
        tilEmail           = findViewById(R.id.tilEmail);
        tilPassword        = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etName             = findViewById(R.id.etName);
        etEmail            = findViewById(R.id.etEmail);
        etPassword         = findViewById(R.id.etPassword);
        etConfirmPassword  = findViewById(R.id.etConfirmPassword);
        btnSignUp          = findViewById(R.id.btnSignUp);
        tvLogin            = findViewById(R.id.tvLogin);
        progressBar        = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        // OOP: Lambda expressions
        btnSignUp.setOnClickListener(v -> registerUser());
        tvLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void registerUser() {
        clearErrors();

        String name        = etName.getText().toString().trim();
        String email       = etEmail.getText().toString().trim();
        String password    = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(name, email, password, confirmPass)) return;

        setLoading(true);

        // OOP: Background/async task
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                String uid = authResult.getUser().getUid();
                saveUserToFirestore(uid, name, email);
            })
            .addOnFailureListener(e -> {
                setLoading(false);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void saveUserToFirestore(String uid, String name, String email) {
        // OOP: HashMap usage
        Map<String, Object> user = new HashMap<>();
        user.put("name",      name);
        user.put("email",     email);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener(v -> {
                setLoading(false);
                Toast.makeText(this, "Account created! 🎉", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            })
            .addOnFailureListener(e -> {
                setLoading(false);
                Toast.makeText(this, "Profile save error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private boolean validateInputs(String name, String email,
                                   String password, String confirmPass) {
        boolean valid = true;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_empty_name));
            valid = false;
        }
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
        if (!password.equals(confirmPass)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        return valid;
    }

    private void clearErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!loading);
        btnSignUp.setText(loading ? "Creating account..." : getString(R.string.sign_up));
    }
}
