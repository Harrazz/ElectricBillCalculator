package com.example.electricbillcalculator;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.electricbillcalculator.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseUser user;
    TextView profileText;

    String[] register;
    int[] recordIds;
    ListView ListView01;
    protected Cursor cursor;
    DataHelper dbcenter;
    public static MainActivity ma;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateBill.class);
                startActivity(intent);
            }
        });

        ImageButton infoButton = findViewById(R.id.info_button);
        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutApp.class);
            startActivity(intent);
        });


        ma = this;
        dbcenter = new DataHelper(this);
        RefreshList();

        auth = FirebaseAuth.getInstance();
        profileText = (TextView)findViewById(R.id.textView);

        user = auth.getCurrentUser();
        profileText.setText(user.getEmail());
    }

    public void RefreshList() {
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT no, month, finalCost FROM bill", null);

        register = new String[cursor.getCount()];
        recordIds = new int[cursor.getCount()];

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            recordIds[i] = cursor.getInt(cursor.getColumnIndexOrThrow("no"));
            String month = cursor.getString(cursor.getColumnIndexOrThrow("month"));
            double finalCost = cursor.getDouble(cursor.getColumnIndexOrThrow("finalCost"));
            register[i] = month + " - RM " + String.format("%.2f", finalCost);
        }

        ListView01 = findViewById(R.id.listView1);
        ListView01.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, register));
        ListView01.setSelected(true);

        ListView01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int selectedId = recordIds[position];
                final CharSequence[] dialogitem = {"View Details", "Update Record", "Delete Record"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose an option");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent viewIntent = new Intent(getApplicationContext(), ViewBill.class);
                                viewIntent.putExtra("id", selectedId);
                                startActivity(viewIntent);
                                break;
                            case 1:
                                Intent updateIntent = new Intent(getApplicationContext(), UpdateBill.class);
                                updateIntent.putExtra("id", selectedId);
                                startActivity(updateIntent);
                                break;
                            case 2:
                                SQLiteDatabase db = dbcenter.getWritableDatabase();
                                db.execSQL("DELETE FROM bill WHERE no = " + selectedId);
                                Toast.makeText(getApplicationContext(), "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                                RefreshList();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        ((ArrayAdapter<?>) ListView01.getAdapter()).notifyDataSetInvalidated();
    }

    public void signout(View v){
        auth.signOut();
        Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
        finish();
        Intent i = new Intent(this,Authentication.class);
        startActivity(i);

    }
}