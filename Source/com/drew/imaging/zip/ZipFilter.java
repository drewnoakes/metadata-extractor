package com.drew.imaging.zip;

import com.drew.imaging.FileType;

import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Abstract class to apply a filter that determines the FileType/use-case of the given ZipInputStream
 *
 * @author Payton Garland
 */
public abstract class ZipFilter
{
    FileType getFileType()
    {
        HashMap<List<Boolean>, FileType> conditionsMap = createConditionsMap();
        for (List<Boolean> booleans : conditionsMap.keySet()) {
            boolean isFound = true;
            for (Boolean bool : booleans) {
                if (!bool) {
                    isFound = false;
                }
            }
            if (isFound) {
                return conditionsMap.get(booleans);
            }
        }
        return FileType.Zip;
    }

    abstract void filterEntry(ZipEntry entry, ZipInputStream inputStream);

    abstract HashMap<List<Boolean>, FileType> createConditionsMap();
}
