package com.example.scholerswheel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashNew extends AppCompatActivity {
    DatabaseReference db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_new);

        findViewById(R.id.register_button).setOnClickListener((v)->{
            startActivity(new Intent(this, RegisterActivity.class));
        });
        findViewById(R.id.login_button).setOnClickListener((v)->{
            startActivity(new Intent(this, LoginActivity.class));
        });

//        Objects.requireNonNull(getSupportActionBar()).hide(); //hide action bar
        hide();

        db = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null) {
            String uid = firebaseAuth.getCurrentUser().getUid();

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
        } else {
            show();
        }

    }

    private void show() {
        findViewById(R.id.register_button).setVisibility(View.VISIBLE);
        findViewById(R.id.login_button).setVisibility(View.VISIBLE);
    }

    private void hide() {
        findViewById(R.id.register_button).setVisibility(View.GONE);
        findViewById(R.id.login_button).setVisibility(View.GONE);
    }

    private void handleNotAdmin() {
        startActivity(new Intent(SplashNew.this, HomeActivity.class));
        finish();
    }

    private void handleAdmin() {
        startActivity(new Intent(SplashNew.this, AdminActivity.class));
        finish();
    }
}


/*

android:configChanges="orientation|keyboardHidden|screenSize"
            android:label="@string/title_activity_splash"
            android:theme="@style/FullscreenTheme"

 */