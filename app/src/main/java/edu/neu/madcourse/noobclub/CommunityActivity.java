package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.constants.GameCategoryRepo;
import edu.neu.madcourse.noobclub.entity.GameCategory;
import edu.neu.madcourse.noobclub.holder.GameCategoryHolder;
import edu.neu.madcourse.noobclub.holder.GameCategoryTitleHolder;
import edu.neu.madcourse.noobclub.widget.LetterIndexView;

public class CommunityActivity extends AppCompatActivity {

    private RecyclerView gameRecyclerView;
    private RecyclerAdapter<GameCategory> mAdapter;
    private SearchView searchView;
    private LetterIndexView letterIndexView;
    private List<GameCategory> mAllGames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        searchView = findViewById(R.id.searchView);
        gameRecyclerView = findViewById(R.id.gamesRv);
        letterIndexView = findViewById(R.id.letterIndexView);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                GameCategory category = mAdapter.getItems().get(position);
                if(category.id <= 0){
                    return 3;
                }else{
                    return 1;
                }
            }
        });
        gameRecyclerView.setLayoutManager(manager);
        mAdapter = new RecyclerAdapter<GameCategory>(){

            @Override
            public ViewHolder<GameCategory> onCreateViewHolder(View root, int viewType) {
                if(viewType == R.layout.item_game_category_title){
                    return new GameCategoryTitleHolder(root);
                }else {
                    return new GameCategoryHolder(root);
                }
            }

            @Override
            public int getItemLayout(GameCategory gameCategory, int position) {
                if(gameCategory.id <= 0){
                    return R.layout.item_game_category_title;
                }else {
                    return R.layout.item_game_category;
                }
            }
        };

        gameRecyclerView.setAdapter(mAdapter);
        setListener();
        initData();
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

    private void setListener()
    {
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<GameCategory>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<GameCategory> holder, GameCategory gameCategory) {
                if(gameCategory.id > 0) {
                    Intent intent = new Intent(CommunityActivity.this, GamePostListActivity.class);
                    intent.putExtra("game_category", gameCategory);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<GameCategory> holder, GameCategory gameCategory) {

            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchView.getQuery().toString();
                queryGames(keyword);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryGames(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    mAdapter.replace(mAllGames);
                }
                return false;
            }
        });

        letterIndexView.setOnTouchingLetterChangedListener(new LetterIndexView.OnTouchingLetterChangedListener() {
            @Override
            public void onHit(String letter) {
//              letter index
//                Toast.makeText(CommunityActivity.this,letter,Toast.LENGTH_SHORT).show();
                int findPos = 0;
                if("#".equals(letter)){
                    findPos = mAllGames.size()-1;
                }
                for(int i=0;i<mAllGames.size();i++){
                    GameCategory category = mAllGames.get(i);
                    if(category.firstChar.equalsIgnoreCase(letter) && category.id <=0){
                        findPos = i ;
                        break;
                    }
                }
                gameRecyclerView.scrollToPosition(findPos);
            }

            @Override
            public void onCancel() {

            }
        });

        int v1 = R.drawable.avatar_01;
        int v2 = R.drawable.avatar_02;
        int v3 = R.drawable.avatar_03;
        int v4 = R.drawable.avatar_04;
        int v5 = R.drawable.avatar_05;
        int v6 = R.drawable.avatar_06;
        Log.d("id","v1="+v1);
        Log.d("id","v2="+v2);
        Log.d("id","v3="+v3);
        Log.d("id","v4="+v4);
        Log.d("id","v5="+v5);
        Log.d("id","v6="+v6);
    }

    private void initData()
    {
        mAllGames.clear();
        mAllGames.addAll(GameCategoryRepo.getCategories());
        mAdapter.replace(mAllGames);
    }

    private void queryGames(String keyword)
    {
        if(TextUtils.isEmpty(keyword)){
            mAdapter.replace(mAllGames);
        }else {
            List<GameCategory> result = new ArrayList<>();
            for (GameCategory item : mAllGames) {
                if (item.gameName.toLowerCase().contains(keyword.toLowerCase())) {
                    result.add(item);
                }
            }
            mAdapter.replace(result);
        }
    }

}