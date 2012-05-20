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

import com.drew.metadata.ExtractAppSegmentBytesToFileUtility;
import com.drew.metadata.Metadata;
import junit.framework.TestCase;

import java.io.File;

public class IccReaderTest extends TestCase
{
    public void testExtract() throws Exception
    {
        String iccSegmentFile = "Tests/com/drew/metadata/icc/iccDataInvalid1.app2bytes";
        byte[] app2Bytes = ExtractAppSegmentBytesToFileUtility.read(new File(iccSegmentFile));

        // skip first 14 bytes
        byte[] icc = new byte[app2Bytes.length-14];
        System.arraycopy(app2Bytes, 14, icc, 0, app2Bytes.length-14);

        Metadata metadata = new Metadata();
        new IccReader().extract(icc, metadata);
    }
}
