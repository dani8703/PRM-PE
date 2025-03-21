package com.example.sp25_trandangquocdat_njs1706;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import com.example.sp25_trandangquocdat_njs1706.model.Book;

import java.util.List;

public class ChildActivityCreate extends AppCompatActivity {

    private EditText edTitle, edPublicationDate, edType;
    private Spinner spinnerAuthor;
    private Button btnAdd, btnCancel;
    private AppDatabase appDatabase;
    private List<Author> authorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_create);
        init();

        btnAdd.setOnClickListener(v -> {
            if (validate()) {
                createBook();
            }
        });

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void init() {
        edTitle = findViewById(R.id.edName); // Reuse name field for book title
        edPublicationDate = findViewById(R.id.edPublicationDate);
        edType = findViewById(R.id.edType);
        spinnerAuthor = findViewById(R.id.spinnerAuthor);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();

        loadAuthors();
    }

    private void loadAuthors() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            authorList = appDatabase.authorDAO().getAll();
            runOnUiThread(() -> {
                ArrayAdapter<Author> adapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, authorList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAuthor.setAdapter(adapter);
            });
        });
    }

    private void createBook() {
        String bookTitle = edTitle.getText().toString().trim();
        String publicationDate = edPublicationDate.getText().toString().trim();
        String type = edType.getText().toString().trim();

        Author selectedAuthor = (Author) spinnerAuthor.getSelectedItem();
        if (selectedAuthor == null) {
            Toast.makeText(this, "Please select an author", Toast.LENGTH_SHORT).show();
            return;
        }

        Book newBook = new Book(bookTitle, publicationDate, type, selectedAuthor.getId());

        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.bookDAO().insert(newBook);
            runOnUiThread(() -> {
                Toast.makeText(this, "Book created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ChildActivity.class);
                startActivity(intent);
                finish();
            });
        });
    }

    public boolean validate() {
        String title = edTitle.getText().toString().trim();
        String publicationDate = edPublicationDate.getText().toString().trim();
        String type = edType.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Book title cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Character.isUpperCase(title.charAt(0))) {
            Toast.makeText(this, "Book title must start with a capital letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(publicationDate)) {
            Toast.makeText(this, "Publication date cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Book type cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
