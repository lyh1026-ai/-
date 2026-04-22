package com.minesweeper.game;

import com.minesweeper.model.Cell;
import java.util.Random;

/**
 * 游戏板数据类 - 管理扫雷游戏的数据结构
 */
public class BoardData {
    private final int rows;
    private final int cols;
    private final int mineCount;
    private final Random random;
    private Cell[][] board;

    public BoardData(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.random = new Random();
        this.board = new Cell[rows][cols];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell();
            }
        }
    }

    public void placeMines(int firstRow, int firstCol) {
        int minesPlaced = 0;
        while (minesPlaced < mineCount) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            if (!isAdjacentToFirstClick(row, col, firstRow, firstCol) && !board[row][col].isMine()) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
        calculateNumbers();
    }

    private boolean isAdjacentToFirstClick(int row, int col, int firstRow, int firstCol) {
        return Math.abs(row - firstRow) <= 1 && Math.abs(col - firstCol) <= 1;
    }

    public void calculateNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) {
                    continue;
                }

                int count = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        if (di == 0 && dj == 0) continue;

                        int ni = i + di;
                        int nj = j + dj;

                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols
                                && board[ni][nj].isMine()) {
                            count++;
                        }
                    }
                }
                board[i][j].setValue(count);
            }
        }
    }

    public boolean revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return false;
        }

        if (cell.isMine()) {
            cell.setRevealed(true);
            return true;
        }

        revealRecursive(row, col);
        return false;
    }

    private void revealRecursive(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }

        Cell cell = board[row][col];
        if (cell.isRevealed() || cell.isFlagged()) {
            return;
        }

        cell.setRevealed(true);

        if (cell.getValue() == 0 && !cell.isMine()) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di != 0 || dj != 0) {
                        revealRecursive(row + di, col + dj);
                    }
                }
            }
        }
    }

    public boolean toggleFlag(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        Cell cell = board[row][col];
        if (!cell.isRevealed()) {
            cell.setFlagged(!cell.isFlagged());
            return true;
        }
        return false;
    }

    public boolean checkWin() {
        int revealedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isRevealed()) {
                    revealedCount++;
                }
            }
        }
        return revealedCount == rows * cols - mineCount;
    }

    public int getRemainingFlags() {
        int flaggedCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isFlagged()) {
                    flaggedCount++;
                }
            }
        }
        return mineCount - flaggedCount;
    }

    public Cell getCell(int row, int col) {
        return board[row][col];
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void reset() {
        initializeBoard();
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMineCount() { return mineCount; }
}