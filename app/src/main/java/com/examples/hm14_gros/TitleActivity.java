package com.examples.hm14_gros;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TitleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlescreen);

        Button playGameButton = (Button) findViewById(R.id.playGameButton);
        Button highScoreButton = (Button) findViewById(R.id.highScoreButton);

        // Set up the click action for the Play Game button
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switches to the Game Screen
                Intent intent = new Intent(TitleActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set up the click action for the High Score button
        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Opens the highscore preferences screen
                Intent intent = new Intent(TitleActivity.this, PrefsActivity.class);
                startActivity(intent);
            }
        });
    }
}