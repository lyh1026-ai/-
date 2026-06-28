package org.example.Client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatClient extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running = true;
    private String currentUser = "我";
    private String selectedAvatar = "😀";

    private JSplitPane mainSplitPane;
    private JPanel leftPanel;
    private JPanel userProfilePanel;
    private JList<String> onlineUsersList;
    private DefaultListModel<String> onlineUsersModel;
    private JLabel connectionStatus;

    private JPanel rightPanel;
    private JPanel chatHeader;
    private JLabel chatTitle;
    private JPanel chatContentPanel;
    private JPanel messageContainer;
    private JScrollPane messageScrollPane;
    private JPanel inputPanel;
    private JTextArea messageInput;
    private JButton sendButton;
    private JButton emojiButton;

    private JButton avatarButton;
    private static final String[] AVATARS = {
            "😀", "😎", "🤗", "😇", "🥳", "😜", "🤩",
            "🐶", "🐼", "🐨", "🦊", "🐯",
            "🌟", "⭐", "💎", "💫", "🎨", "🎭", "🎵", "🎮"
    };

    public ChatClient() {
        initializeGUI();
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

    private void initializeGUI() {
        setTitle("多人聊天室");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(new Color(240, 242, 245));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setDividerSize(3);
        mainSplitPane.setBorder(null);

        leftPanel = createLeftPanel();
        rightPanel = createRightPanel();

        mainSplitPane.setLeftComponent(leftPanel);
        mainSplitPane.setRightComponent(rightPanel);

        setContentPane(mainSplitPane);
    }

    private JPanel createLeftPanel() {
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(45, 52, 54));
        leftPanel.setPreferredSize(new Dimension(250, 650));

        userProfilePanel = createUserProfilePanel();
        leftPanel.add(userProfilePanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(new Color(45, 52, 54));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel listTitle = new JLabel("在线用户");
        listTitle.setForeground(new Color(185, 195, 205));
        listTitle.setFont(new Font("微软雅黑", Font.BOLD, 14));
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        listPanel.add(listTitle, BorderLayout.NORTH);

        onlineUsersModel = new DefaultListModel<>();
        onlineUsersList = new JList<>(onlineUsersModel);
        onlineUsersList.setCellRenderer(new UserListCellRenderer());
        onlineUsersList.setBackground(new Color(45, 52, 54));
        onlineUsersList.setForeground(Color.WHITE);
        onlineUsersList.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        onlineUsersList.setSelectionBackground(new Color(57, 68, 77));
        onlineUsersList.setSelectionForeground(Color.WHITE);
        onlineUsersList.setBorder(null);

        JScrollPane listScrollPane = new JScrollPane(onlineUsersList);
        listScrollPane.setBorder(null);
        listScrollPane.getViewport().setBackground(new Color(45, 52, 54));
        listPanel.add(listScrollPane, BorderLayout.CENTER);

        leftPanel.add(listPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(45, 52, 54));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        connectionStatus = new JLabel("● 未连接");
        connectionStatus.setForeground(new Color(231, 76, 60));
        connectionStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        bottomPanel.add(connectionStatus, BorderLayout.WEST);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    private JPanel createUserProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(57, 68, 77));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);

        avatarButton = new JButton(selectedAvatar);
        avatarButton.setFont(createEmojiFont(32));
        avatarButton.setPreferredSize(new Dimension(50, 50));
        avatarButton.setBackground(new Color(67, 78, 87));
        avatarButton.setForeground(Color.WHITE);
        avatarButton.setBorder(BorderFactory.createLineBorder(new Color(100, 110, 120), 2));
        avatarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        avatarButton.setToolTipText("点击选择头像");
        avatarButton.addActionListener(e -> showAvatarPicker());
        topPanel.add(avatarButton);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setOpaque(false);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        namePanel.setOpaque(false);
        JLabel nameLabel = new JLabel("聊天室");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        namePanel.add(nameLabel);

        infoPanel.add(namePanel, BorderLayout.NORTH);

        JTextField usernameField = new JTextField("用户" + (int)(Math.random() * 1000), 15);
        usernameField.setBackground(new Color(67, 78, 87));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 110, 120), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        infoPanel.add(usernameField, BorderLayout.CENTER);

        topPanel.add(infoPanel);
        panel.add(topPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showAvatarPicker() {
        JDialog avatarDialog = new JDialog(this, "选择头像", true);
        avatarDialog.setSize(400, 350);
        avatarDialog.setLocationRelativeTo(this);
        avatarDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("选择你的头像", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel avatarGrid = new JPanel(new GridLayout(4, 5, 10, 10));
        avatarGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        avatarGrid.setBackground(new Color(245, 247, 250));

        for (String avatar : AVATARS) {
            JButton avatarBtn = new JButton(avatar);
            avatarBtn.setFont(createEmojiFont(36));
            avatarBtn.setPreferredSize(new Dimension(55, 55));
            avatarBtn.setMinimumSize(new Dimension(55, 55));
            avatarBtn.setMaximumSize(new Dimension(55, 55));
            avatarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            avatarBtn.setToolTipText("选择头像: " + avatar);
            avatarBtn.setHorizontalAlignment(SwingConstants.CENTER);
            avatarBtn.setVerticalAlignment(SwingConstants.CENTER);
            avatarBtn.setBackground(Color.WHITE);
            avatarBtn.setOpaque(true);
            avatarBtn.setFocusPainted(false);
            avatarBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            avatarBtn.setMargin(new Insets(0, 0, 0, 0));

            avatarBtn.addActionListener(e -> {
                selectedAvatar = avatar;
                avatarButton.setText(selectedAvatar);
                avatarDialog.dispose();

                if (socket != null && !socket.isClosed()) {
                    chatTitle.setText("聊天室 - " + currentUser + " " + selectedAvatar);
                }
            });

            avatarGrid.add(avatarBtn);
        }

        JScrollPane scrollPane = new JScrollPane(avatarGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> avatarDialog.dispose());
        bottomPanel.add(cancelBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        avatarDialog.add(mainPanel);
        avatarDialog.setVisible(true);
    }

    private JPanel createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        chatHeader = createChatHeader();
        rightPanel.add(chatHeader, BorderLayout.NORTH);

        chatContentPanel = createChatContentPanel();
        rightPanel.add(chatContentPanel, BorderLayout.CENTER);

        inputPanel = createInputPanel();
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel createChatHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        chatTitle = new JLabel("聊天室 - 所有人");
        chatTitle.setFont(new Font("微软雅黑", Font.BOLD, 16));
        chatTitle.setForeground(new Color(51, 51, 51));
        panel.add(chatTitle, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton settingsButton = new JButton("设置");
        settingsButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        settingsButton.setBackground(new Color(240, 242, 245));
        settingsButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        buttonPanel.add(settingsButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createChatContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));

        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBackground(new Color(245, 247, 250));
        messageContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageScrollPane = new JScrollPane(messageContainer);
        messageScrollPane.setBorder(null);
        messageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messageScrollPane.getViewport().setBackground(new Color(245, 247, 250));

        panel.add(messageScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        toolbarPanel.setOpaque(false);

        emojiButton = new JButton("😊");
        emojiButton.setFont(createEmojiFont(24));
        emojiButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        emojiButton.setBackground(Color.WHITE);
        emojiButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emojiButton.addActionListener(e -> showEmojiPicker());
        emojiButton.setToolTipText("点击选择表情");
        emojiButton.setHorizontalAlignment(SwingConstants.CENTER);
        toolbarPanel.add(emojiButton);

        panel.add(toolbarPanel, BorderLayout.NORTH);

        messageInput = new JTextArea(3, 30);
        messageInput.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageInput.setLineWrap(true);
        messageInput.setWrapStyleWord(true);
        messageInput.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        messageInput.setEnabled(false);

        JScrollPane inputScrollPane = new JScrollPane(messageInput);
        inputScrollPane.setBorder(null);
        panel.add(inputScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 13));
        sendButton.setPreferredSize(new Dimension(80, 35));
        sendButton.setBackground(new Color(76, 175, 80));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setEnabled(false);
        sendButton.addActionListener(e -> sendMessage());
        buttonPanel.add(sendButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    if (sendButton.isEnabled()) {
                        sendMessage();
                    }
                }
            }
        });

        return panel;
    }

    private void addMessage(String message, boolean isMine) {
        SwingUtilities.invokeLater(() -> {
            JPanel messageBubble = createMessageBubble(message, isMine);
            messageContainer.add(messageBubble);
            messageContainer.revalidate();
            messageContainer.repaint();

            Timer timer = new Timer(100, e -> {
                JScrollBar vertical = messageScrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
            timer.setRepeats(false);
            timer.start();
        });
    }

    private JPanel createMessageBubble(String message, boolean isMine) {
        JPanel bubblePanel = new JPanel(new BorderLayout(10, 0));
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        bubblePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        if (isMine) {
            bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));

            JLabel timeLabel = new JLabel(time);
            timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            timeLabel.setForeground(new Color(150, 150, 150));
            bubblePanel.add(timeLabel);
            bubblePanel.add(Box.createHorizontalStrut(10));

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);

            JPanel bubble = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 230, 62));
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            bubble.setLayout(new BorderLayout(10, 5));
            bubble.setOpaque(false);
            bubble.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            JLabel textLabel = new JLabel("<html><body style='width: 400px;'>" + message + "</body></html>");
            textLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textLabel.setForeground(Color.BLACK);
            bubble.add(textLabel, BorderLayout.CENTER);

            textPanel.add(bubble, BorderLayout.EAST);
            bubblePanel.add(textPanel);
            bubblePanel.add(Box.createHorizontalStrut(10));

            JLabel userAvatar = new JLabel(selectedAvatar);
            userAvatar.setFont(createEmojiFont(24));
            bubblePanel.add(userAvatar);
        } else {
            JLabel userAvatar = new JLabel("😀");
            userAvatar.setFont(createEmojiFont(24));
            bubblePanel.add(userAvatar);
            bubblePanel.add(Box.createHorizontalStrut(10));

            JLabel timeLabel = new JLabel(time);
            timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            timeLabel.setForeground(new Color(150, 150, 150));
            bubblePanel.add(timeLabel);
            bubblePanel.add(Box.createHorizontalStrut(10));

            JPanel textPanel = new JPanel(new BorderLayout());
            textPanel.setOpaque(false);

            JPanel bubble = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            bubble.setLayout(new BorderLayout(10, 5));
            bubble.setOpaque(false);
            bubble.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            JLabel textLabel = new JLabel("<html><body style='width: 400px;'>" + message + "</body></html>");
            textLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            textLabel.setForeground(Color.BLACK);
            bubble.add(textLabel, BorderLayout.CENTER);

            textPanel.add(bubble, BorderLayout.WEST);
            bubblePanel.add(textPanel);
        }

        return bubblePanel;
    }

    private class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setOpaque(!isSelected);
            if (isSelected) {
                panel.setBackground(new Color(57, 68, 77));
            } else {
                panel.setBackground(new Color(45, 52, 54));
            }

            JPanel avatarPanel = new JPanel(new GridBagLayout());
            avatarPanel.setOpaque(false);
            avatarPanel.setPreferredSize(new Dimension(35, 35));

            String username = value.toString();
            String initial = username.substring(0, 1).toUpperCase();
            Color avatarColor = Color.getHSBColor(username.hashCode() % 360 / 360.0f, 0.5f, 0.7f);

            JLabel avatarLabel = new JLabel(initial);
            avatarLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setOpaque(true);
            avatarLabel.setBackground(avatarColor);
            avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLabel.setPreferredSize(new Dimension(32, 32));

            JPanel onlineIndicator = new JPanel();
            onlineIndicator.setPreferredSize(new Dimension(10, 10));
            onlineIndicator.setBackground(new Color(76, 175, 80));
            onlineIndicator.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 54), 2));

            GridBagLayout gbl = (GridBagLayout) avatarPanel.getLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            avatarPanel.add(avatarLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            avatarPanel.add(onlineIndicator, gbc);

            panel.add(avatarPanel, BorderLayout.WEST);

            JLabel nameLabel = new JLabel(username);
            nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
            nameLabel.setForeground(isSelected ? Color.WHITE : new Color(185, 195, 205));
            panel.add(nameLabel, BorderLayout.CENTER);

            return panel;
        }
    }

    public void connect() {
        String server = SERVER_ADDRESS;
        int port = SERVER_PORT;
        String username = "用户" + (int)(Math.random() * 1000);

        currentUser = username;

        connectionStatus.setText("● 连接中...");
        connectionStatus.setForeground(new Color(241, 196, 15));

        Thread connectThread = new Thread(() -> {
            try {
                socket = new Socket(server, port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                running = true;

                SwingUtilities.invokeLater(() -> {
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                    connectionStatus.setText("● 已连接");
                    connectionStatus.setForeground(new Color(76, 175, 80));
                    chatTitle.setText("聊天室 - " + username + " " + selectedAvatar);
                    messageInput.requestFocus();
                    addMessage("=== 已连接到服务器 ===", false);
                });

                Thread receiveThread = new Thread(this::receiveMessages);
                receiveThread.setDaemon(true);
                receiveThread.start();

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    addMessage("=== 连接失败: " + e.getMessage() + " ===", false);
                    connectionStatus.setText("● 连接失败");
                    connectionStatus.setForeground(new Color(231, 76, 60));
                });
            }
        });
        connectThread.start();
    }

    public void disconnect() {
        if (out != null) {
            out.println("/quit");
        }
        close();
        SwingUtilities.invokeLater(() -> {
            messageInput.setEnabled(false);
            sendButton.setEnabled(false);
            connectionStatus.setText("● 未连接");
            connectionStatus.setForeground(new Color(231, 76, 60));
            chatTitle.setText("聊天室 - 所有人");
        });
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            addMessage(message, true);
            messageInput.setText("");
            messageInput.requestFocus();
        }
    }

    public void insertEmoji(String emoji) {
        SwingUtilities.invokeLater(() -> {
            if (messageInput.isEnabled()) {
                int caretPos = messageInput.getCaretPosition();
                String currentText = messageInput.getText();
                String newText = currentText.substring(0, caretPos) + emoji + currentText.substring(caretPos);
                messageInput.setText(newText);
                messageInput.setCaretPosition(caretPos + emoji.length());
                messageInput.requestFocus();
            }
        });
    }

    private void showEmojiPicker() {
        String[] emojis = {
                "😊", "😂", "❤️", "😄", "🎉", "🔥", "😍", "😢",
                "👏", "😴", "🌟", "💪", "🙏", "😎",
                "😅", "😭", "🤗", "😱", "😇", "🙂", "😋",
                "👋", "👌", "🤝", "💕", "✨", "🎈", "🎁"
        };

        JDialog emojiDialog = new JDialog(this, "表情选择器", true);
        emojiDialog.setSize(450, 350);
        emojiDialog.setLocationRelativeTo(this);
        emojiDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        JLabel titleLabel = new JLabel("选择表情", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel emojiGrid = new JPanel(new GridLayout(5, 6, 10, 10));
        emojiGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        emojiGrid.setBackground(new Color(245, 247, 250));

        for (String emoji : emojis) {
            JButton emojiBtn = new JButton(emoji);
            emojiBtn.setFont(createEmojiFont(28));
            emojiBtn.setPreferredSize(new Dimension(52, 52));
            emojiBtn.setMinimumSize(new Dimension(52, 52));
            emojiBtn.setMaximumSize(new Dimension(52, 52));
            emojiBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            emojiBtn.setToolTipText("点击插入: " + emoji);
            emojiBtn.setHorizontalAlignment(SwingConstants.CENTER);
            emojiBtn.setVerticalAlignment(SwingConstants.CENTER);
            emojiBtn.setBackground(Color.WHITE);
            emojiBtn.setOpaque(true);
            emojiBtn.setFocusPainted(false);
            emojiBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            emojiBtn.setMargin(new Insets(0, 0, 0, 0));

            emojiBtn.addActionListener(e -> {
                insertEmoji(emoji);
                emojiDialog.dispose();
            });

            emojiGrid.add(emojiBtn);
        }

        JScrollPane scrollPane = new JScrollPane(emojiGrid);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(245, 247, 250));
        JButton cancelBtn = new JButton("取消");
        cancelBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        cancelBtn.addActionListener(e -> emojiDialog.dispose());
        bottomPanel.add(cancelBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        emojiDialog.add(mainPanel);
        emojiDialog.setVisible(true);
    }

    private void receiveMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                final String finalMessage = message;
                SwingUtilities.invokeLater(() -> {
                    if (finalMessage.startsWith("【系统】")) {
                        addMessage(finalMessage.replace("【系统】", ""), false);
                    } else if (finalMessage.startsWith("【私聊】")) {
                        addMessage(finalMessage, false);
                    } else if (finalMessage.startsWith("=== 在线用户列表")) {
                        addMessage(finalMessage, false);
                    } else if (finalMessage.contains("】")) {
                        String sender = finalMessage.substring(0, finalMessage.indexOf("】")).replace("【", "");
                        String content = finalMessage.substring(finalMessage.indexOf("】") + 1);
                        boolean isMine = sender.equals(currentUser);
                        addMessage(isMine ? content : finalMessage, isMine);
                    } else {
                        addMessage(finalMessage, false);
                    }
                });
            }
        } catch (IOException e) {
            if (running) {
                SwingUtilities.invokeLater(() -> {
                    addMessage("=== 与服务器的连接已断开 ===", false);
                    connectionStatus.setText("● 连接断开");
                    connectionStatus.setForeground(new Color(231, 76, 60));
                    messageInput.setEnabled(false);
                    sendButton.setEnabled(false);
                });
            }
        }
    }

    private void close() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("关闭连接时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChatClient client = new ChatClient();
            client.setVisible(true);
            client.connect();
        });
    }
}
