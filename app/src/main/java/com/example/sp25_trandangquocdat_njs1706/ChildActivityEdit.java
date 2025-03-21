package com.example.sp25_trandangquocdat_njs1706;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Major;

public class ChildActivityEdit extends AppCompatActivity {

    private EditText edName;
    private Button btnUpdate, btnCancel;
    private AppDatabase appDatabase;
    private Major major;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        init();

        btnUpdate.setOnClickListener(v -> {
            if (validate()) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    major.setNameMajor(edName.getText().toString().trim());
                    appDatabase.majorDAO().update(major); 
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Major Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ChildActivity.class);
                        startActivity(intent);
                        finish();
                    });
                });
            }
        });

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void init() {
        edName = findViewById(R.id.edName);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        major = (Major) getIntent().getSerializableExtra(StringConst.PutExtraNameChild);
        if (major != null) {
            edName.setText(major.getNameMajor());
        } else {
            Toast.makeText(this, "Error loading major data", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is not loaded
        }

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
    }

    public boolean validate() {
        String name = edName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Major name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Character.isUpperCase(name.charAt(0))) {
            Toast.makeText(this, "Major name must start with a capital letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
