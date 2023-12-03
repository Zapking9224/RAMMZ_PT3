package com.example.rammzexpensetracker.ui.expenses;

/*
    This file provides the Expense class with its respective setter and getter functions
 */
public class Expense {
    String key, date, amount, category;

    // Empty constructor needed for Firebase
    public Expense() {
    }

    // Getter functions for each entry box
    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    // gets unique key generated by Firebase
    public String getKey() {
        return key;
    }

    // Setter functions for each entry box variable
    public void setTitle(String date) {
        this.date = date;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(String key) {
        this.key = key;
    }
}