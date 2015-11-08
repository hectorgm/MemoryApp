package com.example.hector.memory;

/**
 * Created by Hector on 25/10/2015.
 */
public class Card {

    private int colour;
    private boolean matched;
    private boolean shown;

    public Card(int colour)
    {
        this.colour = colour;
        this.matched = false;
        this.shown = false;
    }

    public int getColour() {
        return colour;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }
}
