package com.demo.service.chatservice;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineUsers {

    private static final Set<String> users = ConcurrentHashMap.newKeySet();

    public static void add(String userId) {
        users.add(userId);
    }

    public static void remove(String userId) {
        users.remove(userId);
    }

    public static boolean isOnline(String userId) {
        return users.contains(userId);
    }
}