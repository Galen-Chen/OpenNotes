package com.galen.opennotes.Notes.content;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Galen on 5/19/17.
 */


//create singleton to pass notecontent to notes acitivity.
public class NoteContentHolder {
    private static NoteContentHolder sInstance;

    private List<NoteContentObjects> mNotesContentList;

    private NoteContentHolder() {
        mNotesContentList = new ArrayList<>();
    }

    public static NoteContentHolder getInstance() {
        if (sInstance == null) {
            sInstance = new NoteContentHolder();
        }
        return sInstance;
    }

    public void addNotesContentObject(NoteContentObjects noteContentObjects) {
        mNotesContentList.add(0, noteContentObjects);
    }

    public List<NoteContentObjects> getmNotesContentList() {
        return mNotesContentList;
    }
    public List<NoteContentObjects> clearTheNotesContentList(){
        mNotesContentList=new ArrayList<>();
        return mNotesContentList;
    }
}
