package com.drew.imaging.quicktime;

import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifTiffHandler;

import java.lang.reflect.InvocationTargetException;

class QuickTimeTiffHandler extends ExifTiffHandler {

    private final Class<Directory> _clazz;

    public QuickTimeTiffHandler(Class clazz, Metadata metadata, Directory parentDirectory, int exifStartOffset) {
        super(metadata, parentDirectory, exifStartOffset);
        this._clazz = clazz;
    }
    @Override
    public void setTiffMarker(int marker) throws TiffProcessingException {
        int standardTiffMarker = 0x002A;
        if (marker != standardTiffMarker)
        {
            throw new TiffProcessingException("Unexpected TIFF marker: 0x{marker:X}");
        }
        try {
            pushDirectory(this._clazz.getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
