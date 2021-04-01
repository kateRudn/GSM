package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.math.BigInteger;


public class Server 
{
    private static final int SERVER_PORT = 9876;

    private static ServerSocket serverSoket = null;
    
    private static Socket clientSocket = null;
    
    private static String frameNumber="C45AF";
    
    private static int iTMSI=-1;
    
    private static String [] arrayTMSI={"TMSI2", "TMSI1", "TMSI3" };
    
    private static String [] arrayKeys= {"8227bef049c9a51e728d77bd808f877e", "4227bef049c5a51e728d17bd508f677e", "4317bef044c9a71e781d77bd805f876e" };
    
    public static void main(String args[]) throws Exception
    {
    	try {
    		serverSoket = new ServerSocket(SERVER_PORT);
    	    } catch (IOException e) {
    	      System.out.println("Couldn't listen to port");
    	      System.exit(-1);
    	    }
    	while (true)
    	{
    		
    	try {
    	      System.out.print("\nWaiting for a client...\n\n");
    	      clientSocket = serverSoket.accept();
    	      System.out.println("Client connected");
    	      
    	    } catch (IOException e) {
    	      System.out.println("Can't accept");
    	      System.exit(-1);
    	    }
    	 
         byte[] IMSI = Functions.recvData(clientSocket, 5);
         
         String stringIMSI=new String(IMSI, 0, IMSI.length);
         
         System.out.println("Client TMSI: "+ stringIMSI);
         
         for (int i=0; i<arrayTMSI.length; i++)
         {
        	 if (stringIMSI.equals(arrayTMSI[i]))
        	 {
        		 iTMSI=i;
        		 System.out.println("Client is in the database");
        		 break;
        	 }
         }
         if (iTMSI==-1) {
        	 
        	 System.out.println("Client is not in the database");
        	 System.out.println("Socket is closed");
        	 clientSocket.close();
        	 continue;
        	 //break;
         }
         
         System.out.println("Send RAND...");
         
         SecureRandom random = new SecureRandom();
         
         byte bytes[] = new byte[16]; // 128 bits are converted to 16 bytes;
         
         random.nextBytes(bytes);
         
         BigInteger tmp=new BigInteger(bytes);
         
         tmp=tmp.abs();
         
         bytes=tmp.toByteArray();
         
         Functions.sendData(clientSocket, bytes);
         
         BigInteger xres=Functions.xres(arrayKeys[iTMSI], bytes);
         BigInteger keyC=Functions.keyC(arrayKeys[iTMSI], bytes);
         
         byte[] sres = Functions.recvData(clientSocket, 4);
         
         BigInteger clSres=new BigInteger(sres);
         
    	 clSres=clSres.abs();
         
         if (xres.equals(clSres))
         {
        	 System.out.println("Authentification is successful");
        	 String msg=A51.encrypt("Authentification is successful", Functions.byteArrayToHex(keyC.toByteArray()), frameNumber);
 			 System.out.println("Send message for client: " + msg);
 			 Functions.sendData(clientSocket, msg.getBytes());
         }
         else {
        	 
        	 System.out.println("Authentification is not successful");
        	 String msg=A51.encrypt("Authentification is not successful", Functions.byteArrayToHex(keyC.toByteArray()), frameNumber);
 			 System.out.println("Send message for client: " + msg);
 			 Functions.sendData(clientSocket, msg.getBytes());
        	 System.out.println("Socket is closed");
        	 clientSocket.close();
        	 continue;
        	 //break;
         }
         
			while (true)
			{
				
				byte [] data=Functions.recvData(clientSocket, 1024);
				
				if (clientSocket.isClosed())
				{
					break;
				}
				
				String decData=A51.decrypt(new String(data, 0, data.length, "UTF-8"), Functions.byteArrayToHex(keyC.toByteArray()), frameNumber);
		         
		        System.out.println("Recv message from client: "+ decData);
		        
				
			}
		
    }
    
}
}