package com.example.data_protection_android.activity;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Archiver {
    private final int BYTE_SIZE = 8;
    private final int ASCII_COUNT = 256;
    private final int BIT_SIZE = 13;
    private final int MAX_SIZE = 8192;

//    public static void main(String[] args) throws IOException {
//        Archiver archiver = new Archiver();
//        String fileName = "file.txt";
//        String archive = archiver.archive(fileName);
//        archiver.unzip(fileName, archive);
//    }

    public void writeFile(StringBuilder bitsBuffer, String archive) throws IOException {
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

    private char getChar(Byte b) {
        int i = b.intValue();
        if (i < 0) {
            i += ASCII_COUNT;
        }
        return (char) i;
    }

    private String toBits(int i) {
        StringBuilder temp = new StringBuilder(Integer.toBinaryString(i));
        while (temp.length() < BIT_SIZE) {
            temp.insert(0, "0");
        }
        return temp.toString();
    }

    private String byteToBits(byte b) {
        return String.format("%" + BYTE_SIZE + "s", Integer.toBinaryString(b & 0xFF))
                .replace(' ', '0');
    }

    private int getNum(StringBuilder bitTemp) {
        int i = Integer.parseInt(bitTemp.substring(0, BIT_SIZE), 2);
        bitTemp.delete(0, BIT_SIZE);
        return i;
    }

    private void read(StringBuilder bitTemp, DataInputStream in) throws IOException {
        bitTemp.append(byteToBits(in.readByte()));
    }
}
