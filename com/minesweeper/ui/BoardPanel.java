package com.minesweeper.ui;

import com.minesweeper.model.Cell;
import com.minesweeper.model.GameState;
import com.minesweeper.ui.effects.ExplosionEffect;
import com.minesweeper.ui.effects.ScreenShake;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {
    private final GameFrame parentFrame;
    private JButton[][] buttons;
    private Cell[][] board;
    private int rows;
    private int cols;
    private boolean firstMove = true;
    private static final int CELL_SIZE = 40;

    private ExplosionEffect explosionEffect;
    private ScreenShake screenShake;
    private Point lastClickPoint;

    private static final Color UNREVEALED_COLOR = new Color(240, 240, 240);
    private static final Color REVEALED_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);

    // 数字颜色 - 经典扫雷配色
    private static final Color COLOR_1 = new Color(0, 0, 255);      // 蓝色
    private static final Color COLOR_2 = new Color(0, 128, 0);      // 绿色
    private static final Color COLOR_3 = new Color(255, 0, 0);      // 红色
    private static final Color COLOR_4 = new Color(0, 0, 128);      // 深蓝色
    private static final Color COLOR_5 = new Color(128, 0, 0);      // 深红色
    private static final Color COLOR_6 = new Color(0, 128, 128);    // 蓝绿色
    private static final Color COLOR_7 = new Color(0, 0, 0);        // 黑色
    private static final Color COLOR_8 = new Color(128, 128, 128);  // 灰色

    public BoardPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.explosionEffect = new ExplosionEffect();
        this.screenShake = new ScreenShake();
        setLayout(new GridLayout(1, 1));
        setBackground(new Color(250, 250, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (explosionEffect != null && explosionEffect.isPlaying()) {
            explosionEffect.draw(g, this);
        }
    }

    public void setBoardData(Cell[][] board, int rows, int cols) {
        this.board = board;
        this.rows = rows;
        this.cols = cols;
        this.firstMove = true;
        initializeButtons();
    }

    private void initializeButtons() {
        removeAll();
        setLayout(new GridLayout(rows, cols, 1, 1));
        buttons = new JButton[rows][cols];

        int width = cols * CELL_SIZE;
        int height = rows * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton button = createButton(i, j);
                buttons[i][j] = button;
                add(button);
            }
        }

        revalidate();
        repaint();
    }

    private JButton createButton(int row, int col) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        button.setBackground(UNREVEALED_COLOR);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (parentFrame.getGameState() != GameState.PLAYING) {
                    return;
                }

                lastClickPoint = SwingUtilities.convertPoint(button, e.getPoint(), BoardPanel.this);

                if (SwingUtilities.isLeftMouseButton(e)) {
                    handleLeftClick(row, col);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    handleRightClick(row, col);
                }
            }
        });

        return button;
    }

    private void handleLeftClick(int row, int col) {
        if (board == null) return;

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }

        if (firstMove) {
            parentFrame.placeMines(row, col);
            firstMove = false;
        }

        if (cell.isMine()) {
            if (lastClickPoint != null) {
                explosionEffect.playExplosion(this, lastClickPoint.x, lastClickPoint.y);
            } else {
                int x = col * CELL_SIZE + CELL_SIZE / 2;
                int y = row * CELL_SIZE + CELL_SIZE / 2;
                explosionEffect.playExplosion(this, x, y);
            }
            screenShake.quickShake((JFrame) SwingUtilities.getWindowAncestor(this));
            parentFrame.gameOver();
        } else {
            parentFrame.revealCell(row, col);
        }
    }

    private void handleRightClick(int row, int col) {
        if (board == null) return;

        Cell cell = board[row][col];
        if (!cell.isRevealed()) {
            parentFrame.toggleFlag(row, col);
        }
    }

    public void updateCell(int row, int col, Cell cell) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }

        JButton button = buttons[row][col];

        if (cell.isRevealed()) {
            button.setBackground(REVEALED_COLOR);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            button.setEnabled(false);

            if (cell.isMine()) {
                button.setText("💣");
                button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                button.setBackground(new Color(255, 200, 200));
                button.setForeground(Color.BLACK);
            } else if (cell.isEmpty()) {
                button.setText("");
                button.setBackground(REVEALED_COLOR);
            } else {
                int value = cell.getValue();
                button.setText(String.valueOf(value));
                // 确保数字颜色正确设置
                button.setForeground(getNumberColor(value));
                button.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
                button.setBackground(REVEALED_COLOR);
            }
        } else if (cell.isFlagged()) {
            button.setText("🚩");
            button.setForeground(Color.RED);
            button.setBackground(UNREVEALED_COLOR);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            button.setEnabled(true);
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        } else {
            button.setText("");
            button.setBackground(UNREVEALED_COLOR);
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            button.setEnabled(true);
        }
    }

    /**
     * 获取数字对应的颜色 - 经典扫雷颜色
     * 1: 蓝色, 2: 绿色, 3: 红色, 4: 深蓝色, 5: 深红色, 6: 蓝绿色, 7: 黑色, 8: 灰色
     */
    private Color getNumberColor(int number) {
        switch (number) {
            case 1:
                return COLOR_1;  // 蓝色
            case 2:
                return COLOR_2;  // 绿色
            case 3:
                return COLOR_3;  // 红色
            case 4:
                return COLOR_4;  // 深蓝色
            case 5:
                return COLOR_5;  // 深红色
            case 6:
                return COLOR_6;  // 蓝绿色
            case 7:
                return COLOR_7;  // 黑色
            case 8:
                return COLOR_8;  // 灰色
            default:
                return Color.BLACK;
        }
    }

    public void reset() {
        firstMove = true;
        if (buttons != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    JButton button = buttons[i][j];
                    button.setText("");
                    button.setBackground(UNREVEALED_COLOR);
                    button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
                    button.setEnabled(true);
                    button.setForeground(Color.BLACK);
                }
            }
        }
    }
}