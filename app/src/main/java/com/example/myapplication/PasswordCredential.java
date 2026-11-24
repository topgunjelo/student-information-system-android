package com.example.myapplication;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PasswordCredential {
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 220_000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String HEX = "0123456789abcdef";

    private PasswordCredential() {}

    static String create(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return encode(salt) + encode(derive(password, salt));
    }

    static boolean matches(String credential, String password) {
        if (credential.length() != (SALT_LENGTH + HASH_LENGTH) * 2) {
            return false;
        }
        byte[] salt = decode(credential, 0, SALT_LENGTH);
        byte[] expected = decode(credential, SALT_LENGTH * 2, HASH_LENGTH);
        return salt != null && expected != null
                && MessageDigest.isEqual(expected, derive(password, salt));
    }

    private static byte[] derive(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, HASH_LENGTH * 8);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Password derivation unavailable", exception);
        } finally {
            spec.clearPassword();
        }
    }

    private static String encode(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            hex.append(HEX.charAt((value & 0xff) >>> 4));
            hex.append(HEX.charAt(value & 0xf));
        }
        return hex.toString();
    }

    private static byte[] decode(String hex, int offset, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex.charAt(offset + 2 * i), 16);
            int low = Character.digit(hex.charAt(offset + 2 * i + 1), 16);
            if (high < 0 || low < 0) {
                return null;
            }
            bytes[i] = (byte) ((high << 4) | low);
        }
        return bytes;
    }
}
