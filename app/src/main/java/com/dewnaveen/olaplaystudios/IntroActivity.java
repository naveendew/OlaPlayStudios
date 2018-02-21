package com.dewnaveen.olaplaystudios;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;


import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class IntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.yellow)
                        .buttonsColor(R.color.yellow_dark)
                        .image(R.drawable.intro1)
                        .title("Welcome to OLA PLAY STUDIO")
                        .description("Stream your favourite Songs with OLA.")
                        .build());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.red_500)
                        .buttonsColor(R.color.red_dark)
                        .image(R.drawable.intro2)
                        .title("Streaming Music")
                        .description("Stream Songs on the go.")
                        .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.purple)
                .buttonsColor(R.color.deep_purple)
                .image(R.drawable.intro3)
                .title("Download Songs")
                .description("Offline Access.")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.pink)
                .buttonsColor(R.color.deep_pink)
                .image(R.drawable.intro4)
                .title("Music Player")
                .description("Built in Music Player with latest features.")
                .build());



    }

    @Override
    public void onFinish() {
        super.onFinish();
        Intent mainIntent =  new Intent(IntroActivity.this, MainActivity.class);
        startActivity(mainIntent);

    }

}