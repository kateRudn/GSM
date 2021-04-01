package server;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class A51 {

    private static String key = null;
    private static String frameNumber = null;


    /**
     *
     * @param stringToEncrypt
     * @param encryptKey
     * @param encryptFrameNumber
     * @return The filename of the encrypted file.
     * @throws UnsupportedEncodingException 
     */
    public static String encrypt(String stringToEncrypt, String encryptKey, String encryptFrameNumber) throws UnsupportedEncodingException {

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
        
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(encryptedBytes);
    }

    /**
     *
     * @param fileToDecrypt
     * @param decryptKey
     * @param decryptFrameNumber
     * @return decrypted string.
     */
    public static String decrypt(String fileToDecrypt, String decryptKey, String decryptFrameNumber) throws UnsupportedEncodingException {

        KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(decryptKey, decryptFrameNumber);

        Base64.Decoder dec = Base64.getMimeDecoder();
        String decString=new String(dec.decode(fileToDecrypt.getBytes()));
        byte[] encryptedBytes=decString.getBytes();
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
