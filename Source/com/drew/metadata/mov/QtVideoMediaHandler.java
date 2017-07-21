package com.drew.metadata.mov;

import com.drew.lang.SequentialByteArrayReader;

import java.io.IOException;

public class QtVideoMediaHandler implements QtMediaHandler
{

    @Override
    public boolean shouldAcceptAtom(String fourCC)
    {
        return fourCC.equals(QtAtomTypes.ATOM_VIDEO_MEDIA_INFO)
            || fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(QtContainerTypes.ATOM_SAMPLE_TABLE)
            || fourCC.equals(QtContainerTypes.ATOM_MEDIA_INFORMATION);
    }

    @Override
    public QtHandler processAtom(String fourCC, byte[] payload, QtDirectory directory) throws IOException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_VIDEO_MEDIA_INFO)) {
            processMediaInformation(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        }
        return this;
    }

    @Override
    public QtHandler processContainer(String fourCC)
    {
        return this;
    }

    @Override
    public void processSampleDescription(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int numberOfEntries = reader.getInt32();
        int sampleSize = reader.getInt32();
        String dataFormat = new String(reader.getBytes(4));
        reader.skip(6); // 6-bytes reserved
        int dataReference = reader.getInt16();
        int version = reader.getInt16();
        int revisionLevel = reader.getInt16();
        String vendor = new String(reader.getBytes(4));
        int temporalQuality = reader.getInt32();
        int spatialQuality = reader.getInt32();
        int width = reader.getInt16();
        int height = reader.getInt16();
        int horizontalResolution = reader.getInt32();
        int verticalResolution = reader.getInt32();
        int dataSize = reader.getInt32();
        int frameCount = reader.getInt16();
        String compressorName = new String(reader.getBytes(32));
        int depth = reader.getInt16();
        int colorTableId = reader.getInt16();

        directory.setInt(QtDirectory.TAG_TEMPORAL_QUALITY, temporalQuality);
        directory.setInt(QtDirectory.TAG_SPATIAL_QUALITY, spatialQuality);
        directory.setInt(QtDirectory.TAG_WIDTH, width);
        directory.setInt(QtDirectory.TAG_HEIGHT, height);
        directory.setString(QtDirectory.TAG_COMPRESSOR_NAME, compressorName.trim());
        directory.setString(QtDirectory.TAG_COMPRESSION_TYPE, QtDictionary.lookup(QtDirectory.TAG_COMPRESSION_TYPE, dataFormat));
        directory.setInt(QtDirectory.TAG_DEPTH, depth);

        double horizontalInteger = (horizontalResolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (horizontalResolution & 0xFFFF) / Math.pow(2, 4);
        double verticalInteger = (verticalResolution & 0xFFFF0000) >> 16;
        double verticalFraction = (verticalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);
        directory.setDouble(QtDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
    }

    @Override
    public void processMediaInformation(QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int graphicsMode = reader.getInt16();
        int opcolorRed = reader.getInt16();
        int opcolorGreen = reader.getInt16();
        int opcolorBlue = reader.getInt16();

        directory.setString(QtDirectory.TAG_OPCOLOR, "R:" + opcolorRed + " G:" + opcolorGreen + " B:" + opcolorBlue);

        switch (graphicsMode) {
            case (0x00):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Copy");
                break;
            case (0x40):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Dither copy");
                break;
            case (0x20):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Blend");
                break;
            case (0x24):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Transparent");
                break;
            case (0x100):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Straight alpha");
                break;
            case (0x101):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Premul white alpha");
                break;
            case (0x102):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Premul black alpha");
                break;
            case (0x104):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Straight alpha blend");
                break;
            case (0x103):
                directory.setString(QtDirectory.TAG_GRAPHICS_MODE, "Composition (dither copy)");
                break;
            default:
        }
    }
}
