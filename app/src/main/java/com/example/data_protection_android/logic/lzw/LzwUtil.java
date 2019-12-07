package com.example.data_protection_android.logic.lzw;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class LzwUtil {

    public static final int BYTE_SIZE = 8;
    public static  final int ASCII_COUNT = 256;
    public static  final int BIT_SIZE = 13;
    public static  final int MAX_SIZE = 8192;

    public static  void writeFile(StringBuilder bitsBuffer, String archive) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(archive)));
        while (bitsBuffer.length() > 0) {
            while (bitsBuffer.length() < BYTE_SIZE) {
                bitsBuffer.append('0');
            }
            outputStream.writeByte((byte) Integer.parseInt(bitsBuffer.substring(0, BYTE_SIZE), 2));
            bitsBuffer.delete(0, BYTE_SIZE);
        }
        outputStream.close();
    }

    public static  char getChar(Byte b) {
        int i = b.intValue();
        if (i < 0) {
            i += ASCII_COUNT;
        }
        return (char) i;
    }

    public static  String toBits(int i) {
        StringBuilder temp = new StringBuilder(Integer.toBinaryString(i));
        while (temp.length() < BIT_SIZE) {
            temp.insert(0, "0");
        }
        return temp.toString();
    }

    public static  String byteToBits(byte b) {
        return String.format("%" + BYTE_SIZE + "s", Integer.toBinaryString(b & 0xFF))
                .replace(' ', '0');
    }

    public static  int getNum(StringBuilder bitTemp) {
        int i = Integer.parseInt(bitTemp.substring(0, BIT_SIZE), 2);
        bitTemp.delete(0, BIT_SIZE);
        return i;
    }

    public static  void read(StringBuilder bitTemp, DataInputStream in) throws IOException {
        bitTemp.append(byteToBits(in.readByte()));
    }
}
