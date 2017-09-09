package com.drew.metadata.eps;

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.*;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifTiffHandler;
import com.drew.metadata.icc.IccReader;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
 *     <li>Lines end with a CR(0xD) or LF(0xA) character</li>
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
                        new TiffReader().processTiff(byteArrayReader, new ExifTiffHandler(metadata, null), 0);
                    } catch (TiffProcessingException ex) {
                        directory.addError("Unable to process TIFF data: " + ex.getMessage());
                    }
                } else if (wmfSize != 0) {
                    directory.setInt(EpsDirectory.TAG_WMF_PREVIEW_SIZE, wmfSize);
                    directory.setInt(EpsDirectory.TAG_WMF_PREVIEW_OFFSET, wmfOffset);
                }

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
        char curr;
        String name;
        String value;
        do {
            // Get full line until new line character
            do {
                curr = (char) reader.getByte();
                line.append(curr);
            } while (curr != 0xD && curr != 0xA);

            // Only parse if it is an EPS comment
            if (line.toString().startsWith("%")) {
                // ':' signifies there is an associated keyword (should be put in directory)
                // otherwise, the name could be a marker
                if (line.toString().contains(":")) {
                    name = line.substring(0, line.indexOf(":")).trim();
                    value = line.substring(line.indexOf(":") + 1, line.length()).trim();
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
            } else {
                name = "";
            }
            line = new StringBuilder();
        } while (!name.equals("%%BeginBinary")
            && !name.equals("%%BeginData")
            && !name.equals("%AI9_PrivateDataEnd"));
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
        if (EpsDirectory._tagIntegerMap.get(name) != null) {
            switch (EpsDirectory._tagIntegerMap.get(name)) {
                case EpsDirectory.TAG_IMAGE_DATA:
                    extractImageData(directory, value);
                    break;
                case EpsDirectory.TAG_CONTINUE_LINE:
                    directory.setString(_previousTag, directory.getString(_previousTag) + " " + value);
                    break;
                default:
                    if (EpsDirectory._tagNameMap.containsKey(EpsDirectory._tagIntegerMap.get(name))
                        && directory.getString(EpsDirectory._tagIntegerMap.get(name)) == null) {
                        directory.setString(EpsDirectory._tagIntegerMap.get(name), value);
                        _previousTag = EpsDirectory._tagIntegerMap.get(name);
                    } else {
                        // Set previous tag to an Integer that doesn't exist in EpsDirectory
                        _previousTag = 0;
                    }
            }
            _previousTag = EpsDirectory._tagIntegerMap.get(name);
        }
    }

    /**
     * Parses %ImageData comment which holds several values including width in px,
     * height in px, color type, and ram size.
     *
     * @param directory EpsDirectory to add data to
     */
    private void extractImageData(@NotNull final EpsDirectory directory, String d1) throws IOException
    {
        // 	%ImageData: 1000 1000 8 3 1 1000 7 "beginimage"
        directory.setString(EpsDirectory.TAG_IMAGE_DATA, d1.trim());

        String[] imageDataParts = d1.split(" ");
        int width = Integer.parseInt(imageDataParts[0]);
        int height = Integer.parseInt(imageDataParts[1]);

        // Verify this value was not already added
        if (directory.getString(EpsDirectory.TAG_IMAGE_WIDTH) == null) {
            directory.setString(EpsDirectory.TAG_IMAGE_WIDTH, Integer.toString(width));
        }
        // Verify this value was not already added
        if (directory.getString(EpsDirectory.TAG_IMAGE_HEIGHT) == null) {
            directory.setString(EpsDirectory.TAG_IMAGE_HEIGHT, Integer.toString(height));
        }

        int colorType = Integer.parseInt(imageDataParts[3]);
        String colorTypeDescription;
        double ramSize;

        switch (colorType) {
            case 1:
                colorTypeDescription = "Grayscale";
                ramSize = width * height;
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

        // Verify this value was not already added
        if (directory.getString(EpsDirectory.TAG_COLOR_TYPE) == null) {
            directory.setString(EpsDirectory.TAG_COLOR_TYPE, colorTypeDescription);
        }
        // Verify this value was not already added
        if (directory.getString(EpsDirectory.TAG_RAM_SIZE) == null) {
            directory.setString(EpsDirectory.TAG_RAM_SIZE, Double.toString(ramSize));
        }
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to a byte array
     * with fillBuffer and then the rest of the job is passed on to PhotoshopReader.
     *
     * @param metadata Metadata to add directory to and extracted photoshop data
     */
    private void extractPhotoshopData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        List<String> comments = extractHelper("%EndPhotoshop", reader);
        // Create a buffer for the comments (they can be a maximum of 32 bytes per line)
        byte[] buffer = fillBuffer(comments);

        PhotoshopReader photoshopReader = new PhotoshopReader();
        SequentialReader psdReader = new StreamReader(new ByteArrayInputStream(buffer));
        photoshopReader.extract(psdReader, buffer.length, metadata);
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to a byte array
     * with fillBuffer and then the rest of the job is passed on to IccReader.
     *
     * @param metadata Metadata to add directory to and extracted icc data
     */
    private void extractIccData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        List<String> comments = extractHelper("%%EndICCProfile", reader);
        // Create a buffer for the comments (they can be a maximum of 32 bytes per line)
        byte[] buffer = fillBuffer(comments);

        IccReader iccReader = new IccReader();
        RandomAccessReader randomAccessReader = new RandomAccessStreamReader(new ByteArrayInputStream(buffer));
        iccReader.extract(randomAccessReader, metadata);
    }

    /**
     * Lines are retrieved from extractHelper and stored in a List.  All lines get transferred to one String
     * where the rest of the job is passed on to XmpReader.
     *
     * @param metadata Metadata to add directory to and extracted xmp data
     */
    private void extractXmpData(@NotNull final Metadata metadata, @NotNull SequentialReader reader) throws IOException
    {
        List<String> comments = extractHelper("<?xpacket end=\"w\"?>", reader);

        StringBuilder all = new StringBuilder();
        for (String temp : comments) {
            all.append(temp);
        }

        XmpReader xmpReader = new XmpReader();
        xmpReader.extract(all.toString(), metadata);
    }

    /**
     * Parses file until reaching indicator.  Each line is stored as a String in an ArrayList.  Extra
     * characters like returns, spaces, and %'s are removed.
     *
     * @param indicator String that represents when to stop reading lines
     * @return ArrayList of comments
     */
    private List<String> extractHelper(@NotNull String indicator, @NotNull SequentialReader reader) throws IOException
    {
        StringBuilder comment;
        char curr;
        List<String> comments = new ArrayList<String>();

        do {
            comment = new StringBuilder();
            // Read in entire line
            do {
                curr = (char) reader.getByte();
                comment.append(curr);
            } while (curr != 0xD && curr != 0xA);

            if (comment.length() > 2)
                comments.add(comment.toString().substring(1, comment.length() - 1));
        } while (!comment.toString().startsWith(indicator));

        // Get rid of last line (%%EndCCProfile)
        comments.remove(comments.size() - 1);

        return comments;
    }

    /**
     * Adds comments list to a byte array for use in later readers.  Comment's data are
     * stored in hex in ascii form, so the list is read in sets of two characters which are
     * converted to bytes.
     *
     * @param comments ArrayList of Strings that contains data in hex (ascii)
     */
    private byte[] fillBuffer(List<String> comments)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (String comment : comments) {
            comment = comment.trim();
            int pos = 0;
            while (pos < comment.length()) {
                bytes.write((byte)Integer.parseInt(comment.substring(pos, pos + 2), 16));
                pos += 2;
            }
        }
        return bytes.toByteArray();
    }
}
