package com.galen.opennotes.wall;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.galen.opennotes.Notes.NotesActivity;
import com.galen.opennotes.Notes.content.NoteContentObjects;
import com.galen.opennotes.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WallActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGobackButton, mShareButton, mRefreshButton;
    private TextView mDateView;
    private String mShareCode,mShareTitle, mDatabaseReference;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        mGobackButton = (Button) findViewById(R.id.go_back_to_notes_button);
        mShareButton = (Button) findViewById(R.id.share_code_button);
        mRefreshButton = (Button) findViewById(R.id.refresh_button);
        mDateView = (TextView) findViewById(R.id.current_date);
        mGobackButton.setOnClickListener(this);


        mShareButton.setVisibility(View.GONE);
        mRefreshButton.setVisibility(View.GONE);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
        String mCurrentDate = df.format(c.getTime());


        Intent intent = getIntent();
        mShareCode = intent.getStringExtra(NotesActivity.SHARE_CODE_TO_WALL);
        mShareTitle=intent.getStringExtra(NotesActivity.SHARE_TITLE_TO_WALL);

        mDateView.setText(mShareTitle);

         mDatabaseReference=mShareTitle+mShareCode;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(mDatabaseReference);

        FirebaseRecyclerAdapter firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<NoteContentObjects,WallViewHolder>(NoteContentObjects.class,R.layout.wall_list_layout,WallViewHolder.class,myRef) {

            @Override
            protected void populateViewHolder(WallViewHolder viewHolder, NoteContentObjects model, int position) {
                viewHolder.theNoteView.setText(model.getNotesYouWrite());
                viewHolder.datecreateView.setText(model.getTimeCreated());
                viewHolder.userNamerView.setText(model.getUserName());

                try {
                    Picasso.with(viewHolder.imageView.getContext()).load(Uri.parse(model.getImageUrl())).into(viewHolder.imageView);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        };

        RecyclerView recycler = (RecyclerView) findViewById(R.id.wall_notes_listview);
        recycler.setHasFixedSize(false);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back_to_notes_button:
                finish();
        }
    }
}
