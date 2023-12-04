package com.example.scufflegame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements OnClickListener {

    private ImageView imageView;
    private ImageView playerALives, playerBLives;
    private boolean aHit;
    private boolean bHit;

    MediaPlayer punch_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        punch_sound = MediaPlayer.create(MainActivity.this, R.raw.punch_sound);

        //The buttons have parameters corresponding to the IDs in Main.xml
        Button AattackR = (Button) findViewById(R.id.AattackR);
        Button AattackL = (Button) findViewById(R.id.AattackL);
        Button AblockR = (Button) findViewById(R.id.AblockR);
        Button AblockL = (Button) findViewById(R.id.AblockL);
        Button BattackR = (Button) findViewById(R.id.BattackR);
        Button BattackL = (Button) findViewById(R.id.BattackL);
        Button BblockR = (Button) findViewById(R.id.BblockR);
        Button BblockL = (Button) findViewById(R.id.BblockL);

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

        playerALives = findViewById(R.id.playerALives);
        playerALives.setImageResource(R.drawable.three_lives);

        playerBLives = findViewById(R.id.playerBLives);
        playerBLives.setImageResource(R.drawable.three_lives);

        updateLivesImage('a');
        updateLivesImage('b');
    }


    //Timers for either side that activate once an attack button on corresponding side is pressed.
    //After some time, the timer will activate the scheduled TimerTask, which indicates that damage
    // has been dealt. Thus, Timers must be canceled in block function.
    // A "flaw" that I want to keep and turn into a feature is that players can spam their own
    // attack button to do a "feint". This resets the attack timer and the opponent needs to adjust
    // to that.
    final Handler handlerR = new Handler();
    final Handler handlerL = new Handler();
    Timer timerR = new Timer();
     Timer timerL = new Timer();
     Timer timerPunchR = new Timer();
     Timer timerPunchL = new Timer();
    punchAnimation punchR;
    punchAnimation punchL;
     // Reaction time for the block

     //Player health
     int healthA = 3;
     int healthB = 3;

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
        if (v.getId() == R.id.AattackR && BR == false && AR == false) {
            attack('a','r');
        } else if (v.getId() == R.id.AblockR){
           block('a','r');
        }

        if (v.getId() == R.id.AattackL && BL == false && AL == false) {
            attack('a','l');
        } else if (v.getId() == R.id.AblockL){
           block('a','l');
        }

        if (v.getId() == R.id.BattackR && AR == false && BR == false) {
            attack('b','r');
        } else if (v.getId() == R.id.BblockR){
           block('b','r');
        }

        if (v.getId() == R.id.BattackL && AL == false && BL == false) {
            attack('b','l');
        } else if (v.getId() == R.id.BblockL){
           block('b','l');
        }



    }

    private void attack(char player, char side)
    {
        //activate attack indication animation

        //Activate the correct "active attack" indicator
        //TimerTask damage;

        // Determine which handler to use based on the side
        Handler currentHandler = (side == 'r') ? handlerR : handlerL;

        currentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               
                // Set the lightning bolt image based on the player and side
                int lightningBoltImageId = getLightningBoltImageId(player, side);
                imageView.setImageResource(lightningBoltImageId);

                // Schedule the punch animation to occur after the lightning bolt
                Runnable punch = new punchAnimation(player, side);

                currentHandler.postDelayed(punch, 400); // Show the bolt for 200 ms before punch
            }
        }, 0); // Immediate execution for lightning bolt
        

        if (side == 'r') {
            //timerR.cancel();
            //timerR = new Timer(); //start timer associated with right side attack
            //begin = System.nanoTime();

            if (player == 'a') {
                AR = true;

                //damage = new Damage('a','r');
                punchR = new punchAnimation('a','r');

            } else {
                BR = true;

                //damage = new Damage('b','r');
                punchR = new punchAnimation('b','r');
            }

            //timerR.schedule(damage, 400);

            //separate timer just for animation, which plays even if it doesn't do damage
            handlerR.postDelayed(punchR, 400);

        } else {
            //timerL.cancel();
            //timerL = new Timer(); // start timer associated with left side attack
            //long begin = System.nanoTime();

            if (player == 'a') {
                AL = true;

                //damage = new Damage('a','l');
                punchL = new punchAnimation('a','l');

            } else {
                BL = true;

                //damage = new Damage('b','l');
                punchL = new punchAnimation('b','l');
            }

            //timerL.schedule(damage, 400);

            //separate time just for animation, which plays even if it doesn't do damage
            handlerL.postDelayed(punchL, 400);
        }
        //return begin;
    }

    //adding lightning bolt indicator
    private int getLightningBoltImageId(char player, char side) {
        // Determine the correct lightning bolt image based on player and side
        if (player == 'a' && side == 'r') {
            return R.drawable.lightning_bolt_bottom_right;
        } else if (player == 'a' && side == 'l') {
            return R.drawable.lightning_bolt_bottom_left;
        } else if (player == 'b' && side == 'r') {
            return R.drawable.lightning_bolt_top_right;
        } else { // player == 'b' && side == 'l'
            return R.drawable.lightning_bolt_top_left;
        }
    }


    //updating the lives tracker
    private void updateLivesImage(char player) {
        runOnUiThread(() -> {
            ImageView livesImageView = (player == 'a') ? playerALives : playerBLives;
            int health = (player == 'a') ? healthA : healthB;

            switch (health) {
                case 2:
                    livesImageView.setImageResource(R.drawable.two_lives);
                    break;
                case 1:
                    livesImageView.setImageResource(R.drawable.one_life);
                    break;
                case 0:
                    livesImageView.setImageDrawable(null); 
                    break;
                default:
                    livesImageView.setImageResource(R.drawable.three_lives);
            }
        });
    }
    
    
    // cancel method of timer class
    private void block(char player, char side)
    {
      if(AR && player == 'b' && side == 'r'){
          //timerR.cancel();
          handlerR.removeCallbacks(punchR);
          AR = false;
          imageView.setImageResource(R.drawable.top_right_block);
      }
      else if(BR && player == 'a' && side == 'r'){
          //timerR.cancel();
          handlerR.removeCallbacks(punchR);
          BR = false;
          imageView.setImageResource(R.drawable.bottom_right_block);
      }

      if (AL && player == 'b' && side == 'l'){
          //timerL.cancel();
          handlerL.removeCallbacks(punchL);
          AL = false;
          imageView.setImageResource(R.drawable.top_left_block);
      }
      else if(BL && player == 'a' && side == 'l'){
          //timerL.cancel();
          handlerL.removeCallbacks(punchL);
          BL = false;
          imageView.setImageResource(R.drawable.bottom_left_block);
      }

    }

//    class Damage extends TimerTask
//    {
//        char player;
//        char side;
//        Damage (char player, char side) {
//            this.player = player;
//            this.side = side;
//        }
//
//        public void run()
//        {
//            if (this.player == 'a') {
//                healthB--;
//                if (this.side == 'r') {
//                    AR = false;
//                } else {
//                    AL = false;
//                }
//            } else {
//                healthA--;
//                if (this.side == 'r') {
//                    BR = false;
//                } else {
//                    BL = false;
//                }
//            }
//        }
//
//    }

    class punchAnimation implements Runnable
    {
        char player;
        char side;
        punchAnimation (char player, char side) {
            this.player = player;
            this.side = side;
        }

        @Override
        public void run() {
            //punch animation code

            punch_sound.start();


            if ((this.player == 'a' && !BL) || (this.player == 'b' && !AL) ){

                if(this.player == 'a') {
                    // Decrement health only if it's more than 0
                    if (healthB > 0) {
                        Log.d("ScuffleGame", "Before attack, healthB: " + healthB);
                        healthB--;
                        updateLivesImage('b');
                        Log.d("ScuffleGame", "After attack, healthB: " + healthB);
                    }
                    checkGameOver();

                } else {
                    
                    if (healthA > 0) {
                        Log.d("ScuffleGame", "Before attack, healthA: " + healthA);
                        healthA--;
                        Log.d("ScuffleGame", "After attack, healthA: " + healthA);
                        updateLivesImage('a');
                    }
                    checkGameOver();

                }
            
            if (this.player == 'a') {
               
                if (this.side == 'r') {
                    AR = false;
                    //setting bottom right attack character image
                    imageView.setImageResource(R.drawable.bottom_right);
                } else {
                    AL = false;
                    //setting bottom left attack character image
                    imageView.setImageResource(R.drawable.bottom_left);
                }
            } else {
               
                if (this.side == 'r') {
                    BR = false;
                    //setting top left attack character image
                    imageView.setImageResource(R.drawable.top_left);
                } else {
                    BL = false;
                    //setting top right attack character image
                    imageView.setImageResource(R.drawable.top_right);
                    }
                }
            }

            Handler handler = (side == 'r') ? handlerR : handlerL;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageResource(R.drawable.normal);
                }
            }, 500); // Delay time in milliseconds after which to revert image
        }
        
        }


    // checks if the game is over
    private void checkGameOver() {
        if (healthA == 0 || healthB == 0) {
            String winner = (healthA == 0) ? "Player B" : "Player A";
            showGameOverDialog(winner);
        }
    }

    // show game over dialogue
    private void showGameOverDialog(String winner) {

        new AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage(winner + " wins! Do you want to play again?")
                .setPositiveButton("Yes", (dialog, which) -> resetGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .show();

    }


    //resets game back to the start
    private void resetGame() {
        healthA = 3;
        healthB = 3;
        updateLivesImage('a');
        updateLivesImage('b');
        imageView.setImageResource(R.drawable.normal);
        // Reset any other game state as necessary
    }


    }


