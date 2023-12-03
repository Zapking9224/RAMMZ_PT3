package com.example.rammzexpensetracker.ui.expenses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rammzexpensetracker.R;

import java.util.ArrayList;

/*
    A simple adapter that is used to display a list of Expense objects in a RecyclerView.
    It provides methods for managing the data and binding it to the views.
 */

// ExpenseAdapter manages the users collection of data uses RecyclerView for display
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    Context context;    // reference to the context where the adapter is running
    ArrayList<Expense> arrayList;   // stores user input data
    OnItemClickListener onItemClickListener;    // listens to clicks

    // Initialize context and arrayList
    public ExpenseAdapter(Context context, ArrayList<Expense> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    // onCreateViewHolder is required for a RecyclerView
    // Creates a new ViewHolder object for each item in the RecyclerView
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Adds data to ViewHolder object that was added to RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to ViewHolder
        holder.date.setText(arrayList.get(position).getDate());
        holder.amount.setText(arrayList.get(position).getAmount());
        holder.category.setText(arrayList.get(position).getCategory());
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(arrayList.get(position)));
    }

    // Gets total database count
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Contains references to views displayed in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, amount, category;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.list_item_date);
            amount = itemView.findViewById(R.id.list_item_amount);
            category = itemView.findViewById(R.id.list_item_category);
        }
    }

    // This function will be notified when an item in the RecyclerView is clicked
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Expense expense);  // will be called on a click
    }
}