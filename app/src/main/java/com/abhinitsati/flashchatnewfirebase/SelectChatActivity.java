package com.abhinitsati.flashchatnewfirebase;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SelectChatActivity extends AppCompatActivity {

    private EditText mEmailText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);

        // get the firebase instance
        mAuth = FirebaseAuth.getInstance();

        // link up the email view
        mEmailText = findViewById(R.id.chat_email);
        mEmailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                moveToChat();
                return true;
            }
        });

    }

    private void moveToChat(){

        String email = mEmailText.getText().toString();

        if (email.equals("")
                || !isEmailValid(email)
                || email.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
            return;

        // check if the email entered is valid
        mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                boolean check = Objects.requireNonNull(task.getResult().getProviders()).isEmpty();

                if (!check)
                    moveToMainChat();
                else
                    showErrorDialog();
            }
        });

    }

    private boolean isEmailValid(String email){

        // match the email regex
        Pattern p = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
        Matcher m = p.matcher(email);
        return m.find();
    }

    private void showErrorDialog(){

        // show an alert message to the user
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(R.string.no_such_user)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void moveToMainChat(){

        Intent intent = new Intent(SelectChatActivity.this, MainChatActivity.class);
        // send the email ids to the MainChatActivity
        // email is the email address of the user we want to chat with
        String email = mEmailText.getText().toString();
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
