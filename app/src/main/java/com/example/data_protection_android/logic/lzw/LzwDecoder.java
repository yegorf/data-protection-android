package com.example.data_protection_android.logic.lzw;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LzwDecoder {

    public void unzip(String fileName, String archive) throws IOException {
        String output = "new-" + fileName;
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(archive)));
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output)));

        ArrayList<String> vocabulary = new ArrayList<>();
        for (int i = 0; i < LzwUtil.ASCII_COUNT; i++) {
            vocabulary.add(Character.toString((char) i));
        }

        int firstString;
        int secondString;

        boolean neof = true;

        StringBuilder binBuffer = new StringBuilder();
        LzwUtil.read(binBuffer, inputStream);
        LzwUtil.read(binBuffer, inputStream);

        firstString = LzwUtil.getNum(binBuffer);

        outputStream.writeBytes(vocabulary.get(firstString));

        while (neof) {
            try {
                while (binBuffer.length() < LzwUtil.BIT_SIZE) {
                    LzwUtil.read(binBuffer, inputStream);
                }
            } catch (EOFException e) {
                neof = false;
            }
            if(binBuffer.length() < LzwUtil.BIT_SIZE) {
                break;
            } else {
                secondString = LzwUtil.getNum(binBuffer);
            }

            if (secondString >= vocabulary.size()) {
                String s = vocabulary.get(firstString) + vocabulary.get(firstString).charAt(0);
                if (vocabulary.size() < LzwUtil.MAX_SIZE) {
                    vocabulary.add(s);
                }
                outputStream.writeBytes(s);
            } else {
                if (vocabulary.size() < LzwUtil.MAX_SIZE) {
                    vocabulary.add(vocabulary.get(firstString) + vocabulary.get(secondString).charAt(0));
                }
                outputStream.writeBytes(vocabulary.get(secondString));
            }
            firstString = secondString;
        }

        inputStream.close();
        outputStream.close();
    }
}
