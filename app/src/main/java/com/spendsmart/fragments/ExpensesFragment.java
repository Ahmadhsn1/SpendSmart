package com.spendsmart.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.spendsmart.R;
import com.spendsmart.adapters.ExpenseAdapter;
import com.spendsmart.helpers.FirestoreHelper;
import com.spendsmart.models.Expense;

import java.util.ArrayList;
import java.util.List;

// OOP: Inherits Fragment (Inheritance)
public class ExpensesFragment extends Fragment {

    private RecyclerView     recyclerView;
    private LinearLayout     layoutEmpty;
    private TextInputEditText etSearch;
    private ExpenseAdapter   adapter;
    private List<Expense>    expenseList;
    private FirestoreHelper  firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        firestoreHelper = FirestoreHelper.getInstance();
        expenseList     = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        setupSearch();
        loadExpenses();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutEmpty  = view.findViewById(R.id.layoutEmpty);
        etSearch     = view.findViewById(R.id.etSearch);
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expenseList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // OOP: Lambda-style - calls adapter filter
                adapter.filter(s.toString());
                layoutEmpty.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadExpenses() {
        // OOP: Background task - real-time Firestore listener
        firestoreHelper.listenToExpenses(expenses -> {
            if (!isAdded()) return;

            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.updateList(new ArrayList<>(expenses));

            boolean empty = expenses.isEmpty();
            layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        });
    }
}
