package com.minesweeper.ui.effects;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 爆炸特效类
 */
public class ExplosionEffect {
    private List<Particle> particles;
    private Timer animationTimer;
    private JPanel targetPanel;
    private boolean isPlaying;
    private Random random;

    public ExplosionEffect() {
        this.particles = new ArrayList<>();
        this.random = new Random();
        this.isPlaying = false;
    }

    /**
     * 播放爆炸效果
     * @param panel 目标面板
     * @param x 爆炸X坐标
     * @param y 爆炸Y坐标
     */
    public void playExplosion(JPanel panel, int x, int y) {
        this.targetPanel = panel;
        createParticles(x, y);
        startAnimation();
    }

    /**
     * 创建粒子效果
     */
    private void createParticles(int centerX, int centerY) {
        particles.clear();
        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = random.nextDouble() * 8 + 3;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;

            Color color = getRandomExplosionColor();
            int size = random.nextInt(6) + 3;
            int life = random.nextInt(30) + 20;

            particles.add(new Particle(centerX, centerY, vx, vy, color, size, life));
        }
    }

    /**
     * 获取随机爆炸颜色
     */
    private Color getRandomExplosionColor() {
        int type = random.nextInt(4);
        switch (type) {
            case 0: return new Color(255, 100, 0);  // 橙色
            case 1: return new Color(255, 50, 0);   // 橙红
            case 2: return new Color(255, 200, 0);  // 金黄色
            default: return new Color(255, 0, 0);    // 红色
        }
    }

    /**
     * 开始动画
     */
    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        isPlaying = true;
        animationTimer = new Timer(16, e -> {
            updateParticles();
            targetPanel.repaint();

            if (particles.isEmpty()) {
                animationTimer.stop();
                isPlaying = false;
                targetPanel.repaint();
            }
        });
        animationTimer.start();
    }

    /**
     * 更新粒子位置
     */
    private void updateParticles() {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update();
            if (!p.isAlive()) {
                iterator.remove();
            }
        }
    }

    /**
     * 绘制粒子效果
     */
    public void draw(Graphics g, JPanel panel) {
        if (!isPlaying || particles.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        for (Particle p : particles) {
            p.draw(g2d);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}