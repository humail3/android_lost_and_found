package com.gopi.lostandfound.authenticationactivities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.gopi.lostandfound.MainActivity;
import com.gopi.lostandfound.R;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameET_login, passwordET_login;
    private Button loginBTN_login, signUpBTN_login;
    private TextView forgetPasswordTV_login;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        progressBar = findViewById(R.id.progressBar_login);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);


        usernameET_login = findViewById(R.id.ET_username_login);
        passwordET_login = findViewById(R.id.ET_Password_login);

        loginBTN_login = findViewById(R.id.BTN_login_login);
        signUpBTN_login = findViewById(R.id.BTN_register_login);

        forgetPasswordTV_login = findViewById(R.id.TV_forgetPassword_login);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Check if user is already logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, take them to the main screen
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Close MainActivity to prevent the user from going back
        }


        signUpBTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });


        forgetPasswordTV_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));
                finish();
            }
        });


        loginBTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                String username = usernameET_login.getText().toString().trim();
                String password = passwordET_login.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    hideProgressBar();
                    Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Query Firestore to find the user's email based on the provided username
                db.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String email = document.getString("email");
                                        // Authenticate user using retrieved email and password
                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            hideProgressBar();
                                                            // Login success
                                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            finish(); // Close LoginActivity
                                                        } else {
                                                            hideProgressBar();
                                                            // If login fails, display a message to the user.
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    hideProgressBar();
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });


    }


    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


}