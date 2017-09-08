package com.drew.lang;

public class ByteUtil
{
    public static int getInt16(byte[] buffer, int offset, boolean bigEndian)
    {
        if (bigEndian) {
            return
                ((buffer[offset    ] & 0xFF) << 8) |
                ((buffer[offset + 1] & 0xFF)     );
        } else {
            return
                ((buffer[offset    ] & 0xFF)     ) |
                ((buffer[offset + 1] & 0xFF) << 8);
        }
    }

    public static int getInt32(byte[] buffer, int offset, boolean bigEndian)
    {
        if (bigEndian) {
            return
                ((buffer[offset    ] & 0xFF) << 24) |
                ((buffer[offset + 1] & 0xFF) << 16) |
                ((buffer[offset + 2] & 0xFF) <<  8) |
                ((buffer[offset + 3] & 0xFF));
        } else {
            return
                ((buffer[offset    ] & 0xFF)      ) |
                ((buffer[offset + 1] & 0xFF) <<  8) |
                ((buffer[offset + 2] & 0xFF) << 16) |
                ((buffer[offset + 3] & 0xFF) << 24);
        }
    }

    public static long getLong64(byte[] buffer, int offset, boolean bigEndian)
    {
        if (bigEndian) {
            return
                ((buffer[offset    ] & 0xFF) << 56) |
                ((buffer[offset + 1] & 0xFF) << 48) |
                ((buffer[offset + 2] & 0xFF) << 40) |
                ((buffer[offset + 3] & 0xFF) << 32) |
                ((buffer[offset + 4] & 0xFF) << 24) |
                ((buffer[offset + 5] & 0xFF) << 16) |
                ((buffer[offset + 6] & 0xFF) <<  8) |
                ((buffer[offset + 7] & 0xFF));

        } else {
            return
                ((buffer[offset    ] & 0xFF)      ) |
                ((buffer[offset + 1] & 0xFF) <<  8) |
                ((buffer[offset + 2] & 0xFF) << 16) |
                ((buffer[offset + 3] & 0xFF) << 24) |
                ((buffer[offset + 4] & 0xFF) << 32) |
                ((buffer[offset + 5] & 0xFF) << 40) |
                ((buffer[offset + 6] & 0xFF) << 48) |
                ((buffer[offset + 7] & 0xFF) << 56);
        }
    }

    public static long getUnsignedInt32(byte[] buffer, int offset, boolean bigEndian)
    {
        int firstByte = (0x000000FF & ((int)buffer[offset]));
        int secondByte = (0x000000FF & ((int)buffer[offset+1]));
        int thirdByte = (0x000000FF & ((int)buffer[offset+2]));
        int fourthByte = (0x000000FF & ((int)buffer[offset+3]));

        return ((long) (firstByte << 24
            | secondByte << 16
            | thirdByte << 8
            | fourthByte))
            & 0xFFFFFFFFL;

    }

    public static long getLong32(byte[] buffer, int offset, boolean bigEndian)
    {
        if (bigEndian) {
            return (buffer[offset] << 24 & 0xFF000000) |
                (buffer[offset + 1] << 16 & 0xFF0000) |
                (buffer[offset + 2] << 8 & 0xFF00) |
                (buffer[offset + 3] & 0xFF);
        } else {
            return (buffer[offset + 3] << 24 & 0xFF000000) |
                (buffer[offset + 2] << 16 & 0xFF0000) |
                (buffer[offset + 1] << 8 & 0xFF00) |
                (buffer[offset] & 0xFF);
        }
    }
}
