package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class user {
    private String username;
    private LocalDateTime joinTime;
    private String ipAddress;

    public user(String username, String ipAddress) {
        this.username = username;
        this.joinTime = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getJoinTimeFormatted() {
        return joinTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        user user = (user) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
