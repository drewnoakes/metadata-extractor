package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class ZipFileTypeDetector
{
    static List<ZipFilter> filters;

    public static FileType detectFileType(@NotNull InputStream inputStream) throws IOException
    {
        filters = Arrays.asList(new IndesignPackageFilter(), new OoxmlFilter());

        ZipInputStream reader = new ZipInputStream(inputStream);
        ZipEntry entry = reader.getNextEntry();

        while (entry != null) {
            for (ZipFilter filter : filters) {
                filter.filterEntry(entry, reader);
            }
            reader.closeEntry();
            entry = reader.getNextEntry();
        }
        reader.close();

        for (ZipFilter filter : filters) {
            FileType fileType = filter.getFileType();
            if (!fileType.equals(FileType.Zip)) {
                return fileType;
            }
        }

        return FileType.Zip;
    }
}
