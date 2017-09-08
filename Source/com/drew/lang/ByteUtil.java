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
}
