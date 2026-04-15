import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Minesweeper extends JFrame {
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

    private final Color SILVER_WHITE = new Color(220, 220, 220);
    private final Color SILVER_LIGHT = new Color(230, 230, 230);
    private final Color CLICKED_COLOR = Color.WHITE;
    private final Color BORDER_COLOR = new Color(180, 180, 180);

    public Minesweeper() {
        setTitle("扫雷游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        // 强制使用跨平台外观，避免系统主题干扰
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

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(SILVER_LIGHT);
        timerLabel = new JLabel("⏱️ 时间: 0 秒");
        mineLabel = new JLabel("💣 剩余: " + mineCount);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        mineLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(timerLabel);
        topPanel.add(mineLabel);
        add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(SILVER_LIGHT);
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

    private void setDifficulty(int newRows, int newCols, int newMines) {
        this.rows = newRows;
        this.cols = newCols;
        this.mineCount = newMines;

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

        int width = cols * buttonSize + 80;
        int height = rows * buttonSize + 150;
        setSize(Math.max(width, 500), Math.min(height, 800));
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
                button.setMinimumSize(new Dimension(buttonSize, buttonSize));
                button.setMaximumSize(new Dimension(buttonSize, buttonSize));

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
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        if (!revealed[row][col] && !flagged[row][col] && !gameOver && !gameWon) {
                            button.setBackground(SILVER_LIGHT);
                        }
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        if (!revealed[row][col] && !flagged[row][col] && !gameOver && !gameWon) {
                            button.setBackground(SILVER_WHITE);
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
        }

        if (board[row][col] == -1) {
            gameOver = true;
            if (timer != null) timer.stop();
            revealAllMines();
            JOptionPane.showMessageDialog(this, "💣 踩到地雷了！游戏结束！",
                    "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        revealCell(row, col);
        checkWin();
    }

    private Color getNumberColor(int num) {
        switch (num) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            case 5: return new Color(128, 0, 0);
            case 6: return Color.CYAN;
            case 7: return Color.MAGENTA;
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

            // 递归翻开周围8个格子
            for (int di = -1; di <= 1; di++) {
                for (int dj = -1; dj <= 1; dj++) {
                    if (di == 0 && dj == 0) continue;
                    revealCell(row + di, col + dj);
                }
            }
        } else {
            // ⭐ 关键：显示数字并设置颜色
            button.setText(String.valueOf(value));
            button.setBackground(CLICKED_COLOR);
            button.setForeground(getNumberColor(value));  // 设置文字颜色
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
        } else {
            flagged[row][col] = true;
            button.setText("🚩");
            button.setForeground(Color.RED);
            button.setFont(new Font("Segoe UI", Font.BOLD, button.getFont().getSize()));
            button.setBackground(SILVER_WHITE);
            flagCount++;
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
            new Minesweeper().setVisible(true);
        });
    }
}