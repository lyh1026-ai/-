package com.minesweeper.model;

public enum Difficulty {
    EASY("简单", 9, 9, 10),
    MEDIUM("中等", 16, 16, 40),
    HARD("困难", 16, 30, 99);

    private final String name;
    private final int rows;
    private final int cols;
    private final int mineCount;

    Difficulty(String name, int rows, int cols, int mineCount) {
        this.name = name;
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
    }

    public String getName() { return name; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMineCount() { return mineCount; }
}