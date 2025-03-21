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
import com.example.sp25_trandangquocdat_njs1706.model.Author;

public class MainCreateActivity extends AppCompatActivity {
    private EditText edName, edEmail, edPhone, edAddress;
    private Button btnAdd, btnCancel;
    private Author author;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_create);
        init();

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            if (validateFields()) {
                createAuthor();
            }
        });
    }

    private void init() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edGender); // Reusing gender field for phone
        edAddress = findViewById(R.id.edAddress);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        author = new Author();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
    }

    private void createAuthor() {
        author.setName(edName.getText().toString().trim());
        author.setEmail(edEmail.getText().toString().trim());
        author.setPhone(edPhone.getText().toString().trim());
        author.setAddress(edAddress.getText().toString().trim());

        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.authorDAO().insert(author);
            runOnUiThread(() -> {
                Toast.makeText(this, "Author created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }

    private boolean validateFields() {
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        String address = edAddress.getText().toString().trim();

        // Check if name is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if name starts with a capital letter
        if (!Character.isUpperCase(name.charAt(0))) {
            Toast.makeText(this, "Name must start with a capital letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if email is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if phone is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if address is empty
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
