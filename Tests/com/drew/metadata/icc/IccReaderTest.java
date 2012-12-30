/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */

package com.drew.metadata.icc;

import com.drew.lang.ByteArrayReader;
import com.drew.metadata.Metadata;
import com.drew.testing.TestHelper;
import com.drew.tools.FileUtil;
import junit.framework.TestCase;
import org.junit.Assert;

public class IccReaderTest extends TestCase
{
    public void testExtract() throws Exception
    {
        byte[] app2Bytes = FileUtil.readBytes("Tests/Data/iccDataInvalid1.jpg.app2");

        // ICC data starts after a 14-byte preamble
        byte[] icc = TestHelper.skipBytes(app2Bytes, 14);

        Metadata metadata = new Metadata();
        new IccReader().extract(new ByteArrayReader(icc), metadata);

        IccDirectory directory = metadata.getDirectory(IccDirectory.class);

        Assert.assertNotNull(directory);

        // TODO validate expected values

//        for (Tag tag : directory.getTags()) {
//            System.out.println(tag);
//        }
    }
}
