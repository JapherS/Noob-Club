package edu.neu.madcourse.noobclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.UUID;

public class MatchActivity extends AppCompatActivity {

    private final static String TAG = MatchActivity.class.getSimpleName();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Navigation bar action listener starts
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavigationMatch);
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

                            case R.id.bottomNavigationHome:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
        // Navigation bar action listener ends

    }

   public void onClick(View view) {
       Bundle bundle;
       Intent intent;
       if(!UserSignInStatus.getInstance().getSignInStatus()) {
           openDialog();
           return;
       }
       switch (view.getId()) {
            case R.id.button_csgo :
                //startActivity(new Intent(getApplicationContext(), MatchWaitingActivity.class));
                bundle = new Bundle();
                bundle.putString("gameName", "csgo");
                intent = new Intent(this,  MatchWaitingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //overridePendingTransition(0, 0);
                break;
           case R.id.button_r6s :
               //startActivity(new Intent(getApplicationContext(), MatchWaitingActivity.class));
               bundle = new Bundle();
               bundle.putString("gameName", "r6s");
               intent = new Intent(this,  MatchWaitingActivity.class);
               intent.putExtras(bundle);
               startActivity(intent);
               //overridePendingTransition(0, 0);
               break;
           case R.id.button_apex :
               //startActivity(new Intent(getApplicationContext(), MatchWaitingActivity.class));
               bundle = new Bundle();
               bundle.putString("gameName", "apex");
               intent = new Intent(this,  MatchWaitingActivity.class);
               intent.putExtras(bundle);
               startActivity(intent);
               //overridePendingTransition(0, 0);
               break;
           case R.id.button_ow :
               //startActivity(new Intent(getApplicationContext(), MatchWaitingActivity.class));
               bundle = new Bundle();
               bundle.putString("gameName", "ow");
               intent = new Intent(this,  MatchWaitingActivity.class);
               intent.putExtras(bundle);
               startActivity(intent);
               //overridePendingTransition(0, 0);
               break;
        }
   }

    // Open the dialog
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchActivity.this);
        // create the cancel/ok button
        builder.setTitle("Please log in first");
        builder.setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

}
