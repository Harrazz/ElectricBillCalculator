package com.example.electricbillcalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewBill extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button backBtn;
    TextView textMonth, textUnit, textTotalCharges, textRebate, textFinalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewBill.this, AboutApp.class);
            startActivity(intent);
        });

        dbHelper = new DataHelper(this);
        textMonth = findViewById(R.id.textMonth);
        textUnit = findViewById(R.id.textUnit);
        textTotalCharges = findViewById(R.id.textTotalCharges);
        textRebate = findViewById(R.id.textRebate);
        textFinalCost = findViewById(R.id.textFinalCost);
        backBtn = findViewById(R.id.buttonBack);

        int id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM bill WHERE no = " + id, null);
            if (cursor.moveToFirst()) {
                double totalCharges = cursor.getDouble(cursor.getColumnIndexOrThrow("totalCharges"));
                double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"));

                textMonth.setText(cursor.getString(cursor.getColumnIndexOrThrow("month")));
                textUnit.setText(cursor.getString(cursor.getColumnIndexOrThrow("unit")));
                textTotalCharges.setText(String.format("RM %.2f", totalCharges));
                textRebate.setText(cursor.getString(cursor.getColumnIndexOrThrow("rebate")));
                textFinalCost.setText(String.format("RM %.2f", finalCost));
            }
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
