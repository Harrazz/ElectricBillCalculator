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
    EditText editUnit, editRebate;
    TextView textId;

    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

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
        editRebate = findViewById(R.id.editText4);
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
                editRebate.setText(cursor.getString(cursor.getColumnIndexOrThrow("rebate")));
            }
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = spinnerMonth.getSelectedItem().toString();
                double unit = Double.parseDouble(editUnit.getText().toString().trim());
                double rebate = Double.parseDouble(editRebate.getText().toString().trim());

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