package com.drew.imaging.zip;

import com.drew.imaging.FileType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class IndesignPackageFilter extends ZipFilter
{
    private boolean containsInddFile;
    private boolean containsLinksDirectory;

    public IndesignPackageFilter()
    {
        containsInddFile = false;
        containsLinksDirectory = false;
    }

    @Override
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.isDirectory() && entry.getName().endsWith("Links/")) {
            containsLinksDirectory = true;
        } else if (entry.getName().endsWith(".indd")) {
            containsInddFile = true;
        }
    }

    @Override
    HashMap<List<Boolean>, FileType> createConditionsMap()
    {
        HashMap<List<Boolean>, FileType> conditionsMap = new HashMap<List<Boolean>, FileType>();

        List<Boolean> isIndd = Arrays.asList(containsInddFile, containsLinksDirectory);
        conditionsMap.put(isIndd, FileType.Indd);

        return conditionsMap;
    }
}
