package com.minesweeper.game;

import com.minesweeper.model.Cell;
import java.util.Random;

public class BoardData {
    private int rows;
    private int cols;
    private int mineCount;
    private Cell[][] board;
    private Random random;

    public BoardData(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.random = new Random();
        initBoard();
    }

    public void initBoard() {
        board = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = new Cell();
            }
        }
    }

    public void placeMines(int firstRow, int firstCol) {
        boolean[][] exclude = getExcludeArea(firstRow, firstCol);
        int placed = 0;

        while (placed < mineCount) {
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);
            if (!exclude[row][col] && !board[row][col].isMine()) {
                board[row][col].setValue(-1);
                placed++;
            }
        }
        calculateNumbers();
    }

    private boolean[][] getExcludeArea(int firstRow, int firstCol) {
        boolean[][] exclude = new boolean[rows][cols];
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = firstRow + di;
                int nj = firstCol + dj;
                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                    exclude[ni][nj] = true;
                }
            }
        }
        return exclude;
    }

    public void calculateNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) continue;
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

    // ⭐ 修复后的 revealCell 方法
    public void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;

        Cell cell = board[row][col];

        if (cell.isRevealed()) return;
        if (cell.isFlagged()) return;

        cell.setRevealed(true);

        if (cell.isEmpty()) {
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue;
                    revealCell(row + di, col + dj);
                }
            }
        }
    }

    public boolean checkWin() {
        int unrevealedSafe = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!board[i][j].isRevealed() && !board[i][j].isMine()) {
                    unrevealedSafe++;
                }
            }
        }
        return unrevealedSafe == 0;
    }

    public int[][] getAllMines() {
        int[][] mines = new int[mineCount][2];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j].isMine()) {
                    mines[index][0] = i;
                    mines[index][1] = j;
                    index++;
                }
            }
        }
        return mines;
    }

    public Cell getCell(int row, int col) { return board[row][col]; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMineCount() { return mineCount; }
}