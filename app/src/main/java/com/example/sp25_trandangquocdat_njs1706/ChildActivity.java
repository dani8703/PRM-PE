package com.example.sp25_trandangquocdat_njs1706;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.example.sp25_trandangquocdat_njs1706.adapter.MajorAdapter;
import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.data.GlobalData;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Major;

import java.io.Serializable;
import java.util.List;

public class ChildActivity extends AppCompatActivity {

    private RecyclerView rvChildModel;
    private ExtendedFloatingActionButton btnNew;
    private MaterialToolbar topAppBar;
    private MajorAdapter adapter;
    private AppDatabase appDatabase;
    private List<Major> childModels;
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_DB_INITIALIZED = "dbInitialized";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        init();

        // Setup toolbar back button
        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(view -> {
            // Go directly to Control Activity instead of previous activity
            Intent intent = new Intent(ChildActivity.this, ControlActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear activity stack
            startActivity(intent);
            finish();
        });
    }

    private void init() {
        rvChildModel = findViewById(R.id.rvChildModel);
        btnNew = findViewById(R.id.btnNew);
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        GlobalData.getInstance().setAppDatabase(appDatabase);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDbInitialized = prefs.getBoolean(KEY_DB_INITIALIZED, false);

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (!isDbInitialized) {
                // Xóa dữ liệu cũ
                appDatabase.majorDAO().deleteAll();

                // Chèn dữ liệu mới
                appDatabase.majorDAO().insert(new Major("Software Engineering"));
                appDatabase.majorDAO().insert(new Major("Computer Science"));
                appDatabase.majorDAO().insert(new Major("Information Technology"));

                // Đánh dấu là đã khởi tạo
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_DB_INITIALIZED, true);
                editor.apply();

                runOnUiThread(() -> Toast
                        .makeText(ChildActivity.this, "Database initialized with new data", Toast.LENGTH_SHORT).show());
            }

            // Lấy dữ liệu mới
            childModels = appDatabase.majorDAO().getAll();

            runOnUiThread(() -> {
                adapter = new MajorAdapter(childModels, this);
                rvChildModel.setAdapter(adapter);
                rvChildModel.setLayoutManager(new LinearLayoutManager(ChildActivity.this));

                // Create and Update
                initEventListeners();
            });
        });
    }

    private void initEventListeners() {
        // event handling
        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChildActivityCreate.class);
            startActivity(intent);
        });

        // Sửa StudentAdapter.OnItemClickListener -> MajorAdapter.OnItemClickListener
        adapter.setOnItemClickListener(new MajorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                View currentView = rvChildModel.getChildAt(position);
                currentView.setBackgroundColor(Color.parseColor("#CD8484"));
                Intent intent = new Intent(ChildActivity.this, ChildActivityEdit.class);
                intent.putExtra(StringConst.PutExtraNameChild, (Serializable) childModels.get(position));
                startActivity(intent);
            }
        });
    }

    public void resetDatabase(View view) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DB_INITIALIZED, false);
        editor.apply();

        // Khởi động lại activity để áp dụng thay đổi
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
