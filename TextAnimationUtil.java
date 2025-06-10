package com.Joseph.WEE_GEnder_Tracker;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class TextAnimationUtil {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Handler blinkHandler = new Handler(Looper.getMainLooper());

    public static void animateTextWithTypingEffect(TextView textView, String fullText, long delay) {
        textView.setText("");
        addBlinkingCursor(textView);

        final int[] currentIndex = {0};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] <= fullText.length()) {
                    textView.setText(fullText.substring(0, currentIndex[0]));
                    currentIndex[0]++;
                    handler.postDelayed(this, delay);
                } else {
                    handler.postDelayed(() ->
                                    animateTextWithErasingEffect(textView, fullText, delay),
                            2000);
                }
            }
        };
        handler.post(runnable);
    }

    private static void animateTextWithErasingEffect(TextView textView, String fullText, long delay) {
        final int[] currentIndex = {fullText.length()};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex[0] >= 0) {
                    textView.setText(fullText.substring(0, currentIndex[0]));
                    currentIndex[0]--;
                    handler.postDelayed(this, delay);
                } else {
                    handler.postDelayed(() ->
                                    animateTextWithTypingEffect(textView, fullText, delay),
                            1000);
                }
            }
        };
        handler.post(runnable);
    }

    private static void addBlinkingCursor(TextView textView) {
        final boolean[] showCursor = {true};

        Runnable blinkRunnable = new Runnable() {
            @Override
            public void run() {
                String currentText = textView.getText().toString();
                if (showCursor[0]) {
                    if (!currentText.endsWith("|")) {
                        textView.setText(currentText + "|");
                    }
                } else {
                    if (currentText.endsWith("|")) {
                        textView.setText(currentText.substring(0, currentText.length() - 1));
                    }
                }
                showCursor[0] = !showCursor[0];
                blinkHandler.postDelayed(this, 500);
            }
        };
        blinkHandler.post(blinkRunnable);
    }

    public static void stopTextAnimation(TextView textView) {
        handler.removeCallbacksAndMessages(null);
        blinkHandler.removeCallbacksAndMessages(null);
        String currentText = textView.getText().toString();
        if (currentText.endsWith("|")) {
            textView.setText(currentText.substring(0, currentText.length() - 1));
        }
    }
}