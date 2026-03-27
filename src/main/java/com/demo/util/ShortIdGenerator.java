package com.demo.util;

import java.util.UUID;

public class ShortIdGenerator {

    public static String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}