package com.example.data_protection_android.logic.lzw;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class LzwEncoder {

    public String archive(String fileName) throws IOException {
        String archive = fileName + ".lzw";
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

        HashMap<String, Integer> vocabulary = new HashMap<>();
        for(int i = 0; i < LzwUtil.ASCII_COUNT; i++) {
            vocabulary.put(Character.toString((char) i), i);
        }

        byte readByte;
        StringBuilder charBuffer = new StringBuilder();
        StringBuilder binBuffer = new StringBuilder();
        int count = LzwUtil.ASCII_COUNT;
        char symbol;

        while (true) {
            try {
                readByte = inputStream.readByte();
            } catch (EOFException e) {
                break;
            }
            symbol = LzwUtil.getChar(readByte);

            if (vocabulary.containsKey(charBuffer.toString() + symbol)) {
                charBuffer.append(symbol);
            } else {
                String bits = LzwUtil.toBits(vocabulary.get(charBuffer.toString()));
                binBuffer.append(bits);

                if(count < LzwUtil.MAX_SIZE) {
                    charBuffer.append(symbol);
                    vocabulary.put(charBuffer.toString(), count);
                    count++;
                }
                charBuffer = new StringBuilder((String.valueOf(symbol)));
            }
        }
        inputStream.close();
        LzwUtil.writeFile(binBuffer, archive);
        return archive;
    }
}
