package com.drew.imaging.zip;

import com.drew.imaging.FileType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Interface to apply a filter that determines the FileType/use-case of the given ZipInputStream
 *
 * @author Payton Garland
 */
public abstract class ZipFilter
{
    /**
     * If the conditions that were checked in the filterEntry method are now all true,
     * return the correct FileType.  Otherwise, return Zip
     *
     * @return FileType of current file depending upon conditions applied
     */
    FileType getFileType()
    {
        return checkConditions(addConditions());
    }

    abstract void filterEntry(ZipEntry entry, ZipInputStream inputStream);

    abstract HashMap<List<Boolean>, FileType> addConditions();

    FileType checkConditions(HashMap<List<Boolean>, FileType> conditionsMap)
    {
        for (List<Boolean> booleans : conditionsMap.keySet()) {
            boolean found = true;
            for (Boolean bool : booleans) {
                if (!bool) {
                    found = false;
                }
            }
            if (found) {
                return conditionsMap.get(booleans);
            }
        }
        return FileType.Zip;
    }
}
