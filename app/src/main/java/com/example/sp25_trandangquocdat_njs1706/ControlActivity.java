package com.example.sp25_trandangquocdat_njs1706;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class ControlActivity extends AppCompatActivity {

    private Button btnMain, btnChild, btnOpenMap, btnSignOut;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        btnMain = findViewById(R.id.btnMain);
        btnChild = findViewById(R.id.btnChild);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        signInButton = findViewById(R.id.sign_in_button);
        btnSignOut = findViewById(R.id.sign_out_button);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnMain.setOnClickListener(v -> {
            Intent intent = new Intent(ControlActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnChild.setOnClickListener(v -> {
            Intent intent = new Intent(ControlActivity.this, ChildActivity.class);
            startActivity(intent);
        });

        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(ControlActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(v -> signIn());
        btnSignOut.setOnClickListener(v -> signOut());

        // Check for existing Google Sign In account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    Toast.makeText(ControlActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
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
            updateUI(account);
        } catch (ApiException e) {
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
}
