package com.example.sp25_trandangquocdat_njs1706;

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

import com.example.sp25_trandangquocdat_njs1706.Utils.DateUtils;
import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Student;
import com.example.sp25_trandangquocdat_njs1706.model.Major;

import java.util.Calendar;
import java.util.List;

public class MainCreateActivity extends AppCompatActivity {
    private EditText edName, edEmail, edAddress;
    private TextView txtDate;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnAdd, btnCancel;
    private Spinner spinnerMajor;
    private Student student;
    private AppDatabase appDatabase;
    private List<Major> majorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_create);
        init();

        txtDate.setOnClickListener(v -> openDatePicker());
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            if (validateFields()) {
                createStudent();
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
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        spinnerMajor = findViewById(R.id.spinnerMajor);

        student = new Student();
        student.setDate(DateUtils.getCurrentDate()); // Sử dụng DateUtils để lấy ngày hiện tại
        txtDate.setText(student.getDate());

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        loadMajors();
    }

    private void loadMajors() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            majorList = appDatabase.majorDAO().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Major> adapter = new ArrayAdapter<Major>(
                        this, android.R.layout.simple_spinner_item, majorList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                
                // Cấu hình spinner để hiển thị tên major
                spinnerMajor.setAdapter(adapter);
            });
        });
    }

    private void createStudent() {
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

        Major selectedMajor = majorList.get(spinnerMajor.getSelectedItemPosition());
        if (selectedMajor != null) {
            student.setIdMajor(selectedMajor.getIdMajor());
        } else {
            Toast.makeText(this, "Please select a major", Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.studentDAO().insert(student);
            runOnUiThread(() -> {
                Toast.makeText(this, "Student created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

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


        // Check if address is empty
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
