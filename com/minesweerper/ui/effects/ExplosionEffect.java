package com.minesweeper.ui.effects;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ExplosionEffect {
    private JPanel parentPanel;
    private List<Particle> particles;
    private Timer animationTimer;
    private boolean isPlaying;
    private int centerX, centerY;

    public ExplosionEffect(JPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.particles = new ArrayList<>();
    }

    public void explode(int x, int y) {
        this.centerX = x;
        this.centerY = y;
        particles.clear();

        int particleCount = 60 + (int)(Math.random() * 40);
        for (int i = 0; i < particleCount; i++) {
            particles.add(new Particle(x, y));
        }

        isPlaying = true;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> {
            boolean anyAlive = false;
            for (Particle p : particles) {
                p.update();
                if (p.isAlive()) anyAlive = true;
            }
            parentPanel.repaint();
            if (!anyAlive) {
                animationTimer.stop();
                isPlaying = false;
                particles.clear();
                parentPanel.repaint();
            }
        });
        animationTimer.start();
    }

    public void draw(Graphics g) {
        if (!isPlaying) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particle particle : particles) {
            particle.draw(g);
        }

        int flashAlpha = 120 - (int)((System.currentTimeMillis() % 300) / 2.5);
        if (flashAlpha > 0 && isPlaying) {
            g2d.setColor(new Color(255, 200, 100, Math.min(flashAlpha, 100)));
            g2d.fillOval(centerX - 40, centerY - 40, 80, 80);
            g2d.setColor(new Color(255, 255, 200, Math.min(flashAlpha / 2, 50)));
            g2d.fillOval(centerX - 60, centerY - 60, 120, 120);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}