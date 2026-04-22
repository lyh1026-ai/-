package com.minesweeper.game;

import com.minesweeper.model.Cell;

/**
 * 游戏逻辑类 - 整合游戏逻辑和UI交互
 */
public class GameLogic {
    private BoardData boardData;
    private boolean gameOver;
    private boolean gameWon;
    private boolean firstMove = true;

    public GameLogic(int rows, int cols, int mines) {
        this.boardData = new BoardData(rows, cols, mines);
        this.gameOver = false;
        this.gameWon = false;
        this.firstMove = true;
    }

    public void initializeGame() {
        boardData.reset();
        gameOver = false;
        gameWon = false;
        firstMove = true;
    }

    public boolean revealCell(int row, int col) {
        if (gameOver || gameWon) {
            return false;
        }

        if (firstMove) {
            boardData.placeMines(row, col);
            firstMove = false;
        }

        boolean hitMine = boardData.revealCell(row, col);

        if (hitMine) {
            gameOver = true;
            return true;
        }

        if (boardData.checkWin()) {
            gameWon = true;
        }

        return true;
    }

    public void toggleFlag(int row, int col) {
        if (!gameOver && !gameWon) {
            boardData.toggleFlag(row, col);
        }
    }

    public int getRemainingMines() {
        return boardData.getRemainingFlags();
    }

    public boolean isRevealed(int row, int col) {
        return boardData.getCell(row, col).isRevealed();
    }

    public boolean isMine(int row, int col) {
        return boardData.getCell(row, col).isMine();
    }

    public boolean isFlagged(int row, int col) {
        return boardData.getCell(row, col).isFlagged();
    }

    public int getAdjacentMines(int row, int col) {
        return boardData.getCell(row, col).getValue();
    }

    public int getRows() {
        return boardData.getRows();
    }

    public int getCols() {
        return boardData.getCols();
    }

    public int getMines() {
        return boardData.getMineCount();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }
}