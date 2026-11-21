// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////

package com.drew.metadata.fuzzer;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.drew.imaging.wav.WavMetadataReader;
import com.drew.imaging.ImageProcessingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WavFuzzer {
    private static final byte[] RIFF = {'R', 'I', 'F', 'F'};
    private static final byte[] WAVE = {'W', 'A', 'V', 'E'};
    private static final byte[] FMT = {'f', 'm', 't', ' '};
    private static final byte[] DATA = {'d', 'a', 't', 'a'};
    private static final byte[] LIST = {'L', 'I', 'S', 'T'};
    private static final byte[] INFO = {'I', 'N', 'F', 'O'};
    private static final byte[] INAM = {'I', 'N', 'A', 'M'};
    private static final byte[] IART = {'I', 'A', 'R', 'T'};
    private static final byte[] ICRD = {'I', 'C', 'R', 'D'};
    private static final byte[] FACT = {'f', 'a', 'c', 't'};
    private static final byte[] CUE = {'c', 'u', 'e', ' '};

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            byte[] input = generateWav(data);
            com.drew.metadata.Metadata metadata = WavMetadataReader.readMetadata(
                new ByteArrayInputStream(input));
            FuzzInputBuilder.touchMetadata(metadata);
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    private static byte[] generateWav(FuzzedDataProvider data) throws IOException {
        int mode = data.consumeInt(0, 2);
        
        if (mode == 0) {
            return generateSimpleWav(data);
        } else {
            return generateWavWithChunks(data);
        }
    }

    private static byte[] generateSimpleWav(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write(RIFF);
        baos.write(intToLittleEndian(data.consumeInt(1000, 100000)));
        baos.write(WAVE);
        
        baos.write(FMT);
        baos.write(intToLittleEndian(16));
        baos.write(shortToLittleEndian(data.consumeInt(1, 10)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 8)));
        baos.write(intToLittleEndian(data.consumeInt(1000, 100000)));
        baos.write(intToLittleEndian(data.consumeInt(1000, 100000)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 32)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 32)));
        
        baos.write(DATA);
        int dataSize = data.consumeInt(0, 10000);
        baos.write(intToLittleEndian(dataSize));
        baos.write(data.consumeBytes(dataSize));
        
        return baos.toByteArray();
    }

    private static byte[] generateWavWithChunks(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        ByteArrayOutputStream riffBody = new ByteArrayOutputStream();
        
        riffBody.write(WAVE);
        
        riffBody.write(FMT);
        int fmtSize = 16 + data.consumeInt(0, 16);
        riffBody.write(intToLittleEndian(fmtSize));
        riffBody.write(shortToLittleEndian(data.consumeInt(1, 10)));
        riffBody.write(shortToLittleEndian(data.consumeInt(1, 8)));
        riffBody.write(intToLittleEndian(data.consumeInt(1000, 100000)));
        riffBody.write(intToLittleEndian(data.consumeInt(1000, 100000)));
        riffBody.write(shortToLittleEndian(data.consumeInt(1, 32)));
        riffBody.write(shortToLittleEndian(data.consumeInt(0, 32)));
        
        if (data.consumeBoolean()) {
            riffBody.write(FACT);
            riffBody.write(intToLittleEndian(4));
            riffBody.write(intToLittleEndian(data.consumeInt(0, 100000)));
        }
        
        if (data.consumeBoolean()) {
            riffBody.write(CUE);
            int cueSize = 4 + data.consumeInt(1, 10) * 24;
            riffBody.write(intToLittleEndian(cueSize));
            riffBody.write(intToLittleEndian(data.consumeInt(0, 100)));
            for (int i = 0; i < data.consumeInt(0, 5); i++) {
                riffBody.write(intToLittleEndian(i));
                riffBody.write(intToLittleEndian(data.consumeInt(0, 100)));
                riffBody.write(intToLittleEndian(data.consumeInt(0, 1000)));
                riffBody.write(intToLittleEndian(data.consumeInt(0, 1000)));
                riffBody.write(intToLittleEndian(data.consumeInt(0, 10)));
                riffBody.write(new byte[4]);
            }
        }
        
        if (data.consumeBoolean()) {
            ByteArrayOutputStream listBody = new ByteArrayOutputStream();
            listBody.write(INFO);
            
            if (data.consumeBoolean()) {
                listBody.write(INAM);
                byte[] title = data.consumeBytes(50);
                listBody.write(intToLittleEndian(title.length));
                listBody.write(title);
            }
            
            if (data.consumeBoolean()) {
                listBody.write(IART);
                byte[] artist = data.consumeBytes(50);
                listBody.write(intToLittleEndian(artist.length));
                listBody.write(artist);
            }
            
            if (data.consumeBoolean()) {
                listBody.write(ICRD);
                byte[] date = data.consumeBytes(20);
                listBody.write(intToLittleEndian(date.length));
                listBody.write(date);
            }
            
            riffBody.write(LIST);
            riffBody.write(intToLittleEndian(listBody.size()));
            riffBody.write(listBody.toByteArray());
        }
        
        riffBody.write(DATA);
        int dataSize = data.consumeInt(0, 1000);
        riffBody.write(intToLittleEndian(dataSize));
        riffBody.write(data.consumeBytes(dataSize));
        
        baos.write(RIFF);
        baos.write(intToLittleEndian(riffBody.size()));
        baos.write(riffBody.toByteArray());
        
        return baos.toByteArray();
    }

    private static byte[] intToLittleEndian(int v) {
        return new byte[]{
            (byte) (v & 0xFF),
            (byte) ((v >> 8) & 0xFF),
            (byte) ((v >> 16) & 0xFF),
            (byte) ((v >> 24) & 0xFF)
        };
    }

    private static byte[] shortToLittleEndian(int v) {
        return new byte[]{
            (byte) (v & 0xFF),
            (byte) ((v >> 8) & 0xFF)
        };
    }
}
