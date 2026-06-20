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
package com.drew.imaging;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class FileTypeDetectorTest
{
    @Test
    public void testDetectFileTypeWithEmptyStream() throws IOException
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        inputStream.mark(1000);  // Mark supported
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectFileTypeWithSingleByte() throws IOException
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{(byte)0xFF});
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectFileTypeWithSmallArray() throws IOException
    {
        // Test with only 2 bytes - should not cause ArrayIndexOutOfBoundsException
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{(byte)0xFF, (byte)0xE0});
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectFileTypeWithPartialQuickTimeData() throws IOException
    {
        // Test with partial QuickTime data (needs 12 bytes but only provide 8)
        byte[] bytes = new byte[]{0x00, 0x00, 0x00, 0x20, 'f', 't', 'y', 'p'};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectFileTypeWithPartialRiffData() throws IOException
    {
        // Test with partial RIFF data (needs 12 bytes but only provide 8)
        byte[] bytes = new byte[]{'R', 'I', 'F', 'F', 0x00, 0x00, 0x00, 0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectFileTypeWithPartialMpegData() throws IOException
    {
        // Test with partial MPEG data (needs 3 bytes but only provide 2)
        byte[] bytes = new byte[]{(byte)0xFF, (byte)0xE0};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Unknown, fileType);
    }

    @Test
    public void testDetectValidJpegFile() throws IOException
    {
        // Valid JPEG header
        byte[] bytes = new byte[]{(byte)0xFF, (byte)0xD8, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Jpeg, fileType);
    }

    @Test
    public void testDetectValidQuickTimeFile() throws IOException
    {
        // Valid QuickTime header with sufficient bytes
        byte[] bytes = new byte[]{0x00, 0x00, 0x00, 0x20, 'f', 't', 'y', 'p', 'q', 't', ' ', ' '};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.QuickTime, fileType);
    }

    @Test
    public void testDetectValidRiffWaveFile() throws IOException
    {
        // Valid RIFF WAVE header with sufficient bytes
        byte[] bytes = new byte[]{'R', 'I', 'F', 'F', 0x00, 0x00, 0x00, 0x00, 'W', 'A', 'V', 'E'};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Wav, fileType);
    }

    @Test
    public void testDetectValidMp3File() throws IOException
    {
        // Valid MP3 header with sufficient bytes
        byte[] bytes = new byte[]{(byte)0xFF, (byte)0xFB, (byte)0x90, 0x00, 0x00, 0x00, 0x00, 0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        assertEquals(FileType.Mp3, fileType);
    }

    @Test(expected = IOException.class)
    public void testDetectFileTypeWithNonMarkableStream() throws IOException
    {
        // Create a non-markable stream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{(byte)0xFF, (byte)0xD8}) {
            @Override
            public boolean markSupported() {
                return false;
            }
        };
        
        FileTypeDetector.detectFileType(inputStream);
    }
}