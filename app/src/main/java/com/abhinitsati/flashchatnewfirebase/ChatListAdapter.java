package com.abhinitsati.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter{

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mEmail;  // email of the user that is viewing these messages
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            // when a new message is added to the DB
            // add the message to the end of the array
            mSnapshotList.add(dataSnapshot);
            notifyDataSetChanged(); // notify the ListView to update itself
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChatListAdapter(Activity activity, DatabaseReference databaseReference,
                    String email, String DBLocation) {
        mActivity = activity;
        mEmail = email;

        // database reference to the messages location
        // this is the location where all messages are stored
        mDatabaseReference = databaseReference.child(DBLocation);

        // add a listener to the DatabaseRef
        // add a listener to detect changes made to the Database
        mDatabaseReference.addChildEventListener(mEventListener);
        mSnapshotList = new ArrayList<>();  // hold all the messages
    }

    static class ViewHolder{

        // hold the chat message that is composed of two TextViews
        // hence the name ViewHolder
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {

        DataSnapshot snapshot = mSnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // convert view represents a list item
        if (convertView == null){

            // create a new row
            // create a view from the layout xml file
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // use the chat_msg_row.xml
            assert inflater != null;
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = convertView.findViewById(R.id.author);
            holder.body = convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            convertView.setTag(holder);
        }

        final InstantMessage message = getItem(position);
        // retrieve the ViewHolder that we saved in convertView
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        // check to see if the author of the message is you
        // or someone else
        boolean isItMe = message.getAuthor().equals(mEmail);
        setChatRowAppearance(isItMe, holder);

        // get new messages
        holder.authorName.setText(message.getAuthor());
        holder.body.setText(message.getMessage());


        return convertView;
    }

    // differentiate between ur own and other peoples chat messages
    private void setChatRowAppearance(boolean isItMe, ViewHolder holder){

        if (isItMe){
            // align the message bubble to right
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            // cover the view with a speech bubble
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }
        else{
            // align the message bubble to the left
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            // cover the view with a speech bubble
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanup(){

        // free up resources no longer in use
        mDatabaseReference.removeEventListener(mEventListener);
    }
}
