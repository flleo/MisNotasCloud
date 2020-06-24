package utilidades.misnotas.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import utilidades.misnotas.model.Nota;

public class EncriptaDesencriptaAES {

    public Nota encriptacionAES(Nota nota) {
        // Generamos una clave de 128 bits adecuada para AES
        KeyGenerator keyGenerator = null;
        Nota nota1 = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            Key key = keyGenerator.generateKey();
            //Clave que debera conocer cada extremo, en este caso daria igual porque los dos extremos estan en el dispositivo
            //Aun asi la transcripcion cambia con respecto a la primera forma key, no por la frase, sino por la forma en si.
            key = new SecretKeySpec("una clave de 16 dicharachera".getBytes(),  0, 16, "AES");
            // Se obtiene un cifrador AES
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Se inicializa para encriptacion y se encripta el texto, que debemos pasar como bytes.
            aes.init(Cipher.ENCRYPT_MODE, key);
            byte[] tituloEncriptado = aes.doFinal(nota.getTitulo().getBytes());
            byte[] contenidoEncriptado = aes.doFinal(nota.getContenido().getBytes());
            String titulo="",contenido="";
            titulo = encodeHexString(tituloEncriptado);
            contenido = encodeHexString(contenidoEncriptado);
            nota1 = new Nota(nota.get_id(),nota.getUser_id(),titulo,contenido);
        } catch (NoSuchAlgorithmException e) {
            e.getMessage();
        } catch (NoSuchPaddingException e) {
            e.getMessage();
        } catch (InvalidKeyException e) {
            e.getMessage();
        } catch (IllegalBlockSizeException e) {
            e.getMessage();
        } catch (BadPaddingException e) {
            e.getMessage();
        }
        return nota1;
    }

    private String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public Nota desencriptaAES(Nota nota) {
        // Se iniciliza el cifrador para desencriptar, con la misma clave y se desencripta
        Cipher aes = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            Key key = keyGenerator.generateKey();
            key = new SecretKeySpec("una clave de 16 bits dicharachera".getBytes(), 0, 16, "AES");
            aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, key);
            byte[] tituloDesencriptado = aes.doFinal(decodeHexString(nota.getTitulo()));
            byte[] contenidoDesencriptado = aes.doFinal(decodeHexString(nota.getContenido()));
            // Texto obtenido, igual al original.
            nota.setTitulo(new String(tituloDesencriptado));
            nota.setContenido(new String(contenidoDesencriptado));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return nota;

    }

    public byte[] decodeHexString(String hexString) {
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
