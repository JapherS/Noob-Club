package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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

public class GamePostListActivity extends AppCompatActivity {

    private RecyclerView postRv;
    private RecyclerAdapter<GamePostTopic> mAdapter;
    private FloatingActionButton createPostBtn;
    private GameCategory mGameCategory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_game_post_list);
        mGameCategory = getIntent().getParcelableExtra("game_category");
        if(mGameCategory != null) {
            setTitle(mGameCategory.gameName);
        }
        postRv = findViewById(R.id.postRv);
        createPostBtn = findViewById(R.id.createPostBtn);
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
        postRv.setAdapter(mAdapter);
        setListener();
        initData();
        setLoadingView(true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationCommunity);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottomNavigationHome:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
    }

    private void setLoadingView(boolean loading)
    {
        if(loading){
            postRv.setVisibility(View.GONE);
            createPostBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            postRv.setVisibility(View.VISIBLE);
            createPostBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setListener()
    {
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<GamePostTopic>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<GamePostTopic> holder, GamePostTopic gamePostTopic) {
                Intent intent = new Intent(GamePostListActivity.this,GamePostDetailActivity.class);
                intent.putExtra("game_topic",gamePostTopic);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<GamePostTopic> holder, GamePostTopic gamePostTopic) {

            }
        });

        createPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserSignInStatus.getInstance().getSignInStatus()) {
                    // jump to login activity
//                    startActivity(new Intent(GamePostListActivity.this,LoginActivity.class));
                    Toast.makeText(GamePostListActivity.this, "Please login before post", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GamePostListActivity.this, GamePostEditActivity.class);
                intent.putExtra("game_category", mGameCategory);
                startActivity(intent);
            }
        });
    }

    private void initData()
    {
        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .child(String.valueOf(mGameCategory.id))
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
                .child(String.valueOf(mGameCategory.id))
                .removeEventListener(mValueEventListener);
    }

    private ValueEventListener mValueEventListener = new ValueEventListener(){

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, GamePostTopic>> type = new GenericTypeIndicator<HashMap<String, GamePostTopic>>() {};
            HashMap<String, GamePostTopic> chatLists = snapshot.getValue(type);
            List<GamePostTopic> result = new ArrayList<>();
            if(chatLists != null) {
                for(Map.Entry<String, GamePostTopic> entry:chatLists.entrySet()){
                    result.add(entry.getValue());
                }
            }
            result.sort(new Comparator<GamePostTopic>() {
                @Override
                public int compare(GamePostTopic left, GamePostTopic right) {
                    long d = right.createTime - left.createTime;
                    if (d>0) {
                        return 1;
                    } else if(d<0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            mAdapter.replace(result);
            setLoadingView(false);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}