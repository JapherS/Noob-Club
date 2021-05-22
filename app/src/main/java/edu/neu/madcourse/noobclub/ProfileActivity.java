package edu.neu.madcourse.noobclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private final static String TAG = ProfileActivity.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private AlertDialog avatarSelectDialog;
    private HashMap<Integer, Integer> avatarViewToDrawableMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Init map
        avatarViewToDrawableMap = new HashMap<>();
        for(int i = R.id.imageView_avatar_01, j = R.drawable.avatar_01; i <= R.id.imageView_avatar_06; i++, j++) {
            avatarViewToDrawableMap.put(i, j);
        }



        // check if user is signed in
        UserSignInStatus userSignInStatus = UserSignInStatus.getInstance();
        if (userSignInStatus.getSignInStatus()) {
            setContentView(R.layout.test_profile_after_login);
            fetchUserFromDB(userSignInStatus.getUid());
            //initUI();
        }
        else {
            setContentView(R.layout.test_profile_before_login);
        }

        // hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationProfile);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottomNavigationCommunity:
                                startActivity(new Intent(getApplicationContext(), CommunityActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                                break;

                            case R.id.bottomNavigationMatch:
                                startActivity(new Intent(getApplicationContext(), MatchActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                                break;

                            case R.id.bottomNavigationHome:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });

        View myPost = findViewById(R.id.myPostTv);
        if(myPost != null) {
            myPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this, MyPostListActivity.class));
                }
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.signInPageSignInButton:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;

            case R.id.beforeSignUpButton:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;

            default:
                System.out.println("In profile activity onClick, no case found!");
        }
    }

    public void initUI() {
        // set username, user avatar here
        TextView profileUserName = findViewById(R.id.textview_profile_username);
        profileUserName.setText(UserSignInStatus.getInstance().getUsername());
        ImageView avatar = findViewById(R.id.avatar_holder);
        avatar.setImageResource(UserSignInStatus.getInstance().getAvatarID());


    }

    private void fetchUserFromDB(String UID) {
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDBReference = mDatabase.getReference();
        UserSignInStatus userSignInStatus = UserSignInStatus.getInstance();
        mDBReference.child("user").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userSignInStatus.setEmail(user.getEmail());
                userSignInStatus.setUsername(user.getUserName());
                userSignInStatus.setAvatarID(user.getAvatarID());
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Open the avatar selector dialog
    public void openDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(R.layout.avatar_selector);
        avatarSelectDialog = builder.create();
        avatarSelectDialog.show();
    }

    // Handle the click of choosing avatar
    public void onClickAvatar(View view) {
        int id = view.getId();
        int drawableID = avatarViewToDrawableMap.get(id);
        ImageView avatar = findViewById(R.id.avatar_holder);
        avatar.setImageResource(drawableID);
        updateAvatarOnDB(drawableID);
        avatarSelectDialog.cancel();
    }

    // Update and fetch the latest avatar to DB
    public void updateAvatarOnDB(int drawableID) {
        DatabaseReference databaseReference = mDatabase.getReference();
        databaseReference.child("user").child(UserSignInStatus.getInstance().getUid())
                .runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);
                if(user == null) return Transaction.success(currentData);
                user.setAvatarID(drawableID);
                currentData.setValue(user);
                return Transaction.success(currentData) ;
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                fetchUserFromDB(UserSignInStatus.getInstance().getUid());
            }
        });
    }

    // Log out the current user
    public void logOut(View view) {
        UserSignInStatus.getInstance().setSignInStatus(false);
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}