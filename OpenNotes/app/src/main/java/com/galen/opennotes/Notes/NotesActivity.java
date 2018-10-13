package com.galen.opennotes.Notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.galen.opennotes.Notes.content.NoteContentActivity;
import com.galen.opennotes.Notes.content.NoteContentHolder;
import com.galen.opennotes.Notes.content.NoteContentObjects;
import com.galen.opennotes.Notes.notecontentlistrecyvlerview.NotesContentListAdapter;
import com.galen.opennotes.R;
import com.galen.opennotes.wall.WallActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SEND_KEY_TO_CONTENT = "SENDSENDKEY";
    public static final String SEND_SHARE_CODE_TO_CONTENT = "sharecodecodecodeah";
    public static final String SHARE_CODE_TO_WALL="FQWAEFASDFADFAFS";
    public static final String SHARE_TITLE_TO_WALL="ADFAFEFACADS";
    private String mUserName = null;
    private String mUserEmail = null;
    private String mUserId = null;
    private String mShareCode = null;
    private String mCurrentDate = null;
    private String mTitle = null;
    private String mType = null;
    private String mDescription = null;
    private String mKey = null;
    private String mTheCurrentDate, mTheDescription, mTheShareCode, mTheTitle, mTheType;
    private List<NoteContentObjects> mNewNoteContentList = null, mOldNotesContentList = null, mTempNotesCoententlistFromHolder = null, mTheNoteContentList;
    EditText mTitleView, mTypeView, mDescriptionView;
    Button mAddNoteContentButton, mAddShareCodeButton, mWallButton, mGobackButton, mSaveButton,mRefreshButton;
    NotesObject mNotesObjectForNewNotes;
    TextView mCurrentDateView;
    private RecyclerView mRecyclerView;
    private NotesContentListAdapter madapter;
    private String checkIfthereIsAShareCode =null,checkIfthereisTitleForShare=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        //get the key from the notelistview and use it to get data from firebase and put values into textview
        Intent intent = getIntent();
        mKey = intent.getStringExtra(NotesListActivity.SEND_KEY_TO_NEXT_ACTIVITY);

        getUserInfor();

        mTitleView = (EditText) findViewById(R.id.notes_details_title);
        mTypeView = (EditText) findViewById(R.id.notes_details_type);
        mDescriptionView = (EditText) findViewById(R.id.notes_details_description);
        mAddNoteContentButton = (Button) findViewById(R.id.add_notes_content_button);
        mAddShareCodeButton = (Button) findViewById(R.id.share_code_button);
        mWallButton = (Button) findViewById(R.id.wall);
        mGobackButton = (Button) findViewById(R.id.go_back_to_notes_button);
        mSaveButton = (Button) findViewById(R.id.save_button_notes);
        mCurrentDateView = (TextView) findViewById(R.id.current_date);
        mRefreshButton= (Button) findViewById(R.id.refresh_button);
        mWallButton.setOnClickListener(this);
        mRefreshButton.setOnClickListener(this);


        if (mKey != null) {
            mTitleView.setFocusable(false);
            mTypeView.setFocusable(false);
            mDescriptionView.setFocusable(false);

            String databaseReference = mUserName + mUserId;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference(databaseReference);

            mRef.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //get the notesobject from the database
                    //and put all the values of that object into textviews.

                    // sometimes the object doesn't have a value, then we don't want the app crush.
                    try {
                        NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);
                       checkIfthereIsAShareCode= notesObject.getShareCode();
                        checkIfthereisTitleForShare= notesObject.getTitle();


                        mOldNotesContentList = notesObject.getTheNotes();
                        if (mOldNotesContentList == null) {

                        } else {

                            mRecyclerView = (RecyclerView) findViewById(R.id.notecontentlist_recyclerview);
                            madapter = new NotesContentListAdapter(mOldNotesContentList, getApplicationContext(), mKey);
                            LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            mRecyclerView.setLayoutManager(lm);
                            mRecyclerView.setAdapter(madapter);
                        }
                        mTypeView.setText(notesObject.getNotesType());
                        mTitleView.setText(notesObject.getTitle());
                        mDescriptionView.setText(notesObject.getNotesDescription());

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mGobackButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.GONE);

        }

        if (mKey == null) {

            // let the user to add share code  after they create the note object
            // and we don't need refresh here
            mAddShareCodeButton.setVisibility(View.GONE);
            mRefreshButton.setVisibility(View.GONE);
            // when there is input, the go back button will gone and  the save button will show
            mTitleView.addTextChangedListener(new TheTextWatch(mGobackButton, mSaveButton, mDescriptionView, mTypeView));
            mDescriptionView.addTextChangedListener(new TheTextWatch(mGobackButton, mSaveButton, mTitleView, mTypeView));
            mTypeView.addTextChangedListener(new TheTextWatch(mGobackButton, mSaveButton, mTitleView, mDescriptionView));


            String databaseReference = mUserName + mUserId;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(databaseReference);

            // save all the inputs and send it to firebase realtime database
            mSaveButton.setOnClickListener(this);

            // you can not see the wall, when you try to add a new note.
            mWallButton.setVisibility(View.GONE);

        }

        // show the current date in the tool bar
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
        mCurrentDate = df.format(c.getTime());
        mCurrentDateView.setText(mCurrentDate);

        // make the go back button go back to mainactivity
        mGobackButton.setOnClickListener(this);
        // make the go to note content button works and go to noteContent Activity
        mAddNoteContentButton.setOnClickListener(this);
        mAddShareCodeButton.setOnClickListener(this);


    }


    // get user's infor, in order to create database for them
    private void getUserInfor() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            mUserName = user.getDisplayName();
            mUserEmail = user.getEmail();
            mUserId = user.getUid();

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.share_code_button:
                    if (checkIfthereIsAShareCode == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Add the share code");

                        //make a customer layout for dialog
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText input = new EditText(this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                        layout.addView(input);

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

                                mShareCode = input.getText().toString();

                                mTheShareCode = mShareCode;
                                String databaseReference = mUserName + mUserId;
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference ref = database.getReference(databaseReference);
                                ref.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);
                                        try {

                                            mTheCurrentDate = notesObject.getCurrentDate();
                                            mTheDescription = notesObject.getNotesDescription();
                                            mTheTitle = notesObject.getTitle();
                                            mTheType = notesObject.getNotesType();
                                            mTheNoteContentList=notesObject.getTheNotes();

                                        } catch (NullPointerException e) {
                                            e.printStackTrace();

                                        }


                                        NotesObject mUpdateNotesObject = new NotesObject(mUserId, mTheTitle, mTheShareCode, mTheCurrentDate, mTheType, mTheDescription, mTheNoteContentList);

                                        // update the firebase data by pass the new notesobject under the same key.
                                        dataSnapshot.getRef().setValue(mUpdateNotesObject);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                dialog.dismiss();
                            }
                        });
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("You already have the share code, do you want to cancel it?");
                        builder.setPositiveButton("Yes", null);
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                                mShareCode = null;

                                mTheShareCode = mShareCode;
                                String databaseReference = mUserName + mUserId;
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference ref = database.getReference(databaseReference);
                                ref.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);
                                        try {

                                            mTheCurrentDate = notesObject.getCurrentDate();
                                            mTheDescription = notesObject.getNotesDescription();
                                            mTheTitle = notesObject.getTitle();
                                            mTheType = notesObject.getNotesType();

                                        } catch (NullPointerException e) {
                                            e.printStackTrace();

                                        }


                                        NotesObject mUpdateNotesObject = new NotesObject(mUserId, mTheTitle, mTheShareCode, mTheCurrentDate, mTheType, mTheDescription, mTheNoteContentList);

                                        // update the firebase data by pass the new notesobject under the same key.
                                        dataSnapshot.getRef().setValue(mUpdateNotesObject);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                dialog.dismiss();
                            }
                        });

                    }
                break;
            case R.id.save_button_notes:

                mTitle = mTitleView.getText().toString();
                mType = mTypeView.getText().toString();
                mDescription = mDescriptionView.getText().toString();


                mNotesObjectForNewNotes = new NotesObject(mUserId, mTitle, mShareCode, mCurrentDate, mType, mDescription, mNewNoteContentList);

                String databaseReference = mUserName + mUserId;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(databaseReference);

                myRef.push().setValue(mNotesObjectForNewNotes);

                Toast.makeText(this, "Saved! " + mCurrentDate + " " + mShareCode, Toast.LENGTH_SHORT).show();


                finish();
                break;


            case R.id.go_back_to_notes_button:
                finish();
                break;

            case R.id.add_notes_content_button:
                //input strings that send mkey and share code to next activity.
                Intent intent3 = new Intent(NotesActivity.this, NoteContentActivity.class);
                if (mKey != null) {
                    intent3.putExtra(SEND_KEY_TO_CONTENT, mKey);

                }
                if (mShareCode != null) {
                    intent3.putExtra(SEND_SHARE_CODE_TO_CONTENT, mShareCode);
                }
                startActivity(intent3);
                break;

            case R.id.wall:

                if (checkIfthereIsAShareCode==null) {
                    Toast.makeText(this, "You haven't add a share code yet.", Toast.LENGTH_SHORT).show();
                }

                else {
                    Intent intent = new Intent(NotesActivity.this, WallActivity.class);
                    intent.putExtra(SHARE_CODE_TO_WALL,checkIfthereIsAShareCode);
                    intent.putExtra(SHARE_TITLE_TO_WALL,checkIfthereisTitleForShare);
                    startActivity(intent);

                }
                break;

            case R.id.refresh_button:

                String theDatabaseReference = mUserName + mUserId;
                FirebaseDatabase theDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mtheRef = theDatabase.getReference(theDatabaseReference);
                mtheRef.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        NotesObject notesObject =dataSnapshot.getValue(NotesObject.class);
                        checkIfthereIsAShareCode= notesObject.getShareCode();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(this, "Thank you for sharing, you can access to the wall now!", Toast.LENGTH_SHORT).show();
                break;




        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();


        mTempNotesCoententlistFromHolder = NoteContentHolder.getInstance().getmNotesContentList();

        // if  this note don't have notecontentlist, then create one, otherwise, update the list
        if (mOldNotesContentList == null) {


            //if only has one notecontent input which there is not mrecyvlerview yet, then set up the recyvlerview, if more then one ,just notifydatasetchanges
            if (mRecyclerView == null) {


                mNewNoteContentList = mTempNotesCoententlistFromHolder;

                mRecyclerView = (RecyclerView) findViewById(R.id.notecontentlist_recyclerview);
                madapter = new NotesContentListAdapter(mNewNoteContentList, getApplicationContext(), mKey);
                LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(lm);
                mRecyclerView.setAdapter(madapter);

                if(NoteContentHolder.getInstance().getmNotesContentList() !=null) {
                    mSaveButton.setVisibility(View.VISIBLE);
                    mGobackButton.setVisibility(View.GONE);
                }




                // clear the holder after the recyclerview and the new note contentlist are both updated
                NoteContentHolder.getInstance().clearTheNotesContentList();

            } else {

                try {
                    mNewNoteContentList.add(0, mTempNotesCoententlistFromHolder.get(0));
                    madapter.notifyDataSetChanged();
                    // clear the holder after the recyclerview and the new note contentlist are both updated
                    NoteContentHolder.getInstance().clearTheNotesContentList();


                    if(NoteContentHolder.getInstance().getmNotesContentList() !=null) {
                        mSaveButton.setVisibility(View.VISIBLE);
                        mGobackButton.setVisibility(View.GONE);
                    }

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();

                }


            }

        } else if (mOldNotesContentList != null) {


            try {
                mOldNotesContentList.add(0, mTempNotesCoententlistFromHolder.get(0));

                madapter.notifyDataSetChanged();

                // clear the holder after the recyclerview and the new note contentlist are both updated
                NoteContentHolder.getInstance().clearTheNotesContentList();

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            String databaseReference = mUserName + mUserId;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference(databaseReference);

            myRef.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);
                    List<NoteContentObjects> updatedList = notesObject.getTheNotes();

                    if (updatedList == null) {
                        updatedList = new ArrayList<NoteContentObjects>();
                    }

                    madapter = new NotesContentListAdapter(updatedList, getApplicationContext(), mKey);
                    mRecyclerView.setAdapter(madapter);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }
}
