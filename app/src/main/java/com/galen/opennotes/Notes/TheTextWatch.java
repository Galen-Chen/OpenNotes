package com.galen.opennotes.Notes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Galen on 5/18/17.
 */



// create a text watcher method to watch all the edittextview, if there is input, the go back button will gone and save button will show.

public class TheTextWatch implements TextWatcher {

    Button buttonStarted,buttonLater;
    EditText editText1,editText2;


    public TheTextWatch(Button buttonStarted, Button buttonLater, EditText editText1, EditText editText2) {
        this.buttonStarted = buttonStarted;
        this.buttonLater = buttonLater;
        this.editText1 = editText1;
        this.editText2 = editText2;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if(s.length()!=0) {
            buttonStarted.setVisibility(View.GONE);
            buttonLater.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length()==0&&editText1.length()==0&&editText2.length()==0) {
            buttonLater.setVisibility(View.GONE);
            buttonStarted.setVisibility(View.VISIBLE);
        }


    }
}
