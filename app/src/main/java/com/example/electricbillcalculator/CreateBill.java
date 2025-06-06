package com.example.electricbillcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    Spinner spinnerMonth;

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

        spinnerMonth = findViewById(R.id.spinnerMonth);
        editUnit = findViewById(R.id.editUnit);
        editRebate = findViewById(R.id.editRebate);
        btnSave = findViewById(R.id.button1);
        btnBack = findViewById(R.id.button2);

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spinnerMonth.getSelectedItem().toString();
                String unitStr = editUnit.getText().toString().trim();
                String rebateStr = editRebate.getText().toString().trim();

                if (unitStr.isEmpty() || rebateStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in both Unit and Rebate fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double unit = Double.parseDouble(unitStr);
                double rebate = Double.parseDouble(rebateStr);

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