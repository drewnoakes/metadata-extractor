package com.drew.tools;

import java.io.File;
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
}
