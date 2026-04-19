package com.minesweeper.model;

public class Cell {
    private int value;
    private boolean revealed;
    private boolean flagged;

    public Cell() {
        this.value = 0;
        this.revealed = false;
        this.flagged = false;
    }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public boolean isRevealed() { return revealed; }
    public void setRevealed(boolean revealed) { this.revealed = revealed; }

    public boolean isFlagged() { return flagged; }
    public void setFlagged(boolean flagged) { this.flagged = flagged; }

    public boolean isMine() { return value == -1; }
    public boolean isEmpty() { return value == 0; }
    public boolean isNumber() { return value >= 1 && value <= 8; }
}