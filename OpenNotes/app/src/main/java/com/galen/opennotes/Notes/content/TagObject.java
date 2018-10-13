package com.galen.opennotes.Notes.content;

import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Galen on 5/19/17.
 */

public class TagObject {
    EditText mEditText;
    Button mSaveButton;

    public TagObject(EditText mEditText, Button mSaveButton) {
        this.mEditText = mEditText;
        this.mSaveButton = mSaveButton;
    }

    public EditText getmEditText() {
        return mEditText;
    }

    public void setmEditText(EditText mEditText) {
        this.mEditText = mEditText;
    }

    public Button getmSaveButton() {
        return mSaveButton;
    }

    public void setmSaveButton(Button mSaveButton) {
        this.mSaveButton = mSaveButton;
    }
}
