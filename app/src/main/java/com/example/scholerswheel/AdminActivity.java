package com.example.scholerswheel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class userDataHolder extends RecyclerView.ViewHolder {
    TextView namebox, emailbox, phonebox, studentDetailsbox, verifiedbox;
    Button approve, reject;

    public userDataHolder(@NonNull View view) {
        super(view);
        namebox = view.findViewById(R.id.name);
        emailbox = view.findViewById(R.id.email);
        phonebox = view.findViewById(R.id.phone);
        studentDetailsbox = view.findViewById(R.id.student_details);
        verifiedbox = view.findViewById(R.id.verified);

        approve = view.findViewById(R.id.btn_approve);
        reject = view.findViewById(R.id.btn_reject);
    }
}

class UserAdapter extends RecyclerView.Adapter<userDataHolder> {
    ArrayList<User> users;
    Activity activity;
    DatabaseReference db;

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public UserAdapter(Activity activity) {
        this.activity = activity;
        this.db = FirebaseDatabase.getInstance().getReference();
        this.users = new ArrayList<>();

        db.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                users.add(user);

                notifyItemInserted(users.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                for(int i=0; i<users.size(); i++) {
                    User u = users.get(i);
                    assert user != null;
                    if(u.getUid().equals(user.getUid())) {
                        users.set(i, user);
                        notifyItemChanged(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public userDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new userDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userDataHolder holder, int position) {
        User user = users.get(position);

        holder.namebox.setText(user.getName());
        holder.emailbox.setText(user.getEmail());
        holder.phonebox.setText(user.getPhone());
        String detailsText = "Details: "+ user.getStudentDetails();
        holder.studentDetailsbox.setText(detailsText);

        if(user.isAdmin()) {
            holder.approve.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
        } else if(user.isVerified()) {
            holder.approve.setVisibility(View.GONE);
            holder.reject.setVisibility(View.VISIBLE);
        } else {
            holder.approve.setVisibility(View.VISIBLE);
            holder.reject.setVisibility(View.GONE);
        }

        String verifiedStatus = "Status: " + ((user.isVerified() || user.isAdmin()) ? "Verified": "Not Verified");
        holder.verifiedbox.setText(verifiedStatus);

        holder.approve.setOnClickListener((v)->{
            user.setVerified(true);
            db.child("user").child(user.getUid()).child("verified").setValue(true);
        });

        holder.reject.setOnClickListener((v) ->{
            user.setVerified(false);
            db.child("user").child(user.getUid()).child("verified").setValue(false);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.user_data;
    }
}


public class AdminActivity extends AppCompatActivity {
    RecyclerView userList;
    DatabaseReference db;
    UserAdapter userAdapter;
    ArrayList<User> users;

    ImageButton logoutBtn;
    FirebaseAuth firebaseAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_drawer);

        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseDatabase.getInstance().getReference();

        userList = findViewById(R.id.user_list);
        userAdapter = new UserAdapter(AdminActivity.this);
        userList.setAdapter(userAdapter);
        userList.setLayoutManager(new LinearLayoutManager(AdminActivity.this));

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup Navigation Drawer Layout
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_logout:
                firebaseAuth.signOut();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
