package com.minesweeper.ui;

import com.minesweeper.model.Difficulty;
import com.minesweeper.resource.Language;
import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {
    private final GameFrame parentFrame;

    private static final Color PANEL_BG_COLOR = new Color(250, 250, 250);
    private static final Color BUTTON_BG_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_HOVER_COLOR = new Color(220, 220, 220);

    public BottomPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        setBackground(PANEL_BG_COLOR);
        setPreferredSize(new Dimension(400, 55));

        JButton easyBtn = createDifficultyButton(Language.EASY_BTN, Difficulty.EASY);
        JButton mediumBtn = createDifficultyButton(Language.MEDIUM_BTN, Difficulty.MEDIUM);
        JButton hardBtn = createDifficultyButton(Language.HARD_BTN, Difficulty.HARD);
        JButton customBtn = createDifficultyButton(Language.CUSTOM_BTN, Difficulty.CUSTOM);

        add(easyBtn);
        add(mediumBtn);
        add(hardBtn);
        add(customBtn);
    }

    private JButton createDifficultyButton(String text, Difficulty difficulty) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_BG_COLOR);
            }
        });

        button.addActionListener(e -> {
            if (difficulty == Difficulty.CUSTOM) {
                parentFrame.showCustomDialog();
            } else {
                parentFrame.initializeGame(difficulty);
            }
        });

        return button;
    }
}