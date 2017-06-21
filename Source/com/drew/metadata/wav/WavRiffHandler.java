package com.drew.metadata.wav;

import com.drew.imaging.riff.RiffHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;

/**
 * Sources: http://www.neurophys.wisc.edu/auditory/riff-format.txt
 *          http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 *
 * @author Payton Garland
 */
public class WavRiffHandler implements RiffHandler
{
    @NotNull
    private final Metadata _metadata;

    public WavRiffHandler(@NotNull Metadata metadata)
    {
        _metadata = metadata;
    }

    public boolean shouldAcceptRiffIdentifier(@NotNull String identifier)
    {
        return identifier.equals("WAVE");
    }

    public boolean shouldAcceptChunk(@NotNull String fourCC)
    {
        return fourCC.equals("fmt ");
    }

    public void processChunk(@NotNull String fourCC, @NotNull byte[] payload)
    {
        WavDirectory directory = new WavDirectory();
        _metadata.addDirectory(directory);

//        System.out.println("Chunk " + fourCC + " " + payload.length + " bytes");

        try {
            if (fourCC.equals("fmt ")) {
                ByteArrayReader reader = new ByteArrayReader(payload);
                reader.setMotorolaByteOrder(false);
                int wFormatTag = reader.getInt16(0);
                int wChannels = reader.getInt16(2);
                int dwSamplesPerSec = reader.getInt32(4);
                int dwAvgBytesPerSec = reader.getInt32(8);
                int wBlockAlign = reader.getInt16(12);

                switch (wFormatTag) {
                    // Microsoft Pulse Code Modulation (PCM)
                    case (0x0001):
                        int wBitsPerSample = reader.getInt16(14);
                        directory.setInt(WavDirectory.TAG_BITS_PER_SAMPLE, wBitsPerSample);
                        directory.setString(WavDirectory.TAG_FORMAT, "Microsoft Pulse Code Modulation (PCM)");
                        break;
                    // IBM mu-law
                    case (0x0101):
                        // Contents not handled
                        directory.setString(WavDirectory.TAG_FORMAT, "IBM mu-law");
                        break;
                    // IBM a-law
                    case (0x0102):
                        // Contents not handled
                        directory.setString(WavDirectory.TAG_FORMAT, "IBM a-law");
                        break;
                    // IBM AVC Adaptive Differential Pulse Code Modulation
                    case (0x0103):
                        // Contents not handled
                        directory.setString(WavDirectory.TAG_FORMAT, "IBM AVC Adaptive Differential Pulse Code Modulation");
                        break;
                    default:
                }

                directory.setInt(WavDirectory.TAG_CHANNELS, wChannels);
                directory.setInt(WavDirectory.TAG_SAMPLES_PER_SEC, dwSamplesPerSec);
                directory.setInt(WavDirectory.TAG_BYTES_PER_SEC, dwAvgBytesPerSec);
                directory.setInt(WavDirectory.TAG_BLOCK_ALIGNMENT, wBlockAlign);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
