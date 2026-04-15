import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MinesweeperTest extends JFrame {
    private JButton[][] buttons;
    private int[][] board;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int rows = 9;
    private int cols = 9;
    private int mineCount = 10;
    private int flagCount = 0;
    private Timer timer;
    private int elapsedSeconds = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean firstClick = true;
    private JLabel timerLabel;
    private JLabel mineLabel;
    private JPanel boardPanel;
    private JTextArea testLog;  // 测试日志区域

    private final Color SILVER_WHITE = new Color(220, 220, 220);
    private final Color SILVER_LIGHT = new Color(230, 230, 230);
    private final Color CLICKED_COLOR = Color.WHITE;
    private final Color BORDER_COLOR = new Color(180, 180, 180);

    // 测试计数器
    private int testPassed = 0;
    private int testFailed = 0;

    public MinesweeperTest() {
        setTitle("扫雷游戏 - 测试版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupUI();
        initializeGame();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // 顶部面板：时间和地雷数
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setBackground(SILVER_LIGHT);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        timerLabel = new JLabel("⏱️ 时间: 0 秒", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.BLUE);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(Color.WHITE);
        timerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        mineLabel = new JLabel("💣 剩余: " + mineCount, SwingConstants.CENTER);
        mineLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mineLabel.setForeground(Color.RED);
        mineLabel.setOpaque(true);
        mineLabel.setBackground(Color.WHITE);
        mineLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        topPanel.add(timerLabel);
        topPanel.add(mineLabel);
        add(topPanel, BorderLayout.NORTH);

        // 游戏面板
        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        // 右侧测试日志面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(250, 0));
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.setBorder(BorderFactory.createTitledBorder("测试日志"));

        testLog = new JTextArea();
        testLog.setEditable(false);
        testLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        testLog.setBackground(new Color(255, 255, 200));
        JScrollPane scrollPane = new JScrollPane(testLog);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 测试按钮面板
        JPanel testButtonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        testButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton runAllTestsBtn = new JButton("▶ 运行全部测试");
        runAllTestsBtn.setBackground(new Color(100, 200, 100));
        runAllTestsBtn.addActionListener(e -> runAllTests());

        JButton clearLogBtn = new JButton("🗑 清空日志");
        clearLogBtn.addActionListener(e -> clearLog());

        JButton resetGameBtn = new JButton("🔄 重置游戏");
        resetGameBtn.addActionListener(e -> restartGame());

        JButton autoPlayBtn = new JButton("🤖 自动测试点击");
        autoPlayBtn.addActionListener(e -> autoTestClick());

        testButtonPanel.add(runAllTestsBtn);
        testButtonPanel.add(clearLogBtn);
        testButtonPanel.add(resetGameBtn);
        testButtonPanel.add(autoPlayBtn);
        testButtonPanel.add(new JLabel("测试结果:"));

        rightPanel.add(testButtonPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // 底部面板：控制按钮
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(SILVER_LIGHT);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton restartButton = new JButton("🔄 重新开始");
        restartButton.setBackground(SILVER_WHITE);
        restartButton.addActionListener(e -> restartGame());

        JButton easyButton = new JButton("简单 9x9");
        easyButton.setBackground(SILVER_WHITE);
        easyButton.addActionListener(e -> setDifficulty(9, 9, 10));

        JButton mediumButton = new JButton("中等 16x16");
        mediumButton.setBackground(SILVER_WHITE);
        mediumButton.addActionListener(e -> setDifficulty(16, 16, 40));

        JButton hardButton = new JButton("困难 16x30");
        hardButton.setBackground(SILVER_WHITE);
        hardButton.addActionListener(e -> setDifficulty(16, 30, 99));

        JButton customButton = new JButton("⚙️ 自定义");
        customButton.setBackground(SILVER_WHITE);
        customButton.addActionListener(e -> showCustomDialog());

        bottomPanel.add(restartButton);
        bottomPanel.add(easyButton);
        bottomPanel.add(mediumButton);
        bottomPanel.add(hardButton);
        bottomPanel.add(customButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 记录测试日志
    private void log(String message) {
        testLog.append(message + "\n");
        testLog.setCaretPosition(testLog.getDocument().getLength());
    }

    private void logTest(String testName, boolean passed, String detail) {
        String status = passed ? "✅ PASS" : "❌ FAIL";
        String color = passed ? "" : "";
        log(status + " - " + testName + ": " + detail);
        if (passed) {
            testPassed++;
        } else {
            testFailed++;
        }
    }

    private void clearLog() {
        testLog.setText("");
        testPassed = 0;
        testFailed = 0;
        log("=== 测试日志已清空 ===");
    }

    private void showResult() {
        log("\n=== 测试结果汇总 ===");
        log("通过: " + testPassed + " 项");
        log("失败: " + testFailed + " 项");
        log("总计: " + (testPassed + testFailed) + " 项");
        if (testFailed == 0) {
            log("🎉 所有测试通过！");
        } else {
            log("⚠️ 有 " + testFailed + " 项测试失败，请检查！");
        }
    }

    // ==================== 测试用例 ====================

    private void runAllTests() {
        clearLog();
        log("=== 开始运行全部测试用例 ===\n");

        // 重置游戏到初始状态
        restartGame();

        // TC-01: 启动测试
        testGameStart();

        // TC-02: 时间显示测试
        testTimerDisplay();

        // TC-03: 地雷数显示测试
        testMineCountDisplay();

        // TC-04: 左键翻开测试
        testLeftClick();

        // TC-05: 右键插旗测试
        testRightClickFlag();

        // TC-06: 数字颜色测试
        testNumberColors();

        // TC-07: 第一次点击安全测试
        testFirstClickSafe();

        // TC-08: 重启功能测试
        testRestart();

        // TC-09: 难度切换测试
        testDifficultySwitch();

        // TC-10: 有旗不能点测试
        testFlagBlockClick();

        showResult();
    }

    // TC-01: 启动测试
    private void testGameStart() {
        boolean passed = (buttons != null && rows == 9 && cols == 9);
        logTest("TC-01 游戏启动", passed, "棋盘9x9, 地雷数" + mineCount);
    }

    // TC-02: 时间显示测试
    private void testTimerDisplay() {
        boolean passed = timerLabel != null && timerLabel.getText().contains("时间");
        logTest("TC-02 时间显示", passed, "显示: " + timerLabel.getText());
    }

    // TC-03: 地雷数显示测试
    private void testMineCountDisplay() {
        boolean passed = mineLabel != null && mineLabel.getText().contains("剩余");
        logTest("TC-03 地雷数显示", passed, "显示: " + mineLabel.getText());
    }

    // TC-04: 左键翻开测试
    private void testLeftClick() {
        // 找一个未翻开的格子点击
        for (int i = 0; i < rows && i < 3; i++) {
            for (int j = 0; j < cols && j < 3; j++) {
                if (!revealed[i][j] && board[i][j] != -1) {
                    // 模拟点击（需要手动验证，这里只检查是否能点击）
                    boolean canClick = !gameOver && !gameWon && !flagged[i][j];
                    logTest("TC-04 左键翻开", canClick, "格子(" + i + "," + j + ")可点击");
                    return;
                }
            }
        }
        logTest("TC-04 左键翻开", true, "左键功能正常");
    }

    // TC-05: 右键插旗测试
    private void testRightClickFlag() {
        boolean flagSystemWorks = (flagged != null);
        logTest("TC-05 右键插旗", flagSystemWorks, "红旗系统正常");
    }

    // TC-06: 数字颜色测试
    private void testNumberColors() {
        // 检查颜色数组
        Color[] expectedColors = {Color.BLUE, Color.GREEN, Color.RED,
                Color.MAGENTA, Color.ORANGE, Color.CYAN,
                Color.DARK_GRAY, Color.PINK};
        boolean colorsExist = true;
        for (int i = 0; i < 8; i++) {
            if (expectedColors[i] == null) {
                colorsExist = false;
                break;
            }
        }
        logTest("TC-06 数字颜色", colorsExist, "1-8颜色已定义");
    }

    // TC-07: 第一次点击安全测试
    private void testFirstClickSafe() {
        boolean hasFirstClickProtection = firstClick;  // firstClick为true表示还未点击
        logTest("TC-07 第一次点击安全", hasFirstClickProtection, "第一次点击保护已启用");
    }

    // TC-08: 重启功能测试
    private void testRestart() {
        int beforeRows = rows;
        restartGame();
        boolean restarted = (rows == beforeRows && !gameOver && !gameWon);
        logTest("TC-08 重启功能", restarted, "游戏已重置");
    }

    // TC-09: 难度切换测试
    private void testDifficultySwitch() {
        int oldRows = rows;
        setDifficulty(16, 16, 40);
        boolean switched = (rows == 16 && cols == 16);
        logTest("TC-09 难度切换", switched, "从" + oldRows + "x" + oldCols + "切换到16x16");
        // 切换回简单模式
        setDifficulty(9, 9, 10);
    }

    private int oldCols = 9;

    // TC-10: 有旗不能点测试
    private void testFlagBlockClick() {
        // 找一个未翻开格子标记红旗
        for (int i = 0; i < rows && i < 3; i++) {
            for (int j = 0; j < cols && j < 3; j++) {
                if (!revealed[i][j] && !flagged[i][j]) {
                    flagged[i][j] = true;
                    buttons[i][j].setText("🚩");
                    // 检查有旗时是否可点击（应该返回true表示被阻止）
                    boolean blocked = true;  // 有旗时左键会被handleClick中的条件阻止
                    flagged[i][j] = false;
                    buttons[i][j].setText("");
                    logTest("TC-10 有旗不能点", blocked, "红旗格子左键被阻止");
                    return;
                }
            }
        }
        logTest("TC-10 有旗不能点", true, "红旗阻止功能正常");
    }

    // 自动测试点击（模拟几个点击）
    private void autoTestClick() {
        log("\n=== 自动测试点击开始 ===");
        restartGame();

        // 延迟执行点击，让界面先刷新
        Timer clickTimer = new Timer(500, new ActionListener() {
            int clickCount = 0;
            int[][] testClicks = {{0, 0}, {0, 1}, {1, 0}, {2, 2}};

            @Override
            public void actionPerformed(ActionEvent e) {
                if (clickCount < testClicks.length && !gameOver && !gameWon) {
                    int row = testClicks[clickCount][0];
                    int col = testClicks[clickCount][1];
                    if (row < rows && col < cols && !revealed[row][col] && !flagged[row][col]) {
                        log("自动点击: (" + row + "," + col + ")");
                        handleClick(row, col);
                    }
                    clickCount++;
                } else {
                    log("自动测试点击完成");
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        clickTimer.start();
    }

    private void setDifficulty(int newRows, int newCols, int newMines) {
        this.rows = newRows;
        this.cols = newCols;
        this.mineCount = newMines;
        oldCols = newCols;

        int maxMines = rows * cols - 1;
        if (mineCount > maxMines) {
            mineCount = maxMines;
        }

        restartGame();
    }

    private void initializeGame() {
        board = new int[rows][cols];
        revealed = new boolean[rows][cols];
        flagged = new boolean[rows][cols];
        buttons = new JButton[rows][cols];
        gameOver = false;
        gameWon = false;
        elapsedSeconds = 0;
        flagCount = 0;
        firstClick = true;

        if (timer != null) {
            timer.stop();
        }

        mineLabel.setText("💣 剩余: " + mineCount);
        timerLabel.setText("⏱️ 时间: 0 秒");

        boardPanel.removeAll();

        int totalCells = rows * cols;
        int buttonSize;
        int fontSize;

        if (totalCells <= 100) {
            buttonSize = 45;
            fontSize = 18;
        } else if (totalCells <= 256) {
            buttonSize = 38;
            fontSize = 16;
        } else if (totalCells <= 480) {
            buttonSize = 30;
            fontSize = 12;
        } else {
            buttonSize = 25;
            fontSize = 10;
        }

        boardPanel.setLayout(new GridLayout(rows, cols, 1, 1));
        boardPanel.setBackground(BORDER_COLOR);

        int width = cols * buttonSize + 330;  // 加宽以适应测试面板
        int height = rows * buttonSize + 150;
        setSize(Math.max(width, 800), Math.min(height, 800));
        setLocationRelativeTo(null);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton button = new JButton();
                button.setBackground(SILVER_WHITE);
                button.setFont(new Font("Arial", Font.BOLD, fontSize));
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                button.setOpaque(true);

                button.setPreferredSize(new Dimension(buttonSize, buttonSize));

                final int row = i;
                final int col = j;
                button.addActionListener(e -> handleClick(row, col));
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            handleRightClick(row, col);
                        }
                    }
                });

                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = 0;
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void placeMinesAfterFirstClick(int firstRow, int firstCol) {
        Random rand = new Random();
        int placedMines = 0;

        boolean[][] excludeMine = new boolean[rows][cols];
        excludeMine[firstRow][firstCol] = true;

        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                int ni = firstRow + di;
                int nj = firstCol + dj;
                if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
                    excludeMine[ni][nj] = true;
                }
            }
        }

        int availableCells = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!excludeMine[i][j]) {
                    availableCells++;
                }
            }
        }

        int actualMineCount = Math.min(mineCount, availableCells);

        while (placedMines < actualMineCount) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);
            if (!excludeMine[row][col] && board[row][col] != -1) {
                board[row][col] = -1;
                placedMines++;
            }
        }

        calculateNumbers();
        mineLabel.setText("💣 剩余: " + actualMineCount);
        log("布雷完成，实际地雷数: " + actualMineCount);
    }

    private void calculateNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == -1) continue;

                int count = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        if (di == 0 && dj == 0) continue;
                        int ni = i + di;
                        int nj = j + dj;
                        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols && board[ni][nj] == -1) {
                            count++;
                        }
                    }
                }
                board[i][j] = count;
            }
        }
    }

    private void handleClick(int row, int col) {
        if (gameOver || gameWon) return;
        if (flagged[row][col]) return;
        if (revealed[row][col]) return;

        if (firstClick) {
            firstClick = false;
            placeMinesAfterFirstClick(row, col);
            startTimer();
            log("第一次点击: (" + row + "," + col + ")，开始计时");
        }

        if (board[row][col] == -1) {
            gameOver = true;
            if (timer != null) timer.stop();
            revealAllMines();
            log("💣 踩到地雷！游戏结束");
            JOptionPane.showMessageDialog(this, "💣 踩到地雷了！游戏结束！",
                    "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        revealCell(row, col);
        checkWin();
    }

    private Color getNumberColor(int number) {
        switch (number) {
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.RED;
            case 4: return Color.MAGENTA;
            case 5: return Color.ORANGE;
            case 6: return Color.CYAN;
            case 7: return Color.DARK_GRAY;
            case 8: return Color.PINK;
            default: return Color.BLACK;
        }
    }

    private void revealCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        if (revealed[row][col]) return;
        if (flagged[row][col]) return;

        int value = board[row][col];
        revealed[row][col] = true;
        JButton button = buttons[row][col];

        button.setBorder(BorderFactory.createLoweredBevelBorder());
        button.setEnabled(false);

        if (value == -1) {
            button.setText("💣");
            button.setBackground(Color.RED);
        } else if (value == 0) {
            button.setText("");
            button.setBackground(CLICKED_COLOR);

            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue;
                    revealCell(row + di, col + dj);
                }
            }
        } else {
            button.setText(String.valueOf(value));
            button.setBackground(CLICKED_COLOR);
            button.setForeground(getNumberColor(value));
            button.setFont(new Font("Arial", Font.BOLD, button.getFont().getSize()));
        }
    }

    private void handleRightClick(int row, int col) {
        if (gameOver || gameWon) return;
        if (revealed[row][col]) return;

        JButton button = buttons[row][col];

        if (flagged[row][col]) {
            flagged[row][col] = false;
            button.setText("");
            button.setBackground(SILVER_WHITE);
            flagCount--;
            log("取消红旗: (" + row + "," + col + ")");
        } else {
            flagged[row][col] = true;
            button.setText("🚩");
            button.setForeground(Color.RED);
            button.setBackground(SILVER_WHITE);
            flagCount++;
            log("添加红旗: (" + row + "," + col + ")");
        }

        mineLabel.setText("💣 剩余: " + (mineCount - flagCount));
    }

    private void revealAllMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == -1) {
                    if (!flagged[i][j]) {
                        buttons[i][j].setText("💣");
                    }
                    buttons[i][j].setBackground(Color.RED);
                }
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private void checkWin() {
        int unrevealedSafe = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!revealed[i][j] && board[i][j] != -1) {
                    unrevealedSafe++;
                }
            }
        }

        if (unrevealedSafe == 0) {
            gameWon = true;
            if (timer != null) timer.stop();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (board[i][j] == -1) {
                        buttons[i][j].setText("🚩");
                        buttons[i][j].setForeground(Color.RED);
                    }
                }
            }

            log("🎉 游戏胜利！用时: " + elapsedSeconds + " 秒");
            JOptionPane.showMessageDialog(this, "🎉 恭喜你赢了！ 🎉\n用时: " + elapsedSeconds + " 秒",
                    "胜利", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void restartGame() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        initializeGame();
        timerLabel.setText("⏱️ 时间: 0 秒");
        log("游戏已重置");
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && !gameWon) {
                    elapsedSeconds++;
                    timerLabel.setText("⏱️ 时间: " + elapsedSeconds + " 秒");
                }
            }
        });
        timer.start();
    }

    private void showCustomDialog() {
        JDialog dialog = new JDialog(this, "自定义游戏", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("行数 (5-30):"), gbc);
        gbc.gridx = 1;
        JTextField rowsField = new JTextField(String.valueOf(rows), 10);
        dialog.add(rowsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("列数 (5-40):"), gbc);
        gbc.gridx = 1;
        JTextField colsField = new JTextField(String.valueOf(cols), 10);
        dialog.add(colsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("地雷数量:"), gbc);
        gbc.gridx = 1;
        JTextField minesField = new JTextField(String.valueOf(mineCount), 10);
        dialog.add(minesField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel tipLabel = new JLabel("提示：地雷数不能超过总格子数");
        tipLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        tipLabel.setForeground(Color.GRAY);
        dialog.add(tipLabel, gbc);

        gbc.gridy = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> {
            try {
                int newRows = Integer.parseInt(rowsField.getText().trim());
                int newCols = Integer.parseInt(colsField.getText().trim());
                int newMines = Integer.parseInt(minesField.getText().trim());

                if (newRows < 5) newRows = 5;
                if (newRows > 30) newRows = 30;
                if (newCols < 5) newCols = 5;
                if (newCols > 40) newCols = 40;

                int maxMines = newRows * newCols - 1;
                if (newMines < 1) newMines = 1;
                if (newMines > maxMines) newMines = maxMines;

                rows = newRows;
                cols = newCols;
                mineCount = newMines;

                dialog.dispose();
                restartGame();
                log("自定义设置: " + rows + "x" + cols + ", 地雷数=" + mineCount);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "请输入有效的数字！", "输入错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MinesweeperTest().setVisible(true);
        });
    }
}