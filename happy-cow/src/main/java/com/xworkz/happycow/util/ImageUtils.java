package com.xworkz.happycow.util;

import java.util.Base64;

public class ImageUtils {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    private ImageUtils() {
        // Private constructor to prevent instantiation
    }


    public static String encodeToBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            return null;
        }
        return ENCODER.encodeToString(imageBytes);
    }


    public static byte[] decodeFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        return DECODER.decode(base64String);
    }
}
