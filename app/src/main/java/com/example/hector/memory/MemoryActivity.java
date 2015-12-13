package com.example.hector.memory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MemoryActivity extends Activity {

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    private int NUM_CARDS = 20;

    // Map of the card index and the colour assigne
    private HashMap<Integer, Card> mButtonCardMap = new HashMap<Integer, Card>();

    Card mFirstCardClicked = null;
    Card mSecondCardClicked = null;

    private static final boolean PLAYER1_TURN = false;
    private static final boolean PLAYER2_TURN = true;
    private boolean mTurn = PLAYER1_TURN;

    private Score mScore = new Score();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memory);

        populateButtons();

        findViewById(R.id.imageView).bringToFront();

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }


    public void buttonOnClick(View view) {
        Card card = mButtonCardMap.get(view.getId());
        ImageButton clickedImageButton = (ImageButton)view;
        //if (card.isShown() == false)
        //{
            card.setShown(true);
            // First card clicked
            if (mFirstCardClicked == null) {
                mFirstCardClicked = card;
                clickedImageButton.setClickable(false);
                clickedImageButton.setBackgroundResource(card.getColour());
                if (mSecondCardClicked != null) {
                    ImageButton secondCardButton = (ImageButton) findViewById(getImageButtonIDbyCard(mSecondCardClicked));
                    secondCardButton.setBackgroundResource(R.drawable.back);
                    mSecondCardClicked.setShown(false);
                    mSecondCardClicked = null;
                }
            }
            // Second card clicked
            else if (mFirstCardClicked != null && mSecondCardClicked == null) {
                mSecondCardClicked = card;
                clickedImageButton.setBackgroundResource(card.getColour());
                if (card.getColour() != mFirstCardClicked.getColour()) {
                    ImageButton firstCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(mFirstCardClicked));
                    clickedImageButton.setClickable(true);
                    firstCardButton.setClickable(true);

                    changeTurn();

                    Toast toast = Toast.makeText(getApplicationContext(), "No match", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    mSecondCardClicked.setMatched(true);
                    mFirstCardClicked.setMatched(true);
                    clickedImageButton.setClickable(false);
                    mFirstCardClicked = null;
                    mSecondCardClicked = null;

                    playerScores();

                    Toast toast = Toast.makeText(getApplicationContext(), "Match", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if(mFirstCardClicked != null && mSecondCardClicked != null) {
                ImageButton firstCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(mFirstCardClicked));
                ImageButton secondCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(mSecondCardClicked));
                firstCardButton.setBackgroundResource(R.drawable.back);
                secondCardButton.setBackgroundResource(R.drawable.back);
                clickedImageButton.setClickable(false);
                mFirstCardClicked.setShown(false);
                mSecondCardClicked.setShown(false);
                mSecondCardClicked = null;
                mFirstCardClicked = card;
                card.setShown(true);
                clickedImageButton.setBackgroundResource(card.getColour());
            }
        //}

        zoomImageFromThumb(view, card.getColour());
    }

    private int[] getCardColoursVector() {
        int[] cardColours = new int[NUM_CARDS / 2];
        cardColours[0] = R.drawable.number1;
        cardColours[1] = R.drawable.number2;
        cardColours[2] = R.drawable.number3;
        cardColours[3] = R.drawable.number4;
        cardColours[4] = R.drawable.number5;
        cardColours[5] = R.drawable.number6;
        cardColours[6] = R.drawable.number7;
        cardColours[7] = R.drawable.number8;
        cardColours[8] = R.drawable.number9;
        cardColours[9] = R.drawable.number10;

        return cardColours;
    }

    private int[] getImageButtonsVector() {
        int[] imageButtons = new int[NUM_CARDS];
        imageButtons[0] = R.id.imageButton;
        imageButtons[1] = R.id.imageButton2;
        imageButtons[2] = R.id.imageButton3;
        imageButtons[3] = R.id.imageButton4;
        imageButtons[4] = R.id.imageButton5;
        imageButtons[5] = R.id.imageButton6;
        imageButtons[6] = R.id.imageButton7;
        imageButtons[7] = R.id.imageButton8;
        imageButtons[8] = R.id.imageButton9;
        imageButtons[9] = R.id.imageButton10;
        imageButtons[10] = R.id.imageButton11;
        imageButtons[11] = R.id.imageButton12;
        imageButtons[12] = R.id.imageButton13;
        imageButtons[13] = R.id.imageButton14;
        imageButtons[14] = R.id.imageButton15;
        imageButtons[15] = R.id.imageButton16;
        imageButtons[16] = R.id.imageButton17;
        imageButtons[17] = R.id.imageButton18;
        imageButtons[18] = R.id.imageButton19;
        imageButtons[19] = R.id.imageButton20;

        return imageButtons;
    }

    private void populateButtons() {
        // Map of colours and the number of times that colour has been assigned to a card
        HashMap<Integer, Integer> coloursMap = new HashMap<Integer, Integer>();
        Card[] cards = new Card[NUM_CARDS];
        int[] cardColours = getCardColoursVector();
        int[] imageButtons = getImageButtonsVector();

        for (int i = 0; i < cardColours.length; ++i) {
            coloursMap.put(cardColours[i], 0);
        }

        Random random = new Random();
        for (int cardIndex = 0; cardIndex < NUM_CARDS; ++cardIndex) {
            int colourIndex = random.nextInt(cardColours.length);
            int colour = cardColours[colourIndex];
            while (coloursMap.get(colour) > 1) {
                colourIndex = random.nextInt(cardColours.length);
                colour = cardColours[colourIndex];
            }

            Card card = new Card(colour);
            cards[cardIndex] = card;
            // Increase the counter that shows the number of times the colour has been used
            coloursMap.put(colour, coloursMap.get(colour) + 1);
            mButtonCardMap.put(imageButtons[cardIndex], cards[cardIndex]);
            setImageToButton(imageButtons[cardIndex], R.drawable.back);
        }
    }

    public void setImageToButton(int buttonId, int imageId) {
        ImageButton imageButton = (ImageButton)findViewById(buttonId);
        imageButton.setBackgroundResource(imageId);
    }

    public Integer getImageButtonIDbyCard(Card card) {
        return (Integer)getKeyFromValue(mButtonCardMap, card);
    }

    private void changeTurn() {
        mTurn = !mTurn;
    }

    private void playerScores() {
        int viewID;
        CharSequence scoreText = "";
        if (mTurn == PLAYER1_TURN) {
            mScore.player1Scores();
            viewID = R.id.score1;
            scoreText = Integer.toString(mScore.getScore1());
        }
        else {
            mScore.player2Scores();
            viewID = R.id.score2;
            scoreText = Integer.toString(mScore.getScore2());
        }

        TextView scoreView = (TextView)findViewById(viewID);
        scoreView.setText(scoreText);
    }

    private void restartGame() {
        mScore.restartScore();
        mTurn = PLAYER1_TURN;

        mButtonCardMap.clear();
        populateButtons();
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.imageView);
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.imageView)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
