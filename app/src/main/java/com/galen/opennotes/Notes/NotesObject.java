package com.galen.opennotes.Notes;

import com.galen.opennotes.Notes.content.NoteContentObjects;

import java.util.List;

/**
 * create a notes object that has its id which is the sharecode and user id.
 * it contains a list of notesdetail obects that the usre can create.
 */

public class NotesObject {

    String userID, title, shareCode,currentDate,notesType,notesDescription;
    List<NoteContentObjects> theNotes;

    public NotesObject(String userID, String title, String shareCode, String currentDate, String notesType, String notesDescription, List<NoteContentObjects> theNotes) {
        this.userID = userID;
        this.title = title;
        this.shareCode = shareCode;
        this.currentDate = currentDate;
        this.notesType = notesType;
        this.notesDescription = notesDescription;
        this.theNotes = theNotes;
    }


    public NotesObject() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getNotesType() {
        return notesType;
    }

    public void setNotesType(String notesType) {
        this.notesType = notesType;
    }

    public String getNotesDescription() {
        return notesDescription;
    }

    public void setNotesDescription(String notesDescription) {
        this.notesDescription = notesDescription;
    }

    public List<NoteContentObjects> getTheNotes() {
        return theNotes;
    }

    public void setTheNotes(List<NoteContentObjects> theNotes) {
        this.theNotes = theNotes;
    }
}
