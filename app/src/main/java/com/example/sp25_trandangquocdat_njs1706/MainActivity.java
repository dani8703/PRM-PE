package com.example.sp25_trandangquocdat_njs1706;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.sp25_trandangquocdat_njs1706.adapter.AuthorAdapter;
import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.data.GlobalData;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import com.example.sp25_trandangquocdat_njs1706.model.Book;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private List<Author> list;
    private List<Book> childList;
    private RecyclerView rvAuthor;
    private AuthorAdapter adapter;
    private ExtendedFloatingActionButton btnNew;
    private MaterialToolbar topAppBar;
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_DB_INITIALIZED = "dbInitialized";
    private final String REQUIRE = "Require";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private Button btnSignOut;

    private Button btnOpenMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        // Setup toolbar back button
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(view -> {
            // Go directly to Control Activity instead of previous activity
            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear activity stack
            startActivity(intent);
            finish();
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());

        btnSignOut = findViewById(R.id.sign_out_button);
        btnSignOut.setOnClickListener(v -> signOut());

        btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> openMap());

        // Check for existing Google Sign In account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void init() {
        rvAuthor = findViewById(R.id.rvStudent);
        btnNew = findViewById(R.id.btnNew);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        GlobalData.getInstance().setAppDatabase(appDatabase);
        String currentDBPath = getDatabasePath(StringConst.DBNAME).getAbsolutePath();
        Log.d("currentDBPath", currentDBPath);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDbInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false);

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (!isDbInitialized) {
                // Xóa dữ liệu cũ
                appDatabase.authorDAO().deleteAll();
                appDatabase.bookDAO().deleteAll();

                // Chèn dữ liệu mới cho Author
                appDatabase.authorDAO()
                        .insert(new Author("Nguyen Van A", "nguyenvana@gmail.com", "0123456789", "Ha Noi"));
                appDatabase.authorDAO()
                        .insert(new Author("Tran Thi B", "tranthib@gmail.com", "0987654321", "HCM City"));
                appDatabase.authorDAO().insert(new Author("Le Van C", "levanc@gmail.com", "0123456789", "Da Nang"));

                // Chèn dữ liệu mới cho Book
                appDatabase.bookDAO().insert(new Book("Java Programming", "2021-10-15", "Programming", 1));
                appDatabase.bookDAO().insert(new Book("Android Development", "2022-05-20", "Technology", 1));
                appDatabase.bookDAO().insert(new Book("Machine Learning Basics", "2023-01-10", "AI", 2));

                // Đánh dấu là đã khởi tạo
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_DB_INITIALIZED, true);
                editor.apply();

                runOnUiThread(() -> Toast
                        .makeText(MainActivity.this, "Database initialized with new data", Toast.LENGTH_SHORT).show());
            }

            list = appDatabase.authorDAO().getAll();
            childList = appDatabase.bookDAO().getAll();

            runOnUiThread(() -> {
                adapter = new AuthorAdapter(list, childList, MainActivity.this);
                rvAuthor.setAdapter(adapter);
                rvAuthor.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                initEventListeners();
            });
        });
    }

    private void initEventListeners() {
        // event handling
        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainCreateActivity.class);
            startActivity(intent);
        });

        adapter.setOnItemClickListener(new AuthorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                View currentView = rvAuthor.getChildAt(position);
                currentView.setBackgroundColor(Color.parseColor("#CD8484"));
                Intent intent = new Intent(MainActivity.this, MainEditActivity.class);
                intent.putExtra(StringConst.PutExtraNameMain, (Serializable) list.get(position));
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            Toast.makeText(this, "Signed in as: " + account.getEmail(), Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
            signInButton.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
        }
    }

    private void openMap() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}