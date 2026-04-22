package com.minesweeper.ui.effects;

import java.awt.*;

/**
 * 粒子类 - 用于爆炸效果
 */
public class Particle {
    private double x, y;
    private double vx, vy;
    private Color color;
    private int size;
    private int life;
    private int maxLife;
    private double gravity = 0.3;
    private double friction = 0.98;

    public Particle(double x, double y, double vx, double vy, Color color, int size, int life) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.size = size;
        this.life = life;
        this.maxLife = life;
    }

    /**
     * 更新粒子状态
     */
    public void update() {
        // 应用重力和摩擦力
        vy += gravity;
        vx *= friction;
        vy *= friction;

        // 更新位置
        x += vx;
        y += vy;

        // 减少生命值
        life--;
    }

    /**
     * 绘制粒子
     */
    public void draw(Graphics2D g2d) {
        if (life <= 0) return;

        // 根据生命值计算透明度
        int alpha = (int) (255 * ((double) life / maxLife));
        alpha = Math.max(0, Math.min(255, alpha));

        Color particleColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        g2d.setColor(particleColor);
        g2d.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
    }

    /**
     * 检查粒子是否还存活
     */
    public boolean isAlive() {
        return life > 0;
    }
}