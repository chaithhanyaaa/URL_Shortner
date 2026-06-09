package com.chaithanya.shortening_service.util;

public class Base62Encoder {
    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String encode(long number) {

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            result.append(CHARACTERS.charAt(remainder));
            number /= 62;
        }

        return result.reverse().toString();
    }

}
