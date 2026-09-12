package com.okhedef.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Güvenli Tam Ekran (Immersive Mode)
        hideSystemUI();

        gameView = new GameView(this);
        setContentView(gameView);
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        if (gameView != null) {
            gameView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    public static class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

        private Thread gameThread = null;
        private SurfaceHolder surfaceHolder;
        private volatile boolean isPlaying;
        private Paint paint;

        private int screenWidth = 1080;
        private int screenHeight = 1920;

        public GameView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            resume();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (width > 0 && height > 0) {
                this.screenWidth = width;
                this.screenHeight = height;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            pause();
        }

        @Override
        public void run() {
            while (isPlaying) {
                update();
                draw();
                sleep();
            }
        }

        private void update() {
            // Oyun mantığı güncellemeleri
        }

        private void draw() {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    try {
                        // Ekranı temizle
                        canvas.drawColor(Color.BLACK);

                        // Örnek çizim (Hedef / Oyun Görselleri)
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(50);
                        canvas.drawText("Ok Hedef Pro", 100, 100, paint);
                        
                        // Ekran boyutunu kontrol çizimi
                        paint.setColor(Color.GREEN);
                        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void sleep() {
            try {
                Thread.sleep(17); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void resume() {
            isPlaying = true;
            if (gameThread == null || !gameThread.isAlive()) {
                gameThread = new Thread(this);
                gameThread.start();
            }
        }

        public void pause() {
            isPlaying = false;
            try {
                if (gameThread != null) {
                    gameThread.join();
                    gameThread = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }
    }
}
