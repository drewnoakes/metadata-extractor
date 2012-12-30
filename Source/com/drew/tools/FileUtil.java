package com.drew.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Drew Noakes http://drewnoakes.com
 */
public class FileUtil
{
    public static void saveBytes(File file, byte[] bytes) throws IOException
    {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(bytes);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    public static byte[] readBytes(File file) throws IOException
    {
        int length = (int)file.length();
        // should only be zero if loading from a network or similar
        assert(length != 0);
        byte[] bytes = new byte[length];

        int totalBytesRead = 0;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            while (totalBytesRead != length) {
                int bytesRead = inputStream.read(bytes, totalBytesRead, length - totalBytesRead);
                if (bytesRead == -1) {
                    break;
                }
                totalBytesRead += bytesRead;
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return bytes;
    }

    public static byte[] readBytes(String filePath) throws IOException
    {
        return readBytes(new File(filePath));
    }
}
