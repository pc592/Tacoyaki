package com.example.puilam.tacoyaki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class StartScreen extends AppCompatActivity {

    // start screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
    }

    // when user clicks start
    public void clickedStart(View v) {
        // identify and name user action as intent to go choose board
        Intent goChooseBoard = new Intent();
        goChooseBoard.setClass(this, ChooseBoard.class);
        // change screens/activities and permanently close start screen
        startActivity(goChooseBoard);
        finish();
    }
}
