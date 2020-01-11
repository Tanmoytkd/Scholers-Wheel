package com.example.scholerswheel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText emailBox, passwordBox;
    CardView loginBtn;

    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        emailBox = findViewById(R.id.email);
        passwordBox = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);

        FirebaseUser fuser = firebaseAuth.getCurrentUser();
        if(fuser!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();

            db.child("user").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser.isAdmin()) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        finish();
                    } else {
                        handleNotAdmin();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        loginBtn.setOnClickListener((v)->{
            String email, password;
            email = emailBox.getText().toString().trim();
            password = passwordBox.getText().toString().trim();
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(v, "Login Failed: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(LoginActivity.this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Snackbar.make(v, "Login Successful", Snackbar.LENGTH_LONG).show();
                    String uid = firebaseAuth.getCurrentUser().getUid();

                    db.child("user").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            if(currentUser.isAdmin()) {
                                handleAdmin();
                            } else {
                                Snackbar.make(v, "You are not admin", Snackbar.LENGTH_LONG).show();
                                handleNotAdmin();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        });
    }

    private void handleNotAdmin() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        finish();
        finishAffinity();
    }

    private void handleAdmin() {
        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
        finish();
        finishAffinity();
    }

    public void openregister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
