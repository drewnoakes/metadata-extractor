package com.drew.metadata.eps;

import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads file passed in through SequentialReader and parses encountered data:
 *      - Basic EPS Comments
 *      - Photoshop
 *      - ICC Profile
 *      - XMP
 * EPS comments are retrieved from EPS directory.  Photoshop, ICC Profile, and XMP processing
 * is passed to their respective reader.
 *
 * @author Payton Garland
 */
public class EpsReader {

    private int pos = 0;
    private RandomAccessStreamReader reader;


    /**
     * Main method that parses all comments and then distributes data extraction among other methods that parse the
     * rest of file and store encountered data in metadata (if there exists an entry in EpsDirectory
     * for the found data).  Reads until a begin data/binary comment is found or reader's estimated
     * available data has run out.  Will extract data from normal EPS comments, Photoshop, ICC, and XMP.
     *
     * @param metadata Metadata to add directory to and extracted data
     *
     */
    public void extract(@NotNull final Metadata metadata)
    {
        EpsDirectory directory = new EpsDirectory();
        metadata.addDirectory(directory);

        try {
            String[] tag = new String[]{"", " "};
            tag[0] += (char)reader.getByte(pos);
            pos++;

            // Loop until data/binary starts OR available data has run out (not accurate, but should not be an issue as we are only interested in the beginning
            while (pos < reader.getLength() && tag[0].hashCode() != EpsDirectory.TAG_BEGIN_BINARY
                && tag[0].hashCode() != EpsDirectory.TAG_BEGIN_DATA
                && tag[0].hashCode() != EpsDirectory.TAG_AI9_END_PRIVATE_DATA)
            {
                // Check if the comment has been found in the tag map - if so, extract the data
                if (EpsDirectory._tagNameMap.containsKey(tag[0].hashCode())) {
                    if (tag[0].hashCode() == EpsDirectory.TAG_DSC_VERSION) {
                        extractDscData(directory, tag);
                    } else if (tag[0].hashCode() == EpsDirectory.TAG_IMAGE_DATA) {
                        extractImageData(directory);
                    } else if (tag[0].hashCode() == EpsDirectory.TAG_BEGIN_PHOTOSHOP) {
                        extractPhotoshopData(metadata);
                    } else if (tag[0].hashCode() == EpsDirectory.TAG_BEGIN_ICC) {
                        extractIccData(metadata);
                    } else if (tag[0].hashCode() == EpsDirectory.TAG_BEGIN_XML_PACKET) {
                        extractXmpData(metadata);
                    } else {
                        extractData(directory, tag);
                    }
                    tag = new String[]{"", ""};
                    tag[0] = "" + (char) reader.getByte(pos);
                    pos++;
                }

                // Comments should never contain spaces/returns as per the EPS conventions
                switch (tag[0].charAt(tag[0].length() - 1)) {
                    case 0xA:
                    case 0xD:
                    case 0x9:
                    case 0x20:
                        tag[0] = "" + (char) reader.getByte(pos);
                        pos++;
                        while (tag[0].charAt(0) != 0x25) {
                            tag[0] = "" + (char) reader.getByte(pos);
                            pos++;
                        }
                        break;
                    default:
                        tag[0] += (char) reader.getByte(pos);
                        pos++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            directory.addError("Unable to read EPS file");
            return;
        }
    }

    /**
     * Filter method that determines if file wil contain an EPSHeader.  If it does, it will read the necessary
     * data and then set the position to the beginning of the PostScript data.  If it does not, the position will not
     * be changed.  After both scenarios, the main extract method is called.
     *
     * @param reader RandomAccessStreamReader to read bytes of file
     * @param metadata Metadata to add directory to and extracted data
     *
     */
    public void extract(@NotNull final RandomAccessStreamReader reader, @NotNull final Metadata metadata) throws IOException
    {
        EpsDirectory directory = new EpsDirectory();
        metadata.addDirectory(directory);
        this.pos = 0;
        this.reader = reader;

        byte[] magicNumber = reader.getBytes(pos, 4);
        pos += 4;

        // 0xC5D0D3C6 signifies an EPSHeader block which contains 32-bytes of basic information
        if (Arrays.equals(magicNumber, new byte[]{(byte)0xC5, (byte)0xD0, (byte)0xD3, (byte)0xC6})) {
            reader.setMotorolaByteOrder(false);
            int postScriptOffset = reader.getInt32(pos);
            int postScriptLength = reader.getInt32(pos + 4);
            int wmfOffset = reader.getInt32(pos + 8);
            int wmfSize = reader.getInt32(pos + 12);
            int tifOffset = reader.getInt32(pos + 16);
            int tifSize = reader.getInt32(pos + 20);
            int checkSum = reader.getInt32(pos + 24);
            pos = postScriptOffset;
            if (tifSize != 0) {
                directory.setString(EpsDirectory.TAG_TIFF_PREVIEW, "[" + tifSize + " bytes]");
                directory.setByteArray(EpsDirectory.TAG_TIFF_PREVIEW_BYTES, reader.getBytes(tifOffset, tifSize));
            }
            if (wmfSize != 0) {
                directory.setString(EpsDirectory.TAG_WMF_PREVIEW, "[" + wmfSize + " bytes]");
                directory.setByteArray(EpsDirectory.TAG_WMF_PREVIEW_BYTES, reader.getBytes(wmfOffset, wmfSize));
            }
            extract(metadata);
        } else if (Arrays.equals(magicNumber, new byte[]{(byte)0x25, (byte)0x21, (byte)0x50, (byte)0x53})) {
            extract(metadata);
        } else {
            directory.addError("File is not of type EPS.");
        }
    }

    /**
     * Default case that parses file until end of line.  This case is taken if no other
     * checked comments are found in tag index 0.  Line is simply read into tag index 1
     * until some return character is found.
     *
     * @param directory EpsDirectory to add extracted data to
     * @param tag String array to hold tag info (index 0 = comment from EpsDirectory, index 1 = data)
     * @throws IOException
     *
     */
    private void extractData(@NotNull final Directory directory, String[] tag) throws IOException
    {
        // Read until end of line
        tag[1] += (char) reader.getByte(pos);
        pos++;
        while (tag[1].charAt(tag[1].length()-1) != 0xA && tag[1].charAt(tag[1].length()-1) != 0xD) {
            tag[1] += (char) reader.getByte(pos);
            pos++;
        }

        // Format values to remove beginning space and ending new line if necessary
        if (tag[1].charAt(tag[1].length()-1) == 0xA || tag[1].charAt(tag[1].length()-1) == 0xD)
            tag[1] = tag[1].substring(0, tag[1].length()-1);
        if (tag[1].charAt(0) == 0x20)
            tag[1] = tag[1].substring(1, tag[1].length());

        directory.setString(tag[0].hashCode(), tag[1]);
    }

    /**
     * Parses line containing DSC version to display any version type along with any
     * data that follows (EPSF-3.0, Query, ExitServer, Resource, etc.)
     *
     * @param directory EpsDirectory to add data to - MUST be an EpsDirectory
     * @param tag String[] that contains data from parsed line
     * @throws IOException
     *
     */
    private void extractDscData(@NotNull final Directory directory, String[] tag) throws IOException
    {
        // Read until first space (up to version number)
        tag[1] += (char) reader.getByte(pos);
        pos++;
        while (tag[1].charAt(tag[1].length()-1) != 0x20 && tag[1].charAt(tag[1].length()-1) != 0xD) {
            tag[1] += (char) reader.getByte(pos);
            pos++;
        }

        double versionNumber = Double.parseDouble(tag[1]);

        tag[1] = "" + (char) reader.getByte(pos);
        pos++;
        // Read rest of line
        while (tag[1].charAt(tag[1].length()-1) != 0xA && tag[1].charAt(tag[1].length()-1) != 0xD) {
            tag[1] += (char) reader.getByte(pos);
            pos++;
        }
        // Format values to remove beginning space and ending new line if necessary
        if (tag[1].charAt(tag[1].length()-1) == 0xA || tag[1].charAt(tag[1].length()-1) == 0xD)
            tag[1] = tag[1].substring(0, tag[1].length()-1);
        if (tag[1].charAt(0) == 0x20)
            tag[1] = tag[1].substring(1, tag[1].length());

        directory.setString(tag[0].hashCode(), versionNumber + " - Other data: " + tag[1]);
    }

    /**
     * Parses %ImageData comment which holds several values including width in px,
     * height in px, color type, and ram size.
     *
     * @param directory EpsDirectory to add data to - MUST be an EpsDirectory
     * @throws IOException
     *
     */
    private void extractImageData(@NotNull final Directory directory) throws IOException
    {
        // 	%ImageData: 1000 1000 8 3 1 1000 7 "beginimage"
        String d1 = " ";
        while (d1.charAt(d1.length()-1) != 0xA && d1.charAt(d1.length()-1) != 0xD) {
            d1 += (char) reader.getByte(pos);
            pos++;
        }

        directory.setString(EpsDirectory.TAG_IMAGE_DATA, d1.trim());

        String[] imageDataParts = d1.split(" ");
        int width = Integer.parseInt(imageDataParts[2]);
        int height = Integer.parseInt(imageDataParts[3]);

        directory.setString(EpsDirectory.TAG_IMAGE_WIDTH_PX, Integer.toString(width));
        directory.setString(EpsDirectory.TAG_IMAGE_HEIGHT_PX, Integer.toString(height));

        int colorType = Integer.parseInt(imageDataParts[5]);
        String colorTypeDescription;
        double ramSize;

        switch (colorType) {
            case 1:
                colorTypeDescription = "Grayscale";
                ramSize = width * height * 1;
                break;
            case 2:
                colorTypeDescription = "Lab Color";
                ramSize = width * height * 3;
                break;
            case 3:
                colorTypeDescription = "RGB";
                ramSize = width * height * 3;
                break;
            case 4:
                colorTypeDescription = "CMYK";
                ramSize = width * height * 4;
                break;
            default:
                colorTypeDescription = "Unknown";
                ramSize = 0;
        }

        directory.setString(EpsDirectory.TAG_COLOR_TYPE, colorTypeDescription);
        directory.setString(EpsDirectory.TAG_RAM_SIZE, Double.toString(ramSize));
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to a byte array
     * with fillBuffer and then the rest of the job is passed on to PhotoshopReader.
     *
     * @param metadata Metadata to add directory to and extracted photoshop data
     * @throws IOException
     *
     */
    private void extractPhotoshopData(@NotNull final Metadata metadata) throws IOException
    {
        List<String> comments = extractHelper(" %EndPhotoshop");
        // Create a buffer for the comments (they can be a maximum of 32 bytes per line)
        byte[] buffer = new byte[comments.size() * 32];
        // Fill the buffer accordingly
        buffer = fillBuffer(buffer, comments);

        PhotoshopReader photoshopReader = new PhotoshopReader();
        SequentialReader psdReader = new StreamReader(new ByteArrayInputStream(buffer));
        photoshopReader.extract(psdReader, buffer.length, metadata);
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to a byte array
     * with fillBuffer and then the rest of the job is passed on to IccReader.
     *
     * @param metadata Metadata to add directory to and extracted icc data
     * @throws IOException
     *
     */
    private void extractIccData(@NotNull final Metadata metadata) throws IOException
    {
        List<String> comments = extractHelper(" %%EndICCProfile");
        // Create a buffer for the comments (they can be a maximum of 32 bytes per line)
        byte[] buffer = new byte[comments.size() * 32];
        // Fill the buffer accordingly
        buffer = fillBuffer(buffer, comments);

        IccReader iccReader = new IccReader();
        RandomAccessReader randomAccessReader = new RandomAccessStreamReader(new ByteArrayInputStream(buffer));
        iccReader.extract(randomAccessReader, metadata);
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to one String
     * where the rest of the job is passed on to XmpReader.
     *
     * @param metadata Metadata to add directory to and extracted xmp data
     * @throws IOException
     *
     */
    private void extractXmpData(@NotNull final Metadata metadata) throws IOException
    {
        String all = "";
        List<String> comments = extractHelper(" <?xpacket end=\"w\"?>");
        for (String temp : comments) { all += temp; }

        XmpReader xmpReader = new XmpReader();
        xmpReader.extract(all, metadata);
    }

    /**
     * Parses file until reaching indicator.  Each line is stored as a String in an ArrayList.  Extra
     * characters like returns, spaces, and %'s are removed.
     *
     * @param indicator String that represents when to stop reading lines
     * @return ArrayList of comments
     * @throws IOException
     */
    public List<String> extractHelper(@NotNull String indicator) throws IOException
    {
        String comment = " ";
        List<String> comments = new ArrayList<String>();

        // Read in entire line
        while (comment.charAt(comment.length() - 1) != 0xA && comment.charAt(comment.length() - 1) != 0xD) {
            comment += (char)reader.getByte(pos);
            pos++;
        }

        // Read in all of the following lines
        while (!comment.startsWith(indicator)) {
            comment = " ";
            while (comment.charAt(comment.length() - 1) != 0xA && comment.charAt(comment.length() - 1) != 0xD) {
                comment += (char) reader.getByte(pos);
                pos++;
            }
            // Add them to ArrayList removing the beginning space/% and ending return character
            if (comment.length() > 2)
                comments.add(comment.substring(1, comment.length()-1));
        }

        // Get rid of last line (%%EndCCProfile)
        comments.remove(comments.size()-1);

        return comments;
    }

    /**
     * Adds comments list to a byte array for use in later readers.  Comment's data are
     * stored in hex in ascii form, so the list is read in sets of two characters which are
     * converted to bytes.
     *
     * @param buffer  byte array that comment data is being transferred to
     * @param comments ArrayList of Strings that contains data in hex (ascii)
     *
     */
    private byte[] fillBuffer(byte[] buffer, List<String> comments) {
        int spacer = 0;
        for (int i = 0; i < comments.size(); i++) {
            spacer = i * 32;
            comments.set(i, comments.get(i).substring(2, comments.get(i).length()));
            for (int j = 0; j < comments.get(i).length(); j += 2) {
                buffer[spacer + (j / 2)] = (byte)Integer.parseInt(comments.get(i).substring(j, j + 2), 16);
            }
        }
        return buffer;
    }
}
