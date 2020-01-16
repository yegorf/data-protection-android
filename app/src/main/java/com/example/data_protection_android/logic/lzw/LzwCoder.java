package com.example.data_protection_android.logic.lzw;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LzwCoder {
    private final int BYTE_SIZE = 8;
    private final int ASCII_COUNT = 256;
    private final int BIT_SIZE = 13;
    private final int MAX_SIZE = 8192;

    private void writeFile(StringBuilder bitsBuffer, String archive) throws IOException {
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

    public String archive(String fileName) throws IOException {
        String archive = fileName + ".lzw";
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

        HashMap<String, Integer> vocabulary = new HashMap<>();
        for (int i = 0; i < ASCII_COUNT; i++) {
            vocabulary.put(Character.toString((char) i), i);
        }

        byte readByte;
        StringBuilder charBuffer = new StringBuilder();
        StringBuilder binBuffer = new StringBuilder();
        int count = ASCII_COUNT;
        char symbol;

        while (true) {
            try {
                readByte = inputStream.readByte();
            } catch (EOFException e) {
                break;
            }
            symbol = getChar(readByte);

            if (vocabulary.containsKey(charBuffer.toString() + symbol)) {
                charBuffer.append(symbol);
            } else {
                String bits = toBits(vocabulary.get(charBuffer.toString()));
                binBuffer.append(bits);

                if (count < MAX_SIZE) {
                    charBuffer.append(symbol);
                    vocabulary.put(charBuffer.toString(), count);
                    count++;
                }
                charBuffer = new StringBuilder((String.valueOf(symbol)));
            }
        }
        inputStream.close();
        writeFile(binBuffer, archive);
        return archive;
    }

    public void unzip(String fileName, String archive) throws IOException {

        String[] split = fileName.split("/");
        String file = split[split.length - 1];


        String output = fileName.replace(file, "new-" + file);
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(archive)));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)));

        ArrayList<String> vocabulary = new ArrayList<>();
        for (int i = 0; i < ASCII_COUNT; i++) {
            vocabulary.add(Character.toString((char) i));
        }

        int firstString;
        int secondString;

        boolean neof = true;

        StringBuilder binBuffer = new StringBuilder();
        read(binBuffer, inputStream);
        read(binBuffer, inputStream);

        firstString = getNum(binBuffer);

        outputStream.writeBytes(vocabulary.get(firstString));

        while (neof) {
            try {
                while (binBuffer.length() < BIT_SIZE) {
                    read(binBuffer, inputStream);
                }
            } catch (EOFException e) {
                neof = false;
            }
            if (binBuffer.length() < BIT_SIZE) {
                break;
            } else {
                secondString = getNum(binBuffer);
            }

            if (secondString >= vocabulary.size()) {
                String s = vocabulary.get(firstString) + vocabulary.get(firstString).charAt(0);
                if (vocabulary.size() < MAX_SIZE) {
                    vocabulary.add(s);
                }
                outputStream.writeBytes(s);
            } else {
                if (vocabulary.size() < MAX_SIZE) {
                    vocabulary.add(vocabulary.get(firstString) + vocabulary.get(secondString).charAt(0));
                }
                outputStream.writeBytes(vocabulary.get(secondString));
            }
            firstString = secondString;
        }

        inputStream.close();
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
