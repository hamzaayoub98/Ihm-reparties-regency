package com.example.ihm_reparties;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;

public class GameFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished);

        vibrate(this.findViewById(android.R.id.content).getRootView());
    }

    public void vibrate(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }
}