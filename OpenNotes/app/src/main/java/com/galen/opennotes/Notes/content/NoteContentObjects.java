package com.galen.opennotes.Notes.content;

import java.util.List;


/** create a notesdetail object class, the notes id is the same id with the notes obecjt id, the notesdetailsID is for itself
 * you can set your notesdeatail private or public and you can create as many as tags you want.
 * create different constructions, so that the notesdetails can be very.
 */

public class NoteContentObjects {

    String userName, theShareCode,timeCreated,notesYouWrite,theParentsTitle,imageUrl;
    Boolean isPublic;
    List<String> tags;


    public NoteContentObjects() {
    }

    public NoteContentObjects(String userName, String theShareCode, String timeCreated, String notesYouWrite, Boolean isPublic, List<String> tags, String imageUrl) {
        this.userName = userName;
        this.theShareCode = theShareCode;
        this.timeCreated = timeCreated;
        this.notesYouWrite = notesYouWrite;
        this.isPublic = isPublic;
        this.tags = tags;
        this.imageUrl = imageUrl;
    }

    // make an other consturcter that take less value, which will go to the wall.
    public NoteContentObjects(String userName, String theParentsTitle, String timeCreated, String notesYouWrite,String imageUrl) {
        this.userName = userName;
        this.theParentsTitle = theParentsTitle;
        this.timeCreated = timeCreated;
        this.notesYouWrite = notesYouWrite;
        this.imageUrl = imageUrl;
    }

    public NoteContentObjects(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTheShareCode() {
        return theShareCode;
    }

    public void setTheShareCode(String theShareCode) {
        this.theShareCode = theShareCode;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getNotesYouWrite() {
        return notesYouWrite;
    }

    public void setNotesYouWrite(String notesYouWrite) {
        this.notesYouWrite = notesYouWrite;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTheParentsTitle() {
        return theParentsTitle;
    }

    public void setTheParentsTitle(String theParentsTitle) {
        this.theParentsTitle = theParentsTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
