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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateBill extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button updateBtn, backBtn;
    Spinner spinnerMonth;
    EditText editUnit;
    RadioGroup radioGroupRebate;
    TextView textId;

    String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private int getRadioButtonIdFromRebate(float rebate) {
        switch ((int) rebate) {
            case 0: return R.id.radio0;
            case 1: return R.id.radio1;
            case 2: return R.id.radio2;
            case 3: return R.id.radio3;
            case 4: return R.id.radio4;
            case 5: return R.id.radio5;
            default: return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateBill.this, AboutApp.class);
            startActivity(intent);
        });

        dbHelper = new DataHelper(this);
        textId = findViewById(R.id.editText1);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        editUnit = findViewById(R.id.editText3);
        radioGroupRebate  = findViewById(R.id.radioGroupRebate);
        updateBtn = findViewById(R.id.button1);
        backBtn = findViewById(R.id.button2);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapter);

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM bill WHERE no = " + id, null);
            if (cursor.moveToFirst()) {
                textId.setText("ID Number: " + id);
                String monthFromDb = cursor.getString(cursor.getColumnIndexOrThrow("month"));
                int monthPosition = 0;
                for (int i = 0; i < months.length; i++) {
                    if (months[i].equalsIgnoreCase(monthFromDb)) {
                        monthPosition = i;
                        break;
                    }
                }
                spinnerMonth.setSelection(monthPosition);
                editUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));

                float rebateValue = cursor.getFloat(cursor.getColumnIndexOrThrow("rebate"));
                int radioId = getRadioButtonIdFromRebate(rebateValue);
                if (radioId != -1) radioGroupRebate.check(radioId);
            }
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spinnerMonth.getSelectedItem().toString();
                String unitStr = editUnit.getText().toString().trim();

                if (unitStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter unit used.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedRebateId = radioGroupRebate.getCheckedRadioButtonId();
                if (selectedRebateId == -1) {
                    Toast.makeText(getApplicationContext(), "Please select a rebate percentage.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedRebateId);
                float rebate = Float.parseFloat(selectedRadioButton.getText().toString().replace("%", ""));


                double unit = Double.parseDouble(unitStr);
                double totalCharges = BillCalculator.calculateTotalCharges(unit);
                double finalCost = BillCalculator.applyRebate(totalCharges, rebate);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE bill SET month='" + month +
                        "', unit='" + unit +
                        "', rebate='" + rebate +
                        "', totalCharges='" + totalCharges +
                        "', finalCost='" + finalCost +
                        "' WHERE no='" + id + "'");

                Toast.makeText(getApplicationContext(), "Data Successfully Updated", Toast.LENGTH_SHORT).show();
                MainActivity.ma.RefreshList();
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}