package com.minesweeper.ui;

import com.minesweeper.game.GameLogic;
import com.minesweeper.model.Difficulty;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GameLogic gameLogic;
    private TopPanel topPanel;
    private BoardPanel boardPanel;
    private BottomPanel bottomPanel;

    private int currentRows = 9;
    private int currentCols = 9;
    private int currentMines = 10;

    public GameFrame() {
        initFrame();
        initComponents();
        initGame();
    }

    private void initFrame() {
        setTitle("💣 扫雷游戏 💣");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(600, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        topPanel = new TopPanel();
        boardPanel = new BoardPanel(this);
        bottomPanel = new BottomPanel();

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.setRestartListener(e -> restartGame());
        bottomPanel.setDifficultyListener(this::changeDifficulty);
        bottomPanel.setCustomListener(e -> showCustomDialog());
    }

    private void initGame() {
        gameLogic = new GameLogic(currentRows, currentCols, currentMines);

        gameLogic.setListener(new GameLogic.GameEventListener() {
            @Override
            public void onCellChanged(int row, int col) {
                boardPanel.updateCell(row, col, gameLogic.getBoardData().getCell(row, col));
            }

            @Override
            public void onGameOver() {
                int[][] mines = gameLogic.getBoardData().getAllMines();
                boardPanel.revealAllMines(mines);
                JOptionPane.showMessageDialog(GameFrame.this,
                        "💣 哎呀！踩到地雷了！游戏结束！",
                        "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onGameWin(int time) {
                JOptionPane.showMessageDialog(GameFrame.this,
                        String.format("🎉 恭喜你赢了！ 🎉\n⏱️ 用时: %d 秒", time),
                        "胜利", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onTimerUpdate(int seconds) {
                topPanel.updateTime(seconds);
            }

            @Override
            public void onMineCountUpdate(int remaining) {
                topPanel.updateMineCount(remaining);
            }

            // ⭐ 必须添加这个方法（爆炸特效回调）
            @Override
            public void onMineExploded(int row, int col) {
                boardPanel.showExplosion(row, col);
            }
        });

        boardPanel.createBoard(currentRows, currentCols,
                (row, col) -> gameLogic.handleLeftClick(row, col),
                (row, col) -> gameLogic.handleRightClick(row, col));
    }

    private void restartGame() {
        gameLogic.reset(currentRows, currentCols, currentMines);
        boardPanel.resetBoard(currentRows, currentCols);
    }

    private void changeDifficulty(Difficulty difficulty) {
        this.currentRows = difficulty.getRows();
        this.currentCols = difficulty.getCols();
        this.currentMines = difficulty.getMineCount();
        restartGame();
    }

    private void showCustomDialog() {
        JDialog dialog = new JDialog(this, "自定义设置", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("行数 (5-30):"), gbc);
        gbc.gridx = 1;
        JTextField rowsField = new JTextField(String.valueOf(currentRows), 10);
        dialog.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("列数 (5-40):"), gbc);
        gbc.gridx = 1;
        JTextField colsField = new JTextField(String.valueOf(currentCols), 10);
        dialog.add(colsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("地雷数量:"), gbc);
        gbc.gridx = 1;
        JTextField minesField = new JTextField(String.valueOf(currentMines), 10);
        dialog.add(minesField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel tipLabel = new JLabel("提示：地雷数不能超过总格子数");
        tipLabel.setFont(new Font("微软雅黑", Font.ITALIC, 10));
        tipLabel.setForeground(Color.GRAY);
        dialog.add(tipLabel, gbc);

        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> {
            try {
                int newRows = Math.max(5, Math.min(30, Integer.parseInt(rowsField.getText().trim())));
                int newCols = Math.max(5, Math.min(40, Integer.parseInt(colsField.getText().trim())));
                int newMines = Math.max(1, Math.min(newRows * newCols - 1, Integer.parseInt(minesField.getText().trim())));

                currentRows = newRows;
                currentCols = newCols;
                currentMines = newMines;

                dialog.dispose();
                restartGame();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字！", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}