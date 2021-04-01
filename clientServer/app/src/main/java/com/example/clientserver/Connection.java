package com.example.clientserver;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import static java.lang.System.exit;

public class Connection
{
    private  Socket  mSocket = null;
    private  String  mHost   = null;
    private  int     mPort   = 0;

    public static final String LOG_TAG = "SOCKET";

    public Connection() {}

    public Connection (final String host, final int port)
    {
        this.mHost = host;
        this.mPort = port;
    }

    public void openConnection() throws Exception
    {
        closeConnection();
        try {
            mSocket = new Socket(mHost, mPort);
        } catch (IOException e) {
            throw new Exception("Could not to create a socket: " + e.getMessage());
        }
    }

    public void closeConnection()
    {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error in closing the socket: " + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }

    public void sendData(byte[] data) throws Exception {
        // Проверка открытия сокета
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception ("Data error. " + "Socket has not been created or closed");
        }
        // Отправка данных
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (IOException e) {
            throw new Exception("Data error: " + e.getMessage());
        }
    }

    public byte[] recvData(int bSize) throws Exception {
        // Проверка открытия сокета
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Data error. " + "Socket has not been created or closed");
        }
        // Получение данных
        int count;
        byte [] bytes=new byte[bSize];
        try {
            count=mSocket.getInputStream().read(bytes, 0, bytes.length);
            if (count>0)
            {
                return bytes;
            }
            else if(count == -1)
            {
                throw new Exception("Data error. " + "InputStream  is interrupted");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        closeConnection();
    }
}
