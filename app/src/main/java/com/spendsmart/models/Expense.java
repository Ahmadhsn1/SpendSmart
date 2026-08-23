package com.spendsmart.models;

// OOP: Class with Encapsulation
public class Expense {

    private String id;
    private String title;
    private double amount;
    private String category;
    private String date;
    private String note;
    private long timestamp;

    // OOP: Empty Constructor (required by Firestore)
    public Expense() {}

    // OOP: Parameterized Constructor
    public Expense(String title, double amount,
                   String category, String date,
                   String note, long timestamp) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
        this.timestamp = timestamp;
    }

    // OOP: Getters (Encapsulation)
    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public double getAmount()    { return amount; }
    public String getCategory()  { return category; }
    public String getDate()      { return date; }
    public String getNote()      { return note; }
    public long getTimestamp()   { return timestamp; }

    // OOP: Setters (Encapsulation)
    public void setId(String id)             { this.id = id; }
    public void setTitle(String title)       { this.title = title; }
    public void setAmount(double amount)     { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(String date)         { this.date = date; }
    public void setNote(String note)         { this.note = note; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // OOP: toString override (Polymorphism)
    @Override
    public String toString() {
        return "Expense{title='" + title + "', amount=" + amount
                + ", category='" + category + "'}";
    }
}
