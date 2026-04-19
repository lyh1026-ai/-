package com.minesweeper.ui;

import com.minesweeper.model.Cell;
import com.minesweeper.ui.effects.ExplosionEffect;
import com.minesweeper.ui.effects.ScreenShake;
import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class BoardPanel extends JPanel {
    private JButton[][] buttons;
    private int rows;
    private int cols;
    private BiConsumer<Integer, Integer> leftClickListener;
    private BiConsumer<Integer, Integer> rightClickListener;
    private JFrame parentFrame;

    private ExplosionEffect explosionEffect;
    private ScreenShake screenShake;
    private boolean isExploding = false;

    public BoardPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.explosionEffect = new ExplosionEffect(this);
        this.screenShake = new ScreenShake(parentFrame);
        setLayout(new GridLayout(1, 1));
    }

    public void createBoard(int rows, int cols,
                            BiConsumer<Integer, Integer> leftClick,
                            BiConsumer<Integer, Integer> rightClick) {
        this.rows = rows;
        this.cols = cols;
        this.leftClickListener = leftClick;
        this.rightClickListener = rightClick;

        removeAll();

        int buttonSize = calculateButtonSize();
        int fontSize = calculateFontSize();

        setLayout(new GridLayout(rows, cols, 1, 1));
        setBackground(new Color(180, 180, 180));

        buttons = new JButton[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton button = createButton(buttonSize, fontSize, i, j);
                buttons[i][j] = button;
                add(button);
            }
        }

        adjustWindowSize(buttonSize);
        revalidate();
        repaint();
    }

    private JButton createButton(int size, int fontSize, int row, int col) {
        JButton button = new JButton();
        button.setBackground(new Color(220, 220, 220));
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(size, size));

        button.addActionListener(e -> {
            if (!isExploding && leftClickListener != null) {
                leftClickListener.accept(row, col);
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!isExploding && SwingUtilities.isRightMouseButton(e) && rightClickListener != null) {
                    rightClickListener.accept(row, col);
                }
            }
        });

        return button;
    }

    public void updateCell(int row, int col, Cell cell) {
        if (row >= rows || col >= cols || buttons[row][col] == null) return;
        JButton button = buttons[row][col];

        if (cell.isRevealed()) {
            button.setBorder(BorderFactory.createLoweredBevelBorder());
            button.setEnabled(false);
            button.setBackground(Color.WHITE);

            if (cell.isMine()) {
                button.setText("💣");
                button.setBackground(Color.RED);
            } else if (cell.isEmpty()) {
                button.setText("");
            } else {
                button.setText(String.valueOf(cell.getValue()));
                button.setForeground(getNumberColor(cell.getValue()));
            }
        } else if (cell.isFlagged()) {
            button.setText("🚩");
            button.setForeground(Color.RED);
            button.setBackground(new Color(220, 220, 220));
        } else {
            button.setText("");
            button.setBackground(new Color(220, 220, 220));
        }
    }

    public void showExplosion(int row, int col) {
        if (row >= rows || col >= cols || buttons[row][col] == null) return;

        isExploding = true;

        JButton button = buttons[row][col];
        Point buttonLocation = button.getLocation();

        int centerX = buttonLocation.x + button.getWidth() / 2;
        int centerY = buttonLocation.y + button.getHeight() / 2;

        explosionEffect.explode(centerX, centerY);
        screenShake.shake(300, 8);

        Timer resetTimer = new Timer(600, e -> {
            isExploding = false;
            ((Timer)e.getSource()).stop();
        });
        resetTimer.start();

        repaint();
    }

    public void revealAllMines(int[][] mines) {
        for (int[] mine : mines) {
            int row = mine[0];
            int col = mine[1];
            if (row < rows && col < cols && buttons[row][col] != null) {
                buttons[row][col].setText("💣");
                buttons[row][col].setBackground(Color.RED);
                buttons[row][col].setEnabled(false);
            }
        }
    }

    public void resetBoard(int rows, int cols) {
        isExploding = false;
        createBoard(rows, cols, leftClickListener, rightClickListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        explosionEffect.draw(g);
    }

    private Color getNumberColor(int number) {
        switch (number) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(128, 0, 128);
            case 5: return new Color(255, 140, 0);
            case 6: return new Color(0, 139, 139);
            case 7: return Color.DARK_GRAY;
            case 8: return Color.PINK;
            default: return Color.BLACK;
        }
    }

    private int calculateButtonSize() {
        int totalCells = rows * cols;
        if (totalCells <= 100) return 45;
        if (totalCells <= 256) return 38;
        if (totalCells <= 480) return 30;
        return 25;
    }

    private int calculateFontSize() {
        int totalCells = rows * cols;
        if (totalCells <= 100) return 18;
        if (totalCells <= 256) return 16;
        if (totalCells <= 480) return 12;
        return 10;
    }

    private void adjustWindowSize(int buttonSize) {
        if (parentFrame != null) {
            int width = cols * buttonSize + 80;
            int height = rows * buttonSize + 150;
            parentFrame.setSize(Math.max(width, 550), Math.min(height, 800));
            parentFrame.setLocationRelativeTo(null);
        }
    }
}