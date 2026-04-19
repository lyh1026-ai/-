package com.minesweeper.ui;

import javax.swing.*;
        import java.awt.*;

public class TopPanel extends JPanel {
    private JLabel timeLabel;
    private JLabel mineLabel;

    public TopPanel() {
        setLayout(new GridLayout(1, 2, 10, 0));
        setBackground(new Color(230, 230, 230));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel timerPanel = createInfoPanel("⏱️", "时间");
        timeLabel = (JLabel) ((JPanel) timerPanel.getComponent(1)).getComponent(1);

        JPanel minePanel = createInfoPanel("💣", "剩余地雷");
        mineLabel = (JLabel) ((JPanel) minePanel.getComponent(1)).getComponent(1);

        add(timerPanel);
        add(minePanel);
    }

    private JPanel createInfoPanel(String icon, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    public void updateTime(int seconds) {
        timeLabel.setText(String.valueOf(seconds));
    }

    public void updateMineCount(int count) {
        mineLabel.setText(String.valueOf(count));
    }
}