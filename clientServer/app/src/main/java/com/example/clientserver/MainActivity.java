package com.example.clientserver;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static java.lang.System.exit;


public class MainActivity extends AppCompatActivity {
    private Button mBtnOpen  = null;
    private Button      mBtnClose = null;
    private Connection mConnect=null;
    private String TMSI="TMSI2";
    //private String keyTMSI="8227bef049c9a51e728d77bd808f877e";
    private String keyTMSI="4227bef049c5a51e728d17bd508f677e";
    private String frameNumber="C45AF";


    private  String     HOST      = "192.168.43.101";
    private  int        PORT      = 9876;

    private  String     LOG_TAG   = "SOCKET";

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.INTERNET};
    private static final int INITIAL_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnOpen  = (Button)   findViewById(R.id.btn_open );
        mBtnClose = (Button)   findViewById(R.id.btn_close);
        mBtnClose.setEnabled(false);

        if (!Functions.AccessInternet(this) ) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }
        mBtnOpen.setOnClickListener(v -> {
            try {
                onOpenClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        mBtnClose.setOnClickListener(v -> {
            try {
                onCloseClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void onOpenClick() throws IOException {

        mConnect=new Connection(HOST, PORT);
        new Thread(() -> {

    // Открытие сокета в отдельном потоке
            try {
                mConnect.openConnection();
                runOnUiThread(() ->mBtnClose.setEnabled(true));
                Log.d(LOG_TAG, "Connection is set");

            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }

            try {
                mConnect.sendData(TMSI.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte [] bRand=new byte[16];
            try {
                bRand=mConnect.recvData(16);
            } catch (Exception e) {
                e.printStackTrace();
            }

            BigInteger keyC=Functions.keyC(keyTMSI,bRand);
            BigInteger sres=Functions.xres(keyTMSI,bRand);

            try {
                mConnect.sendData(sres.toByteArray());
                Log.d(LOG_TAG, "Send SRES to server");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                byte [] msg=mConnect.recvData(1024);
                String decMsg=A51.decrypt(new String(msg, 0, msg.length), Functions.byteArrayToHex(keyC.toByteArray()), frameNumber);
                Log.d(LOG_TAG, "Recv message from server: "+ decMsg);
                if (decMsg.equals("Authentification is not successful"))
                {
                    mConnect.closeConnection();
                    Thread.currentThread().interrupt();
                    finish();
                    startActivity(getIntent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String data = "Secret message for a server";
            try {

                String encMsg=A51.encrypt(data, Functions.byteArrayToHex(keyC.toByteArray()), frameNumber);
                mConnect.sendData(encMsg.getBytes());
                Log.d(LOG_TAG, "Send message for a server: " + encMsg);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void onCloseClick() throws IOException {

        mConnect.closeConnection();

        mBtnClose.setEnabled(false);
        Log.d(LOG_TAG, "Connection closed");
    }
}