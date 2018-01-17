package com.vouchify.vouchify.utility;

import java.io.UnsupportedEncodingException;

public class MyBase64 {

    private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

    private static int[]  toInt   = new int[128];

    static {
        for(int i=0; i< ALPHABET.length; i++){
            toInt[ALPHABET[i]]= i;
        }
    }

    /**
     * Translates the specified byte array into Base64 string.
     *
     * @param buf the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    public static String encode(byte[] buf){
        int size = buf.length;
        char[] ar = new char[((size + 2) / 3) * 4];

        for (int i = 0; i < size; i += 3) {

            int value = 0;
            for (int j = i; j < (i + 3); j++) {

                value <<= 8;
                if (j < size) {

                    value |= (0xff & buf[j]);
                }
            }
            int a = (i/3)*4;
            int mask = 0x3F;
            ar[a + 0] = ALPHABET[(value >> 18) & mask];
            ar[a + 1] = ALPHABET[(value >> 12) & mask];
            ar[a + 2] = ((i + 1) < size) ? ALPHABET[(value >> 6) & mask] : '=';
            ar[a + 3] = ((i + 2) < size) ? ALPHABET[(value >> 0) & mask] : '=';
        }
        String str = new String(ar);
        try {

            return new String(str.getBytes("US-ASCII"));
        } catch (UnsupportedEncodingException e) {

            return new String(str.getBytes());
        }
    }

    /**
     * Translates the specified Base64 string into a byte array.
     *
     * @param s the Base64 string (not null)
     * @return the byte array (not null)
     */
    public static byte[] decode(String s){
        int delta = s.endsWith( "==" ) ? 2 : s.endsWith( "=" ) ? 1 : 0;
        byte[] buffer = new byte[s.length()*3/4 - delta];
        int mask = 0xFF;
        int index = 0;
        for(int i=0; i< s.length(); i+=4){
            int c0 = toInt[s.charAt( i )];
            int c1 = toInt[s.charAt( i + 1)];
            buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c2 = toInt[s.charAt( i + 2)];
            buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
            if(index >= buffer.length){
                return buffer;
            }
            int c3 = toInt[s.charAt( i + 3 )];
            buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
        }
        return buffer;
    }
}
