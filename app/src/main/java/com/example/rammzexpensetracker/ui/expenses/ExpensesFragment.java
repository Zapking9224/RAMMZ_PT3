package com.example.rammzexpensetracker.ui.expenses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.util.Calendar;

import androidx.fragment.app.Fragment;

import com.example.rammzexpensetracker.R;
import com.example.rammzexpensetracker.ui.SharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;
//import  com.example.rammzexpensetracker.ui.expenses.Category;

public class ExpensesFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private Button datePickerButton;
    private TextView dateLayout;


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle
        savedInstanceState){
        // Create view that is attached to fragment_expenses.xml
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // gets the user id from main activity's shared view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String userId = sharedViewModel.getUserId();

        // Initialize Firebase Realtime Database
        FirebaseApp.initializeApp(requireActivity());
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference userRef = database.getReference().child("users").child(userId);
        DatabaseReference expenseRef = userRef.child("expenses");

        FloatingActionButton add = view.findViewById(R.id.addExpense);  // attach action to button



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {    // action when button is clicked
                // Attaches view to use add_expense_dialog.xml
                View view1 = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);

                // Initialize variables with each TextInput and TextEdit widgets
                TextInputLayout amountLayout, dropDownLayout;
                datePickerButton = view1.findViewById(R.id.datePickerButton);
                dateLayout = view1.findViewById(R.id.dateLayout);
                amountLayout = view1.findViewById(R.id.amountLayout);
                dropDownLayout = view1.findViewById(R.id.dropDownLayout);
                TextInputEditText amountET;
                amountET = view1.findViewById(R.id.amountET);
                Spinner dropDown = view1.findViewById(R.id.dropDown);
                System.out.println("TESTING WAY UP HERE BITCHESS");

                datePickerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("TESTING WAY UP HERE BITCH");
                        showDatePickerDialog(v);
                    }
                });

                String[] categoryStrings = new String[Category.values().length];
                for (int i =0; i < Category.values().length; i++) {
                    categoryStrings[i] = Category.values()[i].name();
                }

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        requireActivity(),
                        R.array.categories_array,
                        android.R.layout.simple_spinner_item
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropDown.setAdapter(adapter);


                // Creates an floating box
                // Used to collect expense information from the user
                AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                        .setTitle("Add")
                        .setView(view1)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                // Validates input fields and returns an error if empty
                                if (Objects.requireNonNull(dateLayout.getText()).toString().isEmpty()) {
                                    dateLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                    amountLayout.setError("This field is required!");
                                } else if (Objects.requireNonNull(dropDown.getSelectedItem().toString() == "Select Category")) {
                                    dropDownLayout.setError("This field is required!");
                                } else {  // Shows a progress dialog when expense is being saved
                                    ProgressDialog dialog = new ProgressDialog(requireActivity());
                                    dialog.setMessage("Storing in Database...");
                                    dialog.show();

                                    Expense expense = new Expense();    // create expense item

                                    // use setter functions to store user input
                                    expense.setTitle(dateLayout.getText().toString());
                                    expense.setAmount(amountET.getText().toString());
                                    expense.setCategory((String) dropDown.getSelectedItem());

                                    // Pushes data to the 'expenses' node in Firebase database
                                    expenseRef.push().setValue(expense).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();   // removes floating dialog from screen
                                            dialogInterface.dismiss();
                                            Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {   // if unsuccessful, throw error
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();   // removes floating dialog from screen
                                            Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        // Cancel action button
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();  // remove floating dialog if cancel is selected
                            }
                        })
                        .create();
                alertDialog.show();

                datePickerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                });
            }
        });



        TextView empty = view.findViewById(R.id.empty);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        ArrayList<Expense> arrayList = new ArrayList<>();

        ExpenseAdapter adapter = new ExpenseAdapter(requireActivity(), arrayList);
        recyclerView.setAdapter(adapter);   // sets adapter for RecyclerView

        // Adds eventListener to 'expenses' node in database
        // Will alert when data is modified or added
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {  // called when data is changed

                arrayList.clear();

                // Iterates over Expense object for each child
                // It then adds to an array to locally store data
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Expense expense = dataSnapshot.getValue(Expense.class);
                    Objects.requireNonNull(expense).setKey(dataSnapshot.getKey());
                    arrayList.add(expense);
                }
                adapter.notifyDataSetChanged();

                System.out.println("Size: " + arrayList.size());

                // If no entries are present, no cards will be visible, the screen will be blank
                if (arrayList.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                // Will be triggered when user clicks on RecyclerView object
                adapter.setOnItemClickListener(new ExpenseAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Expense expense) {

                        // connects to add_expense_data.xml file
                        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.add_expense_dialog, null);
                        TextInputLayout amountLayout, dropDownLayout;
                        TextInputEditText dateET, amountET;

                        // Set input data to variable
                        amountET = view.findViewById(R.id.amountET);
                        dateLayout = view.findViewById(R.id.dateLayout);
                        System.out.println("FUCK1");
                        amountLayout = view.findViewById(R.id.amountLayout);
                        System.out.println("FUCK2");
                        dropDownLayout = view.findViewById(R.id.dropDownLayout);
                        System.out.println("FUCK3");
                        Spinner dropDown = view.findViewById(R.id.dropDown);
                        System.out.println("FUCK4");
                        datePickerButton = view.findViewById(R.id.datePickerButton);
                        System.out.println("FUCK5");


                        // Uses setters to store data
                        dateLayout.setText(expense.getDate());
                        amountET.setText(expense.getAmount());

                        datePickerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("TESTING WAY UP HERE BITCH");
                                showDatePickerDialog(v);
                            }
                        });



                        ProgressDialog progressDialog = new ProgressDialog(requireActivity());

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                requireActivity(),
                                R.array.categories_array,
                                android.R.layout.simple_spinner_item
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dropDown.setAdapter(adapter);


                        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                                .setTitle("Edit")
                                .setView(view)

                                // Will be triggered when user clicks "Save" button
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        // Validates user input for empty fields, returns error if empty
                                        if (Objects.requireNonNull(dateLayout.getText()).toString().isEmpty()) {
                                            dateLayout.setError("This field is required!");
                                        } else if (Objects.requireNonNull(amountET.getText()).toString().isEmpty()) {
                                            amountLayout.setError("This field is required!");
                                        } else if (Objects.requireNonNull(dropDown.getSelectedItem().toString() == "Select Category")) {
                                            dropDownLayout.setError("This field is required!");
                                        } else {
                                            System.out.println("test2");
                                            // Shows progress while expense is being saved
                                            progressDialog.setMessage("Saving...");
                                            progressDialog.show();

                                            Expense expense1 = new Expense();   // creates Expense object

                                            // populates Expense object with data
                                            expense1.setTitle(dateLayout.getText().toString());
                                            expense1.setAmount(amountET.getText().toString());
                                            expense1.setCategory(dropDown.getSelectedItem().toString());

                                            // Overwrites data currently stored in database with new data entered by user
                                            expenseRef.child(expense.getKey()).setValue(expense1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    // Dismisses progress and dialog box
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();

                                                    // Shows toast message
                                                    Toast.makeText(requireActivity(), "Saved Successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();   // dismisses progress screen

                                                    // Shows error toast message
                                                    Toast.makeText(requireActivity(), "There was an error while saving data", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                // Allows the user to exit dialog box without any changes
                                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                // Sets delete action to button
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deleting...");
                                        progressDialog.show();

                                        // Deletes item from Firebase database
                                        expenseRef.child(expense.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing. Required function.
            }
        });
        return view;
    }

    public void showDatePickerDialog(View v) {
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateLayout.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
        System.out.println("TESTING HERE BITCH");
    }
}