package org.example.util;

public class MessageFormatter {

    public static String formatPublicMessage(String sender, String message) {
        return "【" + sender + "】" + message;
    }

    public static String formatPrivateMessage(String sender, String content) {
        return "【私聊】" + sender + ": " + content;
    }

    public static String formatSystemMessage(String content) {
        return "【系统】" + content;
    }

    public static String formatPrivateMessageSent(String target, String content) {
        return "【私聊】发送给 " + target + ": " + content;
    }

    public static String formatOnlineUsers(java.util.Set<String> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 在线用户列表 (").append(users.size()).append("人) ===\n");
        for (String user : users) {
            sb.append("  - ").append(user).append("\n");
        }
        return sb.toString();
    }

    public static String getHelpMessage() {
        return "=== 命令帮助 ===\n" +
                "/msg <用户> <消息> - 发送私聊消息\n" +
                "/users - 查看在线用户\n" +
                "/help - 显示帮助信息\n" +
                "/quit 或 /exit - 退出聊天室\n" +
                "直接输入消息 - 发送群聊消息\n";
    }

    public static String getWelcomeMessage() {
        return "=== 欢迎进入聊天室 ===";
    }

    public static String getPromptMessage() {
        return "请输入您的昵称: ";
    }
}
