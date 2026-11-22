package com.examples.hm14_gros;

import android.graphics.Canvas;

public class Bug {

    enum BugState {
        Dead,
        ComingBackToLife,
        Alive,              // in the game
        DrawDead,           // draw dead body on screen
    };

    BugState state;         // current state of bug
    int x,y;                // location on screen (in screen coordinates)
    double speed;           // speed of bug (in pixels per second)
    // All times are in seconds
    float timeToBirth;      // # seconds until birth
    float startBirthTimer;  // starting timestamp when decide to be born
    float deathTime;        // time of death
    float animateTimer;     // used to move and animate the bug

    // Bug start not alive
    public Bug () {state = BugState.Dead;}

    // Bug birth processing
    public void birth (Canvas canvas) {
        // Bring a bug to life?
        if (state == BugState.Dead) {
            // Set state to come to life
            state = BugState.ComingBackToLife;
            // Set a random number off seconds before it comes to life
            timeToBirth = (float)Math.random() * 5;
            // Note the current time
            startBirthTimer = System.nanoTime() / 1e9f;     // 1e9 = 1_000_000_000 (one billion)
        }
        // Check if bug is alive yet
        else if (state == BugState.ComingBackToLife) {
            float curTime = System.nanoTime() / 1e9f;
            // Has birth timer expired?
            if (curTime - startBirthTimer >= timeToBirth) {
                // If so, then bring bug to life
                state = BugState.Alive;
                // Set bug starting location top of screen
                x = (int)(Math.random() * canvas.getWidth());
                // Keep entire bug on screen
                if (x < Assets.ant1.getWidth()/2)
                    x = Assets.ant1.getWidth()/2;
                else if (x > canvas.getWidth() - Assets.ant1.getWidth()/2)
                    x = canvas.getWidth() - Assets.ant1.getWidth()/2;
                y = 0;
                // Base speed: takes 4 seconds to cross the screen height
                speed = canvas.getHeight() / 4.0;
                // Calculate a multiplier between 1.0 (100% speed) and 1.30 (130% speed)
                // Math.random() * 0.30 gives a range of 0.0 to 0.30.
                // Adding 1.0 keeps the speed in a reasonable range of 1.0 to 1.60
                double speedMultiplier = (Math.random() * 0.30) + 1.0;
                // Multiply the base speed so it stays strictly positive
                speed = speed * speedMultiplier;
                // Record timestamp of this bug being born
                animateTimer = curTime;
            }
        }
    }

    // Bug movement processing
    public void move (Canvas canvas) {
        // Make sure this bug is alive
        if (state == BugState.Alive) {
            // Get elapsed time since last call here
            float curTime = System.nanoTime() / 1e9f;
            float elapsedTime = curTime - animateTimer;
            animateTimer = curTime;
            // Compute the amount of pixels to move (vertically down the screen)
            y += (speed * elapsedTime);

            // Cycle from 0 to 9 that changes every tenth of a second
            long animationTick = System.currentTimeMillis() / 100 % 10;

            // Draw bug by alternating between frames 1 and 2 if the tick is even or odd
            if (animationTick % 2 == 0)
                canvas.drawBitmap(Assets.ant1, x-Assets.ant1.getWidth()/2,y-Assets.ant1.getHeight()/2,null);
            else
                canvas.drawBitmap(Assets.ant2, x-Assets.ant1.getWidth()/2,y-Assets.ant1.getHeight()/2,null);

            // Has it reached the bottom of the screen?
            if (y >= canvas.getHeight()) {
                // Kill the bug
                state = BugState.Dead;
                // Subtract 1 life
                Assets.livesLeft--;

                // Only play the sound after time has passed since the last play
                if (curTime - Assets.lastBugEatTime >= 4.0f) {
                    Assets.soundPool.play(Assets.sound_bugeat, 1, 1, 1, 0, 1);
                    Assets.lastBugEatTime = curTime; // Update the global timestamp
                }
            }
        }
    }

    // Process touch to see if bug has been killed - return true if bug is killed
    public boolean touched (Canvas canvas, int touchx, int touchy) {
        boolean touched = false;
        // Make sure this bug is alive
        if (state == BugState.Alive) {
            // Compute distance between touch and center of bug
            float dis = (float)(Math.sqrt ((touchx - x) * (touchx - x) + (touchy - y) * (touchy - y)));
            // Is this close enough for a kill?
            if (dis <= Assets.ant1.getWidth() * 0.5f) {
                state = BugState.DrawDead;  // need to draw dead body on screen for a while
                touched = true;
                // Record time of death
                deathTime = System.nanoTime() / 1e9f;
            }
        }
        return (touched);
    }

    // Draw dead bug body on screen, if needed
    public void drawDead (Canvas canvas) {
        if (state == BugState.DrawDead) {
            canvas.drawBitmap(Assets.ant3, x-Assets.ant3.getWidth()/2, y-Assets.ant3.getHeight()/2,null);
            // Get time since death
            float curTime = System.nanoTime() / 1e9f;
            float timeSinceDeath = curTime - deathTime;
            // Draw dead body long enough (3 seconds)
            if (timeSinceDeath > 3)
                state = BugState.Dead;
        }
    }
}
