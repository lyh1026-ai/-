package com.minesweeper.ui.effects;

import javax.swing.*;
import java.awt.*;

/**
 * 屏幕震动效果类
 */
public class ScreenShake {
    private Timer shakeTimer;
    private Point originalLocation;
    private JFrame targetFrame;
    private int shakeDuration;
    private int shakeIntensity;
    private long startTime;

    public ScreenShake() {
        this.shakeTimer = null;
    }

    /**
     * 触发屏幕震动
     * @param frame 目标窗口
     * @param duration 震动持续时间（毫秒）
     * @param intensity 震动强度
     */
    public void shake(JFrame frame, int duration, int intensity) {
        this.targetFrame = frame;
        this.shakeDuration = duration;
        this.shakeIntensity = intensity;
        this.originalLocation = frame.getLocation();
        this.startTime = System.currentTimeMillis();

        if (shakeTimer != null && shakeTimer.isRunning()) {
            shakeTimer.stop();
        }

        shakeTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= shakeDuration) {
                // 震动结束，恢复原位置
                targetFrame.setLocation(originalLocation);
                shakeTimer.stop();
            } else {
                // 计算当前震动强度（逐渐减弱）
                float progress = (float) elapsed / shakeDuration;
                int currentIntensity = (int) (shakeIntensity * (1 - progress));

                // 随机移动窗口
                int offsetX = (int) (Math.random() * currentIntensity * 2 - currentIntensity);
                int offsetY = (int) (Math.random() * currentIntensity * 2 - currentIntensity);
                targetFrame.setLocation(originalLocation.x + offsetX, originalLocation.y + offsetY);
            }
        });
        shakeTimer.start();
    }

    /**
     * 快速震动（默认参数）
     */
    public void quickShake(JFrame frame) {
        shake(frame, 200, 8);
    }

    public void stop() {
        if (shakeTimer != null && shakeTimer.isRunning()) {
            shakeTimer.stop();
            if (targetFrame != null && originalLocation != null) {
                targetFrame.setLocation(originalLocation);
            }
        }
    }
}