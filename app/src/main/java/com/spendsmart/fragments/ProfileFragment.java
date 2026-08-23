package com.spendsmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.spendsmart.R;
import com.spendsmart.activities.LoginActivity;
import com.spendsmart.helpers.CategoryHelper;
import com.spendsmart.helpers.FirestoreHelper;
import com.spendsmart.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// OOP: Inherits Fragment (Inheritance)
public class ProfileFragment extends Fragment {

    private TextView        tvName, tvEmail, tvProfileTotal, tvProfileCount, tvMemberSince;
    private MaterialButton  btnLogout;
    private FirebaseAuth    mAuth;
    private FirestoreHelper firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth           = FirebaseAuth.getInstance();
        firestoreHelper = FirestoreHelper.getInstance();

        initViews(view);
        loadProfile();
        loadStats();
        setupLogout();

        return view;
    }

    private void initViews(View view) {
        tvName          = view.findViewById(R.id.tvName);
        tvEmail         = view.findViewById(R.id.tvEmail);
        tvProfileTotal  = view.findViewById(R.id.tvProfileTotal);
        tvProfileCount  = view.findViewById(R.id.tvProfileCount);
        tvMemberSince   = view.findViewById(R.id.tvMemberSince);
        btnLogout       = view.findViewById(R.id.btnLogout);
    }

    private void loadProfile() {
        firestoreHelper.getUserProfile((name, email) -> {
            if (!isAdded()) return;
            tvName.setText(name != null ? name : "User");
            tvEmail.setText(email != null ? email : "");
        });

        // Member since
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getMetadata() != null) {
            long creationTime = mAuth.getCurrentUser().getMetadata().getCreationTimestamp();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
            tvMemberSince.setText(sdf.format(new Date(creationTime)));
        }
    }

    private void loadStats() {
        firestoreHelper.listenToExpenses(expenses -> {
            if (!isAdded()) return;

            double total = 0;
            // OOP: ArrayList iteration
            for (Expense e : expenses) {
                total += e.getAmount();
            }

            tvProfileTotal.setText(CategoryHelper.formatAmount(total));
            tvProfileCount.setText(String.valueOf(expenses.size()));
        });
    }

    private void setupLogout() {
        // OOP: Lambda expression
        btnLogout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.logout_confirm_title))
                .setMessage(getString(R.string.logout_confirm_msg))
                .setPositiveButton(getString(R.string.logout), (dialog, which) -> {
                    mAuth.signOut();
                    FirestoreHelper.reset();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
        });
    }
}
