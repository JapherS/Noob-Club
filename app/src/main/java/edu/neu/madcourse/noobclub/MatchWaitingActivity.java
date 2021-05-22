package edu.neu.madcourse.noobclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MatchWaitingActivity extends AppCompatActivity{
    private final static String TAG = MatchWaitingActivity.class.getSimpleName();
    private String gameName;
    private ImageView gameBanner;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDBReference;
    private List<String> localUserList;
    private Bundle bundle;
    private String lobbyID = "";
    private volatile boolean stopThread = false;
    private String userName = "";
    private AlertDialog dialog;
    private boolean onMatching = false;

    private HandlerThread handlerThread = new HandlerThread("handlerThread");
    private Handler handler;

    // Sensor
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    // sensor event listener
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 12) {
                if(!onMatching) {
                    searchLobby();
                }
                //Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_waiting);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Start handler thread
        handlerThread.start();

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
                                break;

                            case R.id.bottomNavigationHome:
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(0, 0);
                                break;

                            case R.id.bottomNavigationProfile:
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                overridePendingTransition(0, 0);
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
        // Navigation bar action listener ends

        // Initialize this activity
        initMatchWaitingActivity();

        initSensor();

        // Bind function to test start match button starts
        findViewById(R.id.button_start_match).setOnClickListener(v -> {
            searchLobby();
        });
        // Bind function to test start match button ends
    }

    // Open the matching dialog
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchWaitingActivity.this);
        builder.setView(R.layout.match_progress);
        builder.setCancelable(false);
        // create the cancel/ok button
        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(lobbyID != null && !lobbyID.equals("")) {
                            closeLobby();
                        }
                        onMatching = false;
                        localUserList.clear();
                        dialog.cancel();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }


    // init sensor
    private void initSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }



    // initialize variables for MatchWaitingActivity
    private void initMatchWaitingActivity() {
        // Initialize database instance & reference
        mDatabase = FirebaseDatabase.getInstance();
        mDBReference = mDatabase.getReference();
        // Initialize game title & banner starts
        bundle = getIntent().getExtras();
        if(bundle != null) {
            gameName = bundle.getString("gameName");
            setBanner(gameName);
            Log.d(TAG, "Entered: " + gameName);
        }
        // Initialize game title & banner ends

        // Initialize local user list
        localUserList = new ArrayList<>();

        // Get user name
        userName = UserSignInStatus.getInstance().getUsername();
    }

    // Setting up the banner of the chosen game
    private void setBanner(String gameName) {
        gameBanner = findViewById(R.id.gameBanner);
        switch (gameName) {
            case "csgo":
                gameBanner.setImageResource(R.drawable.banner_csgo);
                break;
            case "r6s":
                gameBanner.setImageResource(R.drawable.banner_r6s);
                break;
            case "ow":
                gameBanner.setImageResource(R.drawable.banner_ow);
                break;
            case "apex":
                gameBanner.setImageResource(R.drawable.banner_apex);
                break;
        }
    }

    // Open a new lobby
    private void openLobby() {
        String uniqueID = UUID.randomUUID().toString(); // generate a random lobby ID
        Date date = new Date();
        MatchLobby lobby = new MatchLobby(uniqueID, date.toString(), date.getTime(), gameName);
        this.lobbyID = uniqueID; // store the newly generated lobby ID
        lobby.addUser(userName);
        mDBReference = mDatabase.getReference().child("matchmaking").child(gameName).child(uniqueID);
        mDBReference.setValue(lobby);

        // Update dialog when the lobby is opened
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonVisible();
            }
        });

        // Fetching the updated user list from the database (thread) starts
        mDBReference = mDatabase.getReference();
        mDBReference.child("matchmaking").child(gameName).child(lobbyID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Fetch thread " + snapshot.toString());
                localUserList.clear();
                if(snapshot.exists()) {
                    Log.d(TAG, "inside snapshot lobby --------------------------------------------------------------");
                    MatchLobby matchLobby = snapshot.getValue(MatchLobby.class);
                    localUserList = matchLobby.getUserList();
                    Log.d(TAG, " ------> Local user list size: " + localUserList.size());
                }
                for(String str : localUserList) {
                    Log.d(TAG, "userName: " + str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Fetching the updated user list from the database (thread) ends

        // Watching/monitoring the local user list (thread) starts
        stopThread = false;
        handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String TAG = "LobbyWatcherThread";
                for(int i = 0; i < 10; i++) {
                    if(stopThread) return;
                    Log.d(TAG, "Thread running on: " + i + "seconds");
                    Log.d(TAG, "Local user list size: " + localUserList.size());
                    for(String str : localUserList) {
                        Log.d(TAG, "userName: " + str);
                    }
                    // if the second user joined the lobby
                    if(localUserList.size() == 2) {
                        // close the lobby
                        closeLobby();
                        // send the "notification" to the dialog (to the lobby host)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView matchStatus = dialog.findViewById(R.id.match_status);
                                dialog.findViewById(R.id.match_progress_bar).setVisibility(View.GONE);
                                String otherName = localUserList.get(1);
                                matchStatus.setText("You are matched with " + otherName + " !");
                                changeButtonText();
                            }
                        });
                    }
                    // sleep for 1 second in between loops
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Time out - if no one joins the lobby within the specified time window, update dialog and close the lobby
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView matchStatus = dialog.findViewById(R.id.match_status);
                        dialog.findViewById(R.id.match_progress_bar).setVisibility(View.GONE);
                        matchStatus.setText("No match has been found. Please try again later.");
                        changeButtonText();
                    }
                });
                closeLobby();
            }
        });
        // Watching/monitoring the local user list (thread) ends
    }

    // Join an existing lobby
    private void joinLobby(String lobbyID) {
        Log.d(TAG, "lobby join START");
        mDBReference.child("matchmaking").child(gameName).child(lobbyID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                MatchLobby lobby = currentData.getValue(MatchLobby.class);
                if(lobby == null) {
                    return Transaction.success(currentData);
                }
                lobby.addUser(userName);
                currentData.setValue(lobby);
                Log.d(TAG, "lobby join SUCCESS");
                // update the dialog (receiving notification) for the joiner
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView matchStatus = dialog.findViewById(R.id.match_status);
                        dialog.findViewById(R.id.match_progress_bar).setVisibility(View.GONE);
                        String otherName = lobby.getUserList().get(0); // fetch the lobby host's name from the database
                        changeButtonText();
                        setButtonVisible();
                        matchStatus.setText("You are matched with " + otherName + " !");
                    }
                });
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d(TAG, "lobby join COMPLETED");
            }
        });
    }

    // make the cancel/ok button visible
    private void setButtonVisible() {
        Button button =  dialog.getButton(-2);
        button.setVisibility(View.VISIBLE);
    }

    // change the text of the button from "cancel" to "ok"
    private void changeButtonText() {
        Button button =  dialog.getButton(-2);
        button.setText("OK");
    }

    // Iterate through the lobby list on the database and check if there's any active lobbies
    private void searchLobby() {
        onMatching = true;
        openDialog(); // display the dialog
        // hide the cancel/ok button to prevent the user from interrupting lobby room search
        dialog.getButton(-2).setVisibility(View.GONE);
        mDBReference.child("matchmaking").child(gameName).orderByChild("startTimeLong").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                //Toast.makeText(MatchWaitingActivity.this, "Total lobby: " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                // searching through the lobby list on the database
                // if there there IS an active lobby, join the lobby
                while(items.hasNext()) {
                    DataSnapshot item = items.next();
                    if(item.child("lobbyStatus").getValue().equals("ACTIVE")) {
                        Log.d(TAG, "LOBBY ID: " + item.child("lobbyID").getValue().toString());
                        joinLobby( item.child("lobbyID").getValue().toString()); // join the lobby
                        String startTime = item.child("startTime").getValue().toString();
                        Log.d(TAG, "START TIME: " + startTime);
                        return;
                    }
                }
                // if there is no active lobby room, open a brand-new lobby
                openLobby();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // remove the current lobby from the database
    private void removeLobby(String lobbyID) {
        if(lobbyID != null) {
            mDBReference.child("matchmaking").child(gameName).child(lobbyID).removeValue();
            Log.d(TAG, "Lobby closed");
        }
    }

    // close the current lobby from the database (set status from "Active" to "Closed")
    public void closeLobby() {
        stopThread = true; // give the signal to stop the watcher thread
        mDBReference.child("matchmaking").child(gameName).child(lobbyID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                MatchLobby matchLobby = currentData.getValue(MatchLobby.class);
                if(matchLobby == null) {
                    return Transaction.success(currentData);
                }
                matchLobby.close();
                currentData.setValue(matchLobby);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                lobbyID = "";
                if(error != null) {
                    Log.d(TAG, "error occurs when close the lobby");
                }
            }
        });
    }
}