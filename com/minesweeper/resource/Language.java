package com.minesweeper.resource;

/**
 * 语言资源类 - 存储游戏中使用的所有文本
 */
public class Language {
    // 游戏标题
    public static final String GAME_TITLE = "扫雷游戏";

    // 按钮文本
    public static final String RESTART_BTN = "重新开始";
    public static final String EASY_BTN = "简单 9x9";
    public static final String MEDIUM_BTN = "中等 16x16";
    public static final String HARD_BTN = "困难 16x30";
    public static final String CUSTOM_BTN = "自定义";

    // 自定义对话框文本
    public static final String CUSTOM_TITLE = "自定义难度";
    public static final String ROWS_LABEL = "行数 (9-30):";
    public static final String COLS_LABEL = "列数 (9-40):";
    public static final String MINES_LABEL = "地雷数:";
    public static final String ERROR_TITLE = "错误";
    public static final String ERROR_INVALID_NUMBER = "请输入有效的数字！";
    public static final String ERROR_TOO_MANY_MINES = "地雷数太多！最多只能有 %d 个地雷。";
    public static final String ERROR_TOO_FEW_MINES = "地雷数太少！至少需要 1 个地雷。";

    // 游戏消息
    public static final String GAME_OVER_MSG = "哎呀！踩到地雷了！游戏结束！！";
    public static final String WIN_MSG = "恭喜你赢了！\n用时：%d秒";

    // 状态栏文本
    public static final String REMAINING_MINES = "剩余地雷: ";
    public static final String TIME = "时间: ";

    // 游戏状态文本
    public static final String STATUS_PLAYING = "游戏中";
    public static final String STATUS_WIN = "胜利！";
    public static final String STATUS_LOSE = "游戏结束";
}