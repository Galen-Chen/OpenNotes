package com.galen.opennotes.Notes.content;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galen.opennotes.Notes.NotesActivity;
import com.galen.opennotes.Notes.NotesObject;
import com.galen.opennotes.Notes.notecontentlistrecyvlerview.NotesContentListAdapter;
import com.galen.opennotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class NoteContentActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, TagsRecyclerViewAdapter.OnItemClickLisnter {
    public static final int CAMERA_REQUEST = 1233;
    public static final int PHOTO_REQUEST = 11232;
    public static final String THE_WAL_REF_CODE = "GOTOTHEWALL";
    private String mUserName, mUserEmail, mUSERId;
    private String mTheUserId = null, mTheTitle = null, mTheShareCode = null, mTheCurrentDate = null, mTheType = null, mTheDescription = null, mImageUri = null, theImageUril = null;
    ;
    private List<NoteContentObjects> mTheNoteContentList = null;

    ImageView mImageView;
    TextView mTimeView;
    EditText mNotesContentInputView, mTagEditext;
    Button mGobackButton, mPrivatePublicButton, mLocationButton, mAddImageButton, mGetWeatherButton, mSaveButton, mTagSaveButton, mDeleteButton;
    String mContentInput, mKey = null, mCurrentDate, mshareCode = null;
    List<NoteContentObjects> mContentlist;
    NoteContentObjects mObject;
    Bitmap mBitmap = null;
    StorageReference mStorageRef;
    //    private Uri mImageUri = null, theImageUril = null;
    Boolean ispublic = false, zoomOut = false;
    List<TagObject> mTags;
    List<String> mTagList;
    private RecyclerView mRecyclerView;
    private TagsRecyclerViewAdapter mAdapter;
    TagObject newTagObject;
    private DatabaseReference mRef;
    private FirebaseDatabase mDatabase;
    private NotesObject mUpdateNotesObject = null;
    private String theKeyForEditing;
    private int thePostionForEditing;
    private String mDatabaseReference;
    private NoteContentObjects mNeedUpdateNoteContentObject;
    private String theGotoWallRefCode, getTheSharecodeFromCloud, getTheShareNoteTitleFromCloud;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_content);
        getUserInfor();

        mNotesContentInputView = (EditText) findViewById(R.id.notes_content_thenotes);
        mAddImageButton = (Button) findViewById(R.id.add_image_notes_content_toolbar);
        mPrivatePublicButton = (Button) findViewById(R.id.set_public_button_content_notes);
        mGobackButton = (Button) findViewById(R.id.go_back_in_content_notes);
        mSaveButton = (Button) findViewById(R.id.save_button_content_notes);
        mImageView = (ImageView) findViewById(R.id.image_for_content);
        mTagEditext = (EditText) findViewById(R.id.add_tag_edittext);
        mTagSaveButton = (Button) findViewById(R.id.save_tag_button);
        mDeleteButton = (Button) findViewById(R.id.delete_content_button);
        mTimeView = (TextView) findViewById(R.id.current_date_in_content_notes);

        mImageView.setOnClickListener(this);


        //get the key for edit. and the postion for the noteconent.
        Intent intentEdit = getIntent();
        theKeyForEditing = intentEdit.getStringExtra(NotesContentListAdapter.SEND_KEY_TO_CONTENT_FROM_NOTECONTENTLIST);
        thePostionForEditing = intentEdit.getIntExtra(NotesContentListAdapter.SEND_POSTION_TO_CONTENT, -1);

        //set up recyclerview for tags
        mRecyclerView = (RecyclerView) findViewById(R.id.tags_recyvlerview_notecontent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        // try to get all the data first

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference(mDatabaseReference);


            mRef.child(theKeyForEditing).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);

                    try {
                        mTheUserId = notesObject.getUserID();
                        mTheCurrentDate = notesObject.getCurrentDate();
                        mTheDescription = notesObject.getNotesDescription();
                        mTheNoteContentList = notesObject.getTheNotes();
                        mTheShareCode = notesObject.getShareCode();
                        mTheTitle = notesObject.getTitle();
                        mTheType = notesObject.getNotesType();

                        mNeedUpdateNoteContentObject = mTheNoteContentList.get(thePostionForEditing);


                    } catch (NullPointerException e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //get the current time

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


        mCurrentDate = df.format(c.getTime());

        mTimeView.setText(mCurrentDate);


        // if the activity is not from the an existing contentlist, the keyforedited is null
        if (theKeyForEditing == null) {

            //if the notescontent is start from a estiblisht notes activity, there will be a key
            // if there is a key, get all the values from database, if the key is null, the user can edit the notes.

            Intent intent = getIntent();
            mKey = intent.getStringExtra(NotesActivity.SEND_KEY_TO_CONTENT);
            mshareCode = intent.getStringExtra(NotesActivity.SEND_SHARE_CODE_TO_CONTENT);
            mAddImageButton.setOnClickListener(this);
            mGobackButton.setOnClickListener(this);
            // user can not set public here ,they have to create a note object first.
            mPrivatePublicButton.setVisibility(View.GONE);

            mTagList = new ArrayList<>();

            //set up the recyclerveiw for tags
//            mRecyclerView = (RecyclerView) findViewById(R.id.tags_recyvlerview_notecontent);
            mTags = new ArrayList<>();
            EditText firstTagEditext = (EditText) findViewById(R.id.add_tag_edittext);
            Button firstSaveButton = (Button) findViewById(R.id.save_tag_button);
            newTagObject = new TagObject(firstTagEditext, firstSaveButton);
            mTags.add(0, newTagObject);

            mAdapter = new TagsRecyclerViewAdapter(mTags, this);
            mRecyclerView.setAdapter(mAdapter);


            if (mKey == null) {
                // if the user start to type something , change the go back button to save button to secure their inputs.
                mNotesContentInputView.addTextChangedListener(this);

                mSaveButton.setOnClickListener(this);

//                if(NoteContentHolder.getInstance().getmNotesContentList() !=null) {
//                    mSaveButton.setVisibility(View.VISIBLE);
//                    mGobackButton.setVisibility(View.GONE);
//                }


            }

            if (mKey != null) {

                mNotesContentInputView.addTextChangedListener(this);
                mSaveButton.setOnClickListener(this);
            }
        }

        //else if this activity is started from existing notecontentlist, it will do following:
        //get the notecontent details and put value in the edittextview

        else {
            mPrivatePublicButton.setOnClickListener(this);

            //show the deltebutton
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setOnClickListener(this);
            mGobackButton.setOnClickListener(this);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference(mDatabaseReference);

            mRef.child(theKeyForEditing).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);

                    mTheNoteContentList = notesObject.getTheNotes();

                    // get the share code , use it when make the notes public.
                    try {
                        getTheSharecodeFromCloud = notesObject.getShareCode();
                        getTheShareNoteTitleFromCloud = notesObject.getTitle();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                    mNeedUpdateNoteContentObject = notesObject.getTheNotes().get(thePostionForEditing);
                    mNotesContentInputView.setText(mNeedUpdateNoteContentObject.getNotesYouWrite());


                    try {
                        mImageUri = mNeedUpdateNoteContentObject.getImageUrl();
                        Picasso.with(mImageView.getContext()).load(Uri.parse(mImageUri)).into(mImageView);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    mNotesContentInputView.addTextChangedListener(NoteContentActivity.this);

                    mSaveButton.setOnClickListener(NoteContentActivity.this);


                    //show the tags in recyclerview


                    mTagList = new ArrayList<String>();
//

                    mTagList = mNeedUpdateNoteContentObject.getTags();

                    if (mTagList != null) {

                        TagStringAdatper tagStringAdatper = new TagStringAdatper(mTagList);

                        mRecyclerView.setAdapter(tagStringAdatper);


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.length() != 0) {
            mGobackButton.setVisibility(View.GONE);
            mSaveButton.setVisibility(View.VISIBLE);
        } else if (s.length() == 0) {
            mGobackButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            mGobackButton.setVisibility(View.VISIBLE);
            mSaveButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image_notes_content_toolbar:
//
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), PHOTO_REQUEST);


                break;
            case R.id.set_public_button_content_notes:

                if (!ispublic) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Do you want to share this note?");

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

                            if (getTheSharecodeFromCloud == null || getTheShareNoteTitleFromCloud == null) {
                                Toast.makeText(NoteContentActivity.this, "Sorry, You don't have the share code", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if (getTheSharecodeFromCloud != null && getTheShareNoteTitleFromCloud != null) {

                                // create a new database for all shared notecontes, the databasereference is the title pluse the sharecode
                                String databaseReference = getTheShareNoteTitleFromCloud + getTheSharecodeFromCloud;
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference(databaseReference);

                                String theShareNotesYourWirte = mNotesContentInputView.getText().toString();

                                ispublic = true;

                                if (mImageUri != null) {
                                    theImageUril = mImageUri;
                                }

                                NoteContentObjects noteGoToTheWall = new NoteContentObjects(mUserName, getTheShareNoteTitleFromCloud, mCurrentDate, theShareNotesYourWirte, theImageUril);
                                myRef.push().setValue(noteGoToTheWall);
                                Toast.makeText(NoteContentActivity.this, "Great! You share your notes!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }
                        }

                    });

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Do you want to Unshare this note?");

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


                            ispublic = false;
                            Toast.makeText(NoteContentActivity.this, "No one will see your note now!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    });

                }
                break;

            case R.id.save_button_content_notes:
                mContentInput = mNotesContentInputView.getText().toString();

                mStorageRef = FirebaseStorage.getInstance().getReference();

                if (theKeyForEditing == null) {

                    if (mKey == null) {

                        // fix herer
                        //add image
                        if (mBitmap != null) {


                            mStorageRef = FirebaseStorage.getInstance().getReference();

                            Random rand = new Random();
                            int randomNumber = rand.nextInt(999);
                            String imageName = mUserName + randomNumber;
                            StorageReference imageRef = mStorageRef.child(imageName);


                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageData = byteArrayOutputStream.toByteArray();

                            UploadTask uploadTask = imageRef.putBytes(imageData);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    exception.printStackTrace();
                                    Toast.makeText(NoteContentActivity.this, "oh no, fails, why?", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @SuppressWarnings("VisibleForTests")
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                    mImageUri = taskSnapshot.getDownloadUrl().toString();

                                    Toast.makeText(NoteContentActivity.this, "" + mImageUri, Toast.LENGTH_SHORT).show();

                                    NoteContentObjects noteContentObjects = new NoteContentObjects(mUserName, mshareCode, mCurrentDate, mContentInput, ispublic, mTagList, mImageUri);

                                    NoteContentHolder.getInstance().addNotesContentObject(noteContentObjects);
                                    Toast.makeText(NoteContentActivity.this, "" + noteContentObjects.getNotesYouWrite(), Toast.LENGTH_SHORT).show();


                                    finish();

                                }
                            });

                        } else {


                            NoteContentObjects noteContentObjects = new NoteContentObjects(mUserName, mshareCode, mCurrentDate, mContentInput, ispublic, mTagList, mImageUri);

                            NoteContentHolder.getInstance().addNotesContentObject(noteContentObjects);
                            Toast.makeText(this, "" + noteContentObjects.getNotesYouWrite(), Toast.LENGTH_SHORT).show();


                            finish();
                        }
                    }

                    if (mKey != null) {

                        // see if there is a image

                        if (mBitmap != null) {

                            mStorageRef = FirebaseStorage.getInstance().getReference();

                            Random rand = new Random();
                            int randomNumber = rand.nextInt(999);
                            String imageName = mUserName + randomNumber;
                            StorageReference imageRef = mStorageRef.child(imageName);


                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            byte[] imageData = byteArrayOutputStream.toByteArray();

                            UploadTask uploadTask = imageRef.putBytes(imageData);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    exception.printStackTrace();
                                    Toast.makeText(NoteContentActivity.this, "oh no, fails, why?", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @SuppressWarnings("VisibleForTests")
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                                    mImageUri = taskSnapshot.getDownloadUrl().toString();

                                    Toast.makeText(NoteContentActivity.this, "" + mImageUri, Toast.LENGTH_SHORT).show();

                                    final NoteContentObjects noteContentObjectsNeedToUpdate = new NoteContentObjects(mUserName, mshareCode, mCurrentDate, mContentInput, ispublic, mTagList, mImageUri);

                                    NoteContentHolder.getInstance().addNotesContentObject(noteContentObjectsNeedToUpdate);


                                    // fix this later, for reference direct to the child

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference mRef = database.getReference(mDatabaseReference);


                                    mRef.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);

                                            try {
                                                mTheUserId = notesObject.getUserID();
                                                mTheCurrentDate = notesObject.getCurrentDate();
                                                mTheDescription = notesObject.getNotesDescription();
                                                mTheNoteContentList = notesObject.getTheNotes();
                                                mTheShareCode = notesObject.getShareCode();
                                                mTheTitle = notesObject.getTitle();
                                                mTheType = notesObject.getNotesType();


                                            } catch (NullPointerException e) {
                                                e.printStackTrace();

                                            }

                                            if (mTheNoteContentList == null) {
                                                mTheNoteContentList = new ArrayList<NoteContentObjects>();
                                            }
//
                                            mTheNoteContentList.add(0, noteContentObjectsNeedToUpdate);

                                            mUpdateNotesObject = new NotesObject(mTheUserId, mTheTitle, mTheShareCode, mTheCurrentDate, mTheType, mTheDescription, mTheNoteContentList);


                                            // update the firebase data by pass the new notesobject under the same key.
                                            dataSnapshot.getRef().setValue(mUpdateNotesObject);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    finish();


                                }
                            });

                            // if mKey isn't null, it will update the firebase database for this notes.

                        } else {


                            // if mKey isn't null, it will update the firebase database for this notes.
                            final NoteContentObjects noteContentObjectsNeedToUpdate = new NoteContentObjects(mUserName, mshareCode, mCurrentDate, mContentInput, ispublic, mTagList, mImageUri);

                            NoteContentHolder.getInstance().addNotesContentObject(noteContentObjectsNeedToUpdate);


                            // fix this later, for reference direct to the child

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference mRef = database.getReference(mDatabaseReference);


                            mRef.child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);

                                    try {
                                        mTheUserId = notesObject.getUserID();
                                        mTheCurrentDate = notesObject.getCurrentDate();
                                        mTheDescription = notesObject.getNotesDescription();
                                        mTheNoteContentList = notesObject.getTheNotes();
                                        mTheShareCode = notesObject.getShareCode();
                                        mTheTitle = notesObject.getTitle();
                                        mTheType = notesObject.getNotesType();


                                    } catch (NullPointerException e) {
                                        e.printStackTrace();

                                    }

                                    if (mTheNoteContentList == null) {
                                        mTheNoteContentList = new ArrayList<NoteContentObjects>();
                                    }
//
                                    mTheNoteContentList.add(0, noteContentObjectsNeedToUpdate);

                                    mUpdateNotesObject = new NotesObject(mTheUserId, mTheTitle, mTheShareCode, mTheCurrentDate, mTheType, mTheDescription, mTheNoteContentList);


                                    // update the firebase data by pass the new notesobject under the same key.
                                    dataSnapshot.getRef().setValue(mUpdateNotesObject);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            finish();
                        }
                    }


                } else if (theKeyForEditing != null) {


                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference Ref = database.getReference(mDatabaseReference).child(theKeyForEditing).child("theNotes").child(String.valueOf(thePostionForEditing));

                    mContentInput = mNotesContentInputView.getText().toString();
                    mNeedUpdateNoteContentObject.setNotesYouWrite(mContentInput);
                    Ref.setValue(mNeedUpdateNoteContentObject);

                    finish();


                }


                break;

            case R.id.delete_content_button:


                final AlertDialog.Builder builder = new AlertDialog.Builder(NoteContentActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete this note entry?");

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


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference Ref = database.getReference(mDatabaseReference);

                        Ref.child(theKeyForEditing).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                NotesObject notesObject = dataSnapshot.getValue(NotesObject.class);
                                try {
                                    mTheUserId = notesObject.getUserID();
                                    mTheCurrentDate = notesObject.getCurrentDate();
                                    mTheDescription = notesObject.getNotesDescription();
                                    mTheShareCode = notesObject.getShareCode();
                                    mTheTitle = notesObject.getTitle();
                                    mTheType = notesObject.getNotesType();

                                } catch (NullPointerException e) {
                                    e.printStackTrace();

                                }
                                mNeedUpdateNoteContentObject.setNotesYouWrite(mContentInput);
                                mTheNoteContentList.remove(thePostionForEditing);
                                mUpdateNotesObject = new NotesObject(mTheUserId, mTheTitle, mTheShareCode, mTheCurrentDate, mTheType, mTheDescription, mTheNoteContentList);

                                // update the firebase data by pass the new notesobject under the same key.
                                dataSnapshot.getRef().setValue(mUpdateNotesObject);


                                // delete the image in the firebase Storage if there is image.
                                if (mImageUri != null) {
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference httpsReference = storage.getReferenceFromUrl(mImageUri);
                                    httpsReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(NoteContentActivity.this, "Image deleted from cloud", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                finish();


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });


                break;


            case R.id.go_back_in_content_notes:

                finish();
                break;


            case R.id.image_for_content:
                // when click on the image, it go to full screen //  maybe not work ! does not go to full screen
                if (zoomOut) {
                    Toast.makeText(getApplicationContext(), "NORMAL SIZE!", Toast.LENGTH_LONG).show();
                    mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    mImageView.setAdjustViewBounds(true);
                    zoomOut = false;
                } else {
                    Toast.makeText(getApplicationContext(), "FULLSCREEN!", Toast.LENGTH_LONG).show();
                    mImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    zoomOut = true;
                }
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST) {

            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                mImageView.setImageBitmap(mBitmap);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }


        }
    }


    private void getUserInfor() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            mUserName = user.getDisplayName();
            mUserEmail = user.getEmail();
            mUSERId = user.getUid();

            mDatabaseReference = mUserName + mUSERId;

        }
    }

    @Override
    public void saveTags() {
        mTagEditext = (EditText) findViewById(R.id.add_tag_edittext);
        String tag = mTagEditext.getText().toString();
        mTagList.add(tag);
        mTags.add(0, newTagObject);
        mAdapter.notifyDataSetChanged();

    }
}
