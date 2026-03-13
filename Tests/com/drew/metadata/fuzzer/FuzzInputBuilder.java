package com.drew.metadata.fuzzer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

final class FuzzInputBuilder {
    private FuzzInputBuilder() {
    }

    static void touchMetadata(com.drew.metadata.Metadata metadata) {
        if (metadata == null) return;
        for (com.drew.metadata.Directory directory : metadata.getDirectories()) {
            directory.getName();
            for (com.drew.metadata.Tag tag : directory.getTags()) {
                tag.getTagName();
                tag.getDescription();
                tag.toString();
            }
            for (String error : directory.getErrors()) {
                // Just touch the errors too
            }
        }
    }

    static byte[] prepend(byte[] head, byte[] tail) {
        if (head == null || head.length == 0) {
            return tail;
        }
        byte[] out = new byte[head.length + tail.length];
        System.arraycopy(head, 0, out, 0, head.length);
        System.arraycopy(tail, 0, out, head.length, tail.length);
        return out;
    }

    static byte[] minimalLittleEndianTiffWithMake(String make, byte[] tail) throws IOException {
        byte[] makeBytes = (make + "\0").getBytes("UTF-8");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(new byte[]{0x49, 0x49, 0x2A, 0x00, 0x08, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write(new byte[]{0x0F, 0x01});
        baos.write(new byte[]{0x02, 0x00});
        baos.write(intToLittleEndian(makeBytes.length));
        baos.write(intToLittleEndian(26));
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});

        baos.write(makeBytes);
        baos.write(tail);
        return baos.toByteArray();
    }

    static byte[] intToLittleEndian(int v) {
        return new byte[]{
            (byte) (v & 0xFF),
            (byte) ((v >> 8) & 0xFF),
            (byte) ((v >> 16) & 0xFF),
            (byte) ((v >> 24) & 0xFF)
        };
    }
}
