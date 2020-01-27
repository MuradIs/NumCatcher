package com.muradismayilov.numcatcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Random;

public class FeedActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;

    TextView mNumber;
    TextView catchTheNumber;
    int number;
    Handler handler;
    Runnable runnable;
    Animation alphaAnimation;
    Animation uptodown;
    Animation translate2;
    ConstraintLayout mLayout;
    CardView catchContainer;
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayerClick;

    ImageView live3;
    ImageView live2;
    ImageView live1;
    int live;
    int score;
    TextView scoreText;
    TextView bestScoreText;
    int bestScore;

    ArrayList<Integer> numbers;

    AdView mAdView;

    RippleView mNumberRipple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        MobileAds.initialize(this, "ca-app-pub-3531666375863646/9255981641");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3531666375863646/3997336981");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                FeedActivity.this.finish();

                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mNumberRipple = findViewById(R.id.mNumberRipple);

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);
        mediaPlayer.setLooping(true);

        mediaPlayerClick = new MediaPlayer();
        mediaPlayerClick = MediaPlayer.create(getApplicationContext(), R.raw.click);

        if (mediaPlayer != null) {

            mediaPlayer.start();
        }


        SharedPreferences sharedPreferences = FeedActivity.this.getSharedPreferences("com.muradismayilov.numcatcher", Context.MODE_PRIVATE);
        bestScore = sharedPreferences.getInt("best", 0);

        score = 0;
        scoreText = findViewById(R.id.scoreText);
        scoreText.setText("Score: " + score);

        bestScoreText = findViewById(R.id.bestScore);
        bestScoreText.setText("Best Score: " + bestScore);


        mLayout = findViewById(R.id.mLayout);
        mLayout.setClickable(false);

        catchContainer = findViewById(R.id.catchContainer);
        catchContainer.setClickable(false);

        mNumber = findViewById(R.id.mNumber);
        catchTheNumber = findViewById(R.id.catchTheNumber);
        number = 0;

        alphaAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_animation);
        uptodown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
        translate2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate2);

        numbers = new ArrayList<>();

        live3 = findViewById(R.id.live3);
        live2 = findViewById(R.id.live2);
        live1 = findViewById(R.id.live1);
        live = 3;


        for (int i = 1; i < 100000; i++) {
            numbers.add(i);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                Random random = new Random();
                int randomNumber = random.nextInt(500) + 250;
                int randomIncrease = random.nextInt(7) + 5;

                float randomX = (float) (0.1 + random.nextFloat() * (3.0 - 0.1));
                float randomY = (float) (0.1 + random.nextFloat() * (3.0 - 0.1));

                float randompivotX = (float) (0.1 + random.nextFloat() * (0.9 - 0.1));
                float randompivotY = (float) (0.1 + random.nextFloat() * (0.9 - 0.1));


                mNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (number == Integer.parseInt("" + catchTheNumber.getText())) {
                            mNumberRipple.setRippleColor(R.color.neonGreenish);
                            makeToast("+10", R.color.green);

                            if (mediaPlayerClick != null) {

                                mediaPlayerClick.start();
                            }

                            Animation winAnim = new ScaleAnimation(
                                    1.0f, 5.0f, // Start and end values for the X axis scaling
                                    1.0f, 5.0f, // Start and end values for the Y axis scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                            winAnim.setDuration(300);

                            mNumber.startAnimation(winAnim);

                            score += 10;
                            scoreText.setText("Score: " + score);
                            scoreText.setTextColor(Color.GREEN);

                        } else {
                            mNumberRipple.setRippleColor(R.color.neonred);
                            makeToast("-", R.color.neonred);
                            live--;

                            if (live == 2) {
                                live3.startAnimation(uptodown);
                                live3.setVisibility(View.GONE);
                            } else if (live == 1) {
                                live2.startAnimation(uptodown);
                                live2.setVisibility(View.GONE);
                            } else if (live == 0) {
                                live1.startAnimation(uptodown);
                                live1.setVisibility(View.GONE);

                                handler.removeCallbacks(runnable);

                                AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new AlertDialog.Builder(FeedActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    builder = new AlertDialog.Builder(FeedActivity.this);
                                }

                                builder.setTitle("Try Again");
                                builder.setMessage("Your Score: " + score);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (interstitialAd.isLoaded()) {
                                            interstitialAd.show();
                                        } else {

                                            Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                            FeedActivity.this.finish();
                                        }
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        FeedActivity.this.finish();

                                    }
                                });

                                builder.show();


                                if (score > bestScore) {
                                    bestScore = score;
                                    bestScoreText.setText("Best Score: " + bestScore);

                                    SharedPreferences sharedPreferences = FeedActivity.this.getSharedPreferences("com.muradismayilov.numcatcher", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().putInt("best", bestScore).apply();
                                }


                            }
                        }
                    }
                });


                alphaAnimation.setDuration(randomNumber);

                mNumber.setAnimation(alphaAnimation);

                Animation anim = new ScaleAnimation(
                        1.0f, randomX, // Start and end values for the X axis scaling
                        1.0f, randomY, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, randompivotX, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, randompivotY); // Pivot point of Y scaling
                anim.setDuration(randomNumber);

                mNumber.setAnimation(anim);


                if (number < Integer.parseInt("" + catchTheNumber.getText())) {
                    number++;
                    mNumber.setText("" + number);
                } else {
                    for (int a : numbers) {

                        if (a > number && a - number < randomIncrease) {
                            catchTheNumber.setText("" + a);
                            catchContainer.startAnimation(uptodown);

                        }
                    }
                    number++;
                    mNumber.setText("" + number);
                }

                handler.postDelayed(this, randomNumber);
            }
        };
        handler.post(runnable);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    public void makeToast(String text, int color) {
        Toast toast = new Toast(FeedActivity.this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        View toast_view = getLayoutInflater().inflate(R.layout.toast_layout, null);
        CardView toastContainer = toast_view.findViewById(R.id.toastContainer);
        TextView toastTV = toast_view.findViewById(R.id.toastTV);
        toastTV.setText(text);
        toastContainer.setCardBackgroundColor(getResources().getColor(color));
        toast.setView(toast_view);
        toast.show();
    }
}
