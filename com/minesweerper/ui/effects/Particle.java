package com.minesweeper.ui.effects;

import java.awt.*;
import java.util.Random;

public class Particle {
    private int x, y;
    private int vx, vy;
    private int life;
    private int maxLife;
    private Color color;
    private int size;
    private Random random;

    public Particle(int x, int y) {
        this.random = new Random();
        this.x = x;
        this.y = y;
        this.vx = random.nextInt(14) - 7;
        this.vy = random.nextInt(14) - 7;
        this.life = 40;
        this.maxLife = 40;
        this.size = random.nextInt(6) + 3;

        int colorType = random.nextInt(5);
        switch (colorType) {
            case 0: this.color = new Color(255, 80, 0); break;
            case 1: this.color = new Color(255, 50, 0); break;
            case 2: this.color = new Color(255, 200, 0); break;
            case 3: this.color = new Color(255, 100, 0); break;
            default: this.color = new Color(255, 150, 0); break;
        }
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 1;
        life--;
    }

    public void draw(Graphics g) {
        if (life <= 0) return;

        Graphics2D g2d = (Graphics2D) g;
        int alpha = (int)(255 * ((float)life / maxLife));
        Color fadeColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(alpha, 255));
        g2d.setColor(fadeColor);
        g2d.fillOval(x - size/2, y - size/2, size, size);
    }

    public boolean isAlive() {
        return life > 0;
    }
}