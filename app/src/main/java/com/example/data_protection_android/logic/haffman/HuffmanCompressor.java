package com.example.data_protection_android.logic.haffman;

import com.example.data_protection_android.fragment.EncodeListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class HuffmanCompressor {

    public static String byteToBits(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static double compressPercent(File source, File compressed) {
        return 100 - ((double) compressed.length() / (double) source.length() * 100.0);
    }

    public static Node compress(String inputFilename, String outputFilename, EncodeListener listener) {
        try (
                RandomAccessFile file = new RandomAccessFile(inputFilename, "r");
                BufferedOutputStream bufferedWriter = new BufferedOutputStream(new FileOutputStream(outputFilename))
        ) {
            PriorityQueue<Node> nodes = new PriorityQueue<>(
                    (o1, o2) -> {
                        if (o1.getCount() > o2.getCount()) {
                            return 1;
                        } else if (o1.getCount() == o2.getCount()) {
                            return 0;
                        }
                        return -1;
                    }
            );

            Map<Byte, Integer> byteIntegerMap = new HashMap<>();
            listener.displayInfo("Считаем символы");
            while (file.getFilePointer() < file.length()) {
                final byte read = file.readByte();
                if (nodes
                        .stream()
                        .noneMatch(characterCount -> read == characterCount.get_byte())
                ) {
                    byteIntegerMap.merge(read, 1, Integer::sum);
                }
            }

            byteIntegerMap.forEach((key, value) -> nodes.add(new Node(key, value)));
            nodes.forEach((node -> {
                listener.displayInfo(node.getName() + " -> " + node.getCount());
            }));

            listener.displayInfo("\nФормируем дерево");
            while (nodes.size() > 1) {
                Node left = nodes.poll();
                Node right = nodes.poll();
                nodes.add(new Node(left, Objects.requireNonNull(right)));
            }

            Map<Byte, String> codes = new HashMap<>();

            Node node = nodes.poll();
            listener.displayInfo("\nГенерируем коды");
            generateCodes(node, codes, "");
            file.seek(0);
            listener.displayInfo("\nПечатаем в файл");

            StringBuilder bits = new StringBuilder();
            while (file.getFilePointer() < file.length()) {
                byte b;
                try {
                    b = file.readByte();
                    bits.append(codes.get(b));
                    if (bits.length() >= 8) {
                        bufferedWriter.write(Integer.parseInt(bits.substring(0, 8), 2));
                        bits.delete(0, 8);
                    }
                } catch (IOException e) {
                    int toByte = 8 - bits.length();
                    for (int i = 0; i < toByte; i++) {
                        bits.append("0");
                    }
                    bufferedWriter.write(Integer.parseInt(bits.toString(), 2));
                    break;
                }
            }
            return node;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void generateCodes(Node node, Map<Byte, String> codes, String s) {
        if (node != null) {
            if (node.getRight() != null) {
                generateCodes(node.getRight(), codes, s + "1");
            }
            if (node.getLeft() != null) {
                generateCodes(node.getLeft(), codes, s + "0");
            }
            if (node.getLeft() == null && node.getRight() == null) {
                codes.put(node.get_byte(), s);
                System.out.println((char) node.get_byte() + " : " + s);
            }
        }
    }

    public static void decompress(String sourceFilename, String destFilename, Node tree) {
        try (
                RandomAccessFile reader = new RandomAccessFile(sourceFilename, "r");
                FileOutputStream writer = new FileOutputStream(destFilename)
        ) {
            byte b;
            StringBuilder builder = new StringBuilder();
            while (reader.getFilePointer() < reader.length()) {
                if (builder.length() == 0) {
                    b = reader.readByte();
                    builder.append(byteToBits(b));
                }
                byte byteFromCode = getByteFromCode(builder, tree, reader);
                writer.write(byteFromCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte getByteFromCode(StringBuilder builder, Node tree, RandomAccessFile reader) throws IOException {
        if (builder.length() == 0) {
            byte b = reader.readByte();
            builder.append(byteToBits(b));
        }

        if (builder.charAt(0) == '0' && tree.getLeft() != null) {
            builder.deleteCharAt(0);
            return getByteFromCode(builder, tree.getLeft(), reader);
        } else if (builder.charAt(0) == '1' && tree.getRight() != null) {
            builder.deleteCharAt(0);
            return getByteFromCode(builder, tree.getRight(), reader);
        } else {
            return tree.get_byte();
        }
    }
}
