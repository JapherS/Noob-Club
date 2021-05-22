package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.constants.Constants;
import edu.neu.madcourse.noobclub.entity.GameCategory;
import edu.neu.madcourse.noobclub.entity.GamePostTopic;
import edu.neu.madcourse.noobclub.holder.GamePostTopicHolder;

public class MyPostListActivity extends AppCompatActivity {

    private RecyclerView postRv;
    private RecyclerAdapter<GamePostTopic> mAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_my_post_list);
        postRv = findViewById(R.id.postRv);
        progressBar = findViewById(R.id.progressBar);
        postRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter<GamePostTopic>() {
            @Override
            public ViewHolder<GamePostTopic> onCreateViewHolder(View root, int viewType) {
                return new GamePostTopicHolder(root);
            }

            @Override
            public int getItemLayout(GamePostTopic gamePostTopic, int position) {
                return R.layout.item_game_post_topic;
            }
        };
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        postRv.setAdapter(mAdapter);
        setListener();
        initData();
        setLoadingView(true);

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
                                break;

                            case R.id.bottomNavigationMatch:
                                startActivity(new Intent(getApplicationContext(), MatchActivity.class));
                                overridePendingTransition(0, 0);
                                break;

                            case R.id.bottomNavigationHome:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(0, 0);
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    private void setLoadingView(boolean loading)
    {
        if(loading){
            postRv.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            postRv.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setListener()
    {
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<GamePostTopic>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<GamePostTopic> holder, GamePostTopic gamePostTopic) {
                Intent intent = new Intent(MyPostListActivity.this,GamePostDetailActivity.class);
                intent.putExtra("game_topic",gamePostTopic);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<GamePostTopic> holder, GamePostTopic gamePostTopic) {

            }
        });


    }

    private void initData()
    {
        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .addValueEventListener(mValueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .removeEventListener(mValueEventListener);
    }

    private ValueEventListener mValueEventListener = new ValueEventListener(){

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
                        if(topic.creatorId.equals(UserSignInStatus.getInstance().getUid())) {
                            result.add(topic);
                        }
                    }
                }
            }
            ImageView emptyIcon = findViewById(R.id.empty_icon);
            TextView emptyText = findViewById(R.id.empty_text);
            if (result.isEmpty()) {
                emptyIcon.setVisibility(View.VISIBLE);
                emptyText.setVisibility(View.VISIBLE);
            }
            else {
                emptyIcon.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);
                mAdapter.replace(result);
            }
            setLoadingView(false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}