package org.example.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChatClientManager extends JFrame {
    private List<ChatClient> chatWindows;
    private JPanel mainPanel;
    private JButton newChatButton;
    private JButton emojiButton;
    private JLabel windowCountLabel;

    private static final String[] EMOJIS = {
            "😊", "😂", "❤️", "🎉", "🔥", "😍",
            "👏", "💪", "", "😎",
            "😅", "😭", "😘", "😱", "😇", "🙂", "",
            "", "✨"
    };

    public ChatClientManager() {
        chatWindows = new ArrayList<>();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("多窗口聊天管理器");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("多窗口聊天室管理器");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 12));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        newChatButton = new JButton("新建聊天窗口");
        newChatButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        newChatButton.setPreferredSize(new Dimension(180, 45));
        newChatButton.setBackground(new Color(76, 175, 80));
        newChatButton.setForeground(Color.BLACK);
        newChatButton.setFocusPainted(false);
        newChatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewChatWindow();
            }
        });
        buttonPanel.add(newChatButton);

        emojiButton = new JButton("😊 表情面板");
        emojiButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        emojiButton.setPreferredSize(new Dimension(180, 45));
        emojiButton.setBackground(new Color(33, 150, 243));
        emojiButton.setForeground(Color.BLACK);
        emojiButton.setFocusPainted(false);
        emojiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEmojiPicker();
            }
        });
        buttonPanel.add(emojiButton);

        JButton closeAllButton = new JButton("关闭所有窗口");
        closeAllButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        closeAllButton.setPreferredSize(new Dimension(180, 45));
        closeAllButton.setBackground(new Color(244, 67, 54));
        closeAllButton.setForeground(Color.BLACK);
        closeAllButton.setFocusPainted(false);
        closeAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAllWindows();
            }
        });
        buttonPanel.add(closeAllButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel quickEmojiPanel = createQuickEmojiPanel();
        mainPanel.add(quickEmojiPanel, BorderLayout.EAST);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        windowCountLabel = new JLabel("当前打开的聊天窗口数: 0");
        windowCountLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
        windowCountLabel.setForeground(Color.BLACK);
        statusPanel.add(windowCountLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private Font createEmojiFont(int size) {
        String[] emojiFonts = {
                "Segoe UI Emoji",
                "Noto Color Emoji",
                "Apple Color Emoji",
                "Segoe UI Symbol",
                "Symbola",
                "Microsoft YaHei UI"
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        for (String fontName : emojiFonts) {
            for (String available : availableFonts) {
                if (available.equalsIgnoreCase(fontName)) {
                    return new Font(fontName, Font.PLAIN, size);
                }
            }
        }

        return new Font("Microsoft YaHei UI", Font.PLAIN, size);
    }

    private JPanel createQuickEmojiPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 4, 6, 6));
        panel.setBackground(new Color(255, 255, 255));

        javax.swing.border.TitledBorder titleBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                "快捷表情"
        );
        titleBorder.setTitleFont(new Font("微软雅黑", Font.BOLD, 13));
        titleBorder.setTitleColor(Color.BLACK);

        panel.setBorder(BorderFactory.createCompoundBorder(
                titleBorder,
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.setPreferredSize(new Dimension(160, 320));

        for (String emoji : EMOJIS) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(createEmojiFont(20));
            emojiBtn.setPreferredSize(new Dimension(32, 32));
            emojiBtn.setMinimumSize(new Dimension(32, 32));
            emojiBtn.setMaximumSize(new Dimension(32, 32));
            emojiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            emojiBtn.setToolTipText("点击插入到所有窗口: " + emoji);
            emojiBtn.setHorizontalAlignment(SwingConstants.CENTER);
            emojiBtn.setVerticalAlignment(SwingConstants.CENTER);
            emojiBtn.setBackground(Color.WHITE);
            emojiBtn.setOpaque(true);
            emojiBtn.setFocusPainted(false);
            emojiBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
            emojiBtn.setMargin(new Insets(0, 0, 0, 0));

            emojiBtn.addActionListener(e -> {
                insertEmojiToAllWindows(emoji);
            });

            panel.add(emojiBtn);
        }

        return panel;
    }

    private void showEmojiPicker() {
        if (chatWindows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先创建聊天窗口！",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog emojiDialog = new JDialog(this, "表情选择器", true);
        emojiDialog.setSize(420, 380);
        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("选择表情插入到所有聊天窗口", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel emojiGrid = new JPanel(new GridLayout(6, 6, 10, 10));
        emojiGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        emojiGrid.setBackground(new Color(245, 247, 250));

        for (String emoji : EMOJIS) {
            JButton emojiBtn = new JButton(emoji);
            Font emojiFont = createEmojiFont(24);
            emojiBtn.setFont(emojiFont);
            emojiBtn.setPreferredSize(new Dimension(48, 48));
            emojiBtn.setMinimumSize(new Dimension(48, 48));
            emojiBtn.setMaximumSize(new Dimension(48, 48));
            emojiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            emojiBtn.setToolTipText("点击插入: " + emoji);
            emojiBtn.setHorizontalAlignment(SwingConstants.CENTER);
            emojiBtn.setVerticalAlignment(SwingConstants.CENTER);
            emojiBtn.setContentAreaFilled(true);
            emojiBtn.setBorderPainted(true);
            emojiBtn.setBackground(Color.WHITE);
            emojiBtn.setOpaque(true);
            emojiBtn.setFocusPainted(false);
            emojiBtn.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
            emojiBtn.setMargin(new Insets(0, 0, 0, 0));

            emojiBtn.addActionListener(e -> {
                insertEmojiToAllWindows(emoji);
                emojiDialog.dispose();
            });

            emojiGrid.add(emojiBtn);
        }

        JScrollPane scrollPane = new JScrollPane(emojiGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(245, 247, 250));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微软雅黑", Font.BOLD, 12));
        cancelBtn.addActionListener(e -> emojiDialog.dispose());
        bottomPanel.add(cancelBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        emojiDialog.add(mainPanel);
        emojiDialog.setVisible(true);
    }

    private void insertEmojiToAllWindows(String emoji) {
        if (chatWindows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先创建聊天窗口！",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int count = 0;
        for (ChatClient client : chatWindows) {
            if (client != null && client.isVisible()) {
                client.insertEmoji(emoji);
                count++;
            }
        }

        if (count > 0) {
            System.out.println("已插入表情 " + emoji + " 到 " + count + " 个聊天窗口");
        }
    }

    private void createNewChatWindow() {
        SwingUtilities.invokeLater(() -> {
            ChatClient newClient = new ChatClient();
            newClient.setTitle("聊天室 - 窗口 " + (chatWindows.size() + 1));
            newClient.setVisible(true);
            newClient.connect();

            chatWindows.add(newClient);
            updateWindowCount();

            newClient.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    chatWindows.remove(newClient);
                    updateWindowCount();
                }
            });
        });
    }

    private void closeAllWindows() {
        int response = JOptionPane.showConfirmDialog(this,
                "确定要关闭所有聊天窗口吗？",
                "确认关闭",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            for (ChatClient client : chatWindows) {
                client.disconnect();
                client.dispose();
            }
            chatWindows.clear();
            updateWindowCount();
        }
    }

    private void updateWindowCount() {
        windowCountLabel.setText("当前打开的聊天窗口数: " + chatWindows.size());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChatClientManager manager = new ChatClientManager();
            manager.setVisible(true);
        });
    }
}
