package com.examples.hm14_gros;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class MainThread extends Thread {
    private SurfaceHolder holder;
    private Handler handler;        // required for running code in the UI thread
    private boolean isRunning = false;
    Context context;
    Paint paint;
    int touchx, touchy; // x,y of touch event
    boolean touched;    // true if touch happened
    boolean data_initialized;
    private static final Object lock = new Object();

    public MainThread (SurfaceHolder surfaceHolder, Context context) {
        holder = surfaceHolder;
        this.handler = new Handler();
        data_initialized = false;
        this.context = context;
        touched = false;
    }

    public void setRunning(boolean b) {
        isRunning = b;
    }

    // Set the touch event x,y location and flag indicating a touch has happened
    public void setXY (int x, int y) {
        synchronized (lock) {
            touchx = x;
            touchy = y;
            this.touched = true;
        }
    }

    @Override
    public void run() {
        //while (!isRunning);
        while (isRunning) {
            // Lock the canvas before drawing
            Canvas canvas = holder.lockCanvas();
            if (holder != null) {
                if (canvas != null) {
                    // Perform drawing operations on the canvas
                    render(canvas);
                    // After drawing, unlock the canvas and display it
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    // Loads graphics, etc. used in game
    private void LoadData (Canvas canvas) {
        Bitmap bmp;
        int newWidth, newHeight;
        float scaleFactor;

        // Create a paint object for drawing vector graphics
        paint = new Paint();

        // Load score bar
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.scorebar);
        // Compute size of bitmap needed (suppose want height = 10% of screen height)
        newHeight = (int)(canvas.getHeight() * 0.15f);
        // Scale it to a new size
        Assets.scorebar = Bitmap.createScaledBitmap(bmp, canvas.getWidth(), newHeight, false);
        // Delete the original
        bmp = null;

        // Load food bar
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.foodbar);
        // Compute size of bitmap needed (suppose want height = 10% of screen height)
        newHeight = (int)(canvas.getHeight() * 0.5f);
        // Scale it to a new size
        Assets.foodbar = Bitmap.createScaledBitmap(bmp, canvas.getWidth(), newHeight, false);
        // Delete the original
        bmp = null;


        // Load Heart
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        // Compute size of bitmap needed (suppose want width = 20% of screen width)
        newWidth = (int)(canvas.getWidth() * 0.1f);
        // What was the scaling factor to get to this?
        scaleFactor = (float)newWidth / bmp.getWidth();
        // Compute the new height
        newHeight = (int)(bmp.getHeight() * scaleFactor);
        // Scale it to a new size
        Assets.heart = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        // Load ant1
        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ant1);
        // Compute size of bitmap needed (suppose want width = 20% of screen width)
        newWidth = (int)(canvas.getWidth() * 0.2f);
        // What was the scaling factor to get to this?
        scaleFactor = (float)newWidth / bmp.getWidth();
        // Compute the new height
        newHeight = (int)(bmp.getHeight() * scaleFactor);
        // Scale it to a new size
        Assets.ant1 = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ant2);
        // Scale it to a new size
        Assets.ant2 = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ant3);
        // Scale it to a new size
        Assets.ant3 = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
        // Delete the original
        bmp = null;

        // First create the bug array of size 10 and then instantiate each individual bug object in the loop
        Assets.bug = new Bug[10];
        for (int i = 0; i < 10; i++) {
            Assets.bug[i] = new Bug();
        }
    }

    // Load specific background screen
    private void loadBackground (Canvas canvas, int resId) {
        // Load background
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resId);
        // Scale it to fill entire canvas
        Assets.playbackground = Bitmap.createScaledBitmap(bmp, canvas.getWidth(), canvas.getHeight(), false);
        // Delete the original
        bmp = null;
    }

    private void render (Canvas canvas) {
        int i, x, y;

        if (!data_initialized) {
            LoadData(canvas);
            data_initialized = true;
        }

        switch (Assets.state) {
            case GettingReady:
                loadBackground(canvas, R.drawable.playbackground);
                // Draw the background screen
                canvas.drawBitmap(Assets.playbackground, 0, 0, null);
                // Play a sound effect
                Assets.soundPool.play(Assets.sound_getready, 1, 1, 1, 0, 1);
                // Start a timer
                Assets.gameTimer = System.nanoTime() / 1000000000f;
                // Go to next state
                Assets.state = Assets.GameState.Starting;
                break;
            case Starting:
                // Draw the background Screen
                canvas.drawBitmap(Assets.playbackground, 0, 0, null);
                // Has 3 seconds elapsed?
                float currentTime = System.nanoTime() / 1000000000f;
                if (currentTime - Assets.gameTimer >= 3)
                    // Goto next state
                    Assets.state = Assets.GameState.Running;
                break;
            case Running:
                // Draw the background screen
                canvas.drawBitmap(Assets.playbackground, 0, 0, null);
                // Draw the score bar at top of screen
                canvas.drawBitmap(Assets.scorebar, 0, 0, null);
                // Draw the foodbar at bottom of screen
                canvas.drawBitmap(Assets.foodbar, 0, canvas.getHeight() - (Assets.foodbar.getHeight() - (Assets.foodbar.getWidth()*0.6f)), null);

                // Draw one heart image for each life at top right corner of screen
                // int spacing = 3;    // spacing in between hearts
                x = canvas.getWidth() - (Assets.heart.getWidth());   // coordinates for rightmost heart
                y = Assets.heart.getHeight() + 10;
                for (i = 0; i < Assets.livesLeft; i++) {
                    canvas.drawBitmap(Assets.heart, x, y, null);
                    // Reposition to draw the next heart to the left
                    x -= (Assets.heart.getWidth()+10);
                }

                // Process touch input across all bugs
                if (touched) {
                    touched = false; // Reset touch flag
                    boolean soundPlayed = false;
                    // Generate a random integer between 1 and 3
                    int randomSound = (int) (Math.random() * 3) + 1;

                    // Check if a bug has been touched
                    for (i = 0; i <= 9; i++) {
                        if (Assets.bug[i].touched(canvas, touchx, touchy)) {
                            // If a bug was killed play a random squish sound based on the created randomSound
                            switch (randomSound) {
                                case 1:
                                    Assets.soundPool.play(Assets.sound_squish1, 1, 1, 1, 0, 1);
                                    break;
                                case 2:
                                    Assets.soundPool.play(Assets.sound_squish2, 1, 1, 1, 0, 1);
                                    break;
                                case 3:
                                    Assets.soundPool.play(Assets.sound_squish3, 1, 1, 1, 0, 1);
                                    break;
                            }

                            soundPlayed = true;
                            break; // Confirm this instance bug was clicked and don't check other instances
                        }
                    }

                    // If a touch happened but no bug was hit play a random miss sound
                    if (!soundPlayed) {
                        switch (randomSound) {
                            case 1:
                                Assets.soundPool.play(Assets.sound_thump1, 1, 1, 1, 0, 1);
                                break;
                            case 2:
                                Assets.soundPool.play(Assets.sound_thump2, 1, 1, 1, 0, 1);
                                break;
                            case 3:
                                Assets.soundPool.play(Assets.sound_thump3, 1, 1, 1, 0, 1);
                                break;
                        }
                    }
                }

                // Loop through every bug to handle drawing, moving, and birthing
                for (i = 0; i < 10; i++) {
                    // Draw dead bugs on screen
                    Assets.bug[i].drawDead(canvas);
                    // Move bugs on screen
                    Assets.bug[i].move(canvas);
                    // Bring a dead bug to life?
                    Assets.bug[i].birth(canvas);
                }

                // DRAW SCORE
                // canvas.drawText(String.valueOf(Assets.score), 8, 8, paint);

                // Are no lives left?
                if (Assets.livesLeft <= 0)
                    // Goto next state
                    Assets.state = Assets.GameState.GameEnding;
                break;
            case GameEnding:
                // Show a game over message
                handler.post(() -> {
                    Toast.makeText(context, "Game over", Toast.LENGTH_SHORT).show();
                });
                // Goto next state
                Assets.state = Assets.GameState.GameOver;
                break;
            case GameOver:
                // Fill the entire canvas bitmap with 'black'
                canvas.drawColor(Color.BLACK);
                break;
        }
    }
}
