package com.example.clientserver;
import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;

public class A51 {

    private static String key = null;
    private static String frameNumber = null;


    /**
     *
     * @param stringToEncrypt
     * @param encryptKey
     * @param encryptFrameNumber
     * @return The filename of the encrypted file.
     */
    public static String encrypt(String stringToEncrypt, String encryptKey, String encryptFrameNumber) {

        KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(encryptKey, encryptFrameNumber);

        byte[] encBytes = stringToEncrypt.getBytes();
        int fSize = encBytes.length;
        byte[] encryptedBytes = new byte[fSize];
        byte keyStreamByte;

        keyStreamGenerator.init();
        for(int currByte = 0; currByte < fSize; currByte++) {
            try {
                keyStreamByte = keyStreamGenerator.getStreamByte();
                encryptedBytes[currByte] = (byte) (keyStreamByte ^ encBytes[currByte]);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    /**
     *
     * @param fileToDecrypt
     * @param decryptKey
     * @param decryptFrameNumber
     * @return The filename of the decrypted file.
     */
    public static String decrypt(String fileToDecrypt, String decryptKey, String decryptFrameNumber) throws UnsupportedEncodingException {

        KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(decryptKey, decryptFrameNumber);

        byte[] encryptedBytes=Base64.decode(fileToDecrypt, Base64.DEFAULT);
        int fSize=encryptedBytes.length;
        byte[] decryptedBytes = new byte[fSize];
        byte keyStreamByte;

        keyStreamGenerator.init();
        for(int currByte = 0; currByte < fSize; currByte ++) {
            try {
                keyStreamByte = keyStreamGenerator.getStreamByte();
                decryptedBytes[currByte ] = (byte) (keyStreamByte ^ encryptedBytes[currByte]);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return new String (decryptedBytes, "UTF-8");
    }


}
