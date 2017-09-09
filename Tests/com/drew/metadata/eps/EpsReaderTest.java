package com.drew.metadata.eps;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Payton Garland
 */
public class EpsReaderTest
{
    @NotNull
    public static EpsDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(new File(file));
        try {
            new EpsReader().extract(stream, metadata);
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        EpsDirectory directory = metadata.getFirstDirectoryOfType(EpsDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void test8x8x8bitGrayscale() throws Exception
    {
        EpsDirectory directory = processBytes("Tests/Data/8x4x8bit-Grayscale.eps");

        assertEquals(4334, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_SIZE));
        assertEquals(30, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_OFFSET));
        assertEquals(8, directory.getInt(EpsDirectory.TAG_IMAGE_WIDTH));
        assertEquals(4, directory.getInt(EpsDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(1, directory.getInt(EpsDirectory.TAG_COLOR_TYPE));
    }

    @Test
    public void testAdobeJpeg1() throws Exception
    {
        EpsDirectory directory = processBytes("Tests/Data/adobeJpeg1.eps");

        assertEquals(41802, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_SIZE));
        assertEquals(30, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_OFFSET));
        assertEquals(275, directory.getInt(EpsDirectory.TAG_IMAGE_WIDTH));
        assertEquals(207, directory.getInt(EpsDirectory.TAG_IMAGE_HEIGHT));
        assertEquals(3, directory.getInt(EpsDirectory.TAG_COLOR_TYPE));
    }
}
