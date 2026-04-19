package com.minesweeper.game;

import com.minesweeper.model.Cell;
import javax.swing.Timer;

public class GameLogic {
    private BoardData boardData;
    private GameState state;
    private int flagCount;
    private int elapsedSeconds;
    private Timer timer;
    private GameEventListener listener;

    public interface GameEventListener {
        void onCellChanged(int row, int col);
        void onGameOver();
        void onGameWin(int time);
        void onTimerUpdate(int seconds);
        void onMineCountUpdate(int remaining);
        void onMineExploded(int row, int col);  // 爆炸特效
    }

    public GameLogic(int rows, int cols, int mineCount) {
        this.boardData = new BoardData(rows, cols, mineCount);
        this.state = GameState.WAITING_FIRST;
        this.flagCount = 0;
        this.elapsedSeconds = 0;
    }

    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }

    public void handleLeftClick(int row, int col) {
        if (state != GameState.PLAYING && state != GameState.WAITING_FIRST) return;

        Cell cell = boardData.getCell(row, col);
        if (cell.isFlagged()) return;
        if (cell.isRevealed()) return;

        if (state == GameState.WAITING_FIRST) {
            boardData.placeMines(row, col);
            state = GameState.PLAYING;
            startTimer();
        }

        if (cell.isMine()) {
            state = GameState.GAME_OVER;
            if (timer != null) timer.stop();
            // 触发爆炸特效
            if (listener != null) listener.onMineExploded(row, col);
            if (listener != null) listener.onGameOver();
            return;
        }

        // 记录翻开前的状态
        boolean[][] oldRevealed = new boolean[boardData.getRows()][boardData.getCols()];
        for (int i = 0; i < boardData.getRows(); i++) {
            for (int j = 0; j < boardData.getCols(); j++) {
                oldRevealed[i][j] = boardData.getCell(i, j).isRevealed();
            }
        }

        boardData.revealCell(row, col);

        for (int i = 0; i < boardData.getRows(); i++) {
            for (int j = 0; j < boardData.getCols(); j++) {
                if (boardData.getCell(i, j).isRevealed() != oldRevealed[i][j]) {
                    if (listener != null) listener.onCellChanged(i, j);
                }
            }
        }

        if (boardData.checkWin()) {
            state = GameState.GAME_WON;
            if (timer != null) timer.stop();
            if (listener != null) listener.onGameWin(elapsedSeconds);
        }
    }

    public void handleRightClick(int row, int col) {
        if (state != GameState.PLAYING) return;

        Cell cell = boardData.getCell(row, col);
        if (cell.isRevealed()) return;

        if (cell.isFlagged()) {
            cell.setFlagged(false);
            flagCount--;
        } else {
            cell.setFlagged(true);
            flagCount++;
        }

        if (listener != null) {
            listener.onCellChanged(row, col);
            listener.onMineCountUpdate(boardData.getMineCount() - flagCount);
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            if (state == GameState.PLAYING) {
                elapsedSeconds++;
                if (listener != null) listener.onTimerUpdate(elapsedSeconds);
            }
        });
        timer.start();
    }

    public void reset(int rows, int cols, int mineCount) {
        if (timer != null) timer.stop();
        this.boardData = new BoardData(rows, cols, mineCount);
        this.state = GameState.WAITING_FIRST;
        this.flagCount = 0;
        this.elapsedSeconds = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (listener != null) listener.onCellChanged(i, j);
            }
        }
        if (listener != null) {
            listener.onTimerUpdate(0);
            listener.onMineCountUpdate(mineCount);
        }
    }

    public BoardData getBoardData() { return boardData; }
    public GameState getState() { return state; }
}