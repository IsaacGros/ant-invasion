package com.examples.hm14_gros;

import android.graphics.Bitmap;
import android.media.SoundPool;

public class Assets {
    static Bitmap playbackground;
    static Bitmap foodbar;
    static Bitmap scorebar;
    static Bitmap heart;
    static Bitmap ant1;
    static Bitmap ant2;
    static Bitmap ant3;

    // States of the GameScreen
    enum GameState {
        GettingReady,   // play *get ready* sound and start timer, goto next state
        Starting,       // when 3 seconds have elapsed, goto next state
        Running,        // play the game, when livesLeft == 0 goto next state
        GameEnding,     // show game over message
        GameOver,       // game is over, wait for any touch and go back to title activity screen
    };

    static GameState state;     // current state of the game
    static float gameTimer;     // in seconds
    static int livesLeft;        // 0-3
    static int score, highScore;
    public static float lastBugEatTime = 0.0f;  // Stopclock to prevent sound effects playing multiple times

    static SoundPool soundPool;
    static int sound_getready, sound_gameover, sound_bugeat,
               sound_squish1, sound_squish2, sound_squish3,
               sound_thump1, sound_thump2, sound_thump3;

    static Bug bug[];
}
