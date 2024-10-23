package com.gopi.lostandfound.authenticationactivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.gopi.lostandfound.R;

public class ForgetPasswordActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    EditText emailET_forget;
    Button recoverBTN_forget;
    Button loginBTN_forget;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBar_forgetPassword);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(android.R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);


        emailET_forget = findViewById(R.id.ET_email_forget);
        recoverBTN_forget = findViewById(R.id.BTN_recover_forget);
        loginBTN_forget = findViewById(R.id.BTN_login_forget);

        firebaseAuth = FirebaseAuth.getInstance();


        loginBTN_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent h = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(h);
                finish();
            }
        });


        recoverBTN_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                String mail = emailET_forget.getText().toString().trim();
                if (mail.isEmpty()) {
                    hideProgressBar();
                    Toast.makeText(getApplicationContext(), "Enter your Email first", Toast.LENGTH_SHORT).show();
                } else {
//                    we have to send recover password email
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                hideProgressBar();
                                Toast.makeText(getApplicationContext(), "Email sent to you, You can recover your password using email", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                hideProgressBar();
                                Toast.makeText(ForgetPasswordActivity.this, "Email is wrong or account not exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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