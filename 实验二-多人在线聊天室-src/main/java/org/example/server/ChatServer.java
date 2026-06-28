package org.example.server;

import org.example.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> clients;
    private ExecutorService threadPool;

    public ChatServer() {
        clients = new ConcurrentHashMap<>();
        threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("========================================");
            System.out.println("   聊天室服务器已启动");
            System.out.println("   监听端口: " + PORT);
            System.out.println("   在线用户管理: ConcurrentHashMap");
            System.out.println("========================================");

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新客户端连接: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(
                        clientSocket,
                        clients,
                        username -> System.out.println("客户端断开: " + username)
                );
                threadPool.execute(clientHandler);
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            if (threadPool != null) {
                threadPool.shutdown();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("服务器已关闭");
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
