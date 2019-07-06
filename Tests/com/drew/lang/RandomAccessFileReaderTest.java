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

package com.drew.lang;

import com.drew.tools.FileUtil;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class RandomAccessFileReaderTest extends RandomAccessTestBase
{
    private File _tempFile;
    private RandomAccessFile _randomAccessFile;

    @Override
    protected RandomAccessReader createReader(byte[] bytes)
    {
        try {
            // Unit tests can create multiple readers in the same test, as long as they're used one after the other
            deleteTempFile();

            _tempFile = File.createTempFile("metadata-extractor-test-", ".tmp");
            FileUtil.saveBytes(_tempFile, bytes);
            _randomAccessFile = new RandomAccessFile(_tempFile, "r");
            return new RandomAccessFileReader(_randomAccessFile);
        } catch (IOException e) {
            fail("Unable to create temp file");
            return null;
        }
    }

    @After
    public void deleteTempFile() throws IOException
    {
        if (_randomAccessFile == null)
            return;

        _randomAccessFile.close();

        if (_tempFile == null)
            return;

        assertTrue(
                "Unable to delete temp file used during unit test: " + _tempFile.getAbsolutePath(),
                _tempFile.delete());

        _tempFile = null;
        _randomAccessFile = null;
    }

    @SuppressWarnings({ "ConstantConditions" })
    @Test(expected = NullPointerException.class)
    public void testConstructWithNullBufferThrows() throws IOException
    {
        new RandomAccessFileReader(null);
    }
}
