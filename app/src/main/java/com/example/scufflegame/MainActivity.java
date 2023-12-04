package com.example.scufflegame;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
         * to start a new activity/intent when pressed.
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


    //Handlers for either side that activate once an attack button on corresponding side is pressed.
    //After some time, the handler will activate the scheduled runnable, which indicates that damage
    // has been dealt. Thus, runnables must be canceled in block function.
    // A "flaw" that I want to keep and turn into a feature is that players can spam their own
    // attack button to do a "feint". This resets the attack timer and the opponent needs to adjust
    // to that.
    final Handler handlerR = new Handler();
    final Handler handlerL = new Handler();

    final Handler handlerNormA = new Handler();
    final Handler handlerNormB = new Handler();
    final Handler attackState = new Handler();
    stateChange stateChangeAR = new stateChange('a','r');
    stateChange stateChangeBR = new stateChange('b','r');
    stateChange stateChangeAL = new stateChange('a','l');
    stateChange stateChangeBL = new stateChange('b','l');

    punchAnimation punchAR = new punchAnimation('a','r');
    punchAnimation punchAL = new punchAnimation('a','l');
    punchAnimation punchBR = new punchAnimation('b','r');
    punchAnimation punchBL = new punchAnimation('b','l');
    blockAnimation blockR;
    blockAnimation blockL;

    normReset normPos = new normReset();
     //Player health
     int healthA = 3;
     int healthB = 3;

    //"Active attack" indicators: indicates when a character+side is in the process of attacking.
     boolean AR = false;
     boolean AL = false;
     boolean BR = false;
     boolean BL = false;

     //Indicates when a character is feinting
    boolean feintingAR = false;
    boolean feintingAL = false;
    boolean feintingBR = false;
    boolean feintingBL = false;

    //Indicates when a character has interrupted the opponent's feint.
    boolean interruptAR = false;
    boolean interruptAL = false;
    boolean interruptBR = false;
    boolean interruptBL = false;

    @Override
    /*onClick is what is called when the buttons are pressed and they take in Views as arguments
     * as buttons are children of the view class, buttons can polymorphically be passed in. The button
     * that called the onClick is automatically fed in*/
    public void onClick(View v)
    {

        //By using if and else if, only attack or block button can be pushed at one time for either
        //player, either side (attack and block pairs in the four corners).
        //When BR is false, a player can begin an attack because an opposing attack on that side has
        //not yet been initiated. A player can also attack when the opponent is feinting. Players
        //cannot begin a new attack when the opponent has interrupted their feinting.
        if (v.getId() == R.id.AattackR && (!BR || feintingBR) && !interruptBR) {
            attack('a','r');
        } else if (v.getId() == R.id.AblockR){
           block('a','r');
        }

        if (v.getId() == R.id.AattackL && (!BL  || feintingBL) && !interruptBL) {
            attack('a','l');
        } else if (v.getId() == R.id.AblockL){
           block('a','l');
        }

        if (v.getId() == R.id.BattackR && (!AR || feintingAR) && !interruptAR) {
            attack('b','r');
        } else if (v.getId() == R.id.BblockR){
           block('b','r');
        }

        if (v.getId() == R.id.BattackL && (!AL || feintingAL) && !interruptAL) {
            attack('b','l');
        } else if (v.getId() == R.id.BblockL){
           block('b','l');
        }

    }

    private void attack(char player, char side)
    {
        Handler currentHandler = (side == 'r') ? handlerR : handlerL;
        currentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set the lightning bolt image based on the player and side
                int lightningBoltImageId = getLightningBoltImageId(player, side);
                imageView.setImageResource(lightningBoltImageId);

            }
        }, 0); // Immediate execution for lightning bolt
        

        //Determine which animation to play based on player and side of button clicked
        if (side == 'r') {

            if (player == 'a') {
                //If opponent is feinting on same side, replace their attack with your own
                if (feintingBR) {
                    handlerR.removeCallbacks(punchBR);
                    handlerR.postDelayed(punchAR, 500);
                    interruptAR = true;
                } else {
                    //If already attacking, player goes into feinting state if atk clicked again
                    if (AR == true) {
                        //"Feint": cancel previous punch, cancel block for previous punch if any
                        handlerR.removeCallbacks(punchAR);
                        handlerNormA.removeCallbacks(normPos);
                        handlerR.removeCallbacks(blockR);
                        attackState.removeCallbacks(stateChangeAR);
                        feintingAR = true;
                        attackState.postDelayed(stateChangeAR,200);

                    //Else, player is simply initiating an attack.
                    } else {
                        AR = true;
                    }
                    handlerR.postDelayed(punchAR, 500);
                }
                handlerNormA.postDelayed(normPos, 700);

            //Repeat above cases for both players and sides
            } else {
                if (feintingAR) {
                    handlerR.removeCallbacks(punchAR);
                    handlerR.postDelayed(punchBR,500);
                    interruptBR = true;
                } else {
                    if (BR == true) {
                        handlerR.removeCallbacks(punchBR);
                        handlerNormB.removeCallbacks(normPos);
                        handlerR.removeCallbacks(blockR);
                        attackState.removeCallbacks(stateChangeBR);
                        feintingBR = true;
                        attackState.postDelayed(stateChangeBR,200);
                    } else {
                        BR = true;
                    }
                    handlerR.postDelayed(punchBR, 500);
                }
                handlerNormB.postDelayed(normPos, 700);

            }

        } else {

            if (player == 'a') {
                if (feintingBL) {
                    handlerL.removeCallbacks(punchBL);
                    handlerL.postDelayed(punchAL,500);
                    interruptAL = true;
                }
                else{
                    if (AL == true) {
                        handlerL.removeCallbacks(punchAL);
                        handlerNormA.removeCallbacks(normPos);
                        handlerL.removeCallbacks(blockL);
                        attackState.removeCallbacks(stateChangeAL);
                        feintingAL = true;
                        attackState.postDelayed(stateChangeAL, 200);
                    } else {
                        AL = true;
                    }
                    handlerL.postDelayed(punchAL, 500);
                }
                handlerNormA.postDelayed(normPos, 700);

            } else {
                if (feintingAL) {
                    handlerL.removeCallbacks(punchAL);
                    handlerL.postDelayed(punchBL,500);
                    interruptBL = true;
                } else {
                    if (BL == true) {
                        handlerL.removeCallbacks(punchBL);
                        handlerNormB.removeCallbacks(normPos);
                        handlerL.removeCallbacks(blockL);
                        attackState.removeCallbacks(stateChangeBL);
                        feintingBL = true;
                        attackState.postDelayed(stateChangeBL,200);
                    } else {
                        BL = true;
                    }

                    handlerL.postDelayed(punchBL, 500);
                }
                handlerNormB.postDelayed(normPos, 700);
            }

        }

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


    //updating the lives tracker, based on how much health player currently has
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
            //replace punch animation with block animation
            handlerR.removeCallbacks(punchAR);
            handlerNormA.removeCallbacks(normPos);
            blockR = new blockAnimation('b','r');
            handlerR.postDelayed(blockR,200);
            handlerNormB.postDelayed(normPos,600);

        }
        else if(BR && player == 'a' && side == 'r'){
            //replace punch animation with block animation
            handlerR.removeCallbacks(punchBR);
            handlerNormB.removeCallbacks(normPos);
            blockR = new blockAnimation('a','r');
            handlerR.postDelayed(blockR,200);
            handlerNormA.postDelayed(normPos,600);

        }

        if (AL && player == 'b' && side == 'l'){
            //replace punch animation with block animation
            handlerL.removeCallbacks(punchAL);
            handlerNormA.removeCallbacks(normPos);
            blockL = new blockAnimation('b','l');
            handlerL.postDelayed(blockL,200);
            handlerNormB.postDelayed(normPos,600);

        }
        else if(BL && player == 'a' && side == 'l'){
            //replace punch animation with block animation
            handlerL.removeCallbacks(punchBL);
            handlerNormB.removeCallbacks(normPos);
            blockL = new blockAnimation('a','l');
            handlerL.postDelayed(blockL,200);
            handlerNormA.postDelayed(normPos,600);

        }
    }

    class normReset implements  Runnable {
        @Override
        public void run() {
            //normal position
            imageView.setImageResource(R.drawable.normal);

        }
    }

    class blockAnimation implements Runnable
    {
        char player;
        char side;
        //Constructor for block animation, based on player and side
        blockAnimation (char player, char side) {
            this.player = player;
            this.side = side;
        }

        @Override
        public void run() {
            //block animation code
            if (this.player == 'b') {

                if (this.side == 'r') {
                    AR = false;
                    //setting bottom right attack character image
                    imageView.setImageResource(R.drawable.top_right_block);

                } else {
                    AL = false;
                    //setting bottom left attack character image
                    imageView.setImageResource(R.drawable.top_left_block);
                }
            } else {

                if (this.side == 'r') {
                    BR = false;
                    //setting top left attack character image
                    imageView.setImageResource(R.drawable.bottom_right_block);
                } else {
                    BL = false;
                    //setting top right attack character image
                    imageView.setImageResource(R.drawable.bottom_left_block);
                }
            }
        }

    }

    class punchAnimation implements Runnable
    {
        char player;
        char side;
        //Constructor for punchAnimation based on player and side
        punchAnimation (char player, char side) {
            this.player = player;
            this.side = side;
        }

        @Override
        public void run() {
            //punch animation code
            //Punch sound effect
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
                    interruptAR = false;
                    //setting bottom right attack character image
                    imageView.setImageResource(R.drawable.bottom_right);
                } else {
                    AL = false;
                    interruptAL = false;
                    //setting bottom left attack character image
                    imageView.setImageResource(R.drawable.bottom_left);
                }
            } else {
               
                if (this.side == 'r') {
                    BR = false;
                    interruptBR = false;
                    //setting top left attack character image
                    imageView.setImageResource(R.drawable.top_left);
                } else {
                    BL = false;
                    interruptBL = false;
                    //setting top right attack character image
                    imageView.setImageResource(R.drawable.top_right);
                    }
                }
            }

        }


    }


    //State change Runnable implementation for schedule resetting of feinting variables.
    class stateChange implements Runnable {
        char player;
        char side;

        stateChange(char player, char side) {
            this.player = player;
            this.side = side;
        }

        @Override
        public void run() {
            //Reset feinting variable based on player and side
            if (this.player == 'a') {
                if (this.side == 'r') {
                    feintingAR = false;
                } else {
                    feintingAL = false;
                }
            } else {
                if (this.side == 'r') {
                    feintingBR = false;
                } else {
                    feintingBL = false;
                }
            }
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
                .setCancelable(false)
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


