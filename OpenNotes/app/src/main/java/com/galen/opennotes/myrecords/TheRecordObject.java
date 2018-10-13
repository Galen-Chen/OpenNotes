package com.galen.opennotes.myrecords;

/**
 * Created by Galen on 5/21/17.
 */

public class TheRecordObject {
    String theTitle,theType;
    Double hoursTrained;

    public TheRecordObject() {
    }

    public TheRecordObject(String theTitle, String theType, Double hoursTrained) {

        this.theTitle = theTitle;
        this.theType = theType;
        this.hoursTrained = hoursTrained;
    }

    public String getTheTitle() {
        return theTitle;
    }

    public void setTheTitle(String theTitle) {
        this.theTitle = theTitle;
    }

    public String getTheType() {
        return theType;
    }

    public void setTheType(String theType) {
        this.theType = theType;
    }

    public Double getHoursTrained() {
        return hoursTrained;
    }

    public void setHoursTrained(Double hoursTrained) {
        this.hoursTrained = hoursTrained;
    }
}
