package com.example.puilam.tacoyaki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WonGame extends AppCompatActivity {

    // won game screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_won_game);
    }


    // if user decides to play again with same board size
    public void playAgain(View v) {
        // identify and name user action as intent to start another game (in same board size)
        Intent goStartGame = new Intent();
        goStartGame.setClass(this, PlayGame.class);
        final int boardSize = getIntent().getIntExtra("size",5);
        goStartGame.putExtra("size", boardSize);    // pass on board size to next screen/activity

        // change screens/activities and permanently close win screen
        startActivity(goStartGame);
        finish();
    }

    // if user decides to continue play but with different board size
    //   simply permanently close screen, will return to choose board screen
    public void differentSize(View v) {
        finish();
    }
}
