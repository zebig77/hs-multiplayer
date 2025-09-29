package org.zebig.hs.client;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    public static Map<String, String> parseNamedArgs(String[] args, int startIndex) {
        Map<String, String> map = new HashMap<>();
        for (int i = startIndex; i < args.length; i++) {
            String[] parts = args[i].split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0].toLowerCase(), parts[1]);
            }
        }
        return map;
    }
}
