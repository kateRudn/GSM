package server;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;

public class Functions {

	public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static BigInteger xres(String IMSI, byte[] rand)
    {
        byte [] byteIMSI=hexStringToByteArray(IMSI);
        byte [] h=Comp128.v3(byteIMSI, rand);
        BigInteger bigH=new BigInteger(h);
        bigH=bigH.abs();
        BigInteger k1=new BigInteger("4294967295");
        BigInteger xres = (bigH.shiftRight(64)).and(k1);
        xres=xres.abs();

        return xres;
    }
    
    public static BigInteger keyC(String IMSI, byte[] rand)
    {
        byte [] byteIMSI=hexStringToByteArray(IMSI);
        byte [] h=Comp128.v3(byteIMSI, rand);
        BigInteger bigH=new BigInteger(h);
        bigH=bigH.abs();
        BigInteger k2=new BigInteger("18446744073709551615");
        BigInteger keyC = bigH.and(k2);
        keyC=keyC.abs();

        return keyC;
    }
    
    public static byte[] recvData(Socket clientSocket, int bSize) throws Exception
    {
    	byte [] bytes = new byte[bSize];
    	int count=0;
    	
    	if (clientSocket == null || clientSocket.isClosed()) {
            throw new Exception("Ошибка отправки данных. " +
                    "Сокет не создан или закрыт");
        }
    	count=clientSocket.getInputStream().read(bytes, 0, bytes.length);
        if (count>0)
        {
            return bytes;
        }
        else if(count == -1)
        {
        	System.out.println("Socket is closed");
            clientSocket.close();
        }
    	
    	return bytes;
    }
    
    public static void sendData(Socket clientSocket, byte [] bytes) throws Exception
    {
    	if (clientSocket == null || clientSocket.isClosed()) {
            throw new Exception("Ошибка отправки данных. " +
                    "Сокет не создан или закрыт");
        }

        try {
        	clientSocket.getOutputStream().write(bytes);
        	clientSocket.getOutputStream().flush();
        } catch (IOException e) {
            throw new Exception("Ошибка отправки данных : "
                    + e.getMessage());
        }
    }
}
