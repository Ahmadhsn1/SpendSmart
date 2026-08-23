package com.spendsmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.spendsmart.R;
import com.spendsmart.activities.EditExpenseActivity;
import com.spendsmart.helpers.CategoryHelper;
import com.spendsmart.helpers.FirestoreHelper;
import com.spendsmart.models.Expense;

import java.util.ArrayList;
import java.util.List;

// OOP: Implements RecyclerView.Adapter (Interface)
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private List<Expense> expenseListFull; // for search filter
    private final Context context;
    private final FirestoreHelper firestoreHelper;

    // OOP: Constructor
    public ExpenseAdapter(List<Expense> expenseList, Context context) {
        this.expenseList     = expenseList;
        this.expenseListFull = new ArrayList<>(expenseList);
        this.context         = context;
        this.firestoreHelper = FirestoreHelper.getInstance();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    // OOP: Polymorphism - overriding onBindViewHolder
    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvTitle.setText(expense.getTitle());
        holder.tvCategory.setText(expense.getCategory());
        holder.tvDate.setText(expense.getDate());
        holder.tvAmount.setText(CategoryHelper.formatAmount(expense.getAmount()));
        holder.tvCategoryEmoji.setText(CategoryHelper.getEmoji(expense.getCategory()));

        // OOP: Lambda expression for edit click
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditExpenseActivity.class);
            intent.putExtra("expenseId", expense.getId());
            intent.putExtra("title",     expense.getTitle());
            intent.putExtra("amount",    expense.getAmount());
            intent.putExtra("category",  expense.getCategory());
            intent.putExtra("date",      expense.getDate());
            intent.putExtra("note",      expense.getNote() != null ? expense.getNote() : "");
            context.startActivity(intent);
        });

        // OOP: Lambda expression for delete click
        holder.btnDelete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.delete_confirm_title))
                .setMessage(context.getString(R.string.delete_confirm_msg))
                .setPositiveButton(context.getString(R.string.delete), (dialog, which) -> {
                    // OOP: Background task via Firestore async
                    firestoreHelper.deleteExpense(
                        expense.getId(),
                        () -> Toast.makeText(context, "Expense deleted", Toast.LENGTH_SHORT).show(),
                        error -> Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Update list (called from fragment)
    public void updateList(List<Expense> newList) {
        this.expenseList     = newList;
        this.expenseListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    // Search filter
    public void filter(String query) {
        expenseList = new ArrayList<>();
        if (query.isEmpty()) {
            expenseList.addAll(expenseListFull);
        } else {
            String lower = query.toLowerCase().trim();
            for (Expense e : expenseListFull) {
                if (e.getTitle().toLowerCase().contains(lower)
                        || e.getCategory().toLowerCase().contains(lower)) {
                    expenseList.add(e);
                }
            }
        }
        notifyDataSetChanged();
    }

    // OOP: Inner ViewHolder class
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCategory, tvDate, tvAmount, tvCategoryEmoji;
        ImageButton btnEdit, btnDelete;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle         = itemView.findViewById(R.id.tvTitle);
            tvCategory      = itemView.findViewById(R.id.tvCategory);
            tvDate          = itemView.findViewById(R.id.tvDate);
            tvAmount        = itemView.findViewById(R.id.tvAmount);
            tvCategoryEmoji = itemView.findViewById(R.id.tvCategoryEmoji);
            btnEdit         = itemView.findViewById(R.id.btnEdit);
            btnDelete       = itemView.findViewById(R.id.btnDelete);
        }
    }
}
