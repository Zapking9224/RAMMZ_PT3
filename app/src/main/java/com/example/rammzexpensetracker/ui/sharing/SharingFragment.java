package com.example.rammzexpensetracker.ui.sharing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.widget.Button;
import android.widget.EditText;

import com.example.rammzexpensetracker.R;
import com.example.rammzexpensetracker.databinding.FragmentSharingBinding;

public class SharingFragment extends Fragment {

    private EditText editValue;
    private EditText totNumPeople;
    private EditText tipPerc;
    private Button calcButton;
    private TextView resultTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sharing, container, false);

        // attach variable to user input
        editValue = root.findViewById(R.id.edit_value);
        totNumPeople = root.findViewById(R.id.tot_num_people);
        tipPerc = root.findViewById(R.id.tip_perc);
        calcButton = root.findViewById(R.id.calc_button);
        resultTextView = root.findViewById(R.id.result_text);

        calcButton.setOnClickListener(new View.OnClickListener() {  // when button is clicked
            @Override
            public void onClick(View v) {
                calculateExpense();
            }
        });
        return root;
    }

    private void calculateExpense() {
        String valueStr = editValue.getText().toString();
        String numPeopleStr = totNumPeople.getText().toString();
        String tipPercStr = tipPerc.getText().toString();

        if (valueStr.isEmpty() || numPeopleStr.isEmpty()) {
            resultTextView.setText("Please enter both Total and Number of People.");
            return;
        }

        double value = Double.parseDouble(valueStr);
        int numPeople = Integer.parseInt(numPeopleStr);
        double tipPerc = tipPercStr.isEmpty() ? 0 : Double.parseDouble(tipPercStr); // if empty, don't calculate

        double totalExpense = value + (value * tipPerc / 100);
        double perPersonExpense = totalExpense / numPeople;

        resultTextView.setText("Total Expense: $" + totalExpense + "\nPer Person: $" + perPersonExpense);
    }
}