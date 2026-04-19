package com.minesweeper.ui.effects;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ScreenShake {
    private JFrame frame;
    private Point originalLocation;
    private Timer shakeTimer;
    private Random random;

    public ScreenShake(JFrame frame) {
        this.frame = frame;
        this.random = new Random();
    }

    public void shake(int duration, int intensity) {
        if (shakeTimer != null && shakeTimer.isRunning()) {
            shakeTimer.stop();
            if (originalLocation != null) {
                frame.setLocation(originalLocation);
            }
        }

        originalLocation = frame.getLocation();
        long startTime = System.currentTimeMillis();

        shakeTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= duration) {
                frame.setLocation(originalLocation);
                shakeTimer.stop();
            } else {
                int offsetX = random.nextInt(intensity * 2) - intensity;
                int offsetY = random.nextInt(intensity * 2) - intensity;
                frame.setLocation(originalLocation.x + offsetX, originalLocation.y + offsetY);
            }
        });
        shakeTimer.start();
    }

    public void stop() {
        if (shakeTimer != null && shakeTimer.isRunning()) {
            shakeTimer.stop();
            if (originalLocation != null) {
                frame.setLocation(originalLocation);
            }
        }
    }
}