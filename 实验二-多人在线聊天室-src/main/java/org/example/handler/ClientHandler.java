package org.example.handler;

import org.example.model.user;
import org.example.util.MessageFormatter;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private user user;
    private ConcurrentHashMap<String, ClientHandler> clients;
    private Consumer<String> onDisconnect;

    public ClientHandler(Socket socket,
                         ConcurrentHashMap<String, ClientHandler> clients,
                         Consumer<String> onDisconnect) {
        this.socket = socket;
        this.clients = clients;
        this.onDisconnect = onDisconnect;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            initializeUser();

            if (user == null) {
                return;
            }

            clients.put(user.getUsername(), this);
            broadcastToOthers(MessageFormatter.formatSystemMessage(user.getUsername() + " 加入了聊天室"));
            sendToSelf(MessageFormatter.formatOnlineUsers(clients.keySet()));

            System.out.println(user.getUsername() + " 已加入聊天室");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.trim().isEmpty()) continue;

                if (message.equalsIgnoreCase("/quit") || message.equalsIgnoreCase("/exit")) {
                    break;
                } else if (message.startsWith("/msg ")) {
                    handlePrivateMessage(message);
                } else if (message.equalsIgnoreCase("/users")) {
                    sendToSelf(MessageFormatter.formatOnlineUsers(clients.keySet()));
                } else if (message.equalsIgnoreCase("/help")) {
                    sendToSelf(MessageFormatter.getHelpMessage());
                } else {
                    broadcastMessage(message);
                }
            }
        } catch (IOException e) {
            System.err.println("处理客户端消息出错: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void initializeUser() throws IOException {
        sendToSelf(MessageFormatter.getWelcomeMessage());
        sendToSelf(MessageFormatter.getPromptMessage());

        String username = in.readLine();

        if (username == null || username.trim().isEmpty()) {
            username = "匿名用户" + System.currentTimeMillis();
        }

        String ipAddress = socket.getInetAddress().getHostAddress();
        this.user = new user(username, ipAddress);
    }

    private void handlePrivateMessage(String message) {
        String[] parts = message.split("\\s+", 3);
        if (parts.length < 3) {
            sendToSelf(MessageFormatter.formatSystemMessage("用法: /msg <用户名> <消息内容>"));
            return;
        }

        String targetUser = parts[1];
        String content = parts[2];

        ClientHandler target = clients.get(targetUser);
        if (target != null) {
            target.sendToSelf(MessageFormatter.formatPrivateMessage(user.getUsername(), content));
            sendToSelf(MessageFormatter.formatPrivateMessageSent(targetUser, content));
        } else {
            sendToSelf(MessageFormatter.formatSystemMessage("用户 " + targetUser + " 不在线"));
        }
    }

    private void broadcastMessage(String message) {
        String formattedMessage = MessageFormatter.formatPublicMessage(user.getUsername(), message);
        System.out.println(formattedMessage);

        for (ClientHandler client : clients.values()) {
            client.sendToSelf(formattedMessage);
        }
    }

    private void broadcastToOthers(String message) {
        System.out.println(message);

        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(user.getUsername())) {
                entry.getValue().sendToSelf(message);
            }
        }
    }

    public synchronized void sendToSelf(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void disconnect() {
        String username = user != null ? user.getUsername() : "未知用户";
        clients.remove(username);

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        broadcastToOthers(MessageFormatter.formatSystemMessage(username + " 离开了聊天室"));
        System.out.println(username + " 已离开聊天室");

        if (onDisconnect != null) {
            onDisconnect.accept(username);
        }
    }

    public user getUser() {
        return user;
    }

    public Socket getSocket() {
        return socket;
    }
}
