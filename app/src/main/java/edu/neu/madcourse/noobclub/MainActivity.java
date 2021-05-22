package edu.neu.madcourse.noobclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

//import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
//import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.noobclub.constants.Constants;
import edu.neu.madcourse.noobclub.entity.GamePostTopic;

public class MainActivity extends AppCompatActivity {
    RecyclerView.LayoutManager layoutManager;
    MyRecyclerAdapter adapter;
    RecyclerView rcView;
    ArrayList<GamePostTopic> postList;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener lis = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth fbAuth) {
                if (fbAuth.getCurrentUser() != null) {
                    UserSignInStatus userInfo = UserSignInStatus.getInstance();
                    userInfo.setUid(FirebaseAuth.getInstance().getUid());
                    fetchUserFromDB(FirebaseAuth.getInstance().getUid());
                    userInfo.setSignInStatus(true);
                }
                else {
                    assert true;
                }
            }
        };
        firebaseAuth.addAuthStateListener(lis);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationHome);

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

                            case R.id.bottomNavigationProfile:
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                overridePendingTransition(0, 0);
                                finish();
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });


        postList = new ArrayList<>();

        rcView = findViewById(R.id.homePageRcView);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MyRecyclerAdapter(postList);
        adapter.setOnPostCardListener(new MyRecyclerAdapter.onPostCardClickListener() {
            @Override
            public void onPostCardClicked(int position) {
                // TODO: open up detailed post page, also add animation to this click event
                if(position>=0 && position<postList.size()) {
                    GamePostTopic gamePostTopic = postList.get(position);
                    Intent intent = new Intent(MainActivity.this, GamePostDetailActivity.class);
                    intent.putExtra("game_topic", gamePostTopic);
                    startActivity(intent);
                }
            }
        });

        rcView.setLayoutManager(layoutManager);
        rcView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .removeEventListener(mValueEventListener);
    }

    private ValueEventListener mValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, HashMap<String,GamePostTopic>>> type = new GenericTypeIndicator<HashMap<String, HashMap<String,GamePostTopic>>>() {
            };
            HashMap<String, HashMap<String,GamePostTopic>> chatLists = snapshot.getValue(type);
            List<GamePostTopic> result = new ArrayList<>();

            if (chatLists != null) {
                for (Map.Entry<String, HashMap<String,GamePostTopic>> entry : chatLists.entrySet()) {
                    HashMap<String,GamePostTopic> topics = entry.getValue();
                    for(Map.Entry<String, GamePostTopic> item : topics.entrySet()){
                        GamePostTopic topic = item.getValue();
                        result.add(topic);
                    }
                }
            }
            result.sort(new Comparator<GamePostTopic>() {
                @Override
                public int compare(GamePostTopic left, GamePostTopic right) {
                    long d = right.replyCount - left.replyCount;
                    if (d > 0) {
                        return 1;
                    } else if (d < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            postList.clear();
            if(!result.isEmpty()) {
                if(result.size() >10) {
                    postList.addAll(result.subList(0, 10));
                }else{
                    postList.addAll(result);
                }
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void fetchUserFromDB(String UID) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDBReference = mDatabase.getReference();
        UserSignInStatus userSignInStatus = UserSignInStatus.getInstance();
        mDBReference.child("user").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userSignInStatus.setSignInStatus(true);
                userSignInStatus.setEmail(user.getEmail());
                userSignInStatus.setUsername(user.getUserName());
                userSignInStatus.setAvatarID(user.getAvatarID());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}