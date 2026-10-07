package com.driver.panjakent;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.*;
import android.view.*;
import android.content.Context;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setRequestedOrientation(
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        );
        setContentView(new Game(this));
    }

    class Game extends View {

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random random = new Random();

        float playerX;
        float speed = 3;
        float fuel = 100;
        int money = 0;

        boolean left, right, gas, brake;
        boolean night = false;
        boolean crash = false;

        int road = 0;

        float roadMove = 0;

        float[] carsX = new float[10];
        float[] carsY = new float[10];
        float[] carsSpeed = new float[10];
        int[] carsColor = new int[10];

        float[] coinsX = new float[15];
        float[] coinsY = new float[15];

        long crashUntil = 0;

        Game(Context c) {
            super(c);
            setKeepScreenOn(true);

            for (int i = 0; i < 10; i++) {
                carsY[i] = -200 - i * 300;
                carsSpeed[i] = 1 + random.nextFloat() * 3;
                carsColor[i] = Color.rgb(
                        random.nextInt(220) + 20,
                        random.nextInt(220) + 20,
                        random.nextInt(220) + 20
                );
            }

            for (int i = 0; i < 15; i++) {
                coinsY[i] = -300 - i * 250;
            }
        }

        @Override
        protected void onDraw(Canvas c) {

            int w = getWidth();
            int h = getHeight();

            if (playerX == 0) {
                playerX = w / 2f;
            }

            drawBackground(c, w, h);
            drawRoad(c, w, h);
            update(w, h);
            drawCars(c);
            drawCoins(c);
            drawPlayer(c);
            drawUI(c, w, h);

            if (crash) {
                p.setColor(Color.argb(150, 255, 0, 0));
                c.drawRect(0, 0, w, h, p);

                p.setColor(Color.WHITE);
                p.setTextSize(55);
                p.setTypeface(Typeface.DEFAULT_BOLD);
                c.drawText(
                        "САДАМА!",
                        w / 2f - 115,
                        h / 2f,
                        p
                );
            }

            postInvalidateDelayed(16);
        }

        void update(int w, int h) {

            // ГАЗ
            if (gas) {
                speed += 0.12f;

                if (speed > 12) {
                    speed = 12;
                }
            }

            // ТОРМОЗ
            if (brake) {
                speed -= 0.18f;

                if (speed < 0) {
                    speed = 0;
                }
            }

            // Суръати оддӣ
            if (!gas && !brake) {
                speed -= 0.01f;

                if (speed < 2) {
                    speed = 2;
                }
            }

            // ЧАП
            if (left) {
                playerX -= 6;
            }

            // РОСТ
            if (right) {
                playerX += 6;
            }

            float roadLeft = w * 0.25f;
            float roadRight = w * 0.75f;

            if (playerX < roadLeft + 45) {
                playerX = roadLeft + 45;
            }

            if (playerX > roadRight - 45) {
                playerX = roadRight - 45;
            }

            // Ҳаракати роҳ
            roadMove += speed;

            if (roadMove > 100) {
                roadMove = 0;
            }

            // БЕНЗИН ОҲИСТА КАМ МЕШАВАД
            fuel -= speed * 0.00035f;

            if (fuel < 0) {
                fuel = 0;
                speed = 0;
            }

            // Мошинҳо
            for (int i = 0; i < 10; i++) {

                carsY[i] += speed + carsSpeed[i];

                if (carsY[i] > h + 150) {

                    carsY[i] = -random.nextInt(900) - 100;

                    carsX[i] =
                            roadLeft + 50 +
                            random.nextFloat() *
                            (roadRight - roadLeft - 100);
                }

                // САДАМА
                if (Math.abs(playerX - carsX[i]) < 65 &&
                        Math.abs(h * 0.75f - carsY[i]) < 100) {

                    crash = true;
                    crashUntil =
                            System.currentTimeMillis() + 1200;

                    speed = 1;

                    fuel -= 2;

                    if (fuel < 0) {
                        fuel = 0;
                    }

                    carsY[i] = -500;
                }
            }

            // ПУЛ
            for (int i = 0; i < 15; i++) {

                coinsY[i] += speed;

                if (coinsY[i] > h + 50) {

                    coinsY[i] =
                            -random.nextInt(1000) - 100;

                    coinsX[i] =
                            roadLeft + 40 +
                            random.nextFloat() *
                            (roadRight - roadLeft - 80);
                }

                if (Math.abs(playerX - coinsX[i]) < 45 &&
                        Math.abs(h * 0.75f - coinsY[i]) < 70) {

                    money += 10;

                    coinsY[i] =
                            -random.nextInt(1000) - 100;
                }
            }

            if (crash &&
                    System.currentTimeMillis() > crashUntil) {

                crash = false;
            }
        }

        void drawBackground(Canvas c, int w, int h) {

            if (night) {
                p.setColor(Color.rgb(15, 25, 60));
            } else {
                p.setColor(Color.rgb(100, 190, 255));
            }

            c.drawRect(0, 0, w, h, p);

            // Кӯҳҳо
            p.setColor(
                    night
                            ? Color.rgb(35, 45, 65)
                            : Color.rgb(80, 130, 90)
            );

            Path mountain = new Path();

            mountain.moveTo(0, h * .55f);
            mountain.lineTo(w * .15f, h * .25f);
            mountain.lineTo(w * .30f, h * .55f);
            mountain.lineTo(w * .48f, h * .28f);
            mountain.lineTo(w * .65f, h * .55f);
            mountain.lineTo(w * .82f, h * .30f);
            mountain.lineTo(w, h * .55f);
            mountain.close();

            c.drawPath(mountain, p);

            // Замин
            p.setColor(
                    night
                            ? Color.rgb(20, 55, 35)
                            : Color.rgb(60, 150, 65)
            );

            c.drawRect(
                    0,
                    h * .55f,
                    w,
                    h,
                    p
            );

            // Офтоб / моҳ
            p.setColor(
                    night
                            ? Color.LTGRAY
                            : Color.YELLOW
            );

            c.drawCircle(
                    w - 100,
                    70,
                    30,
                    p
            );
        }

        void drawRoad(Canvas c, int w, int h) {

            float left = w * .25f;
            float right = w * .75f;

            // 4 НАМУДИ РОҲ
            if (road == 0) {
                p.setColor(Color.DKGRAY);
            }

            if (road == 1) {
                p.setColor(Color.rgb(75, 75, 75));
            }

            if (road == 2) {
                p.setColor(Color.rgb(95, 80, 70));
            }

            if (road == 3) {
                p.setColor(Color.rgb(40, 40, 40));
            }

            c.drawRect(left, 0, right, h, p);

            // Канорҳо
            p.setColor(Color.WHITE);

            c.drawRect(left, 0, left + 7, h, p);
            c.drawRect(right - 7, 0, right, h, p);

            // Хатҳои роҳ
            p.setColor(Color.YELLOW);

            float lane1 =
                    left + (right - left) / 3;

            float lane2 =
                    left + (right - left) * 2 / 3;

            for (float y = -100 + roadMove;
                 y < h;
                 y += 110) {

                c.drawRect(
                        lane1 - 3,
                        y,
                        lane1 + 3,
                        y + 50,
                        p
                );

                c.drawRect(
                        lane2 - 3,
                        y,
                        lane2 + 3,
                        y + 50,
                        p
                );
            }
        }

        void drawCars(Canvas c) {

            for (int i = 0; i < 10; i++) {

                if (carsY[i] < -100 ||
                        carsY[i] > getHeight() + 100) {
                    continue;
                }

                drawCar(
                        c,
                        carsX[i],
                        carsY[i],
                        carsColor[i]
                );
            }
        }

        void drawPlayer(Canvas c) {

            drawCar(
                    c,
                    playerX,
                    getHeight() * .75f,
                    Color.RED
            );
        }

        void drawCar(
                Canvas c,
                float x,
                float y,
                int color
        ) {

            // Бадан
            p.setColor(color);

            c.drawRoundRect(
                    x - 35,
                    y - 65,
                    x + 35,
                    y + 65,
                    15,
                    15,
                    p
            );

            // Шиша
            p.setColor(Color.rgb(35, 80, 110));

            c.drawRoundRect(
                    x - 25,
                    y - 38,
                    x + 25,
                    y + 5,
                    10,
                    10,
                    p
            );

            // Чароғ
            p.setColor(Color.WHITE);

            c.drawCircle(
                    x - 20,
                    y - 55,
                    6,
                    p
            );

            c.drawCircle(
                    x + 20,
                    y - 55,
                    6,
                    p
            );

            // Чархҳо
            p.setColor(Color.BLACK);

            c.drawRect(
                    x - 42,
                    y - 45,
                    x - 32,
                    y - 10,
                    p
            );

            c.drawRect(
                    x + 32,
                    y - 45,
                    x + 42,
                    y - 10,
                    p
            );

            c.drawRect(
                    x - 42,
                    y + 15,
                    x - 32,
                    y + 50,
                    p
            );

            c.drawRect(
                    x + 32,
                    y + 15,
                    x + 42,
                    y + 50,
                    p
            );
        }

        void drawCoins(Canvas c) {

            for (int i = 0; i < 15; i++) {

                if (coinsY[i] < -50 ||
                        coinsY[i] > getHeight() + 50) {
                    continue;
                }

                p.setColor(Color.YELLOW);

                c.drawCircle(
                        coinsX[i],
                        coinsY[i],
                        16,
                        p
                );

                p.setColor(Color.BLACK);
                p.setTextSize(18);

                c.drawText(
                        "$",
                        coinsX[i] - 5,
                        coinsY[i] + 6,
                        p
                );
            }
        }

        void drawUI(Canvas c, int w, int h) {

            // Панели
            p.setColor(Color.argb(180, 0, 0, 0));

            c.drawRoundRect(
                    15, 15, 350, 100,
                    15, 15, p
            );

            p.setColor(Color.WHITE);
            p.setTypeface(Typeface.DEFAULT_BOLD);
            p.setTextSize(19);

            c.drawText(
                    "DRIVER PANJAKENT",
                    30, 42, p
            );

            c.drawText(
                    "Пул: " + money,
                    30, 70, p
            );

            c.drawText(
                    "Бензин: " +
                            (int)fuel + "%",
                    160, 70, p
            );

            c.drawText(
                    "Суръат: " +
                            (int)(speed * 10),
                    30, 93, p
            );

            c.drawText(
                    night ? "ШАБ" : "РӮЗ",
                    w - 100,
                    40,
                    p
            );

            // ЧАП
            button(
                    c,
                    25,
                    h - 100,
                    105,
                    h - 25,
                    "ЧАП"
            );

            // РОСТ
            button(
                    c,
                    115,
                    h - 100,
                    195,
                    h - 25,
                    "РОСТ"
            );

            // ТОРМОЗ
            button(
                    c,
                    w - 310,
                    h - 100,
                    w - 210,
                    h - 25,
                    "ТОРМ"
            );

            // ГАЗ
            button(
                    c,
                    w - 200,
                    h - 100,
                    w - 100,
                    h - 25,
                    "ГАЗ"
            );

            // РУЛ
            p.setColor(Color.argb(200, 0, 0, 0));

            c.drawCircle(
                    w - 50,
                    h - 55,
                    42,
                    p
            );

            p.setColor(Color.WHITE);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(7);

            c.drawCircle(
                    w - 50,
                    h - 55,
                    27,
                    p
            );

            c.drawLine(
                    w - 75,
                    h - 80,
                    w - 25,
                    h - 30,
                    p
            );

            c.drawLine(
                    w - 25,
                    h - 80,
                    w - 75,
                    h - 30,
                    p
            );

            p.setStyle(Paint.Style.FILL);

            // Тугмаи роҳ
            button(
                    c,
                    w - 150,
                    75,
                    w - 20,
                    120,
                    "РОҲ"
            );

            // Рӯз / шаб
            button(
                    c,
                    w - 150,
                    130,
                    w - 20,
                    175,
                    "РӮЗ/ШАБ"
            );
        }

        void button(
                Canvas c,
                float l,
                float t,
                float r,
                float b,
                String text
        ) {

            p.setColor(Color.argb(190, 0, 0, 0));

            c.drawRoundRect(
                    l, t, r, b,
                    15, 15, p
            );

            p.setColor(Color.WHITE);
            p.setTextSize(18);
            p.setTypeface(Typeface.DEFAULT_BOLD);

            float x =
                    (l + r) / 2 -
                    p.measureText(text) / 2;

            c.drawText(
                    text,
                    x,
                    (t + b) / 2 + 7,
                    p
            );
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            int action = e.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_MOVE) {

                float x = e.getX();
                float y = e.getY();

                int w = getWidth();
                int h = getHeight();

                left = false;
                right = false;
                gas = false;
                brake = false;

                // ЧАП
                if (x >= 25 &&
                        x <= 105 &&
                        y >= h - 120) {
                    left = true;
                }

                // РОСТ
                if (x >= 115 &&
                        x <= 195 &&
                        y >= h - 120) {
                    right = true;
                }

                // ТОРМОЗ
                if (x >= w - 310 &&
                        x <= w - 210 &&
                        y >= h - 120) {
                    brake = true;
                }

                // ГАЗ
                if (x >= w - 200 &&
                        x <= w - 100 &&
                        y >= h - 120) {
                    gas = true;
                }

                // РОҲ
                if (action == MotionEvent.ACTION_DOWN &&
                        x >= w - 150 &&
                        x <= w - 20 &&
                        y >= 75 &&
                        y <= 120) {

                    road++;

                    if (road > 3) {
                        road = 0;
                    }
                }

                // РӮЗ / ШАБ
                if (action == MotionEvent.ACTION_DOWN &&
                        x >= w - 150 &&
                        x <= w - 20 &&
                        y >= 130 &&
                        y <= 175) {

                    night = !night;
                }
            }

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_CANCEL) {

                left = false;
                right = false;
                gas = false;
                brake = false;
            }

            return true;
        }
    }
        }
