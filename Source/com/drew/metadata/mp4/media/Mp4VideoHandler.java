package com.drew.metadata.mp4.media;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtAtomTypes;
import com.drew.metadata.mov.QtDictionary;
import com.drew.metadata.mov.QtHandlerFactory;
import com.drew.metadata.mov.QtMediaHandler;
import com.drew.metadata.mp4.Mp4MediaHandler;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap3/qtff3.html#//apple_ref/doc/uid/TP40000939-CH205-74522
 */
public class Mp4VideoHandler extends Mp4MediaHandler
{
    public Mp4VideoHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected String getMediaInformation()
    {
        return QtAtomTypes.ATOM_VIDEO_MEDIA_INFO;
    }

    @Override
    protected Mp4VideoDirectory getDirectory()
    {
        return new Mp4VideoDirectory();
    }

    @Override
    public void processSampleDescription(@NotNull ByteArrayReader reader) throws IOException
    {
        String dataFormat = new String(reader.getBytes(12,4));
        String vendor = new String(reader.getBytes(28,4));
        int temporalQuality = reader.getInt32(32);
        int spatialQuality = reader.getInt32(36);
        int width = reader.getInt16(40);
        int height = reader.getInt16(42);
        int horizontalResolution = reader.getInt32(44);
        int verticalResolution = reader.getInt32(48);
        String compressorName = new String(reader.getBytes(58,32));
        int depth = reader.getInt16(90);
        int colorTableId = reader.getInt16(92);

        QtDictionary.setLookup(Mp4VideoDirectory.TAG_VENDOR, vendor, directory);
        QtDictionary.setLookup(Mp4VideoDirectory.TAG_COMPRESSION_TYPE, dataFormat, directory);

        directory.setInt(Mp4VideoDirectory.TAG_TEMPORAL_QUALITY, temporalQuality);
        directory.setInt(Mp4VideoDirectory.TAG_SPATIAL_QUALITY, spatialQuality);
        directory.setInt(Mp4VideoDirectory.TAG_WIDTH, width);
        directory.setInt(Mp4VideoDirectory.TAG_HEIGHT, height);
        directory.setString(Mp4VideoDirectory.TAG_COMPRESSOR_NAME, compressorName.trim());

        directory.setInt(Mp4VideoDirectory.TAG_DEPTH, depth);
        directory.setInt(Mp4VideoDirectory.TAG_COLOR_TABLE, colorTableId);

        // Calculate horizontal res
        double horizontalInteger = (horizontalResolution & 0xFFFF0000) >> 16;
        double horizontalFraction = (horizontalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_HORIZONTAL_RESOLUTION, horizontalInteger + horizontalFraction);

        double verticalInteger = (verticalResolution & 0xFFFF0000) >> 16;
        double verticalFraction = (verticalResolution & 0xFFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4VideoDirectory.TAG_VERTICAL_RESOLUTION, verticalInteger + verticalFraction);
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25642
     */
    @Override
    public void processMediaInformation(@NotNull ByteArrayReader reader) throws IOException
    {
        int graphicsMode = reader.getInt16(4);
        int opcolorRed = reader.getUInt16(6);
        int opcolorGreen = reader.getUInt16(8);
        int opcolorBlue = reader.getUInt16(10);

        directory.setString(Mp4VideoDirectory.TAG_OPCOLOR, "R:" + opcolorRed + " G:" + opcolorGreen + " B:" + opcolorBlue);

        switch (graphicsMode) {
            case (0x00):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Copy");
                break;
            case (0x40):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Dither copy");
                break;
            case (0x20):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Blend");
                break;
            case (0x24):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Transparent");
                break;
            case (0x100):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha");
                break;
            case (0x101):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Premul white alpha");
                break;
            case (0x102):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Premul black alpha");
                break;
            case (0x104):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha blend");
                break;
            case (0x103):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Composition (dither copy)");
                break;
            default:
        }
    }

    /**
     * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCGFJII
     */
    @Override
    public void processTimeToSample(@NotNull ByteArrayReader reader) throws IOException
    {
        int flags = reader.getInt32(0);
        int numberOfEntries = reader.getInt32(4);
        int numberOfSamples = reader.getInt32(8);
        int sampleDuration = reader.getInt32(12);

        float frameRate = (float) QtHandlerFactory.HANDLER_PARAM_TIME_SCALE/(float)sampleDuration;
        directory.setFloat(Mp4VideoDirectory.TAG_FRAME_RATE, frameRate);
    }
}
