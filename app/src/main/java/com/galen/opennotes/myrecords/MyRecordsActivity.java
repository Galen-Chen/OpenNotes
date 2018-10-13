package com.galen.opennotes.myrecords;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.galen.opennotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyRecordsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SEND_KEY_TO_RECORDING_ACTIVITY= "eedddaadfadfrecording";
    public static final String SEND_TITLE_TO_RECORDING="sendtititle";

    private Button mGobackButton, mRemoveButton, mAddRecordsButton;
    private Button mCancelRemoveButton;
    private String theRecordTitle = null, theRecordType = null, mUserName, mUserEmail, mUserId;
    private double theRecordHours = 0;
    private TheRecordObject mRecordObject;
    TextView mActivitTitle,mTotalHoursView;
    double mTotalHours =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_records);

        mGobackButton = (Button) findViewById(R.id.go_back_to_main_button);
        mCancelRemoveButton = (Button) findViewById(R.id.cancel_theremove_action);
        mRemoveButton = (Button) findViewById(R.id.remove_button_notes);
        mCancelRemoveButton.setOnClickListener(this);
        mGobackButton.setOnClickListener(this);

        mActivitTitle= (TextView) findViewById(R.id.activity_title_view);
        mTotalHoursView= (TextView) findViewById(R.id.total_training_hour_view);


        mActivitTitle.setText("Records");


        //get user info
        getUserInfor();

        // add records by clicking the floatingbutton

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_myrecords_button);
        fab.setOnClickListener(this);


        //get data from firebase and put into the listview by using firebase adapter

        String theDatabaseReference = "records" + mUserId;
        FirebaseDatabase theDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = theDatabase.getReference(theDatabaseReference);

        FirebaseRecyclerAdapter firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<TheRecordObject,MyRecordsViewHolder >(TheRecordObject.class,
                R.layout.myrecords_listview_layout,MyRecordsViewHolder.class,mRef) {

            @Override
            protected void populateViewHolder(MyRecordsViewHolder viewHolder, final TheRecordObject model, final int position) {
                viewHolder.titleView.setText(model.getTheTitle());
                viewHolder.hoursView.setText(model.getHoursTrained().toString());

                mTotalHours += model.getHoursTrained();

                mTotalHoursView.setText(String.valueOf(mTotalHours));
                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //go to recoding activity and send the key too.
                        Intent intent =new Intent(MyRecordsActivity.this,RecordingActivity.class);
                        intent.putExtra(SEND_KEY_TO_RECORDING_ACTIVITY, getRef(position).getKey());
                        intent.putExtra(SEND_TITLE_TO_RECORDING,model.getTheTitle());
                        startActivity(intent);

                    }
                });

                viewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        mGobackButton.setVisibility(View.GONE);
                        mCancelRemoveButton.setVisibility(View.VISIBLE);
                        mRemoveButton.setVisibility(View.VISIBLE);
                        mActivitTitle.setVisibility(View.GONE);

                        mRemoveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(MyRecordsActivity.this);
                                builder.setTitle("Delete");
                                builder.setMessage("Do you want to delete this record entry?");

                                builder.setPositiveButton("Yes", null);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Toast.makeText(MyRecordsActivity.this, "Removed!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        // remove the record in the database

                                        Toast.makeText(getApplicationContext(), "Record is removed!", Toast.LENGTH_SHORT).show();
                                        mRemoveButton.setVisibility(View.GONE);
                                        mCancelRemoveButton.setVisibility(View.GONE);
                                        mGobackButton.setVisibility(View.VISIBLE);

                                        getRef(position).removeValue();



                                    }
                                });


                            }
                        });


                        return true;


                    }
                });
            }
        };



        RecyclerView recycler = (RecyclerView) findViewById(R.id.my_recordslist_listview);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_myrecords_button:

                final AlertDialog.Builder builder = new AlertDialog.Builder(MyRecordsActivity.this);
                builder.setTitle("Add Record");

                //make a customer layout for dialog
                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText titleInput = new EditText(MyRecordsActivity.this);
                titleInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                final EditText typeInput = new EditText(MyRecordsActivity.this);
                typeInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                final EditText hourInput = new EditText(MyRecordsActivity.this);
                hourInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                titleInput.setHint("type title");
                typeInput.setHint("type type");
                hourInput.setHint("how many hours you have been training before for this one? ");


                layout.addView(titleInput);
                layout.addView(typeInput);
                layout.addView(hourInput);

                builder.setView(layout);
                builder.setPositiveButton("Save", null);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add the records to firebase
                        // bugs for null point.
                        if (titleInput.getText().length() <= 0
                                ) {
                            titleInput.setError("please enter");
                        }
                        if (typeInput.getText().length() <= 0
                                ) {
                            typeInput.setError("please enter");
                        }
                        if (hourInput.getText().length() <= 0
                                ) {
                            hourInput.setError("please enter");
                        } else if
                                (titleInput.getText().length() != 0 && typeInput.getText().length() != 0 && hourInput.getText().length() != 0) {
                            theRecordTitle = titleInput.getText().toString();
                            theRecordType = typeInput.getText().toString();
                            theRecordHours = Double.parseDouble(hourInput.getText().toString());

                            //create a new object which will send to firebase
                            mRecordObject = new TheRecordObject(theRecordTitle, theRecordType, theRecordHours);

                            String databaseReference = "records" + mUserId;
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(databaseReference);
                            myRef.push().setValue(mRecordObject);
                            dialog.dismiss();

                        }
                    }
                });
                break;


            case R.id.cancel_theremove_action:
                mRemoveButton.setVisibility(View.GONE);
                mGobackButton.setVisibility(View.VISIBLE);
                mCancelRemoveButton.setVisibility(View.GONE);
                mActivitTitle.setVisibility(View.VISIBLE);

                break;

            case R.id.go_back_to_main_button:
                finish();
                break;


        }

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
