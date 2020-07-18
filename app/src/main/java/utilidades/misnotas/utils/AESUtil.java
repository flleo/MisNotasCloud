package utilidades.misnotas.utils;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {
    private final Cipher cipher;
    private String passphrase = "Esto es una clave dicharachera";
    private final int ic = 1000;
    private final int keySize = 128;
    private String salt = "577bd45a17977269694908d80905c32a"; //passed from client-side
    private String four = "9a2b73d130c8796309b776eeb09834b0"; //passed from client-side

    public AESUtil() {

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw fail(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encrypt( String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, four, plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        } catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
         //  System.out.println(ciphertext+"¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡")
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, four, base64(ciphertext));
            return new String(decrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw fail(e);
        }
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw fail(e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(),
                    hex(salt), ic, keySize);
            SecretKey key = new SecretKeySpec(factory.
                    generateSecret(spec).getEncoded(), "AES");
            return key;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw fail(e);
        }
    }

    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String base64(byte[] bytes) {
        return new String(Base64.getEncoder().encodeToString(bytes));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] base64(String str) {
        return Base64.getDecoder().decode(str.getBytes());
    }

    public static String hex(byte[] bytes) {
        return encodeHexString(bytes);
    }

    public static byte[] hex(String str) {
            return decodeHexString(str);
    }

    private IllegalStateException fail(Exception e) {
        return new IllegalStateException(e);
    }

    private static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

}