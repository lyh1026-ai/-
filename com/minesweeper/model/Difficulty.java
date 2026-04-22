package com.minesweeper.model;

/**
 * 难度枚举类
 */
public enum Difficulty {
    EASY(9, 9, 10),
    MEDIUM(16, 16, 40),
    HARD(16, 30, 99),
    CUSTOM(9, 9, 10);  // 自定义默认值，实际使用时会被覆盖

    private int rows;
    private int cols;
    private int mineCount;

    Difficulty(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getMineCount() { return mineCount; }

    /**
     * 设置自定义难度参数
     */
    public void setCustom(int rows, int cols, int mineCount) {
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
    }

    /**
     * 检查是否为自定义难度
     */
    public boolean isCustom() {
        return this == CUSTOM;
    }
}