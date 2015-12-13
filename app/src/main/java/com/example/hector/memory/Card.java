package com.example.hector.memory;

/**
 * Created by Hector on 25/10/2015.
 */
public class Card {

    private int mColour;
    private boolean mMatched;
    private boolean mShown;

    public Card(int colour) {
        this.mColour = colour;
        this.mMatched = false;
        this.mShown = false;
    }

    public int getColour() {
        return mColour;
    }

    public boolean isMatched() {
        return mMatched;
    }

    public void setMatched(boolean matched) {
        this.mMatched = matched;
    }

    public boolean isShown() {
        return mShown;
    }

    public void setShown(boolean shown) {
        this.mShown = shown;
    }
}
