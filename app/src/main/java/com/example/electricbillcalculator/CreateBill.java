package com.example.electricbillcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateBill extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button btnSave, btnBack;
    EditText editMonth, editUnit, editRebate;
    AutoCompleteTextView autoCompleteMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateBill.this, AboutApp.class);
            startActivity(intent);
        });

        dbHelper = new DataHelper(this);

        autoCompleteMonth = findViewById(R.id.autoCompleteMonth);
        editUnit = findViewById(R.id.editUnit);
        RadioGroup radioGroupRebate = findViewById(R.id.radioGroupRebate);
        btnSave = findViewById(R.id.button1);
        btnBack = findViewById(R.id.button2);

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        autoCompleteMonth.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = autoCompleteMonth.getText().toString().trim();
                String unitStr = editUnit.getText().toString().trim();

                if (month.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select the month.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (unitStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter the Unit used.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedId = radioGroupRebate.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a rebate percentage.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                String rebateText = selectedRadioButton.getText().toString().replace("%", "").trim();

                double unit = Double.parseDouble(unitStr);
                double rebate = Double.parseDouble(rebateText);

                double totalCharges = BillCalculator.calculateTotalCharges(unit);
                double finalCost = BillCalculator.applyRebate(totalCharges, rebate);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("INSERT INTO bill (month, unit, rebate, totalCharges, finalCost) VALUES ('" +
                        month + "', '" +
                        unit + "', '" +
                        rebate + "', '" +
                        totalCharges + "', '" +
                        finalCost + "')");

                Toast.makeText(getApplicationContext(), "Data Successfully Added", Toast.LENGTH_SHORT).show();
                MainActivity.ma.RefreshList();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}