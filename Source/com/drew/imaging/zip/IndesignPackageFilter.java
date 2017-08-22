package com.drew.imaging.zip;

import com.adobe.xmp.impl.Base64;
import com.adobe.xmp.properties.XMPProperty;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.FileType;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.indd.InddReader;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.metadata.xmp.XmpReader;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class IndesignPackageFilter extends ZipFilter
{
    public static Metadata metadata;
    private boolean containsInddFile;
    private boolean containsLinksDirectory;

    public IndesignPackageFilter()
    {
        metadata = new Metadata();
        containsInddFile = false;
        containsLinksDirectory = false;
    }

    @Override
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.isDirectory() && entry.getName().endsWith("Links/")) {
            containsLinksDirectory = true;
        } else if (entry.getName().endsWith(".indd")) {
            new InddReader().extract(new StreamReader(inputStream), metadata);
            containsInddFile = true;
        }
    }

    @Override
    HashMap<List<Boolean>, FileType> createConditionsMap()
    {
        HashMap<List<Boolean>, FileType> conditionsMap = new HashMap<List<Boolean>, FileType>();

        List<Boolean> isIndd = Arrays.asList(containsInddFile, containsLinksDirectory);
        conditionsMap.put(isIndd, FileType.IndesignPackage);

        return conditionsMap;
    }
}
