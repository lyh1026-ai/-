package com.minesweeper.ui;

import com.minesweeper.model.Difficulty;
import javax.swing.*;
        import java.awt.*;
        import java.util.function.Consumer;

public class BottomPanel extends JPanel {
    private Consumer<Object> restartListener;
    private Consumer<Difficulty> difficultyListener;
    private Consumer<Object> customListener;

    public BottomPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        setBackground(new Color(230, 230, 230));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton restartBtn = createButton("🔄 重新开始", new Color(50, 150, 50));
        restartBtn.addActionListener(e -> { if (restartListener != null) restartListener.accept(null); });

        JButton easyBtn = createButton("🍃 简单 9x9", new Color(70, 130, 180));
        easyBtn.addActionListener(e -> { if (difficultyListener != null) difficultyListener.accept(Difficulty.EASY); });

        JButton mediumBtn = createButton("⚡ 中等 16x16", new Color(255, 140, 0));
        mediumBtn.addActionListener(e -> { if (difficultyListener != null) difficultyListener.accept(Difficulty.MEDIUM); });

        JButton hardBtn = createButton("🔥 困难 16x30", new Color(220, 50, 50));
        hardBtn.addActionListener(e -> { if (difficultyListener != null) difficultyListener.accept(Difficulty.HARD); });

        JButton customBtn = createButton("⚙️ 自定义", new Color(128, 0, 128));
        customBtn.addActionListener(e -> { if (customListener != null) customListener.accept(null); });

        add(restartBtn);
        add(easyBtn);
        add(mediumBtn);
        add(hardBtn);
        add(customBtn);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void setRestartListener(Consumer<Object> listener) { this.restartListener = listener; }
    public void setDifficultyListener(Consumer<Difficulty> listener) { this.difficultyListener = listener; }
    public void setCustomListener(Consumer<Object> listener) { this.customListener = listener; }
}