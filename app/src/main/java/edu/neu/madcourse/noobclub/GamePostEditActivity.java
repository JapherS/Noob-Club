package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.neu.madcourse.noobclub.constants.Constants;
import edu.neu.madcourse.noobclub.entity.GameCategory;
import edu.neu.madcourse.noobclub.entity.GamePostTopic;
import edu.neu.madcourse.noobclub.entity.TopicReply;

public class GamePostEditActivity extends AppCompatActivity {

    private EditText titleEt;
    private EditText contentEt;
    private Button sendBtn;
    private GameCategory mGameCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_game_post_edit);
        titleEt = findViewById(R.id.titleEt);
        contentEt = findViewById(R.id.contentEt);
        sendBtn = findViewById(R.id.sendBtn);
        mGameCategory = getIntent().getParcelableExtra("game_category");
        if(mGameCategory != null) {
            setTitle(mGameCategory.gameName);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentEt.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    createTopic();
                }else {
                    Toast.makeText(GamePostEditActivity.this, "please input something",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public void createTopic()
    {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String postId = UUID.randomUUID().toString();
        final Map<String,Object> values = new HashMap<>();
        values.put("id", postId);
        values.put("title",titleEt.getText().toString());
        values.put("content",contentEt.getText().toString());
//        values.put("creatorId",user.getUid());
//        values.put("creatorName",user.getDisplayName());

        values.put("creatorId",UserSignInStatus.getInstance().getUid());
        values.put("creatorName",UserSignInStatus.getInstance().getUsername());
        values.put("gameId",mGameCategory.id);
        values.put("gameName",mGameCategory.gameName);
        values.put("createTime",System.currentTimeMillis());
        values.put("reply", new ArrayList<TopicReply>());
        values.put("replyCount",0);
        values.put("avatar",UserSignInStatus.getInstance().getAvatarID());
        FirebaseDatabase.getInstance()
                .getReference(Constants.Reference.POST)
                .child(String.valueOf(mGameCategory.id))
                .child(postId)
                .setValue(values)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GamePostEditActivity.this, "create post success",
                                Toast.LENGTH_SHORT).show();
                        GamePostTopic topic = new GamePostTopic();
                        topic.id = postId;
                        topic.title = (String)values.get("title");
                        topic.content = (String)values.get("content");
                        topic.createTime = (long)values.get("createTime");
                        topic.gameId = mGameCategory.id;
                        topic.creatorName = UserSignInStatus.getInstance().getUsername();
                        topic.creatorId = UserSignInStatus.getInstance().getUid();
                        //values.put("creatorId",user.getUid());
                        //values.put("creatorName",user.getDisplayName());
                        topic.replyCount = 0 ;
                        topic.avatar = UserSignInStatus.getInstance().getAvatarID();
                        Intent intent = new Intent(GamePostEditActivity.this,GamePostDetailActivity.class);
                        intent.putExtra("game_topic",topic);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GamePostEditActivity.this, "create post failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}