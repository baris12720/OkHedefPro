package com.okhedef.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        hideSystemUI();

        gameView = new GameView(this);
        setContentView(gameView);
    }

    private void hideSystemUI() {
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } catch (Exception e) {
            Log.e("OkHedefPro", "SystemUI Hide Error: " + e.getMessage());
        }
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
        private volatile boolean isPlaying = false;
        private volatile boolean isSurfaceReady = false;
        private Paint paint;

        private int screenWidth = 1080;
        private int screenHeight = 1920;

        public GameView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            paint = new Paint();
            paint.setAntiAlias(true);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isSurfaceReady = true;
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
            isSurfaceReady = false;
            pause();
        }

        @Override
        public void run() {
            while (isPlaying) {
                if (isSurfaceReady) {
                    update();
                    draw();
                }
                sleep();
            }
        }

        private void update() {
            // Oyun mantığı güncellemeleri
        }

        private void draw() {
            if (!surfaceHolder.getSurface().isValid()) {
                return;
            }

            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    // Yeşil Arka Plan (Siyah Ekranı Engellemek İçin)
                    canvas.drawColor(Color.parseColor("#2E7D32"));

                    // Ekran Ortasına Yazı Çizimi
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(screenWidth / 20f > 30 ? screenWidth / 20f : 40);
                    paint.setTextAlign(Paint.Align.CENTER);
                    
                    canvas.drawText("OK HEDEF PRO", screenWidth / 2f, screenHeight / 3f, paint);
                    
                    paint.setTextSize(screenWidth / 35f > 20 ? screenWidth / 35f : 30);
                    paint.setColor(Color.YELLOW);
                    canvas.drawText("Oyuna Başlamak İçin Ekrana Dokunun", screenWidth / 2f, screenHeight / 2f, paint);
                }
            } catch (Exception e) {
                Log.e("OkHedefPro", "Draw Error: " + e.getMessage());
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        Log.e("OkHedefPro", "Unlock Canvas Error: " + e.getMessage());
                    }
                }
            }
        }

        private void sleep() {
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException ignored) {
            }
        }

        public synchronized void resume() {
            if (!isPlaying) {
                isPlaying = true;
                gameThread = new Thread(this);
                gameThread.start();
            }
        }

        public synchronized void pause() {
            isPlaying = false;
            if (gameThread != null) {
                try {
                    gameThread.join(500);
                } catch (InterruptedException ignored) {
                }
                gameThread = null;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return true;
        }
    }
}
