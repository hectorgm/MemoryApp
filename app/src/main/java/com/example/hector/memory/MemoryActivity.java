package com.example.hector.memory;

import com.example.hector.memory.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MemoryActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = false;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private int NUM_CARDS = 20;

    // Map of the card index and the colour assigned
    private Card[] CARDS = new Card[NUM_CARDS];
    private HashMap<Integer, Card> BUTTON_CARD_MAP = new HashMap<Integer, Card>();

    Card FIRST_CARD_CLICKED = null;
    Card SECOND_CARD_CLICKED = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_memory);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        populateButtons();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void buttonOnClick(View view)
    {
        /*String text = "";
        switch(view.getId())
        {
            case R.id.imageButton:
                text = "Button 1";
                break;

            case R.id.imageButton2:
                text = "Button 2";
                break;

            case R.id.imageButton3:
                text = "Button 3";
                break;

            case R.id.imageButton4:
                text = "Button 4";
                break;
        }
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();*/

        Card card = BUTTON_CARD_MAP.get(view.getId());
        ImageButton clickedImageButton = (ImageButton)view;
        //if (card.isShown() == false)
        //{
            card.setShown(true);
            // First card clicked
            if (FIRST_CARD_CLICKED == null)
            {
                FIRST_CARD_CLICKED = card;
                clickedImageButton.setClickable(false);
                clickedImageButton.setBackgroundResource(card.getColour());
                if (SECOND_CARD_CLICKED != null)
                {
                    ImageButton secondCardButton = (ImageButton) findViewById(getImageButtonIDbyCard(SECOND_CARD_CLICKED));
                    secondCardButton.setBackgroundResource(R.drawable.back);
                    SECOND_CARD_CLICKED.setShown(false);
                    SECOND_CARD_CLICKED = null;
                }
            }
            // Second card clicked
            else if (FIRST_CARD_CLICKED != null && SECOND_CARD_CLICKED == null)
            {
                SECOND_CARD_CLICKED = card;
                clickedImageButton.setBackgroundResource(card.getColour());
                if (card.getColour() != FIRST_CARD_CLICKED.getColour())
                {
                    ImageButton firstCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(FIRST_CARD_CLICKED));
                    clickedImageButton.setClickable(true);
                    firstCardButton.setClickable(true);
                    Toast toast = Toast.makeText(getApplicationContext(), "No match", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    SECOND_CARD_CLICKED.setMatched(true);
                    FIRST_CARD_CLICKED.setMatched(true);
                    clickedImageButton.setClickable(false);
                    FIRST_CARD_CLICKED = null;
                    SECOND_CARD_CLICKED = null;
                    Toast toast = Toast.makeText(getApplicationContext(), "Match", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            else if(FIRST_CARD_CLICKED != null && SECOND_CARD_CLICKED != null)
            {
                ImageButton firstCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(FIRST_CARD_CLICKED));
                ImageButton secondCardButton = (ImageButton)findViewById(getImageButtonIDbyCard(SECOND_CARD_CLICKED));
                firstCardButton.setBackgroundResource(R.drawable.back);
                secondCardButton.setBackgroundResource(R.drawable.back);
                clickedImageButton.setClickable(false);
                FIRST_CARD_CLICKED.setShown(false);
                SECOND_CARD_CLICKED.setShown(false);
                SECOND_CARD_CLICKED = null;
                FIRST_CARD_CLICKED = card;
                card.setShown(true);
                clickedImageButton.setBackgroundResource(card.getColour());
            }
        //}
    }

    private void populateButtons()
    {
        // Map of colours and the number of times that colour has been assigned to a card
        HashMap<Integer, Integer> coloursMap = new HashMap<Integer, Integer>();

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

        for (int i = 0; i < cardColours.length; ++i)
        {
            coloursMap.put(cardColours[i], 0);
        }

        Random random = new Random();
        for (int cardIndex = 0; cardIndex < NUM_CARDS; ++cardIndex)
        {
            int colourIndex = random.nextInt(cardColours.length);
            int colour = cardColours[colourIndex];
            while (coloursMap.get(colour) > 1)
            {
                colourIndex = random.nextInt(cardColours.length);
                colour = cardColours[colourIndex];
            }

            Card card = new Card(colour);
            CARDS[cardIndex] = card;
            // Increase the counter that shows the number of times the colour has been used
            coloursMap.put(colour, coloursMap.get(colour) + 1);
        }

        BUTTON_CARD_MAP.put(R.id.imageButton,   CARDS[0]);
        BUTTON_CARD_MAP.put(R.id.imageButton2,  CARDS[1]);
        BUTTON_CARD_MAP.put(R.id.imageButton3,  CARDS[2]);
        BUTTON_CARD_MAP.put(R.id.imageButton4,  CARDS[3]);
        BUTTON_CARD_MAP.put(R.id.imageButton5,  CARDS[4]);
        BUTTON_CARD_MAP.put(R.id.imageButton6,  CARDS[5]);
        BUTTON_CARD_MAP.put(R.id.imageButton7,  CARDS[6]);
        BUTTON_CARD_MAP.put(R.id.imageButton8,  CARDS[7]);
        BUTTON_CARD_MAP.put(R.id.imageButton9,  CARDS[8]);
        BUTTON_CARD_MAP.put(R.id.imageButton10, CARDS[9]);
        BUTTON_CARD_MAP.put(R.id.imageButton11, CARDS[10]);
        BUTTON_CARD_MAP.put(R.id.imageButton12, CARDS[11]);
        BUTTON_CARD_MAP.put(R.id.imageButton13, CARDS[12]);
        BUTTON_CARD_MAP.put(R.id.imageButton14, CARDS[13]);
        BUTTON_CARD_MAP.put(R.id.imageButton15, CARDS[14]);
        BUTTON_CARD_MAP.put(R.id.imageButton16, CARDS[15]);
        BUTTON_CARD_MAP.put(R.id.imageButton17, CARDS[16]);
        BUTTON_CARD_MAP.put(R.id.imageButton18, CARDS[17]);
        BUTTON_CARD_MAP.put(R.id.imageButton19, CARDS[18]);
        BUTTON_CARD_MAP.put(R.id.imageButton20, CARDS[19]);
        setImageToButton(R.id.imageButton, R.drawable.back);
        setImageToButton(R.id.imageButton2,  R.drawable.back);
        setImageToButton(R.id.imageButton3,  R.drawable.back);
        setImageToButton(R.id.imageButton4,  R.drawable.back);
        setImageToButton(R.id.imageButton5,  R.drawable.back);
        setImageToButton(R.id.imageButton6,  R.drawable.back);
        setImageToButton(R.id.imageButton7,  R.drawable.back);
        setImageToButton(R.id.imageButton8,  R.drawable.back);
        setImageToButton(R.id.imageButton9,  R.drawable.back);
        setImageToButton(R.id.imageButton10, R.drawable.back);
        setImageToButton(R.id.imageButton11, R.drawable.back);
        setImageToButton(R.id.imageButton12, R.drawable.back);
        setImageToButton(R.id.imageButton13, R.drawable.back);
        setImageToButton(R.id.imageButton14, R.drawable.back);
        setImageToButton(R.id.imageButton15, R.drawable.back);
        setImageToButton(R.id.imageButton16, R.drawable.back);
        setImageToButton(R.id.imageButton17, R.drawable.back);
        setImageToButton(R.id.imageButton18, R.drawable.back);
        setImageToButton(R.id.imageButton19, R.drawable.back);
        setImageToButton(R.id.imageButton20, R.drawable.back);
    }

    public void setImageToButton(int buttonId, int imageId)
    {
        ImageButton imageButton = (ImageButton)findViewById(buttonId);
        imageButton.setBackgroundResource(imageId);
    }

    public Integer getImageButtonIDbyCard(Card card)
    {
        return (Integer)getKeyFromValue(BUTTON_CARD_MAP, card);
    }

    public static Object getKeyFromValue(Map hm, Object value)
    {
        for (Object o : hm.keySet())
        {
            if (hm.get(o).equals(value))
            {
                return o;
            }
        }
        return null;
    }
}
