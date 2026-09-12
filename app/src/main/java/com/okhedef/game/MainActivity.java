package com.okhedef.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.WindowInsetsController;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends Activity {
    private GameView gameView;
    private RewardedAd rewardedAd;
    private boolean isLoadingAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUI();

        MobileAds.initialize(this, initializationStatus -> {});
        loadRewardedAd();

        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUI();
    }

    private void hideSystemUI() {
        try {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } catch (Exception e) {
            // ignore, not critical
        }
    }

    public void loadRewardedAd() {
        if (rewardedAd == null && !isLoadingAd) {
            isLoadingAd = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedAd ad) {
                            rewardedAd = ad;
                            isLoadingAd = false;
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            rewardedAd = null;
                            isLoadingAd = false;
                        }
                    });
        }
    }

    public void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                gameView.addBonusArrows(3);
                rewardedAd = null;
                loadRewardedAd();
            });
        } else {
            loadRewardedAd();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    // --- GAME VIEW & ENGINE ---
    public static class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
        private Thread gameThread;
        private volatile boolean playing;
        private SurfaceHolder surfaceHolder;
        private Paint paint;
        private MainActivity activity;

        private int screenWidth = 1, screenHeight = 1;

        private enum State { MENU, PLAYING, GAMEOVER }
        private State gameState = State.MENU;
        private int level = 1;

        private int score = 0;
        private int highScore = 0;
        private int arrowsLeft = 5;
        private SharedPreferences prefs;

        private float bowX = 150, bowY = 0;
        private boolean isAiming = false;
        private float pullX = 0, pullY = 0;
        
        private Arrow flyingArrow = null;
        private ArrayList<Arrow> stuckArrows = new ArrayList<>();
        private ArrayList<FloatingText> floatingTexts = new ArrayList<>();
        private ArrayList<Particle> particles = new ArrayList<>();

        private float targetX = 0, targetY = 0;
        private float targetRadius = 90;
        private float targetSpeedY = 3.5f;
        private int targetDir = 1;

        private float wind = 0f;
        private Random random = new Random();
        private Vibrator vibrator;

        private Bitmap bmpBow, bmpArrow, bmpTarget;
        private boolean useBitmaps = false;

        public GameView(Context context) {
            super(context);
            this.activity = (MainActivity) context;
            surfaceHolder = getHolder();
            paint = new Paint();
            paint.setAntiAlias(true);

            prefs = context.getSharedPreferences("OkHedefPrefs", Context.MODE_PRIVATE);
            highScore = prefs.getInt("highScore", 0);
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            loadBitmaps();
        }

        private void loadBitmaps() {
            try {
                bmpBow = BitmapFactory.decodeResource(getResources(), R.drawable.bow);
                bmpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
                bmpTarget = BitmapFactory.decodeResource(getResources(), R.drawable.target);
                if (bmpBow != null && bmpArrow != null && bmpTarget != null) {
                    useBitmaps = true;
                }
            } catch (Exception e) {
                useBitmaps = false;
            }
        }

        public void addBonusArrows(int count) {
            arrowsLeft += count;
            gameState = State.PLAYING;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            screenWidth = getWidth();
            screenHeight = getHeight();

            bowX = screenWidth / 2f;
            bowY = screenHeight - 220;

            targetX = screenWidth / 2f;
            targetY = 240;

            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            screenWidth = width;
            screenHeight = height;

            bowX = screenWidth / 2f;
            bowY = screenHeight - 220;
            targetX = screenWidth / 2f;
            targetY = 240;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            playing = false;
            try {
                if (gameThread != null) gameThread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        @Override
        public void run() {
            while (playing) {
                if (!surfaceHolder.getSurface().isValid()) continue;

                long startTime = System.currentTimeMillis();

                update();
                draw();

                long timeMillis = System.currentTimeMillis() - startTime;
                long waitTime = 16 - timeMillis;
                if (waitTime > 0) {
                    try {
                        Thread.sleep(waitTime);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }

        private void update() {
            if (gameState == State.PLAYING) {
                float currentSpeed = targetSpeedY + (score / 40f);
                targetX += currentSpeed * targetDir;
                if (targetX - targetRadius < 40 || targetX + targetRadius > screenWidth - 40) {
                    targetDir *= -1;
                }

                if (flyingArrow != null) {
                    flyingArrow.x += flyingArrow.vx;
                    flyingArrow.y += flyingArrow.vy;
                    flyingArrow.vx += wind * 0.02f;
                    flyingArrow.vy += 0.18f;

                    float distToCenter = (float) Math.hypot(flyingArrow.x - targetX, flyingArrow.y - targetY);

                    if (flyingArrow.x > screenWidth || flyingArrow.x < 0 || flyingArrow.y > screenHeight || flyingArrow.y < -150) {
                        missArrow();
                    } else if (distToCenter <= targetRadius) {
                        hitTarget(distToCenter);
                    }
                }

                Iterator<FloatingText> ftIter = floatingTexts.iterator();
                while (ftIter.hasNext()) {
                    FloatingText ft = ftIter.next();
                    ft.y -= 2f;
                    ft.alpha -= 0.03f;
                    if (ft.alpha <= 0) ftIter.remove();
                }

                Iterator<Particle> pIter = particles.iterator();
                while (pIter.hasNext()) {
                    Particle p = pIter.next();
                    p.x += p.vx;
                    p.y += p.vy;
                    p.alpha -= 0.05f;
                    if (p.alpha <= 0) pIter.remove();
                }
            }
        }

        private void hitTarget(float dist) {
            int earnedPoints = 0;
            String text = "";
            boolean isBullseye = false;

            if (dist <= 15) {
                earnedPoints = 10;
                text = "+10 (TAM MERKEZ!)";
                isBullseye = true;
                triggerHaptic(90);
                SoundGenerator.playBullseye();
            } else if (dist <= 40) {
                earnedPoints = 8;
                text = "+8 Puan";
                triggerHaptic(60);
                SoundGenerator.playTargetHit();
            } else if (dist <= 65) {
                earnedPoints = 5;
                text = "+5 Puan";
                triggerHaptic(40);
                SoundGenerator.playTargetHit();
            } else {
                earnedPoints = 2;
                text = "+2 Puan";
                triggerHaptic(25);
                SoundGenerator.playTargetHit();
            }

            score += earnedPoints;
            if (score > highScore) {
                highScore = score;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highScore", highScore);
                editor.apply();
            }

            if (isBullseye) {
                arrowsLeft++;
                floatingTexts.add(new FloatingText("BONUS +1 OK!", targetX, targetY - 60, Color.GREEN));
            }

            floatingTexts.add(new FloatingText(text, targetX, targetY, Color.YELLOW));
            spawnParticles(targetX, targetY, 18);

            int newLevel = 1 + (score / 50);
            if (newLevel > level) {
                level = newLevel;
                floatingTexts.add(new FloatingText("SEVIYE " + level + "!", screenWidth / 2f, screenHeight / 2f, Color.parseColor("#FFD700")));
                spawnParticles(targetX, targetY, 45);
                triggerHaptic(150);
                SoundGenerator.playBullseye();
            }

            stuckArrows.add(new Arrow(flyingArrow.x, flyingArrow.y, 0, 0, true));
            flyingArrow = null;

            checkNextRound();
        }

        private void missArrow() {
            flyingArrow = null;
            floatingTexts.add(new FloatingText("KAÇTI!", screenWidth / 2f, screenHeight / 3f, Color.RED));
            triggerHaptic(120);
            checkNextRound();
        }

        private void checkNextRound() {
            arrowsLeft--;
            if (arrowsLeft <= 0) {
                gameState = State.GAMEOVER;
                activity.loadRewardedAd();
            } else {
                wind = (random.nextFloat() * 10f) - 5f;
            }
        }

        private void spawnParticles(float cx, float cy) {
            spawnParticles(cx, cy, 15);
        }

        private void spawnParticles(float cx, float cy, int count) {
            int[] colors = { Color.YELLOW, Color.parseColor("#FF7043"), Color.parseColor("#FFD700"), Color.RED, Color.WHITE };
            for (int i = 0; i < count; i++) {
                float angle = random.nextFloat() * (float)(Math.PI * 2);
                float speed = random.nextFloat() * 8f + 2f;
                Particle p = new Particle(cx, cy, (float)Math.cos(angle) * speed, (float)Math.sin(angle) * speed);
                p.color = colors[random.nextInt(colors.length)];
                particles.add(p);
            }
        }

        private void triggerHaptic(long duration) {
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(duration);
                }
            }
        }

        private void draw() {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;

            canvas.drawColor(Color.parseColor("#12121f"));

            if (gameState == State.MENU) {
                drawMenu(canvas);
            } else if (gameState == State.PLAYING) {
                drawGame(canvas);
            } else if (gameState == State.GAMEOVER) {
                drawGameOver(canvas);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }

        private void drawMenu(Canvas canvas) {
            paint.setTextSize(85);
            paint.setColor(Color.parseColor("#e94560"));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("OK HEDEF PRO", screenWidth / 2f, screenHeight / 3f, paint);

            paint.setTextSize(40);
            paint.setColor(Color.WHITE);
            canvas.drawText("En Yüksek Skor: " + highScore, screenWidth / 2f, screenHeight / 2f - 40, paint);

            RectF btnRect = new RectF(screenWidth / 2f - 220, screenHeight / 2f + 20, screenWidth / 2f + 220, screenHeight / 2f + 140);
            paint.setColor(Color.parseColor("#0f3460"));
            canvas.drawRoundRect(btnRect, 35, 35, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(45);
            canvas.drawText("OYUNA BAŞLA", screenWidth / 2f, screenHeight / 2f + 95, paint);
        }

        private void drawGame(Canvas canvas) {
            paint.setTextSize(38);
            paint.setColor(Color.CYAN);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(String.format("Rüzgar: %.1f", wind), 40, 70, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("Skor: " + score, 40, 120, paint);
            canvas.drawText("Kalan Ok: " + arrowsLeft, 40, 170, paint);

            drawTarget(canvas, targetX, targetY);

            for (int i = 0; i < stuckArrows.size(); i++) {
                Arrow a = stuckArrows.get(i);
                drawArrowGraphic(canvas, a.x, a.y);
            }

            paint.setColor(Color.parseColor("#d4af37"));
            paint.setStrokeWidth(12);
            canvas.drawLine(bowX - 80, bowY, bowX + 80, bowY, paint);

            paint.setStrokeWidth(4);
            paint.setColor(Color.WHITE);
            canvas.drawLine(bowX - 80, bowY, bowX, bowY - 35, paint);
            canvas.drawLine(bowX, bowY - 35, bowX + 80, bowY, paint);

            if (flyingArrow != null) {
                drawArrowGraphic(canvas, flyingArrow.x, flyingArrow.y);
            }

            for (int i = 0; i < particles.size(); i++) {
                Particle p = particles.get(i);
                paint.setColor(p.color);
                paint.setAlpha((int)(p.alpha * 255));
                canvas.drawCircle(p.x, p.y, 7, paint);
            }
            paint.setAlpha(255);

            for (int i = 0; i < floatingTexts.size(); i++) {
                FloatingText ft = floatingTexts.get(i);
                paint.setTextSize(42);
                paint.setColor(ft.color);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setAlpha((int)(ft.alpha * 255));
                canvas.drawText(ft.text, ft.x, ft.y, paint);
            }
            paint.setAlpha(255);
        }

        private void drawTarget(Canvas canvas, float cx, float cy) {
            if (useBitmaps && bmpTarget != null) {
                canvas.drawBitmap(bmpTarget, cx - targetRadius, cy - targetRadius, paint);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(cx, cy, targetRadius, paint);
                paint.setColor(Color.BLACK);
                canvas.drawCircle(cx, cy, targetRadius * 0.8f, paint);
                paint.setColor(Color.BLUE);
                canvas.drawCircle(cx, cy, targetRadius * 0.6f, paint);
                paint.setColor(Color.RED);
                canvas.drawCircle(cx, cy, targetRadius * 0.4f, paint);
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, targetRadius * 0.18f, paint);
            }
        }

        private void drawArrowGraphic(Canvas canvas, float x, float y) {
            if (useBitmaps && bmpArrow != null) {
                canvas.drawBitmap(bmpArrow, x - 50, y - 15, paint);
            } else {
                paint.setColor(Color.parseColor("#a64b2a"));
                paint.setStrokeWidth(9);
                canvas.drawLine(x, y, x - 90, y, paint);
            }
        }

        private void drawGameOver(Canvas canvas) {
            paint.setTextSize(75);
            paint.setColor(Color.parseColor("#e94560"));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("OYUN BİTTİ", screenWidth / 2f, screenHeight / 3f - 40, paint);

            paint.setTextSize(45);
            paint.setColor(Color.WHITE);
            canvas.drawText("Skor: " + score + " | En Yüksek: " + highScore, screenWidth / 2f, screenHeight / 3f + 30, paint);

            RectF adBtnRect = new RectF(screenWidth / 2f - 240, screenHeight / 2f - 10, screenWidth / 2f + 240, screenHeight / 2f + 90);
            paint.setColor(Color.parseColor("#e94560"));
            canvas.drawRoundRect(adBtnRect, 30, 30, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(36);
            canvas.drawText("VİDEO İZLE (+3 OK KAZAN)", screenWidth / 2f, screenHeight / 2f + 50, paint);

            RectF restartRect = new RectF(screenWidth / 2f - 240, screenHeight / 2f + 110, screenWidth / 2f + 240, screenHeight / 2f + 210);
            paint.setColor(Color.parseColor("#0f3460"));
            canvas.drawRoundRect(restartRect, 30, 30, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("TEKRAR OYNA", screenWidth / 2f, screenHeight / 2f + 172, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (gameState == State.MENU) {
                        if (x >= screenWidth / 2f - 220 && x <= screenWidth / 2f + 220 &&
                                y >= screenHeight / 2f + 20 && y <= screenHeight / 2f + 140) {
                            startGame();
                        }
                    } else if (gameState == State.PLAYING) {
                        if (flyingArrow == null) {
                            shootArrowTo(x, y);
                        }
                    } else if (gameState == State.GAMEOVER) {
                        if (x >= screenWidth / 2f - 240 && x <= screenWidth / 2f + 240 &&
                                y >= screenHeight / 2f - 10 && y <= screenHeight / 2f + 90) {
                            activity.showRewardedAd();
                        } else if (x >= screenWidth / 2f - 240 && x <= screenWidth / 2f + 240 &&
                                y >= screenHeight / 2f + 110 && y <= screenHeight / 2f + 210) {
                            gameState = State.MENU;
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (isAiming) {
                        pullX = Math.max(bowX - 220, Math.min(bowX, x));
                        pullY = Math.max(bowY - 220, Math.min(bowY + 220, y));
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (isAiming) {
                        isAiming = false;
                        float forceX = (bowX - pullX) * 0.28f;
                        float forceY = (bowY - pullY) * 0.28f;

                        flyingArrow = new Arrow(bowX, bowY, forceX, forceY, false);
                        SoundGenerator.playArrowRelease();
                        triggerHaptic(45);
                    }
                    break;
            }
            return true;
        }

        private void shootArrowTo(float touchX, float touchY) {
            float dx = touchX - bowX;
            float dy = touchY - bowY;
            float dist = (float) Math.hypot(dx, dy);
            if (dist < 1f) dist = 1f;
            float speed = 22f;
            float vx = (dx / dist) * speed;
            float vy = (dy / dist) * speed;
            flyingArrow = new Arrow(bowX, bowY, vx, vy, false);
            SoundGenerator.playArrowRelease();
            triggerHaptic(45);
        }

        private void startGame() {
            score = 0;
            arrowsLeft = 5;
            stuckArrows.clear();
            floatingTexts.clear();
            particles.clear();
            flyingArrow = null;
            wind = (random.nextFloat() * 10f) - 5f;
            gameState = State.PLAYING;
        }

        public void pause() {
            playing = false;
            try {
                if (gameThread != null) gameThread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public static class Arrow {
        public float x, y;
        public float vx, vy;
        public boolean stuck;
        public Arrow(float x, float y, float vx, float vy, boolean stuck) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.stuck = stuck;
        }
    }

    public static class FloatingText {
        public String text;
        public float x, y;
        public int color;
        public float alpha = 1.0f;
        public FloatingText(String text, float x, float y, int color) {
            this.text = text; this.x = x; this.y = y; this.color = color;
        }
    }

    public static class Particle {
        public float x, y, vx, vy;
        public float alpha = 1.0f;
        public int color = Color.YELLOW;
        public Particle(float x, float y, float vx, float vy) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
        }
    }
}
