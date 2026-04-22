package com.minesweeper.model;

public class Cell {
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int value;

    public Cell() {
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.value = 0;
    }

    public Cell(boolean isMine, boolean isRevealed, boolean isFlagged, int value) {
        this.isMine = isMine;
        this.isRevealed = isRevealed;
        this.isFlagged = isFlagged;
        this.value = value;
    }

    public boolean isEmpty() {
        return !isMine && value == 0;
    }

    public void reset() {
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;
        this.value = 0;
    }

    public boolean isMine() { return isMine; }
    public void setMine(boolean mine) { isMine = mine; }

    public boolean isRevealed() { return isRevealed; }
    public void setRevealed(boolean revealed) { isRevealed = revealed; }

    public boolean isFlagged() { return isFlagged; }
    public void setFlagged(boolean flagged) { isFlagged = flagged; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    @Override
    public String toString() {
        if (isFlagged) return "⚑";
        if (!isRevealed) return "■";
        if (isMine) return "💣";
        if (value == 0) return " ";
        return String.valueOf(value);
    }
}