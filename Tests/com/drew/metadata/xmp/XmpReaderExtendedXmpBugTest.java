package com.drew.metadata.xmp;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for Extended XMP processing edge cases in XmpReader.
 *
 * These tests verify that malformed Extended XMP segments are handled gracefully
 * rather than throwing ArrayIndexOutOfBoundsException.
 */
public class XmpReaderExtendedXmpBugTest
{
    private static final String XMP_PREAMBLE = "http://ns.adobe.com/xap/1.0/\0";
    private static final String XMP_EXTENSION_PREAMBLE = "http://ns.adobe.com/xmp/extension/\0";
    private static final String TEST_GUID = "0123456789ABCDEF0123456789ABCDEF";

    /**
     * Creates a Standard XMP segment with HasExtendedXMP attribute pointing to the given GUID.
     */
    private byte[] createStandardXmpSegment(String guid)
    {
        String xmpContent = "<?xpacket begin=\"\" id=\"W5M0MpCehiHzreSzNTczkc9d\"?>\n" +
            "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">\n" +
            "  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
            "    <rdf:Description rdf:about=\"\"\n" +
            "      xmlns:xmpNote=\"http://ns.adobe.com/xmp/note/\"\n" +
            "      xmpNote:HasExtendedXMP=\"" + guid + "\"/>\n" +
            "  </rdf:RDF>\n" +
            "</x:xmpmeta>\n" +
            "<?xpacket end=\"w\"?>";

        byte[] preambleBytes = XMP_PREAMBLE.getBytes(StandardCharsets.ISO_8859_1);
        byte[] contentBytes = xmpContent.getBytes(StandardCharsets.UTF_8);

        byte[] result = new byte[preambleBytes.length + contentBytes.length];
        System.arraycopy(preambleBytes, 0, result, 0, preambleBytes.length);
        System.arraycopy(contentBytes, 0, result, preambleBytes.length, contentBytes.length);
        return result;
    }

    /**
     * Creates an Extended XMP segment with the specified parameters.
     *
     * Extended XMP segment structure:
     * - Preamble: "http://ns.adobe.com/xmp/extension/\0" (36 bytes)
     * - GUID: 32-byte ASCII hex string
     * - fullLength: 4-byte big-endian uint32 (declared total size of extended XMP)
     * - chunkOffset: 4-byte big-endian uint32 (where this chunk goes in the buffer)
     * - chunkData: the actual XMP data bytes
     */
    private byte[] createExtendedXmpSegment(String guid, int fullLength, int chunkOffset, int chunkDataSize)
    {
        byte[] preambleBytes = XMP_EXTENSION_PREAMBLE.getBytes(StandardCharsets.ISO_8859_1);
        byte[] guidBytes = guid.getBytes(StandardCharsets.US_ASCII);

        ByteBuffer headerBuffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        headerBuffer.putInt(fullLength);
        headerBuffer.putInt(chunkOffset);
        byte[] headerBytes = headerBuffer.array();

        byte[] chunkData = new byte[chunkDataSize];

        byte[] result = new byte[preambleBytes.length + guidBytes.length + headerBytes.length + chunkData.length];
        int offset = 0;
        System.arraycopy(preambleBytes, 0, result, offset, preambleBytes.length);
        offset += preambleBytes.length;
        System.arraycopy(guidBytes, 0, result, offset, guidBytes.length);
        offset += guidBytes.length;
        System.arraycopy(headerBytes, 0, result, offset, headerBytes.length);
        offset += headerBytes.length;
        System.arraycopy(chunkData, 0, result, offset, chunkData.length);

        return result;
    }

    /**
     * Creates an Extended XMP segment with a raw uint32 chunkOffset value.
     * This allows testing values > Integer.MAX_VALUE which become negative when cast to int.
     */
    private byte[] createExtendedXmpSegmentWithRawOffset(String guid, int fullLength, long chunkOffset, int chunkDataSize)
    {
        byte[] preambleBytes = XMP_EXTENSION_PREAMBLE.getBytes(StandardCharsets.ISO_8859_1);
        byte[] guidBytes = guid.getBytes(StandardCharsets.US_ASCII);

        ByteBuffer headerBuffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        headerBuffer.putInt(fullLength);
        headerBuffer.putInt((int)(chunkOffset & 0xFFFFFFFFL)); // Write raw uint32 bits
        byte[] headerBytes = headerBuffer.array();

        byte[] chunkData = new byte[chunkDataSize];

        byte[] result = new byte[preambleBytes.length + guidBytes.length + headerBytes.length + chunkData.length];
        int offset = 0;
        System.arraycopy(preambleBytes, 0, result, offset, preambleBytes.length);
        offset += preambleBytes.length;
        System.arraycopy(guidBytes, 0, result, offset, guidBytes.length);
        offset += guidBytes.length;
        System.arraycopy(headerBytes, 0, result, offset, headerBytes.length);
        offset += headerBytes.length;
        System.arraycopy(chunkData, 0, result, offset, chunkData.length);

        return result;
    }

    /**
     * Tests that Extended XMP with chunkOffset exceeding buffer size is handled gracefully.
     *
     * Currently fails with:
     * java.lang.ArrayIndexOutOfBoundsException: arraycopy: last destination index 550 out of bounds for byte[100]
     *     at com.drew.metadata.xmp.XmpReader.processExtendedXMPChunk(XmpReader.java:296)
     *
     * Expected behavior: Should record an error in the XmpDirectory instead of throwing.
     */
    @Test
    public void testExtendedXmpWithChunkOffsetExceedingBufferSize()
    {
        byte[] standardXmp = createStandardXmpSegment(TEST_GUID);

        // Malformed: fullLength=100 (buffer size), but chunkOffset=500 (beyond buffer)
        byte[] extendedXmp = createExtendedXmpSegment(TEST_GUID,
            /*fullLength=*/ 100,
            /*chunkOffset=*/ 500,
            /*chunkDataSize=*/ 50);

        Metadata metadata = new Metadata();
        XmpReader reader = new XmpReader();

        // This should NOT throw - it should handle the error gracefully
        reader.readJpegSegments(Arrays.asList(standardXmp, extendedXmp), metadata, JpegSegmentType.APP1);

        // Verify that an error was recorded rather than an exception thrown
        Collection<XmpDirectory> directories = metadata.getDirectoriesOfType(XmpDirectory.class);
        assertFalse("Expected at least one XmpDirectory", directories.isEmpty());

        boolean hasError = directories.stream().anyMatch(XmpDirectory::hasErrors);
        assertTrue("Expected an error to be recorded for invalid Extended XMP", hasError);
    }

    /**
     * Tests that Extended XMP where chunkOffset + dataLength exceeds fullLength is handled gracefully.
     *
     * This simulates the production error:
     * "arraycopy: last destination index 15334120 out of bounds for byte[15262528]"
     */
    @Test
    public void testExtendedXmpWithChunkDataExceedingBufferBounds()
    {
        byte[] standardXmp = createStandardXmpSegment(TEST_GUID);

        // Malformed: buffer=1000 bytes, chunkOffset=900, chunkData=200
        // 900 + 200 = 1100 > 1000 (buffer overflow by 100 bytes)
        byte[] extendedXmp = createExtendedXmpSegment(TEST_GUID,
            /*fullLength=*/ 1000,
            /*chunkOffset=*/ 900,
            /*chunkDataSize=*/ 200);

        Metadata metadata = new Metadata();
        XmpReader reader = new XmpReader();

        // This should NOT throw - it should handle the error gracefully
        reader.readJpegSegments(Arrays.asList(standardXmp, extendedXmp), metadata, JpegSegmentType.APP1);

        // Verify that an error was recorded
        Collection<XmpDirectory> directories = metadata.getDirectoriesOfType(XmpDirectory.class);
        assertFalse("Expected at least one XmpDirectory", directories.isEmpty());

        boolean hasError = directories.stream().anyMatch(XmpDirectory::hasErrors);
        assertTrue("Expected an error to be recorded for invalid Extended XMP", hasError);
    }

    /**
     * Tests that Extended XMP with a large chunkOffset (interpreted as negative int) is handled gracefully.
     *
     * When a uint32 value > Integer.MAX_VALUE is cast to int, it becomes negative.
     * The bounds check must explicitly reject negative chunkOffset values.
     */
    @Test
    public void testExtendedXmpWithNegativeChunkOffset()
    {
        byte[] standardXmp = createStandardXmpSegment(TEST_GUID);

        // chunkOffset = 0x80000000 (2147483648) becomes -2147483648 when cast to int
        // This must be rejected, not cause ArrayIndexOutOfBoundsException
        byte[] extendedXmp = createExtendedXmpSegmentWithRawOffset(TEST_GUID,
            /*fullLength=*/ 1000,
            /*chunkOffset=*/ 0x80000000L,
            /*chunkDataSize=*/ 50);

        Metadata metadata = new Metadata();
        XmpReader reader = new XmpReader();

        // This should NOT throw - it should handle the error gracefully
        reader.readJpegSegments(Arrays.asList(standardXmp, extendedXmp), metadata, JpegSegmentType.APP1);

        // Verify that an error was recorded
        Collection<XmpDirectory> directories = metadata.getDirectoriesOfType(XmpDirectory.class);
        assertFalse("Expected at least one XmpDirectory", directories.isEmpty());

        boolean hasError = directories.stream().anyMatch(XmpDirectory::hasErrors);
        assertTrue("Expected an error to be recorded for negative chunkOffset", hasError);
    }

    /**
     * Tests that valid Extended XMP segments still work correctly.
     */
    @Test
    public void testValidExtendedXmpSegment()
    {
        byte[] standardXmp = createStandardXmpSegment(TEST_GUID);

        // Valid: buffer=1000 bytes, chunkOffset=0, chunkData=500
        // 0 + 500 = 500 <= 1000 (fits in buffer)
        byte[] extendedXmp = createExtendedXmpSegment(TEST_GUID,
            /*fullLength=*/ 1000,
            /*chunkOffset=*/ 0,
            /*chunkDataSize=*/ 500);

        Metadata metadata = new Metadata();
        XmpReader reader = new XmpReader();

        // This should work without errors
        reader.readJpegSegments(Arrays.asList(standardXmp, extendedXmp), metadata, JpegSegmentType.APP1);

        Collection<XmpDirectory> directories = metadata.getDirectoriesOfType(XmpDirectory.class);
        assertFalse("Expected at least one XmpDirectory", directories.isEmpty());

        // Should have no errors for valid Extended XMP
        // (Note: may have XMP parsing errors if chunk data isn't valid XML,
        //  but should NOT have arraycopy errors)
    }

    /**
     * Tests that a real JPEG file with malformed Extended XMP is handled gracefully.
     * This file has Extended XMP where chunkOffset (500) exceeds the declared buffer size (100).
     */
    @Test
    public void testMalformedExtendedXmpJpegFile() throws Exception
    {
        File file = new File("Tests/Data/malformed_extended_xmp.jpg");

        // This should NOT throw ArrayIndexOutOfBoundsException
        Metadata metadata = JpegMetadataReader.readMetadata(file);

        // Verify that an error was recorded rather than an exception thrown
        Collection<XmpDirectory> directories = metadata.getDirectoriesOfType(XmpDirectory.class);
        assertFalse("Expected at least one XmpDirectory", directories.isEmpty());

        boolean hasError = directories.stream().anyMatch(XmpDirectory::hasErrors);
        assertTrue("Expected an error to be recorded for invalid Extended XMP", hasError);
    }
}
