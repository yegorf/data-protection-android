package com.example.data_protection_android.logic.haffman;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Node tree;
        String textFilename = "files/file.txt";
        String compressedTextFilename = "files/file.txt.huff";
        String decodedTextFilename = "files/file_decoded.txt";

        System.out.println("Кодирование текстового файла");
        System.out.println("----------------------------");
        tree = HuffmanCompressor.compress(textFilename, compressedTextFilename);
        System.out.println("-------------------------------");
        System.out.println("Раскодирование текстового файла");
        System.out.println("-------------------------------");
        HuffmanCompressor.decompress(compressedTextFilename, decodedTextFilename, tree);

        System.out.println("% компрессии = " + CompressQualifier.compressPercent(
                new File(textFilename),
                new File(compressedTextFilename))
        );
        System.out.println("Файлы идентичны = " + CompressQualifier.isUncompressedEqualsSource(
                new File(textFilename),
                new File(decodedTextFilename))
        );
    }
}
