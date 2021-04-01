package com.example.clientserver;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.math.BigInteger;

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

    public static boolean AccessInternet(Context context) {
        Log.d("Internet", "Access\n");
        return (grantPermission(Manifest.permission.INTERNET, context));
    }
    public static boolean grantPermission(String perm, Context context) {
        Log.d("grantPermission", "HELLO!\n");
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm));
    }
}

