package com.minesweeper.ui;

import com.minesweeper.model.Difficulty;
import com.minesweeper.resource.Language;
import javax.swing.*;
import java.awt.*;

/**
 * 自定义难度对话框
 */
public class CustomDialog extends JDialog {
    private JTextField rowsField;
    private JTextField colsField;
    private JTextField minesField;
    private boolean confirmed = false;
    private int rows = 16;
    private int cols = 16;
    private int mines = 40;

    public CustomDialog(JFrame parent) {
        super(parent, Language.CUSTOM_TITLE, true);
        setLayout(new BorderLayout());

        // 创建输入面板
        JPanel inputPanel = createInputPanel();

        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // 设置对话框属性
        setSize(300, 180);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 行数输入
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel(Language.ROWS_LABEL), gbc);

        gbc.gridx = 1;
        rowsField = new JTextField("16", 10);
        panel.add(rowsField, gbc);

        // 列数输入
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel(Language.COLS_LABEL), gbc);

        gbc.gridx = 1;
        colsField = new JTextField("16", 10);
        panel.add(colsField, gbc);

        // 地雷数输入
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel(Language.MINES_LABEL), gbc);

        gbc.gridx = 1;
        minesField = new JTextField("40", 10);
        panel.add(minesField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    private boolean validateInput() {
        try {
            rows = Integer.parseInt(rowsField.getText().trim());
            cols = Integer.parseInt(colsField.getText().trim());
            mines = Integer.parseInt(minesField.getText().trim());

            // 验证范围
            if (rows < 9 || rows > 30) {
                JOptionPane.showMessageDialog(this, "行数必须在9-30之间！",
                        Language.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (cols < 9 || cols > 40) {
                JOptionPane.showMessageDialog(this, "列数必须在9-40之间！",
                        Language.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }

            int maxMines = rows * cols - 9;
            if (mines > maxMines) {
                JOptionPane.showMessageDialog(this,
                        String.format(Language.ERROR_TOO_MANY_MINES, maxMines),
                        Language.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (mines < 1) {
                JOptionPane.showMessageDialog(this, Language.ERROR_TOO_FEW_MINES,
                        Language.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, Language.ERROR_INVALID_NUMBER,
                    Language.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }
}