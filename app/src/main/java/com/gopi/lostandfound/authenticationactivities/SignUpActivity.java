package com.gopi.lostandfound.authenticationactivities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gopi.lostandfound.R;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gopi.lostandfound.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameET_signUp, emailET_signUp, passwordET_signUp, confirmPasswordET_signUp;
    private Button createAccountBTN_signUp, loginBTN_signUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameET_signUp = findViewById(R.id.ET_Username_signUp);
        emailET_signUp = findViewById(R.id.ET_email_signUp);
        passwordET_signUp = findViewById(R.id.ET_password_signUp);
        confirmPasswordET_signUp = findViewById(R.id.ET_confirmPassword_signUp);

        createAccountBTN_signUp = findViewById(R.id.BTN_createAccount_signUp);
        loginBTN_signUp = findViewById(R.id.BTN_login_signUp);

        progressBar = findViewById(R.id.progressBar_signUp);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        loginBTN_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(h);
            }
        });


        createAccountBTN_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();

                // Get user input
                String username = usernameET_signUp.getText().toString().trim();
                String email = emailET_signUp.getText().toString().trim();
                String password = passwordET_signUp.getText().toString().trim();
                String confirmPassword = confirmPasswordET_signUp.getText().toString().trim();


                // Validate input fields
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    hideProgressBar();
                    Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    hideProgressBar();
                    Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    hideProgressBar();
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    hideProgressBar();
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkGoogleSignUp(username,email,password);


            }
        });


    }


    private void checkGoogleSignUp(String username, String email, String password) {
        // Check if the email belongs to a Google account using Google Sign-In for Websites API
        AuthCredential credential = GoogleAuthProvider.getCredential(email, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User signed in successfully with Google credentials
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                // If the user is signed in with Google, show an error message
                                hideProgressBar();
                                Toast.makeText(SignUpActivity.this, "Use a valid email", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the case if the user is null
                                hideProgressBar();
                                Toast.makeText(SignUpActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Unable to sign in with Google credentials, indicating that the email is not associated with a Google account
                            // Proceed with creating the user
                            createUserWithEmailAndPassword(username, email, password);
                        }
                    }
                });
    }



    private void createUserWithEmailAndPassword(String username,String email, String password) {
        // Register user with Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Save user data to Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("email", email);

                            db.collection("users")
                                    .document(firebaseAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            hideProgressBar();
                                            Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            // Redirect user to login screen
                                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                            finish(); // Close SignUpActivity
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideProgressBar();
                                            Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            hideProgressBar();
                            Toast.makeText(SignUpActivity.this, "Registration failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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