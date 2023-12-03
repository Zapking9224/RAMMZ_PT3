package com.example.rammzexpensetracker.ui.dashboard;

import static android.content.Intent.getIntent;
import static android.content.Intent.getIntentOld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.ArgbEvaluator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rammzexpensetracker.MainActivity;
import com.example.rammzexpensetracker.ui.SharedViewModel;
import com.example.rammzexpensetracker.ui.User;
import com.example.rammzexpensetracker.ui.expenses.Category;
import com.example.rammzexpensetracker.ui.settings.SettingsActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.rammzexpensetracker.R;
import com.example.rammzexpensetracker.ui.expenses.Expense;

import com.example.rammzexpensetracker.databinding.FragmentDashboardBinding;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//import com.example.rammzexpensetracker.ui.dashboard.BarChart;

public class DashboardFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    double numerator = 0;
    double denominator = 0;
    TextView fractionText;
    boolean setRed;

    private LinearLayout verticalLayout; // Vertical layout in fragment.


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        Budget budget = new Budget(); // creates a new budget object to be put into the database.
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView greetingText = view.findViewById(R.id.greeting);
        EditText editBudget = view.findViewById(R.id.editBudget);
        Button setBudgetButton = view.findViewById(R.id.SetBudgetButton);
        ProgressBar budgetBar = view.findViewById(R.id.budgetBar);
        verticalLayout = view.findViewById(R.id.verticalBox);
        ImageButton settingButton = view.findViewById(R.id.settingsButton);
        fractionText = view.findViewById(R.id.fractionText);



        // Initialize Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // gets the user id from main activity's shared view model.
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        String userId = sharedViewModel.getUserId();
        Map<String, Double> amountOfCategory = new HashMap<>();

        DatabaseReference userRef = database.getReference().child("users").child(userId);
        DatabaseReference expenseRef = userRef.child("expenses");
        DatabaseReference budgetRef = userRef.child("budget");


        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    greetingText.setText("Hello,\n" + user.getName() + '!');
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ButtonClicked", "Button was clicked");
                String budgetText = editBudget.getText().toString();
                try {
                    double budgetValue = Double.parseDouble(budgetText);

                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formattedBudgetValue = decimalFormat.format(budgetValue);
                    budgetValue = Double.parseDouble(formattedBudgetValue);
                    budget.setBudget(budgetValue);
                    UpdateBudgetInDB(budgetRef, budget);
                    CheckBudgetRatio(budget);
                    UpdateProgressBar(budget, budgetBar);
                    fractionText.setText(numerator + "/" + denominator);

                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid budget input", Toast.LENGTH_SHORT).show();
                }
            }
        });


        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double expenseTotal = 0;
                //ArrayList<String> createdCategories = new ArrayList<>();
                Map<String, Integer> createdCategories = new HashMap<>();
                Map<String, BarChart> categoryBarCharts = new HashMap<>();

                // loops through each expense and adds up the total expense.
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        expenseTotal += Double.parseDouble(expense.getAmount());
                    }


                    // Creates a barChart to be added and grabs the expense's category.
                    String category = expense.getCategory();
                    BarChart barChart = categoryBarCharts.get(category);

                    if (barChart == null) {
                        barChart = new BarChart(requireContext());
                        verticalLayout.addView(barChart);

                        categoryBarCharts.put(category, barChart);

                        // adds a bar chart if the category wasn't made yet.
                        if (!createdCategories.containsKey(category)) {
                            createdCategories.put(category, 1);
                            System.out.println("Found new category");
                        }
                    } else {
                        int count = createdCategories.get(category);
                        createdCategories.put(category, count + 1);
                        System.out.println("Found used category");
                    }


                    double ratio = (double) createdCategories.get(category) / snapshot.getChildrenCount();
                    System.out.println("Ratio!!: " + createdCategories.get(category));
                    int percentage = (int) (ratio * 100);
                    System.out.println("percentage!!: " + percentage);
                    barChart.SetData(expense.getCategory(), percentage, category);

                }

                budget.setTotalExpenses(expenseTotal); // sets the budgets totalled up expense
                System.out.println("TEST1");

                budgetRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Budget snapshotBudget = snapshot.getValue(Budget.class);
                        if (snapshotBudget != null) {
                            budget.setBudget(snapshotBudget.getBudget());
                            System.out.println(budget.getBudget());
                            CheckBudgetRatio(budget); // Gets the ratio between the expense and the budget set
                            UpdateProgressBar(budget, budgetBar);
                            System.out.println("TESTING3");
                            editBudget.setHint("Current Budget: " + budget.getBudget());
                        } else {
                            editBudget.setHint("Current Budget: " + 0);
                            System.out.println("Budget object is null");
                        }
                        editBudget.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        SetFractionText(numerator, denominator, fractionText, setRed);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                SetFractionText(numerator, denominator, fractionText, setRed);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void CheckBudgetRatio(Budget budget) {
        // calculates the total expense / budget ratio.
        double ratio = (budget.getTotalExpenses() / budget.getBudget());
        int percentage = (int) (ratio * 100);
        System.out.println("Percentage: " + percentage);
        System.out.println("Budget: " + budget.getBudget());

        // Checks if total expenses reached or exceeds set budget
        if (budget.getTotalExpenses() >= budget.getBudget()) {
            System.out.println("Your Budget has been maxed out!");
            SetFractionText(Double.valueOf(budget.getTotalExpenses()), Double.valueOf(budget.getBudget()), fractionText, true);
        } else {
            System.out.println("Keep spending you dumb idiot. You'll max out eventually!");
            SetFractionText(Double.valueOf(budget.getTotalExpenses()), Double.valueOf(budget.getBudget()), fractionText, false);
        }
    }

    public void UpdateBudgetInDB(DatabaseReference budgetRef, Budget budget) {
        // updates database budget row.
        budgetRef.setValue(budget, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    System.out.println("Budget added successfully");
                } else {
                    System.out.println("Budget failed to be added!");
                }
            }
        });
    }

    public void UpdateProgressBar(Budget budget, ProgressBar budgetBar) {
        int color = 0;

        // calculates the total expense / budget ratio.
        double ratio = (budget.getTotalExpenses() / budget.getBudget());
        int percentage = (int) (ratio * 100);



        if (percentage < 50) {
            color = Color.GREEN;
        } else if (percentage < 90) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }

        budgetBar.setProgressTintList(ColorStateList.valueOf(color));
        System.out.println(color);

        budgetBar.setProgress(percentage);

    }
    public void SetFractionText(Double numerator, Double denominator, TextView fractionText, boolean setRed) {

        this.numerator = numerator;
        this.denominator = denominator;
        this.fractionText = fractionText;
        this.setRed = setRed;

        if (setRed) {
            fractionText.setTextColor(Color.parseColor("#FF0000")); // sets text to red when over budget
        } else {
            fractionText.setTextColor(Color.parseColor("#000000")); // sets text to black when under budget
        }

        String.format("%.2f", numerator);
        String.format("%.2f", denominator);
        fractionText.setText("$" + numerator.toString() + "/" + "$" + denominator.toString());

    }
}

