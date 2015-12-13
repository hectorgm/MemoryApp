package com.example.hector.memory;

/**
 * Created by Hector on 28/11/2015.
 */
public class Score {

    private int mScore1;
    private int mScore2;

    public Score() {
        mScore1 = 0;
        mScore2 = 0;
    }

    public void player1Scores() {
        ++mScore1;
    }

    public void player2Scores() {
        ++mScore2;
    }

    public void restartScore() {
        mScore1 = 0;
        mScore2 = 0;
    }

    public int getScore1() {
        return mScore1;
    }

    public int getScore2() {
        return mScore2;
    }
}
