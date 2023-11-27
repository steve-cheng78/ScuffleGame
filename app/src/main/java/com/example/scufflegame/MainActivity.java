package com.example.scufflegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button AattackR;
    private Button AattackL;
    private Button AblockR;
    private Button AblockL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The buttons have parameters corresponding to the IDs in Main.xml
        AattackR = (Button) findViewById(R.id.AattackR);
        AattackL = (Button) findViewById(R.id.AattackL);
        AblockR = (Button) findViewById(R.id.AblockR);
        AblockL = (Button) findViewById(R.id.AblockL);

        /*The buttons now have onClickListeners set, a method/function of the button class
         * to start a new activity/intent when pressed. In this case, pressing a button
         * will go to the results page.
         * */
        AattackR.setOnClickListener(this);
        AattackL.setOnClickListener(this);
        AblockR.setOnClickListener(this);
        AblockL.setOnClickListener(this);
    }
}