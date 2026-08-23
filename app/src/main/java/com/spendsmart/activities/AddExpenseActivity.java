package com.spendsmart.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.spendsmart.R;
import com.spendsmart.helpers.CategoryHelper;
import com.spendsmart.helpers.FirestoreHelper;
import com.spendsmart.models.Expense;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// OOP: Inherits AppCompatActivity (Inheritance)
public class AddExpenseActivity extends AppCompatActivity {

    private TextInputLayout       tilTitle, tilAmount, tilNote;
    private TextInputEditText     etTitle, etAmount, etDate, etNote;
    private AutoCompleteTextView  categoryDropdown;
    private MaterialButton        btnSave;
    private ImageButton           btnBack;
    private ProgressBar           progressBar;
    private FirestoreHelper       firestoreHelper;
    private Calendar              selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        firestoreHelper = FirestoreHelper.getInstance();
        selectedDate    = Calendar.getInstance();

        initViews();
        setupCategoryDropdown();
        setupDatePicker();
        setupListeners();

        // Set today's date by default
        updateDateField();
    }

    private void initViews() {
        tilTitle         = findViewById(R.id.tilTitle);
        tilAmount        = findViewById(R.id.tilAmount);
        tilNote          = findViewById(R.id.tilNote);
        etTitle          = findViewById(R.id.etTitle);
        etAmount         = findViewById(R.id.etAmount);
        etDate           = findViewById(R.id.etDate);
        etNote           = findViewById(R.id.etNote);
        categoryDropdown = findViewById(R.id.categoryDropdown);
        btnSave          = findViewById(R.id.btnSave);
        btnBack          = findViewById(R.id.btnBack);
        progressBar      = findViewById(R.id.progressBar);
    }

    private void setupCategoryDropdown() {
        // OOP: ArrayList from CategoryHelper
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            CategoryHelper.CATEGORIES
        );
        categoryDropdown.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.tilDate).setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, day) -> {
                selectedDate.set(year, month, day);
                updateDateField();
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateField() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        etDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void setupListeners() {
        // OOP: Lambda expressions
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        tilTitle.setError(null);
        tilAmount.setError(null);

        String title    = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = categoryDropdown.getText().toString().trim();
        String date     = etDate.getText().toString().trim();
        String note     = etNote.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            tilTitle.setError(getString(R.string.error_empty_title));
            return;
        }
        if (amountStr.isEmpty()) {
            tilAmount.setError(getString(R.string.error_empty_amount));
            return;
        }
        if (category.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_SHORT).show();
            return;
        }

        // OOP: Exception Handling
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                tilAmount.setError("Amount must be greater than 0");
                return;
            }

            // OOP: Object creation
            Expense expense = new Expense(
                title, amount, category,
                date, note,
                System.currentTimeMillis()
            );

            setLoading(true);

            // OOP: Background task (Firestore async)
            firestoreHelper.addExpense(
                expense,
                () -> {
                    setLoading(false);
                    Toast.makeText(this, "Expense added! ✅", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                },
                error -> {
                    setLoading(false);
                    Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            );

        } catch (NumberFormatException e) {
            tilAmount.setError(getString(R.string.error_invalid_amount));
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        btnSave.setText(loading ? "Saving..." : getString(R.string.save));
    }
}
