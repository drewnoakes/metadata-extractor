package com.drew.metadata.xmp;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.Charsets;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class XmpReaderBoundsTest
{
    private static final String GUID = "0123456789ABCDEF0123456789ABCDEF";
    private static final String XML =
        "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\"><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
        "<rdf:Description rdf:about=\"\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" dc:format=\"image/jpeg\"/>" +
        "</rdf:RDF></x:xmpmeta>";

    @Test
    public void rejectsAdvertisedLengthBeyondActualPayload() throws Exception
    {
        for (long length : new long[]{1024, 0x7ffffff0L, 0xffffffffL}) {
            Metadata metadata = readJpeg(extension(length, 0, new byte[]{0}));
            assertTrue(metadata.hasErrors());
            assertTrue(hasError(metadata, "length exceeds available payload"));
        }
    }

    @Test
    public void rejectsUnsignedAndOverrunningOffsetsBeforeAllocation() throws Exception
    {
        for (long offset : new long[]{1, 0x7fffffffL, 0xffffffffL}) {
            Metadata metadata = readJpeg(extension(1, offset, new byte[]{0}));
            assertTrue(hasError(metadata, "would write beyond buffer bounds"));
        }
    }

    @Test
    public void reassemblesValidOutOfOrderChunks() throws Exception
    {
        byte[] xml = XML.getBytes(Charsets.UTF_8);
        int split = xml.length / 2;
        Metadata metadata = readJpeg(
            extension(xml.length, split, Arrays.copyOfRange(xml, split, xml.length)),
            extension(xml.length, 0, Arrays.copyOfRange(xml, 0, split)));

        assertFalse(metadata.hasErrors());
        assertEquals(2, metadata.getDirectoriesOfType(XmpDirectory.class).size());
        boolean found = false;
        for (XmpDirectory directory : metadata.getDirectoriesOfType(XmpDirectory.class))
            found |= "image/jpeg".equals(directory.getXmpProperties().get("dc:format"));
        assertTrue(found);
    }

    @Test
    public void rejectsInconsistentChunkLengths() throws Exception
    {
        Metadata metadata = readJpeg(
            extension(2, 0, new byte[]{0}),
            extension(1, 0, new byte[]{0}));
        assertTrue(hasError(metadata, "Inconsistent length"));
    }

    private static boolean hasError(Metadata metadata, String text)
    {
        for (XmpDirectory directory : metadata.getDirectoriesOfType(XmpDirectory.class))
            for (String error : directory.getErrors())
                if (error.contains(text))
                    return true;
        return false;
    }

    private static byte[] extension(long length, long offset, byte[] payload) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.write("http://ns.adobe.com/xmp/extension/\0".getBytes(Charsets.ASCII));
        output.write(GUID.getBytes(Charsets.ASCII));
        output.writeInt((int)length);
        output.writeInt((int)offset);
        output.write(payload);
        return bytes.toByteArray();
    }

    private static Metadata readJpeg(byte[]... extensions) throws Exception
    {
        String standard = "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\"><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
            "<rdf:Description rdf:about=\"\" xmlns:xmpNote=\"http://ns.adobe.com/xmp/note/\" xmpNote:HasExtendedXMP=\"" +
            GUID + "\"/></rdf:RDF></x:xmpmeta>";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeShort(0xffd8);
        writeSegment(output, ("http://ns.adobe.com/xap/1.0/\0" + standard).getBytes(Charsets.UTF_8));
        for (byte[] extension : extensions)
            writeSegment(output, extension);
        output.writeShort(0xffd9);
        return ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes.toByteArray()));
    }

    private static void writeSegment(DataOutputStream output, byte[] bytes) throws Exception
    {
        output.writeShort(0xffe1);
        output.writeShort(bytes.length + 2);
        output.write(bytes);
    }
}
