package com.drew.imaging.zip;

import com.drew.imaging.FileType;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class IndesignPackageFilter implements ZipFilter
{
    private boolean containsInddFile;
    private boolean containsLinksDiretory;

    public IndesignPackageFilter()
    {
        containsInddFile = false;
        containsLinksDiretory = false;
    }

    @Override
    public FileType getFileType()
    {
        if (containsInddFile && containsLinksDiretory) {
            return FileType.Indd;
        } else {
            return FileType.Zip;
        }
    }

    @Override
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.isDirectory() && entry.getName().endsWith("Links/")) {
            containsLinksDiretory = true;
        } else if (entry.getName().endsWith(".indd")) {
            containsInddFile = true;
        }
    }
}
