package com.example.studentsmanagement_njs1706_se171464;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.studentsmanagement_njs1706_se171464.constant.StringConst;
import com.example.studentsmanagement_njs1706_se171464.db.AppDatabase;
import com.example.studentsmanagement_njs1706_se171464.excutors.AppExecutors;
import com.example.studentsmanagement_njs1706_se171464.model.Student;
import com.example.studentsmanagement_njs1706_se171464.model.Major;

import java.util.List;

public class MainEditActivity extends AppCompatActivity {
    private EditText edName, edEmail, edAddress;
    private TextView txtDate;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnUpdate, btnCancel;
    private Spinner spinnerMajor;
    private Student student;
    private AppDatabase appDatabase;
    private List<Major> majorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_edit);
        init();

        txtDate.setOnClickListener(v -> openDatePicker());
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnUpdate.setOnClickListener(v -> {
            if (validateFields()) {
                updateStudent();
            }
        });
    }

    private void init() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edAddress = findViewById(R.id.edAddress);
        txtDate = findViewById(R.id.txtDate);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
        spinnerMajor = findViewById(R.id.spinnerMajor);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();

        student = (Student) getIntent().getSerializableExtra(StringConst.PutExtraNameMain);
        if (student != null) {
            loadStudentData();
        } else {
            Toast.makeText(this, "Error loading student data", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is not loaded
        }
    }

    private void loadStudentData() {
        edName.setText(student.getName());
        edEmail.setText(student.getEmail());
        edAddress.setText(student.getAddress());
        txtDate.setText(student.getDate());

        if (student.getGender().equals("Male")) {
            rbMale.setChecked(true);
        } else if (student.getGender().equals("Female")) {
            rbFemale.setChecked(true);
        } else {
            // Handle other gender cases if needed
        }

        loadMajors();
    }

    private void loadMajors() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            majorList = appDatabase.majorDAO().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Major> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, majorList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMajor.setAdapter(adapter);

                // Set selection for spinner based on student.idMajor
                int majorPosition = getMajorPosition(student.getIdMajor());
                if (majorPosition != -1) {
                    spinnerMajor.setSelection(majorPosition);
                }
            });
        });
    }

    private int getMajorPosition(int majorId) {
        for (int i = 0; i < majorList.size(); i++) {
            if (majorList.get(i).getIdMajor() == majorId) {
                return i;
            }
        }
        return -1; // Not found
    }

    private void updateStudent() {
        student.setName(edName.getText().toString().trim());
        student.setEmail(edEmail.getText().toString().trim());
        student.setAddress(edAddress.getText().toString().trim());

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == rbMale.getId()) {
            student.setGender("Male");
        } else if (selectedGenderId == rbFemale.getId()) {
            student.setGender("Female");
        } else {
            student.setGender("Other"); // Or handle the case where no gender is selected
        }

        Major selectedMajor = (Major) spinnerMajor.getSelectedItem();
        if (selectedMajor != null) {
            student.setIdMajor(selectedMajor.getIdMajor());
        } else {
            Toast.makeText(this, "Please select a major", Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.studentDAO().update(student);
            runOnUiThread(() -> {
                Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }

    private void openDatePicker() {
        String[] dateParts = student.getDate().split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; 
        int day = Integer.parseInt(dateParts[2]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    student.setDate(date);
                    txtDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private boolean validateFields() {
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
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

        // You can add more validation for email format if needed

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
