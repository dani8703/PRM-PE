package com.example.studentsmanagement_net1728_se171060;

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

import com.example.studentsmanagement_net1728_se171060.adapter.StudentAdapter;
import com.example.studentsmanagement_net1728_se171060.constant.StringConst;
import com.example.studentsmanagement_net1728_se171060.data.GlobalData;
import com.example.studentsmanagement_net1728_se171060.db.AppDatabase;
import com.example.studentsmanagement_net1728_se171060.excutors.AppExecutors;
import com.example.studentsmanagement_net1728_se171060.model.Student;
import com.example.studentsmanagement_net1728_se171060.model.Major;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private List<Student> list;
    private List<Major> childList;
    private RecyclerView rvStudent;
    private StudentAdapter adapter;
    private Button btnNew;
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



    // private void init(){
    //     rvStudent = findViewById(R.id.rvStudent);
    //     btnNew  = findViewById(R.id.btnNew);

    //     appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
    //     GlobalData.getInstance().setAppDatabase(appDatabase);
    //     String currentDBPath= getDatabasePath(StringConst.DBNAME).getAbsolutePath();
    //     Log.d("currentDBPath", currentDBPath);
    //     AppExecutors.getInstance().diskIO().execute(() -> {
    //         list = appDatabase.studentDAO().getAll();
    //         if(list != null && list.size() == 0){
    //             appDatabase.studentDAO().insert(new Student("Gia Nhu Chua Tung Gap",
    //                     new Date(),
    //                     "Romance ",
    //                     1));
    //             appDatabase.studentDAO().insert(new Student("Thien Tai Ben Trai - Ke Dien Ben Phai",
    //                     new Date(),
    //                     "Crime",
    //                     2));
    //             list = appDatabase.studentDAO().getAll();
    //         }

    //         childList = appDatabase.majorDAO().getAll();
    //         if(list != null && list.size() == 0){
    //             appDatabase.majorDAO().insert(new Major("Le Nhat Sang - SE171060", "sanglnse171060@fpt.edu.vn",
    //                     StringConst.AddressDefault,"0333336938"));
    //             appDatabase.majorDAO().insert(new Major("Mèo Đi Hia", "meodihia@gmail.com",
    //                     StringConst.AddressDefault,"0123456789"));
    //             appDatabase.majorDAO().insert(new Major("Krildelier", "krildelier@gmail.com",
    //                     StringConst.AddressDefault,"0987654321"));
    //             childList = appDatabase.majorDAO().getAll();
    //         }

    //         runOnUiThread(() -> {
    //             adapter = new StudentAdapter(list, childList,MainActivity.this);
    //             rvStudent.setAdapter(adapter);
    //             rvStudent.setLayoutManager (new LinearLayoutManager(MainActivity.this));

    //             // Create and Update
    //             initEventListeners();
    //         });
    //     });
    // }

    private void init(){
        rvStudent = findViewById(R.id.rvStudent);
        btnNew  = findViewById(R.id.btnNew);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        GlobalData.getInstance().setAppDatabase(appDatabase);
        String currentDBPath= getDatabasePath(StringConst.DBNAME).getAbsolutePath();
        Log.d("currentDBPath", currentDBPath);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDbInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false);

        

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (!isDbInitialized) {
                // Xóa dữ liệu cũ
                appDatabase.studentDAO().deleteAll();
                appDatabase.majorDAO().deleteAll();

                // Chèn dữ liệu mới cho Major
                appDatabase.majorDAO().insert(new Major("Software Engineering"));
                appDatabase.majorDAO().insert(new Major("Computer Science"));
                appDatabase.majorDAO().insert(new Major("Information Technology"));

                // Chèn dữ liệu mới cho Student
                // Lưu ý: Cần thay đổi constructor của Student cho phù hợp
                appDatabase.studentDAO().insert(new Student("Nguyen Van B", "2003-01-01", "Male", "nguyenvanb@fpt.edu.vn", "Ha Noi", 1));
                appDatabase.studentDAO().insert(new Student("Nguyen Van A", "2002-02-02", "Male", "nguyenvana@gmail.com", "HCM City", 2)); 

                // Đánh dấu là đã khởi tạo
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_DB_INITIALIZED, true);
                editor.apply();

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Database initialized with new data", Toast.LENGTH_SHORT).show());
            }

            list = appDatabase.studentDAO().getAll();
            childList = appDatabase.majorDAO().getAll();

            runOnUiThread(() -> {
                adapter = new StudentAdapter(list, childList,MainActivity.this);
                rvStudent.setAdapter(adapter);
                rvStudent.setLayoutManager (new LinearLayoutManager(MainActivity.this));
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

        adapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                View currentView = rvStudent.getChildAt(position);
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