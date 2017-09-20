package com.drew.metadata.eps;

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.photoshop.PhotoshopTiffHandler;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads file passed in through SequentialReader and parses encountered data:
 * <ul>
 *     <li>Basic EPS Comments</li>
 *     <li>EXIF</li>
 *     <li>Photoshop</li>
 *     <li>IPTC</li>
 *     <li>ICC Profile</li>
 *     <li>XMP</li>
 * </ul>
 * EPS comments are retrieved from EPS directory.  Photoshop, ICC Profile, and XMP processing
 * is passed to their respective reader.
 * <p/>
 * EPS Constraints (Source: https://www-cdf.fnal.gov/offline/PostScript/5001.PDF pg.18):
 * <ul>
 *     <li>Max line length is 255 characters</li>
 *     <li>Lines end with a CR(0xD) or LF(0xA) character (or both, in practice)</li>
 *     <li>':' separates keywords (considered part of the keyword)</li>
 *     <li>Whitespace is either a space(0x20) or tab(0x9)</li>
 *     <li>If there is more than one header, the 1st is truth</li>
 * </ul>
 *
 * @author Payton Garland
 */
public class EpsReader
{
    private int _previousTag;

    /**
     * Filter method that determines if file will contain an EPS Header.  If it does, it will read the necessary
     * data and then set the position to the beginning of the PostScript data.  If it does not, the position will not
     * be changed.  After both scenarios, the main extract method is called.
     *
     * @param inputStream InputStream containing file
     * @param metadata Metadata to add directory to and extracted data
     */
    public void extract(@NotNull final InputStream inputStream, @NotNull final Metadata metadata) throws IOException
    {
        RandomAccessStreamReader reader = new RandomAccessStreamReader(inputStream);
        EpsDirectory directory = new EpsDirectory();
        metadata.addDirectory(directory);

        /*
         * 0xC5D0D3C6 signifies an EPS Header block which contains 32-bytes of basic information
         *
         * 0x25215053 (%!PS) signifies an EPS File and leads straight into the PostScript
         */
        switch (reader.getInt32(0)) {
            case 0xC5D0D3C6:
                reader.setMotorolaByteOrder(false);
                int postScriptOffset = reader.getInt32(4);
                int postScriptLength = reader.getInt32(8);
                int wmfOffset = reader.getInt32(12);
                int wmfSize = reader.getInt32(16);
                int tifOffset = reader.getInt32(20);
                int tifSize = reader.getInt32(24);
                //int checkSum = reader.getInt32(28);

                // Get Tiff/WMF preview data if applicable
                if (tifSize != 0) {
                    directory.setInt(EpsDirectory.TAG_TIFF_PREVIEW_SIZE, tifSize);
                    directory.setInt(EpsDirectory.TAG_TIFF_PREVIEW_OFFSET, tifOffset);
                    // Get Tiff metadata
                    try {
                        ByteArrayReader byteArrayReader = new ByteArrayReader(reader.getBytes(tifOffset, tifSize));
                        new TiffReader().processTiff(byteArrayReader, new PhotoshopTiffHandler(metadata, null), 0);
                    } catch (TiffProcessingException ex) {
                        directory.addError("Unable to process TIFF data: " + ex.getMessage());
                    }
                } else if (wmfSize != 0) {
                    directory.setInt(EpsDirectory.TAG_WMF_PREVIEW_SIZE, wmfSize);
                    directory.setInt(EpsDirectory.TAG_WMF_PREVIEW_OFFSET, wmfOffset);
                }

                // TODO avoid allocating byte array here -- read directly from InputStream
                extract(directory, metadata, new SequentialByteArrayReader(reader.getBytes(postScriptOffset, postScriptLength)));
                break;
            case 0x25215053:
                inputStream.reset();
                extract(directory, metadata, new StreamReader(inputStream));
                break;
            default:
                directory.addError("File type not supported.");
                break;
        }
    }

    /**
     * Main method that parses all comments and then distributes data extraction among other methods that parse the
     * rest of file and store encountered data in metadata (if there exists an entry in EpsDirectory
     * for the found data).  Reads until a begin data/binary comment is found or _reader's estimated
     * available data has run out (or AI09 End Private Data).  Will extract data from normal EPS comments, Photoshop, ICC, and XMP.
     *
     * @param metadata Metadata to add directory to and extracted data
     */
    private void extract(@NotNull final EpsDirectory directory, @NotNull Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        StringBuilder line = new StringBuilder();

        while (true) {
            line.setLength(0);

            // Read the next line, excluding any trailing newline character
            // Note that for Windows-style line endings ("\r\n") the outer loop will be run a second time with an empty
            // string, which is fine.
            while (true) {
                char c = (char)reader.getByte();
                if (c == '\r' || c == '\n')
                    break;
                line.append(c);
            }

            // Stop when we hit a line that is not a comment
            if (line.length() != 0 && line.charAt(0) != '%')
                break;

            String name;

            // ':' signifies there is an associated keyword (should be put in directory)
            // otherwise, the name could be a marker
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                name = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                addToDirectory(directory, name, value);
            } else {
                name = line.toString().trim();
            }

            // Some comments will both have a value and signify a new block to follow
            if (name.equals("%BeginPhotoshop")) {
                extractPhotoshopData(metadata, reader);
            } else if (name.equals("%%BeginICCProfile")) {
                extractIccData(metadata, reader);
            } else if (name.equals("%begin_xml_packet")) {
                extractXmpData(metadata, reader);
            }
        }
    }

    /**
     * Default case that adds comment with keyword to directory
     *
     * @param directory EpsDirectory to add extracted data to
     * @param name String that holds name of current comment
     * @param value String that holds value of current comment
     */
    private void addToDirectory(@NotNull final EpsDirectory directory, String name, String value) throws IOException
    {
        Integer tag = EpsDirectory._tagIntegerMap.get(name);

        if (tag == null)
            return;

        switch (tag) {
            case EpsDirectory.TAG_IMAGE_DATA:
                extractImageData(directory, value);
                break;
            case EpsDirectory.TAG_CONTINUE_LINE:
                directory.setString(_previousTag, directory.getString(_previousTag) + " " + value);
                break;
            default:
                if (EpsDirectory._tagNameMap.containsKey(tag) && !directory.containsTag(tag)) {
                    directory.setString(tag, value);
                    _previousTag = tag;
                } else {
                    // Set previous tag to an Integer that doesn't exist in EpsDirectory
                    _previousTag = 0;
                }
                break;
        }
        _previousTag = tag;
    }

    /**
     * Parses <code>%ImageData</code> comment which holds several values including width in px,
     * height in px and color type.
     */
    private static void extractImageData(@NotNull final EpsDirectory directory, String imageData) throws IOException
    {
        // %ImageData: 1000 1000 8 3 1 1000 7 "beginimage"
        directory.setString(EpsDirectory.TAG_IMAGE_DATA, imageData.trim());

        String[] imageDataParts = imageData.split(" ");

        int width = Integer.parseInt(imageDataParts[0]);
        int height = Integer.parseInt(imageDataParts[1]);
        int colorType = Integer.parseInt(imageDataParts[3]);

        // Only add values that are not already present
        if (!directory.containsTag(EpsDirectory.TAG_IMAGE_WIDTH))
            directory.setInt(EpsDirectory.TAG_IMAGE_WIDTH, width);
        if (!directory.containsTag(EpsDirectory.TAG_IMAGE_HEIGHT))
            directory.setInt(EpsDirectory.TAG_IMAGE_HEIGHT, height);
        if (!directory.containsTag(EpsDirectory.TAG_COLOR_TYPE))
            directory.setInt(EpsDirectory.TAG_COLOR_TYPE, colorType);

        if (!directory.containsTag(EpsDirectory.TAG_RAM_SIZE)) {
            int bytesPerPixel = 0;
            if (colorType == 1)
                bytesPerPixel = 1; // grayscale
            else if (colorType == 2 || colorType == 3)
                bytesPerPixel = 3; // Lab or RGB
            else if (colorType == 4)
                bytesPerPixel = 3; // CMYK

            if (bytesPerPixel != 0)
                directory.setInt(EpsDirectory.TAG_RAM_SIZE, bytesPerPixel * width * height);
        }
    }

    /**
     * Decodes a commented hex section, and uses {@link PhotoshopReader} to decode the resulting data.
     */
    private static void extractPhotoshopData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        byte[] buffer = decodeHexCommentBlock(reader);

        if (buffer != null)
            new PhotoshopReader().extract(new SequentialByteArrayReader(buffer), buffer.length, metadata);
    }

    /**
     * Decodes a commented hex section, and uses {@link IccReader} to decode the resulting data.
     */
    private static void extractIccData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        byte[] buffer = decodeHexCommentBlock(reader);

        if (buffer != null)
            new IccReader().extract(new ByteArrayReader(buffer), metadata);
    }

    /**
     * Extracts an XMP xpacket, and uses {@link XmpReader} to decode the resulting data.
     */
    private static void extractXmpData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        byte[] bytes = readUntil(reader, "<?xpacket end=\"w\"?>".getBytes());
        String xmp = new String(bytes, Charsets.UTF_8);
        new XmpReader().extract(xmp, metadata);
    }

    /**
     * Reads all bytes until the given sentinel is observed.
     * The sentinel will be included in the returned bytes.
     */
    private static byte[] readUntil(@NotNull SequentialReader reader, @NotNull byte[] sentinel) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        final int length = sentinel.length;
        int depth = 0;

        while (depth != length) {
            byte b = reader.getByte();
            if (b == sentinel[depth])
                depth++;
            else
                depth = 0;
            bytes.write(b);
        }

        return bytes.toByteArray();
    }

    /**
     * EPS files can contain hexadecimal-encoded ASCII blocks, each prefixed with <c>"% "</c>.
     * This method reads such a block and returns a byte[] of the decoded contents.
     * Reading stops at the first invalid line, which is discarded (it's a terminator anyway).
     * <p/>
     * For example:
     * <pre><code>
     * %BeginPhotoshop: 9564
     * % 3842494D040400000000005D1C015A00031B25471C0200000200041C02780004
     * % 6E756C6C1C027A00046E756C6C1C025000046E756C6C1C023700083230313630
     * % 3331311C023C000B3131343335362B303030301C023E00083230313630333131
     * % 48000000010000003842494D03FD0000000000080101000000000000
     * %EndPhotoshop
     * </code></pre>
     * When calling this method, the reader must be positioned at the start of the first line containing
     * hex data, not at the introductory line.
     *
     * @return The decoded bytes, or <code>null</code> if decoding failed.
     */
    @Nullable
    private static byte[] decodeHexCommentBlock(@NotNull SequentialReader reader) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        // Use a state machine to efficiently parse data in a single traversal

        final int AwaitingPercent = 0;
        final int AwaitingSpace = 1;
        final int AwaitingHex1 = 2;
        final int AwaitingHex2 = 3;

        int state = AwaitingPercent;

        int carry = 0;
        boolean done = false;

        byte b = 0;
        while (!done) {
            b = reader.getByte();

            switch (state) {
                case AwaitingPercent: {
                    switch (b) {
                        case '\r':
                        case '\n':
                        case ' ':
                            // skip newline chars and spaces
                            break;
                        case '%':
                            state = AwaitingSpace;
                            break;
                        default:
                            return null;
                    }
                    break;
                }
                case AwaitingSpace: {
                    switch (b) {
                        case ' ':
                            state = AwaitingHex1;
                            break;
                        default:
                            done = true;
                            break;
                    }
                    break;
                }
                case AwaitingHex1: {
                    int i = tryHexToInt(b);
                    if (i != -1) {
                        carry = i * 16;
                        state = AwaitingHex2;
                    } else if (b == '\r' || b == '\n') {
                        state = AwaitingPercent;
                    } else {
                        return null;
                    }
                    break;
                }
                case AwaitingHex2: {
                    int i = tryHexToInt(b);
                    if (i == -1)
                        return null;
                    bytes.write(carry + i);
                    state = AwaitingHex1;
                    break;
                }
            }
        }

        // skip through the remainder of the last line
        while (b != '\n')
            b = reader.getByte();

        return bytes.toByteArray();
    }

    /**
     * Treats a byte as an ASCII character, and returns it's numerical value in hexadecimal.
     * If conversion is not possible, returns -1.
     */
    private static int tryHexToInt(byte b)
    {
        if (b >= '0' && b <= '9')
            return b - '0';
        if (b >= 'A' && b <= 'F')
            return b - 'A' + 10;
        if (b >= 'a' && b <= 'f')
            return b - 'a' + 10;
        return -1;
    }
}
