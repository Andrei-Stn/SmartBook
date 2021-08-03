package com.example.ebookapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cunoraz.gifview.library.GifView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final GifView gifView1 = (GifView) findViewById(R.id.gif1);
        gifView1.setGifResource(R.mipmap.animation_500_kh0a8ffm1);
        gifView1.setVisibility(View.VISIBLE);
        gifView1.play();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifView1.stopNestedScroll();
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 3000);

    }
}