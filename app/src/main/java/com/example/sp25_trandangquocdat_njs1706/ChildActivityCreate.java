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

import java.util.List;

public class ChildActivityCreate extends AppCompatActivity {

    private EditText edName;
    private Button btnAdd, btnCancel;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_create);
        init();

        btnAdd.setOnClickListener(v -> {
            if (validate()) {
                String majorName = edName.getText().toString().trim();

                AppExecutors.getInstance().diskIO().execute(() -> {
                    List<Major> existingMajors = appDatabase.majorDAO().findByName(majorName);
                    if (existingMajors != null && !existingMajors.isEmpty()) {
                        runOnUiThread(() -> edName.setError("Major name already exists"));
                    } else {
                        Major newMajor = new Major(majorName);
                        appDatabase.majorDAO().insert(newMajor);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Major created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ChildActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                });
            }
        });

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void init(){
        edName = findViewById(R.id.edName);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
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
