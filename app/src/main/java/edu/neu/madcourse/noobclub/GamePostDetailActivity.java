package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.neu.madcourse.noobclub.base.RecyclerAdapter;
import edu.neu.madcourse.noobclub.constants.Constants;
import edu.neu.madcourse.noobclub.entity.GamePostTopic;
import edu.neu.madcourse.noobclub.entity.TopicReply;
import edu.neu.madcourse.noobclub.holder.TopicReplyHolder;
import edu.neu.madcourse.noobclub.utils.DateUtils;

public class GamePostDetailActivity extends AppCompatActivity {

    private GamePostTopic mTopic;
    private RecyclerView replyRv;
    private EditText msgEt;
    private ImageView sendBtn;
    private TextView titleTv;
    private TextView contentTv;
    private TextView createTimeTv;
    private ViewGroup toolLayout;
    private TextView createNameTv;
    private ViewGroup topicInfoLayout;
    private ImageView avatarIv;
    private ProgressBar progressBar;
    private RecyclerAdapter<TopicReply> mAdapter;
    private List<TopicReply> replyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_game_post_detail);
        avatarIv = findViewById(R.id.avatarIv);
        createNameTv = findViewById(R.id.creatorNameTv);
        replyRv = findViewById(R.id.replyRv);
        titleTv = findViewById(R.id.topicTitleTv);
        contentTv = findViewById(R.id.topicContentTv);
        createTimeTv = findViewById(R.id.createTimeTv);
        toolLayout = findViewById(R.id.toolsLayout);
        topicInfoLayout = findViewById(R.id.topicInfoLayout);
        progressBar = findViewById(R.id.progressBar);
        msgEt = findViewById(R.id.msgEt);
        sendBtn = findViewById(R.id.sendBtn);
        mTopic = getIntent().getParcelableExtra("game_topic");
        initView();
        initData();
        setListener();
        setLoadingView(true);
        replyRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                setLoadingView(false);
            }
        },1000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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

        try{
            Drawable drawableLeft = avatarIv.getContext().getDrawable(mTopic.avatar);
            if(drawableLeft != null) {
                avatarIv.setImageDrawable(drawableLeft);
            }
        }catch (Exception e){
            avatarIv.setImageResource(R.drawable.ic_avatar);
        }
    }

    private void setLoadingView(boolean loading)
    {
        if(loading){
            replyRv.setVisibility(View.GONE);
            toolLayout.setVisibility(View.GONE);
            topicInfoLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            replyRv.setVisibility(View.VISIBLE);
            toolLayout.setVisibility(View.VISIBLE);
            topicInfoLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void initView()
    {
        replyRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter<TopicReply>() {
            @Override
            public ViewHolder<TopicReply> onCreateViewHolder(View root, int viewType) {
                return new TopicReplyHolder(root);
            }

            @Override
            public int getItemLayout(TopicReply topicReply, int position) {
                return R.layout.item_game_toipc_reply;
            }
        };

        replyRv.setAdapter(mAdapter);
        titleTv.setText(mTopic.title);
        createNameTv.setText(mTopic.creatorName);
        contentTv.setText(mTopic.content);
        createTimeTv.setText(DateUtils.formatDate(mTopic.createTime));
    }

    private void initData()
    {
        mAdapter.replace(replyList);
        FirebaseDatabase.getInstance().getReference(Constants.Reference.POST)
                .child(String.valueOf(mTopic.gameId))
                .child(mTopic.id)
                .child(Constants.Reference.REPLY)
                .addChildEventListener(mChildEventListener);
    }

    private void setListener()
    {
        mAdapter.setAdapterListener(new RecyclerAdapter.AdapterListener<TopicReply>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder<TopicReply> holder, TopicReply reply) {

            }

            @Override
            public void onItemLongClick(RecyclerAdapter.ViewHolder<TopicReply> holder, TopicReply reply) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserSignInStatus.getInstance().getSignInStatus()) {
//                    startActivity(new Intent(GamePostDetailActivity.this,LoginActivity.class));  *jump to login activity
                    Toast.makeText(GamePostDetailActivity.this, "Please login before reply", Toast.LENGTH_SHORT).show();
                    return;
                }

                String msg = msgEt.getText().toString();
                if(!TextUtils.isEmpty(msg)){
                    sendReply(msg);
                    msgEt.setText("");
                    hideKeyboard(GamePostDetailActivity.this);
                }else{
                    Toast.makeText(GamePostDetailActivity.this, "please input message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendReply(String msg)
    {
        //        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String postId = UUID.randomUUID().toString();
        final Map<String,Object> values = new HashMap<>();
        values.put("id", postId);
        values.put("topicId",mTopic.id);
//        values.put("creatorId",user.getUid());
//        values.put("creatorName",user.getDisplayName());
        values.put("creatorId",UserSignInStatus.getInstance().getUid());
        values.put("creatorName",UserSignInStatus.getInstance().getUsername());
        values.put("content",msg);
        values.put("createTime",System.currentTimeMillis());
        values.put("crateAvatar",UserSignInStatus.getInstance().getAvatarID());
        FirebaseDatabase.getInstance()
                .getReference(Constants.Reference.POST)
                .child(String.valueOf(mTopic.gameId))
                .child(mTopic.id)
                .child(Constants.Reference.REPLY)
                .push()
                .setValue(values)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GamePostDetailActivity.this, "send reply success",
                                Toast.LENGTH_SHORT).show();
                        addReplyCount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GamePostDetailActivity.this, "send reply failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addReplyCount()
    {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("replyCount",mTopic.replyCount+1);
        FirebaseDatabase.getInstance()
                .getReference(Constants.Reference.POST)
                .child(String.valueOf(mTopic.gameId))
                .child(mTopic.id)
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mTopic.replyCount += 1;
                    }
                });
    }

    private ChildEventListener mChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            TopicReply info = snapshot.getValue(TopicReply.class);
            mAdapter.add(info);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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
                .child(String.valueOf(mTopic.gameId))
                .child(mTopic.id)
                .child(Constants.Reference.REPLY)
                .removeEventListener(mChildEventListener);
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}