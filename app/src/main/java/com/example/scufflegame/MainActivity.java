package com.example.scufflegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.widget.Toast;
import java.lang.System;


public class MainActivity extends Activity implements OnClickListener {

    private Button AattackR;
    private Button AattackL;
    private Button AblockR;
    private Button AblockL;
    private Button BattackR;
    private Button BattackL;
    private Button BblockR;
    private Button BblockL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The buttons have parameters corresponding to the IDs in Main.xml
        AattackR = (Button) findViewById(R.id.AattackR);
        AattackL = (Button) findViewById(R.id.AattackL);
        AblockR = (Button) findViewById(R.id.AblockR);
        AblockL = (Button) findViewById(R.id.AblockL);
        BattackR = (Button) findViewById(R.id.BattackR);
        BattackL = (Button) findViewById(R.id.BattackL);
        BblockR = (Button) findViewById(R.id.BblockR);
        BblockL = (Button) findViewById(R.id.BblockL);

        /*The buttons now have onClickListeners set, a method/function of the button class
         * to start a new activity/intent when pressed. In this case, pressing a button
         * will go to the results page.
         * */
        AattackR.setOnClickListener(this);
        AattackL.setOnClickListener(this);
        AblockR.setOnClickListener(this);
        AblockL.setOnClickListener(this);
        BattackR.setOnClickListener(this);
        BattackL.setOnClickListener(this);
        BblockR.setOnClickListener(this);
        BblockL.setOnClickListener(this);
    }

    @Override
    /*onClick is what is called when the buttons are pressed and they take in Views as arguments
     * as buttons are children of the view class, buttons can polymorphically be passed in. The button
     * that called the onClick is automatically fed in*/
    public void onClick(View v)
    {
        //Measures elapsed time, begins once an attack function is activated.
        //The value 0 respresents the timer being off. Once activated, the timer will have a
        //beginning reference value.
        //Timers must be reset to 0 in block function. If not, players can override their opponent's
        //attack with their own. However, a "flaw" that I want to keep and turn into a feature is
        //that players can spam their own attack button to do a "feint". This resets the attack
        //timer and the opponent needs to adjust to that.
        long timerR = 0;
        long timerL = 0;

        //Only attack or block button can be pushed at one time for either player, either side.
        if (v.getId() == R.id.AattackR && timerR == 0) {
            timerR = attack('a','r');
        } else if (v.getId() == R.id.AblockR){
            block('a','r');
        }

        if (v.getId() == R.id.AattackL && timerL == 0) {
            timerL = attack('a','l');
        } else if (v.getId() == R.id.AblockL){
            block('a','l');
        }

        if (v.getId() == R.id.BattackR && timerR == 0) {
            timerR = attack('b','r');
        } else if (v.getId() == R.id.BblockR){
            block('b','r');
        }

        if (v.getId() == R.id.BattackL && timerL == 0) {
            timerL = attack('b','l');
        } else if (v.getId() == R.id.BblockR){
            block('b','l');
        }



    }


    private long attack(char player, char side)
    {
        //activate attack indication animation

        //start timer
        long timerBegin = System.nanoTime();
        return timerBegin;
    }

}