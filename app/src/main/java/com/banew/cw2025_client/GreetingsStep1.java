package com.banew.cw2025_client;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class GreetingsStep1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greetings_step1, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getView() == null) return;

        getView().findViewById(R.id.greetings_button_continue).setOnClickListener(this::onContinue);
        ImageView imageView = getView().findViewById(R.id.logoImage);

        imageView.setAlpha(0f); // почати з прозорого
        imageView.setTranslationY(500);

        imageView.animate()
                .alpha(1f)        // стати повністю видимим
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(2000) // тривалість у мс
                .start();

        imageView.animate()
                .setStartDelay(1700)
                .translationY(0)
                .setDuration(700)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        ConstraintLayout logoBlock = getView().findViewById(R.id.logoBlock);
        logoBlock.setAlpha(0f);
        logoBlock.animate()
                .alpha(1f)
                .setStartDelay(1700)
                .setDuration(1000)
                .start();

        Button continueButton = getView().findViewById(R.id.greetings_button_continue);
        continueButton.setAlpha(0f);
        continueButton.animate()
                .alpha(1f)
                .setStartDelay(2700)
                .setDuration(500)
                .start();
    }

    public void onContinue(View view) {
        if (getView() == null) return;

        View wholeData = getView().findViewById(R.id.greetings1);
        wholeData.animate()
                .alpha(0f)
                .setDuration(500)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.greetingsFrame, new GreetingsStep2())
                            .addToBackStack(null)
                            .commit();
                })
                .start();
    }
}