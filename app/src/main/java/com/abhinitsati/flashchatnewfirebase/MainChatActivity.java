package com.abhinitsati.flashchatnewfirebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainChatActivity extends AppCompatActivity {

    private String mLocation;  // location of the messages of this pair in the DB
    private String mUserMe;    // my email id
    private ListView mChatListView;
    private EditText mInputText;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // generate the chat messages location on the DB
        generateLocation();

        // get the Firebase reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = findViewById(R.id.messageInput);
        ImageButton sendButton = findViewById(R.id.sendButton);
        mChatListView = findViewById(R.id.chat_list_view);

        // Send the message when the "enter" button is pressed on the soft keyboard
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == R.integer.register_form_finished || actionId == EditorInfo.IME_NULL) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        // OnClickListener to the sendButton to send a message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void generateLocation(){

        // get the email string of the other user from the intent
        String otherUser = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            otherUser = bundle.getString("email");
            // email id of the other user
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        mUserMe = user.getEmail();

        // join the two email strings alphabetically to create a DB location
        assert mUserMe != null;
        assert otherUser != null;

        String location = "";
        if (mUserMe.compareTo(otherUser) < 0)
            location = mUserMe + otherUser;
        else
            location = otherUser + mUserMe;

        // use a hash for both emails
        // convert it to a nice hex value
        mLocation = make_hash(location);
    }

    private String make_hash(String location){

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(location.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void sendMessage() {

        // Grab the text the user typed in and push the message to Firebase
        Log.d("FlashChat", "I sent something");
        // get the message the user typed
        String userInput = mInputText.getText().toString();

        if (! (userInput.length() == 0)){

            InstantMessage message = new InstantMessage(userInput, mUserMe);
            // push the message to Firebase DB
            mDatabaseReference.child(mLocation).push().setValue(message);
            mInputText.setText("");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // init the adapter here
        mAdapter = new ChatListAdapter(this, mDatabaseReference, mUserMe,
                                        mLocation);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){

        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();

        // for checking for events from the DB.
        mAdapter.cleanup();
    }

}
