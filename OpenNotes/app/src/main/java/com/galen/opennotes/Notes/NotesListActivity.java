package com.galen.opennotes.Notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.galen.opennotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class NotesListActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SEND_KEY_TO_NEXT_ACTIVITY = "KEYKEYKEYSSSSSKEYS";
    private TextView mTestView,mActivitTitle,mDateCreatedVeiw;
    private String mUserName, mUserId, mUserEmail;

    private Button mCancelRemoveImage;
    private Button mGoBackToMainButton, mRemoveNoteButton;
    private FirebaseListAdapter<NotesObject> mFirebaseAdapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_notes_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(NotesListActivity.this, NotesActivity.class);
                startActivity(intent);

            }
        });
        mCancelRemoveImage = (Button) findViewById(R.id.cancel_theremove_action);
        mGoBackToMainButton = (Button) findViewById(R.id.go_back_to_main_button);
        mRemoveNoteButton = (Button) findViewById(R.id.remove_button_notes);
        mGoBackToMainButton.setOnClickListener(this);
        mRemoveNoteButton.setOnClickListener(this);
        mCancelRemoveImage.setOnClickListener(this);
        mActivitTitle= (TextView) findViewById(R.id.activity_title_view);

        mActivitTitle.setText("Notes");


        //get the firebase databse
        getUserInfor();

        String databaseReference = mUserName + mUserId;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(databaseReference);


        // set the recyclerview
        FirebaseRecyclerAdapter firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<NotesObject,NotesListRecyclerView>(NotesObject.class,R.layout.notes_list_recyclerview,
                NotesListRecyclerView.class,myRef) {

            @Override
            protected void populateViewHolder(NotesListRecyclerView viewHolder, NotesObject model, final int position) {

                viewHolder.dateView.setText(model.getCurrentDate());
                viewHolder.titleView.setText(model.getTitle());
                viewHolder.typeView.setText(model.getNotesType());

                try {
                    Picasso.with(viewHolder.imageView.getContext()).load(Uri.parse(model.getTheNotes().get(0).getImageUrl())).into(viewHolder.imageView);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send key to noteACTIVITY
                        Intent intent = new Intent(NotesListActivity.this, NotesActivity.class);
                        intent.putExtra(SEND_KEY_TO_NEXT_ACTIVITY, getRef(position).getKey());


                        startActivity(intent);

                    }
                });
                viewHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mGoBackToMainButton.setVisibility(View.GONE);
                        mCancelRemoveImage.setVisibility(View.VISIBLE);
                        mRemoveNoteButton.setVisibility(View.VISIBLE);
                        mActivitTitle.setVisibility(View.GONE);

                        mRemoveNoteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(NotesListActivity.this);
                                builder.setTitle("Delete");
                                builder.setMessage("Do you want to delete this note entry?");

                                builder.setPositiveButton("Yes", null);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        mRemoveNoteButton.setVisibility(View.GONE);
                                        mCancelRemoveImage.setVisibility(View.GONE);
                                        mGoBackToMainButton.setVisibility(View.VISIBLE);
                                        mActivitTitle.setVisibility(View.VISIBLE);
                                    }
                                });

                                final AlertDialog dialog = builder.create();
                                dialog.show();

                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Toast.makeText(NotesListActivity.this, "Removed!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        // remove the notes in the database

                                        getRef(position).removeValue();
                                        Toast.makeText(getApplicationContext(), "Note is removed!", Toast.LENGTH_SHORT).show();
                                        mRemoveNoteButton.setVisibility(View.GONE);
                                        mCancelRemoveImage.setVisibility(View.GONE);
                                        mGoBackToMainButton.setVisibility(View.VISIBLE);
                                        mActivitTitle.setVisibility(View.VISIBLE);

                                    }
                                });


                            }
                        });


                        return true;



                    }
                });



            }
        };

        RecyclerView recycler = (RecyclerView) findViewById(R.id.note_list_listview);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(firebaseRecyclerAdapter);

    }

    // get user's infor, in order to get database from firebase
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
            case R.id.go_back_to_main_button:
                finish();
                break;

            case R.id.cancel_theremove_action:
                mRemoveNoteButton.setVisibility(View.GONE);
                mGoBackToMainButton.setVisibility(View.VISIBLE);
                mCancelRemoveImage.setVisibility(View.GONE);
                mActivitTitle.setVisibility(View.VISIBLE);
                break;


        }
    }

}


