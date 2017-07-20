package com.drew.metadata.mov;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Payton Garland
 */
public class QtAtomHandler
{
    public String currentHandler = "";

    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return QtAtomTypes._atomList.contains(fourCC);
    }

    public void processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory, @NotNull List<String> history) throws IOException, ImageProcessingException
    {
        SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
            processMovieHeader(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SOUND_MEDIA_INFO)) {
            processSoundMediaInfo(directory, payload, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
            processFileType(directory, payload, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_VIDEO_MEDIA_INFO)) {
            processVideoMediaHeader(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_SAMPLE_DESCRIPTION)) {
            processSampleDescription(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_HANDLER)) {
            if (history.get(history.size() - 1).equals(QtContainerTypes.ATOM_MEDIA)) {
                reader.skip(8);
                currentHandler = new String(reader.getBytes(4));
            }
        }
    }

    private void processSoundMediaInfo(@NotNull QtDirectory directory, @NotNull byte[] payload, SequentialByteArrayReader reader) throws IOException {
        if (payload.length == 8) {
            reader.skip(4);
            int balance = reader.getInt16();
            double integerPortion = balance & 0xFFFF0000;
            double fractionPortion = (balance & 0x0000FFFF) / Math.pow(2, 4);
            directory.setDouble(QtDirectory.TAG_SOUND_BALANCE, integerPortion + fractionPortion);
        }
    }

    private void processSampleDescription(@NotNull QtDirectory directory, SequentialByteArrayReader reader) throws IOException
    {
        reader.skip(4);
        int numberOfEntries = reader.getInt32();
        int sampleSize = reader.getInt32();
        String dataFormat = new String(reader.getBytes(4));
        reader.skip(6); // 6-bytes reserved
        int dataReference = reader.getInt16();
        if (currentHandler.equals("vide")) {
            processVideoSampleDescription(directory, reader, dataFormat);
        } else if (currentHandler.equals("soun")) {
            processSoundSampleDescription(directory, reader, dataFormat);
        }
    }

    private void processSoundSampleDescription(@NotNull QtDirectory directory, SequentialByteArrayReader reader, String dataFormat) throws IOException
    {
        int version = reader.getInt16();
        switch (version) {
            case (0):
            case (1):
                int revisionLevel = reader.getInt16();
                int vendor = reader.getInt32();
                int numberOfChannels = reader.getInt16();
                int sampleSizeBits = reader.getInt16();
                int compressionId = reader.getInt16();
                int packetSize = reader.getInt16();
                int sampleRate = reader.getInt32();

                directory.setString(QtDirectory.TAG_AUDIO_FORMAT, QtDictionary.lookup(QtDirectory.TAG_AUDIO_FORMAT, dataFormat));
                directory.setInt(QtDirectory.TAG_NUMBER_OF_CHANNELS, numberOfChannels);
                directory.setInt(QtDirectory.TAG_SAMPLE_SIZE, sampleSizeBits);
                directory.setInt(QtDirectory.TAG_SAMPLE_RATE, sampleRate);
                break;
        }
    }

    private void processVideoSampleDescription(@NotNull QtDirectory directory, SequentialByteArrayReader reader, String dataFormat) throws IOException
    {
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

    private void processFileType(@NotNull QtDirectory directory, @NotNull byte[] payload, SequentialByteArrayReader reader) throws IOException, ImageProcessingException {
        directory.setByteArray(QtDirectory.TAG_MAJOR_BRAND, reader.getBytes(4));

        directory.setByteArray(QtDirectory.TAG_MINOR_VERSION, reader.getBytes(4));

        ArrayList<String> compatibleBrands = new ArrayList<String>();
        int brandsCount = (payload.length - 8) / 4;
        for (int i = 8; i < (brandsCount * 4) + 8; i += 4) {
            compatibleBrands.add(new String(reader.getBytes(4)));
        }
        String[] compatibleBrandsReturn = new String[compatibleBrands.size()];
        directory.setStringArray(QtDirectory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(compatibleBrandsReturn));
        if (!compatibleBrands.contains("qt  ")) {
            throw new ImageProcessingException("Not a QuickTime movie file");
        }
    }

    private void processVideoMediaHeader(@NotNull QtDirectory directory, SequentialByteArrayReader reader) throws IOException {
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

    private void processMovieHeader(@NotNull QtDirectory directory, SequentialByteArrayReader reader) throws IOException {
        reader.skip(4);
        directory.setLong(QtDirectory.TAG_CREATION_TIME, reader.getInt32());
        directory.setLong(QtDirectory.TAG_MODIFICATION_TIME, reader.getInt32());

        int timeScale = reader.getInt32();
        double duration = reader.getInt32();
        duration = duration / timeScale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(QtDirectory.TAG_DURATION, time);
        directory.setInt(QtDirectory.TAG_TIME_SCALE, timeScale);

        // Calculate preferred rate fixed point
        int preferredRate = reader.getInt32();
        double preferredRateInteger = (preferredRate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (preferredRate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(QtDirectory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        int preferredVolume = reader.getInt16();
        double preferredVolumeInteger = (preferredVolume & 0xFF00) >> 8;
        double preferredVolumeFraction = (preferredVolume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(QtDirectory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        // 10-byte reserved space at index 26
        reader.skip(10);
        // 36-byte matrix structure at index 36
        reader.skip(36);
        directory.setInt(QtDirectory.TAG_PREVIEW_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_PREVIEW_DURATION, reader.getInt32());
        directory.setInt(QtDirectory.TAG_POSTER_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_SELECTION_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_SELECTION_DURATION, reader.getInt32());
        directory.setInt(QtDirectory.TAG_CURRENT_TIME, reader.getInt32());
        directory.setInt(QtDirectory.TAG_NEXT_TRACK_ID, reader.getInt32());
    }
}
