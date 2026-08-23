package com.spendsmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spendsmart.R;
import com.spendsmart.activities.MainActivity;
import com.spendsmart.adapters.ExpenseAdapter;
import com.spendsmart.helpers.CategoryHelper;
import com.spendsmart.helpers.FirestoreHelper;
import com.spendsmart.models.Expense;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// OOP: Inherits Fragment (Inheritance)
public class HomeFragment extends Fragment {

    private TextView       tvGreeting, tvUserName, tvTotalAmount, tvTotalCount, tvHighestExpense, tvSeeAll;
    private RecyclerView   rvRecent;
    private LinearLayout   layoutEmpty;
    private ExpenseAdapter adapter;
    private List<Expense>  recentList;
    private FirestoreHelper firestoreHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestoreHelper = FirestoreHelper.getInstance();
        recentList      = new ArrayList<>();

        initViews(view);
        setupRecyclerView();
        setGreeting();
        loadDashboardData();

        // See All click
        tvSeeAll.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToExpenses();
            }
        });

        return view;
    }

    private void initViews(View view) {
        tvGreeting      = view.findViewById(R.id.tvGreeting);
        tvUserName      = view.findViewById(R.id.tvUserName);
        tvTotalAmount   = view.findViewById(R.id.tvTotalAmount);
        tvTotalCount    = view.findViewById(R.id.tvTotalCount);
        tvHighestExpense = view.findViewById(R.id.tvHighestExpense);
        tvSeeAll        = view.findViewById(R.id.tvSeeAll);
        rvRecent        = view.findViewById(R.id.rvRecent);
        layoutEmpty     = view.findViewById(R.id.layoutEmpty);
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(recentList, requireContext());
        rvRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecent.setAdapter(adapter);
        rvRecent.setNestedScrollingEnabled(false);
    }

    private void setGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour < 12)      greeting = "Good Morning,";
        else if (hour < 17) greeting = "Good Afternoon,";
        else                greeting = "Good Evening,";
        tvGreeting.setText(greeting);
    }

    private void loadDashboardData() {
        // Load user name
        firestoreHelper.getUserProfile((name, email) -> {
            if (isAdded()) tvUserName.setText(name != null ? name : "User");
        });

        // Load recent expenses (limited to 5)
        firestoreHelper.listenToRecentExpenses(expenses -> {
            if (!isAdded()) return;

            recentList.clear();
            recentList.addAll(expenses);
            adapter.notifyDataSetChanged();

            layoutEmpty.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            rvRecent.setVisibility(expenses.isEmpty() ? View.GONE : View.VISIBLE);
        });

        // Load all for stats
        firestoreHelper.listenToExpenses(expenses -> {
            if (!isAdded()) return;

            double total   = 0;
            double highest = 0;

            // OOP: ArrayList iteration
            for (Expense e : expenses) {
                total += e.getAmount();
                if (e.getAmount() > highest) highest = e.getAmount();
            }

            tvTotalAmount.setText(CategoryHelper.formatAmount(total));
            tvTotalCount.setText(String.valueOf(expenses.size()));
            tvHighestExpense.setText(CategoryHelper.formatAmount(highest));
        });
    }
}
