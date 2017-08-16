package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import com.drew.metadata.Metadata;
import com.drew.metadata.zip.ZipDirectory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class ZipFileFilter extends ZipFilter
{
    public static Metadata metadata;
    public ZipDirectory directory;
    private ArrayList<String> names;
    private ArrayList<String> modDates;
    private ArrayList<String> compressedSizes;
    private ArrayList<String> uncompressedSizes;
    private ArrayList<String> compressionMethods;
    private ArrayList<String> comments;

    public ZipFileFilter()
    {
        metadata = new Metadata();
        directory = new ZipDirectory();
        metadata.addDirectory(directory);

        names = new ArrayList<String>();
        modDates = new ArrayList<String>();
        compressedSizes = new ArrayList<String>();
        uncompressedSizes = new ArrayList<String>();
        compressionMethods = new ArrayList<String>();
        comments = new ArrayList<String>();
    }

    @Override
    void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.getName() != null) {
            names.add(entry.getName());
        } else {
            names.add("");
        }
        directory.setStringArray(12, names.toArray(new String[names.size()]));

        if (entry.getTime() != -1) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            modDates.add(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(new Date(entry.getTime())));
        } else {
            modDates.add("");
        }
        directory.setStringArray(6, modDates.toArray(new String[modDates.size()]));

        if (entry.getCompressedSize() != -1) {
            compressedSizes.add(Long.toString(entry.getCompressedSize()));
        } else {
            compressedSizes.add(Long.toString(-1L));
        }
        directory.setStringArray(8, compressedSizes.toArray(new String[compressedSizes.size()]));

        if (entry.getSize() != -1) {
            uncompressedSizes.add(Long.toString(entry.getSize()));
        } else {
            uncompressedSizes.add(Long.toString(-1L));
        }
        directory.setStringArray(9, uncompressedSizes.toArray(new String[uncompressedSizes.size()]));

        if (entry.getMethod() != -1) {
            compressionMethods.add(getCompressionMethod(entry.getMethod()));
        } else {
            compressionMethods.add("");
        }
        directory.setStringArray(4, compressionMethods.toArray(new String[compressionMethods.size()]));

        if (entry.getComment() != null) {
            comments.add(entry.getComment());
        } else {
            comments.add("");
        }
        directory.setStringArray(18, comments.toArray(new String[comments.size()]));
    }

    @Override
    HashMap<List<Boolean>, FileType> createConditionsMap()
    {
        return new HashMap<List<Boolean>, FileType>();
    }

    private String getCompressionMethod(int compressionMethod)
    {
        switch (compressionMethod) {
            case (0):
                return ("The file is stored (no compression)");
            case (1):
                return ("The file is Shrunk");
            case (2):
                return ("The file is Reduced with compression factor 1");
            case (3):
                return ("The file is Reduced with compression factor 2");
            case (4):
                return ("The file is Reduced with compression factor 3");
            case (5):
                return ("The file is Reduced with compression factor 4");
            case (6):
                return ("The file is Imploded");
            case (7):
                return ("Reserved for Tokenizing compression algorithm");
            case (8):
                return ("The file is Deflated");
            case (9):
                return ("Enhanced Deflating using Deflate64(tm)");
            case (10):
                return ("PKWARE Data Compression Library Imploding (old IBM TERSE)");
            case (11):
                return ("Reserved by PKWARE");
            case (12):
                return ("File is compressed using BZIP2 algorithm");
            case (13):
                return ("Reserved by PKWARE");
            case (14):
                return ("LZMA (EFS)");
            case (15):
                return ("Reserved by PKWARE");
            case (16):
                return ("Reserved by PKWARE");
            case (17):
                return ("Reserved by PKWARE");
            case (18):
                return ("File is compressed using IBM TERSE (new)");
            case (19):
                return ("IBM LZ77 z Architecture (PFS)");
            case (97):
                return ("WavPack compressed data");
            case (98):
                return ("PPMd version I, Rev 1");
            default:
                return (" ");
        }
    }
}
