package com.example.puilam.tacoyaki;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.util.StateSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlayGame extends AppCompatActivity {

    // game screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dynamically create game board
        final int boardSize = getIntent().getIntExtra("size",5);

        // get size of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenMax = Math.min(size.x, size.y)-20;
        // calculate button size
        final int buttonSize = (int) (Math.floor(screenMax/boardSize));

        // get board matrix
        Boolean[][] initialize = getBoard(boardSize);

        // create board
        newGame(boardSize,buttonSize,initialize);
    }

    // get board matrix
    // checks if a board has been saved for this size of board before, and uses that if it exists
    // if no board from previous plays, generates a new board
    public Boolean[][] getBoard(int boardSize) {

        // get saved data
        SharedPreferences prefs = getSharedPreferences("boards", Context.MODE_PRIVATE);

        // initialize board
        Boolean[][] initialize = new Boolean[boardSize][boardSize];

        // look for stored board for selected size
        // stored as initX, where X is the board size desired
        String key = "init" + boardSize;
        if (!prefs.contains(key)) {
            // no stored board, create new board
            generateBoard(boardSize);
            initialize = getBoard(boardSize);
        } else {
            // stored board, get and process to produce board
            String sJsonList = prefs.getString(key, "");

            List<String> s =
                    new Gson().fromJson(sJsonList, new TypeToken<List<String>>() {
                    }.getType());

            int count = 0;

            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    initialize[i][j] = Boolean.valueOf(s.get(count));
                    count += 1;
                }
            }
        }

        return initialize;
    }

    // create a new random board
    public void generateBoard(int boardSize) {

        // initialize board
        Boolean[][] pseudoBoard = new Boolean[boardSize][boardSize];
        for (int i=0;i<boardSize;i++) {
            for (int j=0;j<boardSize;j++) {
                pseudoBoard[i][j] = true;
            }
        }

        // randomly flip set of tiles (guarantees solvability) for large number of times
        Random rand = new Random();
        int scrambles = (16*boardSize) + boardSize;
        for (int k=0;k<scrambles;k++) {

            int row = rand.nextInt(boardSize);
            int col = rand.nextInt(boardSize);

            pseudoBoard[row][col] = !pseudoBoard[row][col];
            //left neighbor
            if (col>0) {
                pseudoBoard[row][col-1] = !pseudoBoard[row][col-1];
            }
            //top neighbor
            if (row>0) {
                pseudoBoard[row-1][col] = !pseudoBoard[row-1][col];
            }
            //right neighbor
            if (col<boardSize-1) {
                pseudoBoard[row][col+1] = !pseudoBoard[row][col+1];
            }
            //bottom neighbor
            if (row<boardSize-1) {
                pseudoBoard[row+1][col] = !pseudoBoard[row+1][col];
            }
        }

        // process into correct format for storage
        String[] s = new String[boardSize*boardSize];
        int count = 0;

        for (int i=0;i<boardSize;i++) {
            for (int j=0;j<boardSize;j++) {
                Boolean b = pseudoBoard[i][j];
                s[count] = b.toString();
                count += 1;
            }
        }

        String sJsonList = new Gson().toJson(s);

        // store into saved data
        String key = "init" + boardSize;
        SharedPreferences prefs = getSharedPreferences("boards", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, sJsonList);
        editor.apply();
    }

    // remove saved data (force a new board)
    public void clearBoard(int boardSize) {

        String key = "init" + boardSize;
        SharedPreferences prefs = getSharedPreferences("boards", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();

    }

    // save board, but using ToggleButton matrix
    public void saveBoard(int boardSize, ToggleButton[][] buttons) {

        // process into correct format for storage
        String[] s = new String[boardSize*boardSize];
        int count = 0;

        for (int i=0;i<boardSize;i++) {
            for (int j=0;j<boardSize;j++) {
                Boolean b = buttons[i][j].isChecked();
                s[count] = b.toString();
                count += 1;
            }
        }


        // store into saved data
        String sJsonList = new Gson().toJson(s);

        String key = "init" + boardSize;
        SharedPreferences prefs = getSharedPreferences("boards", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, sJsonList);
        editor.apply();

    }

    // add buttons at bottom of screen
    public void addBottomButtons(LinearLayoutCompat screenLayout, Button back, Button restart, Button newBoard) {

        // create layout for buttons, placed at bottom of screen
        LinearLayoutCompat bottom = new LinearLayoutCompat(this);
        bottom.setOrientation(LinearLayoutCompat.HORIZONTAL);
        LinearLayoutCompat.LayoutParams bottomParams =
                new LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,0);
        bottomParams.weight=5;
        bottomParams.topMargin=15;
        bottom.setWeightSum(3);     // space out evenly for three buttons

        // create layout for containers that will hold buttons
        LinearLayoutCompat.LayoutParams separateParams =
                new LinearLayoutCompat.LayoutParams(
                        0,ViewGroup.LayoutParams.MATCH_PARENT);
        separateParams.weight=1;

        // create layout to center buttons in containers
        RelativeLayout.LayoutParams centerButtParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        centerButtParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        // create containers and use layouts to place buttons in proper places
        screenLayout.addView(bottom, bottomParams);
        RelativeLayout containLeft = new RelativeLayout(this);
        RelativeLayout containMid = new RelativeLayout(this);
        RelativeLayout containRight = new RelativeLayout(this);
        // add containers (views) to screen
        bottom.addView(containLeft, separateParams);
        bottom.addView(containMid, separateParams);
        bottom.addView(containRight, separateParams);

        // add buttons (views) to containers
        containLeft.addView(back, centerButtParams);
        containMid.addView(restart, centerButtParams);
        containRight.addView(newBoard, centerButtParams);
    }

    // create a new game
    public void newGame(final int boardSize, final int buttonSize, final Boolean[][] initialize) {

        // set layout parameters for buttons
        LinearLayoutCompat screenLayout = new LinearLayoutCompat(this);
        LinearLayoutCompat.LayoutParams screenParams =
                new LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        screenLayout.setOrientation(LinearLayout.VERTICAL);
        screenLayout.setWeightSum(10);

        //TODO: track # of moves in spacer
        // set layout to set aside space for bottom buttons (spacer)
        Space spacer = new Space(this);
        LinearLayoutCompat.LayoutParams spacerParams =
                new LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0);
        spacerParams.weight = 5;
        screenLayout.addView(spacer, spacerParams);

        // set layout to set aside space for buttons grid
        RelativeLayout centerGrid = new RelativeLayout(this);
        RelativeLayout.LayoutParams centerGridParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        // set layout for buttonsGrid
        LinearLayout buttonsGrid = new LinearLayout(this);
        buttonsGrid.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams buttonsGridParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonsGridParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        final ToggleButton [][] buttons = new ToggleButton[boardSize][boardSize];

        // add buttons
        for (int i=0; i<boardSize; i++) {
            // for each row, set row layout
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(
                    new LinearLayoutCompat.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
            );
            // add boardSize buttons to each row
            for (int j=0; j<boardSize; j++) {

                ToggleButton button = createButton(buttonSize, buttons, boardSize);

                buttons[i][j] = button;
                button.setId((i*10)+j);
                button.setChecked(initialize[i][j]);
                row.addView(button);

            }
            buttonsGrid.addView(row);
        }

        // add button grid to screen
        centerGrid.addView(buttonsGrid, buttonsGridParams);
        screenLayout.addView(centerGrid, centerGridParams);
        setContentView(screenLayout, screenParams);

        // create restart button and set listener
        Button restart = new Button(this);
        restart.setText(R.string.restart);
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame(boardSize, buttonSize, initialize);
            }
        });

        // create back button and set listener
        //   ends screen, which will return to choose board screen; same as using built-in back button
        Button back = new Button(this);
        back.setText(R.string.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // create new button and set listener
        Button newBoard = new Button(this);
        newBoard.setText(R.string.newBoard);
        newBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBoard(boardSize);
                generateBoard(boardSize);
                Boolean[][] initialize = getBoard(boardSize);
                newGame(boardSize, buttonSize, initialize);
            }
        });

        addBottomButtons(screenLayout, back, restart, newBoard);
    }

    // create button, with formatting, and set listener
    public ToggleButton createButton(int buttonSize, final ToggleButton[][] buttons, final int boardSize) {
        int divisor = 28;
        int margin = buttonSize/divisor;
        ToggleButton button = new ToggleButton(this);
        button.setText(null);
        button.setTextOff(null);
        button.setTextOn(null);
        button.setMinimumWidth(1);
        button.setMinimumHeight(1);
        button.setWidth(buttonSize-(2*buttonSize/divisor));
        button.setHeight(buttonSize-(2*buttonSize/divisor));
        button.setBackgroundResource(R.drawable.buttonstyle);
        button.setChecked(false);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        target(v, buttons, boardSize);
                    }
                }
        );
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(margin,margin,margin,margin);
        button.setLayoutParams(params);

        return button;
    }

    // flip all appropriate tiles on click
    public void target(View v, ToggleButton[][] buttons, int boardSize) {
        //center
        int id = v.getId();
        int row = (int) Math.floor(id/10);
        int col = id%10;
        v.setBackgroundResource(R.drawable.buttonstyle);
        //left neighbor
        if (col>0) {
            flip(buttons[row][col-1]);
        }
        //top neighbor
        if (row>0) {
            flip(buttons[row-1][col]);
        }
        //right neighbor
        if (col<boardSize-1) {
            flip(buttons[row][col+1]);
        }
        //bottom neighbor
        if (row<boardSize-1) {
            flip(buttons[row+1][col]);
        }
        saveBoard(boardSize,buttons);
        checkWon(buttons, boardSize);
    }

    // check if player has won
    public void checkWon(ToggleButton[][] buttons, int boardSize) {
        // sum buttons
        int won = 0;
        for (int i=0; i<boardSize; i++) {
            for (int j=0; j<boardSize; j++) {
                if (buttons[i][j].isChecked())
                    won += 1;
            }
        }
        // won if all buttons are 1 or all buttons are 0
        if (won==boardSize*boardSize || won==0) {
            // remove board from saved data
            clearBoard(boardSize);

            // create intent as won game
            Intent wonGame = new Intent();
            wonGame.setClass(this, WonGame.class);
            wonGame.putExtra("size", boardSize);    // pass on board size to next screen/activity
            // change screens/activities and permanently close screen
            //   (then future back will return to choose board)
            startActivity(wonGame);
            finish();
        }
    }

    // flip a button
    public void flip(ToggleButton b) {
        //back is checked, front is not

        b.setBackgroundResource(R.drawable.buttonstyle);
        b.setChecked(!b.isChecked());
//        flipCard(b);

    }

    // animate flipping?
    // copied in from android studio flipping animation tutorial
    public void flipCard(ToggleButton b) {

//        // Flip to the back.
//        b.setChecked(!b.isChecked());
//
//        ViewGroup fc = ((ViewGroup) b.getParent()).getId();
//
//        android.app.Fragment flippy = this.getLayoutInflater().inflate(R.layout.fragment_card, fc, false);
//        FragmentManager fm = getFragmentManager();
//        android.app.FragmentTransaction ft = fm.beginTransaction();
//        ft.add(flippy, "flip");
//        ft.setCustomAnimations(
//                R.animator.flip_right_in,
//                R.animator.flip_right_out,
//                R.animator.flip_left_in,
//                R.animator.flip_left_out);
//        int back = R.layout.fragment_card_front;
//        ft.replace(v.getId(), flippy);
//        ft.commit();


        // Flip to the back.
        b.setChecked(!b.isChecked());

        // Create and commit a new fragment transaction that adds the fragment for
        // the back of the card, uses custom animations, and is part of the fragment
        // manager's back stack.

        int back = R.layout.fragment_card_front;

        getFragmentManager()
                .beginTransaction()

                // Replace the default fragment animations with animator resources
                // representing rotations when switching to the back of the card, as
                // well as animator resources representing rotations when flipping
                // back to the front (e.g. when the system Back button is pressed).
                .setCustomAnimations(
                    R.animator.flip_right_in,
                    R.animator.flip_right_out,
                    R.animator.flip_left_in,
                    R.animator.flip_left_out)

                // Replace any fragments currently in the container view with a
                // fragment representing the next page (indicated by the
                // just-incremented currentPage variable).
                .replace(b.getId(), new CardFlipActivity.CardBackFragment())

                // Add this transaction to the back stack, allowing users to press
                // Back to get to the front of the card.
                .addToBackStack(null)

                // Commit the transaction.
                .commit();
    }


}