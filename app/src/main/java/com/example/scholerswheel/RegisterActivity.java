package com.example.scholerswheel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    CardView signUpBtn;
    EditText namebox, phonebox, emailbox, passwordbox, studentDetailsBox;

    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        signUpBtn = findViewById(R.id.track_bus);
        namebox = findViewById(R.id.name);
        phonebox = findViewById(R.id.phone);
        emailbox = findViewById(R.id.email);
        passwordbox = findViewById(R.id.password);
        studentDetailsBox = findViewById(R.id.student_details);

        FirebaseUser fuser = firebaseAuth.getCurrentUser();
        if(fuser!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();

            db.child("user").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User currentUser = dataSnapshot.getValue(User.class);
                    if(currentUser.isAdmin()) {
                        startActivity(new Intent(RegisterActivity.this, AdminActivity.class));
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

        signUpBtn.setOnClickListener( (v) -> {
            String name, phone, email, password, studentDetails;

            name = namebox.getText().toString().trim();
            phone = phonebox.getText().toString().trim();
            email = emailbox.getText().toString().trim();
            password = passwordbox.getText().toString();
            studentDetails = studentDetailsBox.getText().toString().trim();

            User user = new User(name, phone, email, studentDetails);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(RegisterActivity.this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Snackbar.make(v, "User Created", Snackbar.LENGTH_LONG).show();
                    String uid = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                    user.setUid(uid);
                    db.child("user").child(uid).setValue(user);

                    db.child("user").child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            if(currentUser.isAdmin()) {
                                handleAdmin();
                            } else {
                                handleNotAdmin();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }).addOnFailureListener(RegisterActivity.this, e -> Snackbar.make(v, "Failed to create user", Snackbar.LENGTH_LONG).show());
        });
    }

    private void handleNotAdmin() {
        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        finish();
        finishAffinity();
    }

    private void handleAdmin() {
        startActivity(new Intent(RegisterActivity.this, AdminActivity.class));
        finish();
        finishAffinity();
    }

    public void goToLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
