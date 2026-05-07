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
import com.drew.imaging.gif.GifMetadataReader;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class GifFuzzer {
    private static final byte[] GIF87A = {'G', 'I', 'F', '8', '7', 'a'};
    private static final byte[] GIF89A = {'G', 'I', 'F', '8', '9', 'a'};

    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            int mode = data.consumeInt(0, 2);
            byte[] input = generateGif(data);
            
            if (mode == 0) {
                com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(
                    new ByteArrayInputStream(input));
                FuzzInputBuilder.touchMetadata(metadata);
            } else {
                com.drew.metadata.Metadata metadata = GifMetadataReader.readMetadata(
                    new ByteArrayInputStream(input));
                FuzzInputBuilder.touchMetadata(metadata);
            }
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    private static byte[] generateGif(FuzzedDataProvider data) throws IOException {
        int type = data.consumeInt(0, 2);
        
        if (type == 0) {
            return generateSimpleGif(data);
        } else if (type == 1) {
            return generateGifWithBlocks(data);
        } else {
            return generateGifWithBlocks(data);
        }
    }

    private static byte[] generateSimpleGif(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        boolean useGif89a = data.consumeBoolean();
        baos.write(useGif89a ? GIF89A : GIF87A);
        
        baos.write(data.consumeBytes(2));
        baos.write(data.consumeBytes(2));
        
        byte flags = data.consumeByte();
        baos.write(flags);
        
        baos.write(data.consumeByte());
        baos.write(data.consumeByte());
        
        boolean hasGlobalColorTable = (flags & 0x80) != 0;
        if (hasGlobalColorTable) {
            int colorTableSize = 3 * (1 << ((flags & 7) + 1));
            baos.write(data.consumeBytes(colorTableSize));
        }
        
        int numBlocks = data.consumeInt(0, 10);
        for (int i = 0; i < numBlocks; i++) {
            byte blockType = data.consumeByte();
            if (blockType == 0x21) {
                byte[] extData = data.consumeBytes(20);
                baos.write(0x21);
                baos.write(extData);
                baos.write(0);
            } else if (blockType == 0x2c) {
                baos.write(0x2c);
                baos.write(data.consumeBytes(10));
                byte[] imgData = data.consumeBytes(50);
                baos.write(imgData);
                baos.write(0);
            } else if (blockType == 0x3b) {
                baos.write(0x3b);
                break;
            }
        }
        
        if (baos.size() < 10 || baos.toByteArray()[baos.size()-1] != 0x3b) {
            baos.write(0x3b);
        }
        
        return baos.toByteArray();
    }

    private static byte[] generateGifWithBlocks(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        boolean useGif89a = data.consumeBoolean();
        baos.write(useGif89a ? GIF89A : GIF87A);
        
        baos.write(shortToLittleEndian(data.consumeInt(1, 800)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 600)));
        
        byte flags = data.consumeByte();
        boolean hasGlobalColorTable = (flags & 0x80) != 0;
        baos.write(flags);
        
        baos.write(data.consumeByte());
        baos.write(data.consumeByte());
        
        if (hasGlobalColorTable) {
            int colorTableSize = 3 * (1 << ((flags & 7) + 1));
            baos.write(data.consumeBytes(colorTableSize));
        }
        
        int numExtensions = data.consumeInt(0, 8);
        for (int i = 0; i < numExtensions; i++) {
            int extType = data.consumeInt(0, 4);
            
            if (extType == 0) {
                baos.write(0x21);
                baos.write(0xf9);
                baos.write(4);
                baos.write(data.consumeByte());
                baos.write(shortToLittleEndian(data.consumeInt(0, 1000)));
                baos.write(data.consumeByte());
                baos.write(0);
            } else if (extType == 1) {
                baos.write(0x21);
                baos.write(0xfe);
                byte[] comment = data.consumeBytes(30);
                baos.write(comment.length);
                baos.write(comment);
                baos.write(0);
            } else if (extType == 2 && useGif89a) {
                baos.write(0x21);
                baos.write(0xff);
                baos.write(11);
                baos.write("NETSCAPE2.0".getBytes());
                baos.write(3);
                baos.write(1);
                baos.write(shortToLittleEndian(data.consumeInt(0, 1000)));
                baos.write(0);
            } else if (extType == 3 && useGif89a) {
                baos.write(0x21);
                baos.write(0xff);
                baos.write(11);
                baos.write("XMP DataXMP".getBytes());
                byte[] xmp = data.consumeBytes(50);
                baos.write(xmp.length + 257);
                baos.write(xmp);
                baos.write(new byte[257]);
                baos.write(0);
            }
        }
        
        baos.write(0x2c);
        baos.write(shortToLittleEndian(data.consumeInt(0, 100)));
        baos.write(shortToLittleEndian(data.consumeInt(0, 100)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 400)));
        baos.write(shortToLittleEndian(data.consumeInt(1, 300)));
        
        byte imgFlags = data.consumeByte();
        boolean hasLocalColorTable = (imgFlags & 0x80) != 0;
        baos.write(imgFlags);
        
        if (hasLocalColorTable) {
            int colorTableSize = 3 * (1 << ((imgFlags & 7) + 1));
            baos.write(data.consumeBytes(colorTableSize));
        }
        
        baos.write(data.consumeInt(2, 11));
        
        byte[] lzwData = data.consumeBytes(100);
        baos.write(lzwData.length);
        baos.write(lzwData);
        baos.write(0);
        
        baos.write(0x3b);
        
        return baos.toByteArray();
    }

    private static byte[] shortToLittleEndian(int v) {
        return new byte[]{
            (byte) (v & 0xFF),
            (byte) ((v >> 8) & 0xFF)
        };
    }
}
