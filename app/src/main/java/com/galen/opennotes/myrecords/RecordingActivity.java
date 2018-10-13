package com.galen.opennotes.myrecords;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.galen.opennotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {
    private String mKey,mUserName,mUserEmail,mUserId,mTitle;


    private static final int MSG_START_TIMER = 0;
    private static final int MSG_STOP_TIMER = 1;
    private static final int MSG_UPDATE_TIMER = 2;
    private static final int REFRESH_RATE = 100;
    TextView timeViewTextView, titleTv,hoursTv,mActivitTitle;
    Button btnStart, btnStop,btnUpdate,mgobackButton;
    boolean isRuning = false;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    Double oldHours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);


        Intent intent = getIntent();
        mKey = intent.getStringExtra(MyRecordsActivity.SEND_KEY_TO_RECORDING_ACTIVITY);
        mTitle=intent.getStringExtra(MyRecordsActivity.SEND_TITLE_TO_RECORDING);


        timeViewTextView = (TextView) findViewById(R.id.text_time);
      //  titleTv= (TextView) findViewById(R.id.practicing_this_title);
        hoursTv= (TextView) findViewById(R.id._this_hours);

        btnStart = (Button) findViewById(R.id.start_button);
        btnStop = (Button) findViewById(R.id.stop_button);
        btnUpdate= (Button) findViewById(R.id.update_hours_to_firebase_button);
        mgobackButton= (Button) findViewById(R.id.go_back_to_main_button);

        mActivitTitle= (TextView) findViewById(R.id.activity_title_view);

        mActivitTitle.setText(mTitle);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        mgobackButton.setOnClickListener(this);


        //get the data from the firebase by calling the key
        getUserInfor();


        String theDatabaseReference = "records" + mUserId;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(theDatabaseReference).child(mKey);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TheRecordObject theRecordObject = dataSnapshot.getValue(TheRecordObject.class);

                Double hours= theRecordObject.getHoursTrained();


                hoursTv.setText(hours.toString()+" hours");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    Stopwatch timer = new Stopwatch();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_START_TIMER:

                    timer.start();
                    isRuning = true;
                    mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    timeViewTextView.setText("" + formatInterval(timer.getElapsedTime()));
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE);
                    break;
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER);
                    timer.stop();
                    isRuning = false;
                    timeViewTextView.setText("" + formatInterval(timer.getElapsedTime()));


                    break;

                default:
                    break;
            }
        }
    };


    public void onClick(View v) {

        if (btnStart == v) {
            if (isRuning == false) {
                mHandler.sendEmptyMessage(MSG_START_TIMER);
                btnStart.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                timeViewTextView.setVisibility(View.VISIBLE);

            } else {
            }
        } else if (btnStop == v) {

            mHandler.sendEmptyMessage(MSG_STOP_TIMER);
            btnUpdate.setVisibility(View.VISIBLE);
            btnStop.setVisibility(View.GONE);



        }

        switch (v.getId()){
            case R.id.update_hours_to_firebase_button:
                btnUpdate.setVisibility(View.GONE);
                btnStart.setVisibility(View.VISIBLE);
                timeViewTextView.setVisibility(View.GONE);

               final double hours= Double.parseDouble(String.valueOf(timer.getElapsedTime()/(60*60*1000)));

                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TheRecordObject theRecordObject = dataSnapshot.getValue(TheRecordObject.class);

                        oldHours= theRecordObject.getHoursTrained();

                        double newHours= oldHours+hours;

                        mRef.child("hoursTrained").setValue(newHours);
                        // make the updatebutotn gone after updated.
                        btnUpdate.setVisibility(View.GONE);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                break;
            case R.id.go_back_to_main_button:

                if (btnUpdate.getVisibility()==View.VISIBLE) {
                    Toast.makeText(this, "Do you want to update your practice time first? ", Toast.LENGTH_SHORT).show();
                }
                else finish();
                break;

        }
    }

    private static String formatInterval(final long l) {
        final long hr = TimeUnit.MILLISECONDS.toHours(l);
        final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
        final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
        final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
    }


    private void getUserInfor() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            mUserName = user.getDisplayName();
            mUserEmail = user.getEmail();
            mUserId = user.getUid();

        }
    }


}