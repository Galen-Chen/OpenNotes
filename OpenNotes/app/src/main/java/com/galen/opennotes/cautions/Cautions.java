package com.galen.opennotes.cautions;

/**
 * Created by Galen on 5/13/17.
 */

public class Cautions {

    private String mCautions;
    private int mID;

    public Cautions( int mID,String mCautions) {
        this.mCautions = mCautions;
        this.mID = mID;
    }

    public String getmCautions() {
        return mCautions;
    }

    public void setmCautions(String mCautions) {
        this.mCautions = mCautions;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }
}
