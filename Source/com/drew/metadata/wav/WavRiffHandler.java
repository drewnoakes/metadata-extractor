package com.drew.metadata.wav;

import com.drew.imaging.riff.RiffHandler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Implementation of {@link RiffHandler} specialising in Wav support.
 *
 * Extracts data from chunk/list types:
 *
 * <ul>
 *     <li><code>"INFO"</code>: artist, title, product, track number, date created, genre, comments, copyright, software, duration</li>
 *     <li><code>"fmt "</code>: format, channels, samples/second, bytes/second, block alignment, bits/sample</li>
 *     <li><code>"data"</code>: duration</li>
 * </ul>
 *
 * Sources: http://www.neurophys.wisc.edu/auditory/riff-format.txt
 *          http://www-mmsp.ece.mcgill.ca/Documents/AudioFormats/WAVE/WAVE.html
 *          http://wiki.audacityteam.org/wiki/WAV
 *
 * @author Payton Garland
 */
public class WavRiffHandler implements RiffHandler
{
    @NotNull
    private final WavDirectory _directory;

    @NotNull
    private String _currentList = "";

    public WavRiffHandler(@NotNull Metadata metadata)
    {
        _directory = new WavDirectory();
        metadata.addDirectory(_directory);
    }

    public boolean shouldAcceptRiffIdentifier(@NotNull String identifier)
    {
        return identifier.equals(WavDirectory.FORMAT);
    }

    public boolean shouldAcceptChunk(@NotNull String fourCC)
    {
        return fourCC.equals(WavDirectory.CHUNK_FORMAT)
            || (_currentList.equals(WavDirectory.LIST_INFO) && WavDirectory._tagIntegerMap.containsKey(fourCC))
            || fourCC.equals(WavDirectory.CHUNK_DATA);
    }

    @Override
    public boolean shouldAcceptList(@NotNull String fourCC)
    {
        if (fourCC.equals(WavDirectory.LIST_INFO)) {
            _currentList = WavDirectory.LIST_INFO;
            return true;
        } else {
            _currentList = "";
            return false;
        }
    }

    public void processChunk(@NotNull String fourCC, @NotNull byte[] payload)
    {
        try {
            if (fourCC.equals(WavDirectory.CHUNK_FORMAT)) {
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
                        _directory.setInt(WavDirectory.TAG_BITS_PER_SAMPLE, wBitsPerSample);
                        _directory.setString(WavDirectory.TAG_FORMAT, WavDirectory._audioEncodingMap.get(wFormatTag));
                        break;
                    default:
                        if (WavDirectory._audioEncodingMap.containsKey(wFormatTag)) {
                            _directory.setString(WavDirectory.TAG_FORMAT, WavDirectory._audioEncodingMap.get(wFormatTag));
                        } else {
                            _directory.setString(WavDirectory.TAG_FORMAT, "Unknown");
                        }
                }

                _directory.setInt(WavDirectory.TAG_CHANNELS, wChannels);
                _directory.setInt(WavDirectory.TAG_SAMPLES_PER_SEC, dwSamplesPerSec);
                _directory.setInt(WavDirectory.TAG_BYTES_PER_SEC, dwAvgBytesPerSec);
                _directory.setInt(WavDirectory.TAG_BLOCK_ALIGNMENT, wBlockAlign);
            } else if (fourCC.equals(WavDirectory.CHUNK_DATA)) {
                try {
                    if (_directory.containsTag(WavDirectory.TAG_BYTES_PER_SEC)) {
                        double duration = (double)payload.length / _directory.getDouble(WavDirectory.TAG_BYTES_PER_SEC);
                        Integer hours = (int)duration / (int)(Math.pow(60, 2));
                        Integer minutes = ((int)duration / (int)(Math.pow(60, 1))) - (hours * 60);
                        Integer seconds = (int)Math.round((duration / (Math.pow(60, 0))) - (minutes * 60));
                        String time = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);
                        _directory.setString(WavDirectory.TAG_DURATION, time);
                    }
                } catch (MetadataException ex) {
                    _directory.addError("Error calculating duration: bytes per second not found");
                }
            }else if (WavDirectory._tagIntegerMap.containsKey(fourCC)) {
                _directory.setString(WavDirectory._tagIntegerMap.get(fourCC), new String(payload).substring(0, payload.length - 1));
            }
        } catch (IOException ex) {
            _directory.addError(ex.getMessage());
        }
    }
}
