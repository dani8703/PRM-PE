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

public class MainEditActivity extends AppCompatActivity {
    private EditText edName, edEmail, edPhone, edAddress;
    private Button btnUpdate, btnCancel;
    private Author author;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edit);
        init();

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnUpdate.setOnClickListener(v -> {
            if (validateFields()) {
                updateAuthor();
            }
        });
    }

    private void init() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edGender); // Reusing gender field for phone
        edAddress = findViewById(R.id.edAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();

        author = (Author) getIntent().getSerializableExtra(StringConst.PutExtraNameMain);
        if (author != null) {
            loadAuthorData();
        } else {
            Toast.makeText(this, "Error loading author data", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is not loaded
        }
    }

    private void loadAuthorData() {
        edName.setText(author.getName());
        edEmail.setText(author.getEmail());
        edPhone.setText(author.getPhone());
        edAddress.setText(author.getAddress());
    }

    private void updateAuthor() {
        author.setName(edName.getText().toString().trim());
        author.setEmail(edEmail.getText().toString().trim());
        author.setPhone(edPhone.getText().toString().trim());
        author.setAddress(edAddress.getText().toString().trim());

        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.authorDAO().update(author);
            runOnUiThread(() -> {
                Toast.makeText(this, "Author updated successfully", Toast.LENGTH_SHORT).show();
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

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Character.isUpperCase(name.charAt(0))) {
            Toast.makeText(this, "Name must start with a capital letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
