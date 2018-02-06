package com.example.puilam.tacoyaki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

public class ChooseBoard extends AppCompatActivity {

    // choose board screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_board);

        // set number picker to possible board sizes
        NumberPicker chooseNumber = (NumberPicker) findViewById(R.id.chooseNumber);

        chooseNumber.setMinValue(0);
        chooseNumber.setMaxValue(4);
        chooseNumber.setWrapSelectorWheel(false);

        String[] choices = {"3","4","5","6","7"};
        chooseNumber.setDisplayedValues(choices);

    }

    // when user clicks OK
    public void boardChosen (View v) {
        // get board size chosen
        NumberPicker chooseNumber = (NumberPicker) findViewById(R.id.chooseNumber);
        int boardSize = chooseNumber.getValue()+3;

        // identify and name user action as intent to start game
        Intent goStartGame = new Intent();
        goStartGame.setClass(this, PlayGame.class);
        goStartGame.putExtra("size", boardSize); // pass on board size to next screen/activity

        // change screens/activities but leave screen in background
        // on back button returns to this screen/activity
        startActivity(goStartGame);

    }
}
