package com.abhinitsati.flashchatnewfirebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v) {
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {

        // get the email and password
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // check to see if the fields are blank
        if (email.length() == 0 || password.length() == 0)
            return;

        /*
        if (!LoginActivity.this.isFinishing()) {
            // show the user that login is in progress
            Toast.makeText(getApplicationContext(), "Login in Progress..", Toast.LENGTH_SHORT).show();
            // sign in
        }
        */

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                boolean success = task.isSuccessful();
                Log.d("FlashChat", String.valueOf(success));

                if (!success){
                    showErrorDialog();
                }
                else{
                    // sign in was successful
                    // take the user to the main chat activity
                    Intent intent = new Intent(LoginActivity.this, SelectChatActivity.class);
                    // send the email id to the MainChatActivity
                    String email = mEmailView.getText().toString();
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });

    }

    private void showErrorDialog(){

        // show an alert message to the user
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(R.string.login_failed)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
