/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.mp3;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

/**
 * Sources: http://id3.org/mp3Frame
 *          https://www.loc.gov/preservation/digital/formats/fdd/fdd000105.shtml
 *
 * @author Payton Garland
 */
public class Mp3Reader
{

    public void extract(@NotNull final InputStream inputStream, @NotNull final Metadata metadata)
    {
        Mp3Directory directory = new Mp3Directory();
        metadata.addDirectory(directory);

        try {
            inputStream.reset();
            SequentialReader reader = new StreamReader(inputStream);

            int header = reader.getInt32();

            // ID: MPEG-2.5, MPEG-2, or MPEG-1
            double id = 0;
            switch ((header & 0x000180000) >> 19) {
                case (0):
                    directory.setString(Mp3Directory.TAG_ID, "MPEG-2.5");
                    id = 2.5;
                    throw new ImageProcessingException("MPEG-2.5 not supported.");
                case (2):
                    directory.setString(Mp3Directory.TAG_ID, "MPEG-2");
                    id = 2;
                    break;
                case (3):
                    directory.setString(Mp3Directory.TAG_ID, "MPEG-1");
                    id = 1;
                    break;
                default:
            }

            // Layer Type: 1, 2, 3, or not defined
            int layer = ((header & 0x00060000) >> 17);
            switch (layer) {
                case(0):
                    directory.setString(Mp3Directory.TAG_LAYER, "Not defined");
                    break;
                case(1):
                    directory.setString(Mp3Directory.TAG_LAYER, "Layer III");
                    break;
                case(2):
                    directory.setString(Mp3Directory.TAG_LAYER, "Layer II");
                    break;
                case(3):
                    directory.setString(Mp3Directory.TAG_LAYER, "Layer I");
                    break;
                default:
            }


            int protectionBit = ((header & 0x00010000) >> 16);

            // Bitrate: depends on ID and Layer
            int bitrate = ((header & 0x0000F000) >> 12);
            if (bitrate != 0 && bitrate != 15)
                directory.setInt(Mp3Directory.TAG_BITRATE, setBitrate(bitrate, layer, id));

            // Frequency: depends on ID
            int frequency = ((header & 0x00000C00) >> 10);
            int[][] frequencyMapping = new int[2][3];
            frequencyMapping[0] = new int[]{44100, 48000, 32000};
            frequencyMapping[1] = new int[]{22050, 24000, 16000};
            if (id == 2) {
                directory.setInt(directory.TAG_FREQUENCY, frequencyMapping[1][frequency]);
                frequency = frequencyMapping[1][frequency];
            } else if (id == 1) {
                directory.setInt(directory.TAG_FREQUENCY, frequencyMapping[0][frequency]);
                frequency = frequencyMapping[0][frequency];
            }


            int paddingBit = ((header & 0x00000200) >> 9);

            // Encoding type: Stereo, Joint Stereo, Dual Channel, or Mono
            int mode = ((header & 0x000000C0) >> 6);
            switch (mode){
                case(0):
                    directory.setString(Mp3Directory.TAG_MODE, "Stereo");
                    break;
                case(1):
                    directory.setString(Mp3Directory.TAG_MODE, "Joint stereo");
                    break;
                case(2):
                    directory.setString(Mp3Directory.TAG_MODE, "Dual channel");
                    break;
                case(3):
                    directory.setString(Mp3Directory.TAG_MODE, "Mono");
                    break;
                default:
            }

            // Copyright boolean
            int copyright = ((header & 0x00000008) >> 3);
            switch (copyright) {
                case(0):
                    directory.setString(Mp3Directory.TAG_COPYRIGHT, "False");
                    break;
                case(1):
                    directory.setString(Mp3Directory.TAG_COPYRIGHT, "True");
                    break;
                default:
            }

            int emphasis = (header & 0x00000003);
            switch (emphasis) {
                case (0):
                    directory.setString(Mp3Directory.TAG_EMPHASIS, "none");
                    break;
                case (1):
                    directory.setString(Mp3Directory.TAG_EMPHASIS, "50/15ms");
                    break;
                case (3):
                    directory.setString(Mp3Directory.TAG_EMPHASIS, "CCITT j.17");
                    break;
                default:
            }

            int frameSize = ((setBitrate(bitrate, layer, id) * 1000) * 144) / frequency;
            directory.setString(Mp3Directory.TAG_FRAME_SIZE, frameSize + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
    }

    public int setBitrate(int bitrate, int layer, double id)
    {
        int[][] bitrateMapping = new int[14][6];
        bitrateMapping[0] = new int[]{32, 32, 32, 32, 32, 8};
        bitrateMapping[1] = new int[]{64, 48, 40, 64, 48, 16};
        bitrateMapping[2] = new int[]{96, 56, 48, 96, 56, 24};
        bitrateMapping[3] = new int[]{128, 64, 56, 128, 64, 32};
        bitrateMapping[4] = new int[]{160, 80, 64, 160, 80, 64};
        bitrateMapping[5] = new int[]{192, 96, 80, 192, 96, 80};
        bitrateMapping[6] = new int[]{224, 112, 96, 224, 112, 56};
        bitrateMapping[7] = new int[]{256, 128, 112, 256, 128, 64};
        bitrateMapping[8] = new int[]{288, 160, 128, 28, 160, 128};
        bitrateMapping[9] = new int[]{320, 192, 160, 320, 192, 160};
        bitrateMapping[10] = new int[]{352, 224, 192, 352, 224, 112};
        bitrateMapping[11] = new int[]{384, 256, 224, 384, 256, 128};
        bitrateMapping[12] = new int[]{416, 320, 256, 416, 320, 256};
        bitrateMapping[13] = new int[]{448, 384, 320, 448, 384, 320};

        int xPos = 0;
        int yPos = bitrate - 1;

        if (id == 2) {
            // MPEG-2
            switch (layer) {
                case (1):
                    xPos = 5;
                    break;
                case (2):
                    xPos = 4;
                    break;
                case (3):
                    xPos = 3;
                    break;
                default:
            }
        } else if (id == 1) {
            // MPEG-1
            switch (layer) {
                case(1):
                    xPos = 2;
                    break;
                case(2):
                    xPos = 1;
                    break;
                case(3):
                    xPos = 0;
                    break;
                default:
            }
        }

        return bitrateMapping[yPos][xPos];
    }

    /**
     * https://phoxis.org/2010/05/08/synch-safe/
     */
    public static int getSyncSafeSize(int decode)
    {
        int a = decode & 0xFF;
        int b = (decode >> 8) & 0xFF;
        int c  = (decode >> 16) & 0xFF;
        int d = (decode >> 24) & 0xFF;

        int decoded = 0x0;
        decoded = decoded | a;
        decoded = decoded | (b << 7);
        decoded = decoded | (c << 14);
        decoded = decoded | (d << 21);
        return decoded;
    }
}
