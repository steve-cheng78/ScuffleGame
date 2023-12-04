package com.example.scufflegame;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;


public class HomeActivity extends AppCompatActivity {

    MediaPlayer start_song;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //adding background music
        start_song = MediaPlayer.create(HomeActivity.this, R.raw.start_song);
        start_song.start();
        start_song.setLooping(true);

        Button startGameButton = findViewById(R.id.button_start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start game activity
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button infoButton = findViewById(R.id.button_info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show instructions
                showInstructionsPopup();
            }
        });
    }

    private void showInstructionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How to Play");

        // Set up the instructions text. Consider using a string resource for this.
        builder.setMessage("Attack: Press an attack button to hit your opponent.\n" + "\n" +
                "Block: Use block buttons to defend against incoming attacks.\n" + "\n" +
                "Lives: Each player starts with three lives. A successful attack reduces your opponent's life by one.\n"+ "\n" +
                "The first player to drain all of the opponent's lives wins!\n" + "\n" +
                "Tip: Watch for the lightning bolt signal for incoming attacks and time your blocks!\n" +
                "\n" +
                "Restart: After a game, choose \"Yes\" to play again or \"No\" to exit.\n" +
                "\n" +
                "Good luck!");

        // Add a button to dismiss the popup
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }




}
