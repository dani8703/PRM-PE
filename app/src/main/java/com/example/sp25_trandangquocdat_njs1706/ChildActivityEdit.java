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
import com.example.sp25_trandangquocdat_njs1706.model.Book;
import com.example.sp25_trandangquocdat_njs1706.model.Author;

import java.util.List;

public class ChildActivityEdit extends AppCompatActivity {

    private EditText edTitle, edPublicationDate, edType;
    private Spinner spinnerAuthor;
    private Button btnUpdate, btnCancel;
    private AppDatabase appDatabase;
    private Book book;
    private List<Author> authorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_edit);
        init();

        btnUpdate.setOnClickListener(v -> {
            if (validate()) {
                updateBook();
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
        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);

        book = (Book) getIntent().getSerializableExtra(StringConst.PutExtraNameChild);
        if (book != null) {
            edTitle.setText(book.getBookTitle());
            edPublicationDate.setText(book.getPublicationDate());
            edType.setText(book.getType());
        } else {
            Toast.makeText(this, "Error loading book data", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is not loaded
        }

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

                // Set selected author in spinner
                if (book != null) {
                    for (int i = 0; i < authorList.size(); i++) {
                        if (authorList.get(i).getId() == book.getAuthorId()) {
                            spinnerAuthor.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });
    }

    private void updateBook() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            book.setBookTitle(edTitle.getText().toString().trim());
            book.setPublicationDate(edPublicationDate.getText().toString().trim());
            book.setType(edType.getText().toString().trim());

            Author selectedAuthor = (Author) spinnerAuthor.getSelectedItem();
            if (selectedAuthor != null) {
                book.setAuthorId(selectedAuthor.getId());
            }

            appDatabase.bookDAO().update(book);
            runOnUiThread(() -> {
                Toast.makeText(this, "Book Updated", Toast.LENGTH_SHORT).show();
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
