package com.minesweeper.ui;

import com.minesweeper.model.Cell;
import com.minesweeper.model.Difficulty;
import com.minesweeper.model.GameState;
import com.minesweeper.resource.Language;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private BoardPanel boardPanel;
    private TopPanel topPanel;
    private BottomPanel bottomPanel;
    private Cell[][] board;
    private int rows;
    private int cols;
    private int mineCount;
    private GameState gameState;
    private Timer timer;
    private int elapsedSeconds;
    private Difficulty currentDifficulty;

    public GameFrame() {
        setTitle(Language.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 250, 250));

        currentDifficulty = Difficulty.EASY;
        initializeGame(currentDifficulty);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * 显示自定义难度对话框
     */
    public void showCustomDialog() {
        CustomDialog dialog = new CustomDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            int rows = dialog.getRows();
            int cols = dialog.getCols();
            int mines = dialog.getMines();

            // 创建临时自定义难度
            Difficulty customDifficulty = Difficulty.CUSTOM;
            customDifficulty.setCustom(rows, cols, mines);
            initializeGame(customDifficulty);
        }
    }

    public void initializeGame(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.rows = difficulty.getRows();
        this.cols = difficulty.getCols();
        this.mineCount = difficulty.getMineCount();
        this.board = new Cell[rows][cols];
        this.gameState = GameState.PLAYING;
        this.elapsedSeconds = 0;

        // 初始化格子
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell();
            }
        }

        // 停止旧计时器
        if (timer != null) {
            timer.stop();
        }

        // 创建UI
        createUI();

        // 调整窗口大小
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private void createUI() {
        // 移除旧组件
        if (topPanel != null) remove(topPanel);
        if (boardPanel != null) remove(boardPanel);
        if (bottomPanel != null) remove(bottomPanel);

        // 创建新组件
        topPanel = new TopPanel(this);
        boardPanel = new BoardPanel(this);
        bottomPanel = new BottomPanel(this);

        // 设置数据
        boardPanel.setBoardData(board, rows, cols);
        topPanel.setMineCount(mineCount);

        // 添加组件
        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 启动计时器
        startTimer();
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (gameState == GameState.PLAYING) {
                elapsedSeconds++;
                if (topPanel != null) {
                    topPanel.updateTime(elapsedSeconds);
                }
            }
        });
        timer.start();
    }

    public void placeMines(int firstRow, int firstCol) {
        int minesPlaced = 0;
        while (minesPlaced < mineCount) {
            int row = (int) (Math.random() * rows);
            int col = (int) (Math.random() * cols);

            if (!isAdjacentToClick(row, col, firstRow, firstCol) && !board[row][col].isMine()) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
        calculateNumbers();
    }

    private boolean isAdjacentToClick(int row, int col, int firstRow, int firstCol) {
        return Math.abs(row - firstRow) <= 1 && Math.abs(col - firstCol) <= 1;
    }

    private void calculateNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) continue;

                int count = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        if (di == 0 && dj == 0) continue;
                        int ni = i + di, nj = j + dj;
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && board[ni][nj].isMine()) {
                            count++;
                        }
                    }
                }
                board[i][j].setValue(count);
            }
        }
    }

    public void revealCell(int row, int col) {
        if (gameState != GameState.PLAYING) return;

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) return;

        cell.setRevealed(true);
        boardPanel.updateCell(row, col, cell);

        if (cell.isEmpty()) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di != 0 || dj != 0) {
                        int ni = row + di, nj = col + dj;
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                            Cell neighbor = board[ni][nj];
                            if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                                revealCell(ni, nj);
                            }
                        }
                    }
                }
            }
        }

        checkWin();
    }

    public void toggleFlag(int row, int col) {
        if (gameState != GameState.PLAYING) return;

        Cell cell = board[row][col];
        if (!cell.isRevealed()) {
            cell.setFlagged(!cell.isFlagged());
            boardPanel.updateCell(row, col, cell);
            updateRemainingMines();
        }
    }

    private void updateRemainingMines() {
        int flaggedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isFlagged()) flaggedCount++;
            }
        }
        topPanel.updateMineCount(mineCount - flaggedCount);
    }

    private void checkWin() {
        int revealedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isRevealed()) revealedCount++;
            }
        }

        if (revealedCount == rows * cols - mineCount) {
            gameState = GameState.WIN;
            if (timer != null) timer.stop();
            UIManager.put("OptionPane.messageFont", new Font("Microsoft YaHei", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this,
                    String.format(Language.WIN_MSG, elapsedSeconds),
                    "胜利", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void gameOver() {
        gameState = GameState.LOSE;
        if (timer != null) timer.stop();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) {
                    board[i][j].setRevealed(true);
                    boardPanel.updateCell(i, j, board[i][j]);
                }
            }
        }

        Timer delayTimer = new Timer(500, e -> {
            UIManager.put("OptionPane.messageFont", new Font("Microsoft YaHei", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, Language.GAME_OVER_MSG, "游戏结束",
                    JOptionPane.ERROR_MESSAGE);
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public void restartGame() {
        initializeGame(currentDifficulty);
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                // 设置全局字体以支持中文
                UIManager.put("Button.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("Label.font", new Font("Microsoft YaHei", Font.PLAIN, 12));
                UIManager.put("OptionPane.messageFont", new Font("Microsoft YaHei", Font.PLAIN, 14));
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GameFrame();
        });
    }
}