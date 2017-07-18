package com.drew.metadata.mov;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.MetadataException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Payton Garland
 */
public class QtAtomHandler
{
    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return QtAtomTypes._atomList.contains(fourCC);
    }

    public void processAtom(@NotNull String fourCC, @NotNull byte[] payload, @NotNull QtDirectory directory) throws IOException, ImageProcessingException {
        ByteArrayReader reader = new ByteArrayReader(payload);
        if (fourCC.equals(QtAtomTypes.ATOM_MOVIE_HEADER)) {
            processMovieHeader(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_MEDIA_HEADER)) {
            int mediaTimescale = reader.getInt32(12);
            directory.setInt(QtDirectory.TAG_MEDIA_TIME_SCALE, mediaTimescale);
        } else if (fourCC.equals(QtAtomTypes.ATOM_VIDEO_INFO)) {
            processVideoInfo(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_TIME_TO_SAMPLE)) {
            processTimeToSample(directory, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_FILE_TYPE)) {
            processFileType(payload, reader);
        } else if (fourCC.equals(QtAtomTypes.ATOM_PREVIEW)) {
            processPreview(reader);
        }
    }

    private void processPreview(ByteArrayReader reader) throws IOException, ImageProcessingException {
        long modificationDate = reader.getInt32(8);
        int versionNumber = reader.getInt16(12);
        if (versionNumber != 0) {
            throw new ImageProcessingException("Version number should be 0, corrupt file");
        }
        String atomType = new String(reader.getBytes(14, 4));
        int atomIndex = reader.getInt16(18);
    }

    private void processFileType(@NotNull byte[] payload, ByteArrayReader reader) throws IOException, ImageProcessingException {
        String majorBrand = new String(reader.getBytes(8, 4));
        List<String> compatibleBrands = new ArrayList<String>();
        int brandsCount = (payload.length - 8) / 4;
        for (int i = 8; i < (brandsCount * 4) + 8; i += 4) {
            compatibleBrands.add(new String(reader.getBytes(i, 4)));
        }
        if (!compatibleBrands.contains("qt  ")) {
            throw new ImageProcessingException("Not a QuickTime movie file");
        }
    }

    private void processTimeToSample(@NotNull QtDirectory directory, ByteArrayReader reader) throws IOException {
        int numberOfEntries = reader.getInt32(4);
        int numberOfSamples = reader.getInt32(8);
        int sampleDuration = reader.getInt32(12);

        double timeScale = 0;

        try {
            int mediaTimeScale = directory.getInt(QtDirectory.TAG_MEDIA_TIME_SCALE);

            if (mediaTimeScale != 0) {
                timeScale = mediaTimeScale;
            }

            if (sampleDuration != 0) {
                double frameRate = timeScale / sampleDuration;
                directory.setDouble(QtDirectory.TAG_FRAME_RATE, frameRate);
            }
        } catch (MetadataException e) {
            e.printStackTrace();
        }
    }

    private void processVideoInfo(@NotNull QtDirectory directory, ByteArrayReader reader) throws IOException {
        int version = reader.getByte(0);
        int flags = reader.getInt24(1);
        int graphicsMode = reader.getInt16(4);
        int opcolorRed = reader.getInt16(6);
        int opcolorGreen = reader.getInt16(8);
        int opcolorBlue = reader.getInt16(10);

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

    private void processMovieHeader(@NotNull QtDirectory directory, ByteArrayReader reader) throws IOException {
        int version = reader.getByte(0);
        int flags = reader.getInt24(1);
        long creationTime = reader.getInt32(4);
        long modificationTime = reader.getInt32(8);
        double timeScale = reader.getInt32(12);
        double duration = reader.getInt32(16);
        int preferredRate = reader.getInt32(20);
        int preferredVolume = reader.getInt16(24);
        // 10-byte reserved space at index 26
        // 36-byte matrix structure at index 36
        int previewTime = reader.getInt32(72);
        int previewDuration = reader.getInt32(76);
        int posterTime = reader.getInt32(80);
        int selectionTime = reader.getInt32(84);
        int selectionDuration = reader.getInt32(88);
        int currentTime = reader.getInt32(92);
        int nextTrackId = reader.getInt32(96);


        duration = duration / timeScale;
        Integer hours = (int)duration / (int)(Math.pow(60, 2));
        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
        Integer seconds = (int)Math.ceil((duration / (Math.pow(60, 0))) - (minutes * 60));
        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
        directory.setString(QtDirectory.TAG_DURATION, time);

//        ZonedDateTime epoch = ZonedDateTime.of(1904, 1, 1, 0, 0, 0 ,0, ZoneId.of("UTC"));
//        epoch = epoch.plusSeconds(creationTime);
//        System.out.println(epoch);

//        System.out.println(creationTime);
//        System.out.println(modificationTime);
//
//        directory.setDate(QtDirectory.TAG_CREATION_TIMESTAMP, new Date(creationTime));
//        directory.setDate(QtDirectory.TAG_MODIFICATION_TIMESTAMP, new Date(modificationTime));

    }
}
