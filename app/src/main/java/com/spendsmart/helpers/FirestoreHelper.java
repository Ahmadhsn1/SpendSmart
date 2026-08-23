package com.spendsmart.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.spendsmart.models.Expense;

import java.util.HashMap;
import java.util.Map;

// OOP: Abstraction - hides Firestore complexity
public class FirestoreHelper {

    private static FirestoreHelper instance;
    private final FirebaseFirestore db;
    private final String userId;

    // OOP: Singleton pattern
    private FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static FirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirestoreHelper();
        }
        return instance;
    }

    // Reset singleton on logout
    public static void reset() {
        instance = null;
    }

    // Helper to get expenses collection reference
    private com.google.firebase.firestore.CollectionReference getExpensesRef() {
        return db.collection("users")
                .document(userId)
                .collection("expenses");
    }

    // Helper to get user document reference
    private com.google.firebase.firestore.DocumentReference getUserRef() {
        return db.collection("users").document(userId);
    }

    // ==================== CRUD ====================

    // CREATE
    public void addExpense(Expense expense,
                           OnSuccessListener onSuccess,
                           OnFailureListener onFailure) {
        getExpensesRef()
            .add(expense)
            .addOnSuccessListener(ref -> onSuccess.onSuccess())
            .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    // READ - real-time listener
    public void listenToExpenses(ExpenseListListener listener) {
        getExpensesRef()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null || value == null) {
                    listener.onError(error != null ? error.getMessage() : "Unknown error");
                    return;
                }
                java.util.List<Expense> list = new java.util.ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                    Expense e = doc.toObject(Expense.class);
                    if (e != null) {
                        e.setId(doc.getId());
                        list.add(e);
                    }
                }
                listener.onExpensesLoaded(list);
            });
    }

    // READ limited (for dashboard recent)
    public void listenToRecentExpenses(ExpenseListListener listener) {
        getExpensesRef()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .addSnapshotListener((value, error) -> {
                if (error != null || value == null) return;
                java.util.List<Expense> list = new java.util.ArrayList<>();
                for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                    Expense e = doc.toObject(Expense.class);
                    if (e != null) {
                        e.setId(doc.getId());
                        list.add(e);
                    }
                }
                listener.onExpensesLoaded(list);
            });
    }

    // UPDATE
    public void updateExpense(String expenseId,
                              String title, double amount,
                              String category, String date, String note,
                              OnSuccessListener onSuccess,
                              OnFailureListener onFailure) {
        // OOP: HashMap usage
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("amount", amount);
        updates.put("category", category);
        updates.put("date", date);
        updates.put("note", note);

        getExpensesRef()
            .document(expenseId)
            .update(updates)
            .addOnSuccessListener(v -> onSuccess.onSuccess())
            .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    // DELETE
    public void deleteExpense(String expenseId,
                              OnSuccessListener onSuccess,
                              OnFailureListener onFailure) {
        getExpensesRef()
            .document(expenseId)
            .delete()
            .addOnSuccessListener(v -> onSuccess.onSuccess())
            .addOnFailureListener(e -> onFailure.onFailure(e.getMessage()));
    }

    // READ user profile
    public void getUserProfile(UserProfileListener listener) {
        getUserRef()
            .get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name  = doc.getString("name");
                    String email = doc.getString("email");
                    listener.onProfileLoaded(name, email);
                }
            });
    }

    // ==================== Interfaces (OOP: Abstraction) ====================

    public interface OnSuccessListener {
        void onSuccess();
    }

    public interface OnFailureListener {
        void onFailure(String error);
    }

    public interface ExpenseListListener {
        void onExpensesLoaded(java.util.List<Expense> expenses);
        default void onError(String error) {}
    }

    public interface UserProfileListener {
        void onProfileLoaded(String name, String email);
    }
}
