package com.example.scufflegame;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements OnClickListener {

    private Button AattackR;
    private Button AattackL;
    private Button AblockR;
    private Button AblockL;
    private Button BattackR;
    private Button BattackL;
    private Button BblockR;
    private Button BblockL;
    private ImageView imageView;
    private boolean aHit;
    private boolean bHit;



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

        //setting character image corresponding to ID in Main.xml
        imageView = findViewById(R.id.character);

        //setting as normal character image
        imageView.setImageResource(R.drawable.normal);

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


    //Timers for either side that activate once an attack button on corresponding side is pressed.
    //After some time, the timer will activate the scheduled TimerTask, which indicates that damage
    // has been dealt. Thus, Timers must be canceled in block function.
    // A "flaw" that I want to keep and turn into a feature is that players can spam their own
    // attack button to do a "feint". This resets the attack timer and the opponent needs to adjust
    // to that.
     Timer timerR = null;
     Timer timerL = null;
     // Reaction time for the block
    long begin = 0;
    long end = 0;

     //Player health
     int healthA = 1;
     int healthB = 1;

    //"Active attack" indicators: indicates when a character+side is in the process of attacking.
     boolean AR = false;
     boolean AL = false;
     boolean BR = false;
     boolean BL = false;

    @Override
    /*onClick is what is called when the buttons are pressed and they take in Views as arguments
     * as buttons are children of the view class, buttons can polymorphically be passed in. The button
     * that called the onClick is automatically fed in*/
    public void onClick(View v)
    {

        //By using if and else if, only attack or block button can be pushed at one time for either
        //player, either side.
        //When BR is false, a player can begin an attack because an opposing attack on that side has
        // not yet been initiated.
        if (v.getId() == R.id.AattackR && BR == false) {
            attack('a','r');
        } else if (v.getId() == R.id.AblockR){
           block('a','r');
        }

        if (v.getId() == R.id.AattackL && BL == false) {
            attack('a','l');
        } else if (v.getId() == R.id.AblockL){
           block('a','l');
        }

        if (v.getId() == R.id.BattackR && AR == false) {
            attack('b','r');
        } else if (v.getId() == R.id.BblockR){
           block('b','r');
        }

        if (v.getId() == R.id.BattackL && AL == false) {
            attack('b','l');
        } else if (v.getId() == R.id.BblockR){
           block('b','l');
        }



    }

    private long attack(char player, char side)
    {
        //activate attack indication animation

        //Activate the correct "active attack" indicator
        if (side == 'r') {
            timerR.cancel();
            timerR = new Timer(); //start timer associated with right side attack
            begin = System.nanoTime();

            if (player == 'a') {
                AR = true;
                //setting bottom right attack character image
                imageView.setImageResource(R.drawable.bottom_right);
            } else {
                BR = true;
                //setting top left attack character image
                imageView.setImageResource(R.drawable.top_left);
            }
            TimerTask damage = new Damage();
            timerR.schedule(damage, 400);

            Timer timerPunchR = new Timer();
            TimerTask punchR = new punchAnimation();
            timerPunchR.schedule(punchR, 400);

        } else {
            timerL.cancel();
            timerL = new Timer(); // start timer associated with left side attack
            long begin = System.nanoTime();

            if (player == 'a') {
                AL = true;
                //setting bottom left attack character image
                imageView.setImageResource(R.drawable.bottom_left);
            } else {
                BL = true;
                //setting top right attack character image
                imageView.setImageResource(R.drawable.top_right);
            }
            TimerTask damage = new Damage();
            timerL.schedule(damage, 400);

            Timer timerPunchL = new Timer();
            TimerTask punchL = new punchAnimation();
            timerPunchL.schedule(punchL, 400);
        }
        return begin;
    }


    // cancel method of timer class
    /*private void block(char player, char side)
    {
        end = System.nanoTime();
       // Take in the input of defense button
        if (side == 'r') {
            if (begin - end < 400) {
                if (player == 'a') {
                    healthA++; //negate damage done by attack from b on right side
                }
                else if (player =='b'){
                    healthB++; //negate damage done by attack from a on right side
                }
            }
        }
        else if (side == 'l'){
            if (begin - end < 400){
                if (player == 'a') {
                    healthA++; //negate damage done by attack from b on left side
                }
                else if (player =='b'){
                    healthB++; //negate damage done by attack from a on left side
                }
            }
        }
        begin = 0;
        end = 0;
       // reset timer
    }
*/

    class Damage extends TimerTask
    {
        public void run()
        {
            healthA--;
        }

    }

    class punchAnimation extends TimerTask
    {
        public void run()
        {
            //punch animation code
        }
    }


}