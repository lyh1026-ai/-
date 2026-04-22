package com.minesweeper.ui;

import com.minesweeper.resource.Language;
import javax.swing.*;
import java.awt.*;

public class TopPanel extends JPanel {
    private final GameFrame parentFrame;
    private JLabel mineCountLabel;
    private JLabel timerLabel;
    private JButton restartButton;
    private int mineCount;

    private static final Color PANEL_BG_COLOR = new Color(250, 250, 250);
    private static final Color BUTTON_BG_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_HOVER_COLOR = new Color(220, 220, 220);

    public TopPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        // 使用 BorderLayout 确保组件正确布局
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        setBackground(PANEL_BG_COLOR);
        setPreferredSize(new Dimension(400, 65));

        // 左侧：地雷计数
        mineCountLabel = new JLabel(Language.REMAINING_MINES + "0");
        mineCountLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        mineCountLabel.setForeground(new Color(50, 50, 50));
        mineCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // 中间：重新开始按钮
        restartButton = createStyledButton(Language.RESTART_BTN);
        restartButton.addActionListener(e -> {
            // 添加点击动画效果
            restartButton.setBackground(BUTTON_HOVER_COLOR);
            Timer timer = new Timer(100, ev -> {
                restartButton.setBackground(BUTTON_BG_COLOR);
                ((Timer) ev.getSource()).stop();
            });
            timer.start();
            parentFrame.restartGame();
        });

        // 右侧：计时器
        timerLabel = new JLabel(Language.TIME + "0秒");
        timerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        timerLabel.setForeground(new Color(50, 50, 50));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

        // 创建面板来居中按钮
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(PANEL_BG_COLOR);
        centerPanel.add(restartButton);

        // 添加到主面板
        add(mineCountLabel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(timerLabel, BorderLayout.EAST);
    }

    /**
     * 创建样式化的按钮
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
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

        return button;
    }

    public void setMineCount(int mineCount) {
        this.mineCount = mineCount;
        mineCountLabel.setText(Language.REMAINING_MINES + mineCount);
    }

    public void updateMineCount(int remaining) {
        mineCountLabel.setText(Language.REMAINING_MINES + remaining);
    }

    public void updateTime(int seconds) {
        timerLabel.setText(Language.TIME + seconds + "秒");
    }
}