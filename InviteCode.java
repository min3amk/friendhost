package com.friendhost;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class InviteCode {
    private static final String PREFIX = "FH-";

    private InviteCode() {}

    /** Turns "1.2.3.4:25565" into "FH-...". */
    public static String encode(String address) {
        return PREFIX + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(address.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns the address inside a code. Also accepts a plain "host:port". Null if invalid. */
    public static String decode(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty() || s.contains(" ")) return null;
        if (s.regionMatches(true, 0, PREFIX, 0, PREFIX.length())) {
            try {
                String decoded = new String(
                        Base64.getUrlDecoder().decode(s.substring(PREFIX.length())),
                        StandardCharsets.UTF_8);
                return decoded.isBlank() ? null : decoded;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return s;
    }
}
