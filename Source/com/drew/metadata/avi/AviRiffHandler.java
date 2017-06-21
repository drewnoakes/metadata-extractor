package com.drew.metadata.avi;

import com.drew.imaging.riff.RiffHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Source: http://www.alexander-noe.com/video/documentation/avi.pdf
 *
 * @author Payton Garland
 */
public class AviRiffHandler implements RiffHandler
{
    @NotNull
    private final Metadata _metadata;

    public AviRiffHandler(@NotNull Metadata metadata)
    {
        _metadata = metadata;
    }

    public boolean shouldAcceptRiffIdentifier(@NotNull String identifier)
    {
        return identifier.equals("AVI ");
    }

    public boolean shouldAcceptChunk(@NotNull String fourCC)
    {
        return fourCC.equals("strh");
    }

    public boolean shouldAcceptList(@NotNull String fourCC)
    {
        return fourCC.equals("hdrl")
            || fourCC.equals("strl");
    }

    public void processChunk(@NotNull String fourCC, @NotNull byte[] payload)
    {
        System.out.println(fourCC);
        AviDirectory directory = new AviDirectory();
        _metadata.addDirectory(directory);

//        System.out.println("Chunk " + fourCC + " " + payload.length + " bytes");
        try {
            if (fourCC.equals("strh")) {
                SequentialByteArrayReader reader = new SequentialByteArrayReader(payload);
                reader.setMotorolaByteOrder(false);

                String fccType = new String(reader.getBytes(4));
                String fccHandler = new String(reader.getBytes(4));
                int dwFlags = reader.getInt32();
                int wPriority = reader.getInt16();
                int wLanguage = reader.getInt16();
                int dwInitialFrames = reader.getInt32();
                float dwScale = reader.getFloat32();
                float dwRate = reader.getFloat32();
                int dwStart = reader.getInt32();
                int dwLength = reader.getInt32();
                int dwSuggestedBufferSize = reader.getInt32();
                int dwQuality = reader.getInt32();
                int dwSampleSize = reader.getInt32();
                int rcFrame = reader.getInt16();

                if (fccType.equals("vids")) {
                    directory.setDouble(AviDirectory.TAG_FRAMES_PER_SECOND, (dwRate/dwScale));
                } else if (fccType.equals("auds")) {
                    directory.setDouble(AviDirectory.TAG_SAMPLES_PER_SECOND, (dwRate/dwScale));
                }

                System.out.println(dwRate/dwScale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
