package edu.neu.madcourse.noobclub;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = RegisterActivity.class.getSimpleName();
    private EditText username;
    private EditText email;
    private EditText emailConfirm;
    private EditText password;
    private EditText passwordConfirm;
    private Button closeButton;
    private TextView errorText;
    private Dialog popUp;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    private final short PASSWORD_LENGTH_LIMIT = 15;
    private final short PASSWORD_LENGTH_LEAST = 6;
    private final short EMAIL_LENGTH_LIMIT = 37;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_register);

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
            case R.id.signUpPageSignInButton:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;

            // register a new account
            case R.id.signUpPageSignUpButton:
                if (isValidInput()) {
                    registerNewUser();
                }
                break;

            case R.id.error_ok_button:
                popUp.dismiss();
                break;
        }
    }


    // ready up all UI elements for later use
    private void initUI(){
        username = findViewById(R.id.signUppageUsername);
        email = findViewById(R.id.signUpPageEmail);
        emailConfirm = findViewById(R.id.signUpPageEmailConfirm);
        password = findViewById(R.id.signUpPagePassword);
        passwordConfirm = findViewById(R.id.signUpPagePasswordConfirm);
    }

    // returns true is str is not empty, false otherwise
    private boolean isEmpty(String str) {
        return str.equals("");
    }


    // email format verification
    private boolean isValidEmail(String email) {
        String patternString = "^.{4,20}@.{1,10}[.].{2,5}";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isPasswordWithinRange(String pw) {
        return pw.length() >= PASSWORD_LENGTH_LEAST && pw.length() <= PASSWORD_LENGTH_LIMIT;
    }

    // for simplicity reasons, password may only contain numbers and/or English characters
    // this method is
    private boolean isValidPassword(String pw) {
        for (char chr : pw.toCharArray()) {
            if (!((chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z')
                    || (chr >= '0' && chr <= '9'))) {
                // only numbers and English characters
                return false;
            }
        }
        return true;
    }

    // username may only contain numbers and/or English characters
    private boolean isValidUsername(String username) {
        return isValidPassword(username);
    }

    // returns true if all info is valid, returns false otherwise
    @SuppressLint("SetTextI18n")
    private boolean isValidInput() {

        // verify username
        if (isEmpty(username.getText().toString())) {
            errorText.setText("Username cannot be empty string");
            popUp.show();
            return false;
        }

        else if (!isValidUsername(username.getText().toString())) {
            errorText.setText("Username may only contain numbers and English characters");
            popUp.show();
            return false;
        }

        // verify email address
        else if (isEmpty(email.getText().toString())) {
            errorText.setText("Email cannot be empty string");
            popUp.show();
            return false;
        }


        else if (!isValidEmail(email.getText().toString())) {
            errorText.setText("Email format is incorrect");
            popUp.show();
            return false;
        }

        else if (email.getText().toString().length() > EMAIL_LENGTH_LIMIT) {
            errorText.setText("Email too long");
            popUp.show();
            return false;
        }


        else if (isEmpty(emailConfirm.getText().toString())) {
            errorText.setText("Please confirm your email address");
            popUp.show();
            return false;
        }

        else if (!email.getText().toString().equals(emailConfirm.getText().toString())) {
            errorText.setText("Email addresses don't match");
            popUp.show();
            return false;
        }

        // verify password
        else if (isEmpty(password.getText().toString())) {
            errorText.setText("Password cannot be empty string");
            popUp.show();
            return false;
        }

        else if (!isPasswordWithinRange(password.getText().toString())) {
            errorText.setText("Password length ranges from 6 to 15 characters");
            popUp.show();
            return false;
        }

        else if (!isValidPassword(password.getText().toString())) {
            errorText.setText("Password may only contain A(a) - Z(z) and/or 0 - 9");
            popUp.show();
            return false;
        }

        else if (isEmpty(passwordConfirm.getText().toString())) {
            errorText.setText("Please confirm your password");
            popUp.show();
            return false;
        }

        else if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
            errorText.setText("Passwords don't match");
            popUp.show();
            return false;
        }

        return true;
    }

    // register a new user in firebase
    private void registerNewUser() {
        // show a progress bar and disable canceling
        Dialog d = new Dialog(this);
        d.setCanceledOnTouchOutside(false);
        d.setContentView(R.layout.progress_bar);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ProgressBar p = d.findViewById(R.id.progressBar);
        p.setVisibility(ProgressBar.VISIBLE);
        d.show();

        // register user
        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration succeeded!",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG,"UID------------------->" + firebaseAuth.getCurrentUser().getUid());
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            // Create user object and upload it to DB
                            initializeUserDB(firebaseAuth.getCurrentUser().getUid(), username.getText().toString(), email.getText().toString());
                            // pops current activity out of back stack
                            finish();
                        }
                        else {
                            // TODO: figure out Exception type to make different reaction, for example
                            // email already registered
                            // TODO: store username in our real-time database
                            System.out.println(task.getException().getMessage());
                            Toast.makeText(getApplicationContext(), "Registration failed! "
                                            + "Please try again later!",
                                    Toast.LENGTH_LONG).show();

                        }

                        p.setVisibility(ProgressBar.GONE);
                        d.dismiss();
                    }
                });
    }

    private void initializeUserDB(String UID, String userName, String email) {
        User currentUser = new User(userName, UID, email);
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDBReference = mDatabase.getReference();
        mDBReference.child("user").child(UID).setValue(currentUser);
    }

}






























