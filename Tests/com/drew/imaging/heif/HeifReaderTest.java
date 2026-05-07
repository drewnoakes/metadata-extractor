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
package com.drew.imaging.heif;

import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifBoxHandler;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class HeifReaderTest
{
    @Test(expected = IllegalArgumentException.class)
    public void testExtractWithNullInputStream()
    {
        HeifReader reader = new HeifReader();
        Metadata metadata = new Metadata();
        HeifBoxHandler handler = new HeifBoxHandler(metadata);
        
        reader.extract(null, handler);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testExtractWithNullHandler()
    {
        HeifReader reader = new HeifReader();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        reader.extract(inputStream, null);
    }
    
    @Test
    public void testExtractWithValidParameters()
    {
        // Should not throw any exceptions
        HeifReader reader = new HeifReader();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        Metadata metadata = new Metadata();
        HeifBoxHandler handler = new HeifBoxHandler(metadata);
        
        reader.extract(inputStream, handler);
        // This should complete without throwing any exceptions
    }
}