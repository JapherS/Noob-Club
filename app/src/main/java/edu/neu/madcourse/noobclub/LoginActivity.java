package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private EditText email;
    private EditText password;

    private Button closeButton;
    private TextView errorText;
    private Dialog popUp;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initUI();

        popUp = new Dialog(this);
        popUp.setContentView(R.layout.error_pop_up);
        popUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        closeButton = popUp.findViewById(R.id.error_ok_button);
        errorText = popUp.findViewById(R.id.error_text);

        // init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.signInPageSignInButton:
                if (isValidInput()) {
                    loginUser();
                }
                break;
            case R.id.signInPageSignUpButton:
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                // finish current activity after launching new activity, so
                // the back button jump back to profile page that makes sense
                finish();
                break;

            case R.id.error_ok_button:
                popUp.dismiss();
                break;

        }
    }

    private void initUI() {
        email = findViewById(R.id.signInPageEmail);
        password = findViewById(R.id.signInPagePassword);
    }


    @SuppressLint("SetTextI18n")
    private boolean isValidInput() {
        if (email.getText().toString().equals("")) {
            errorText.setText("Please enter email");
            popUp.show();
            return false;
        }
        else if (password.getText().toString().equals("")) {
            errorText.setText("Please enter password");
            popUp.show();
            return false;
        }

        return true;
    }

    // logs user into firebase/our app
    private void loginUser() {
        // show a progress bar and disable canceling
        Dialog d = new Dialog(this);
        d.setCanceledOnTouchOutside(false);
        d.setContentView(R.layout.progress_bar);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ProgressBar p = d.findViewById(R.id.progressBar);
        p.setVisibility(ProgressBar.VISIBLE);
        d.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sign-in succeeded!",
                                    Toast.LENGTH_LONG).show();
                            // TODO: write info into single class
                            UserSignInStatus.getInstance().setUid(FirebaseAuth.getInstance().getUid());
                            UserSignInStatus.getInstance().setSignInStatus(true);
                            // after setting user to real-time db, can also set uid and username to this
                            // singleton class
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        }
                        else {
                            // TODO: figure out Exception type to make more accurate reaction
                            Toast.makeText(getApplicationContext(), "Sign-in Failed",
                                    Toast.LENGTH_LONG).show();
                        }

                        p.setVisibility(ProgressBar.GONE);
                        d.dismiss();
                    }
                });
    }




}
