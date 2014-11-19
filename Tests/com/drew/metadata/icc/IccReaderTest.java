/*
 * Copyright 2002-2014 Drew Noakes
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

package com.drew.metadata.icc;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.testing.TestHelper;
import com.google.common.io.Files;

public class IccReaderTest
{
    @Test
    public void testExtract() throws Exception
    {
		final byte[] app2Bytes = Files.toByteArray(new File("Tests/Data/iccDataInvalid1.jpg.app2"));

        // ICC data starts after a 14-byte preamble
        final byte[] icc = TestHelper.skipBytes(app2Bytes, 14);

        final Metadata metadata = new Metadata();
        new IccReader().extract(new ByteArrayReader(icc), metadata);

        final IccDirectory directory = metadata.getDirectory(IccDirectory.class);

        assertNotNull(directory);

        // TODO validate expected values

//        for (Tag tag : directory.getTags()) {
//            System.out.println(tag);
//        }
    }
}
