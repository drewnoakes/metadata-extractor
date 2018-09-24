/*
 * Copyright 2002-2017 Drew Noakes
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

package com.drew.metadata.netpbm;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.IterableWordReader;
import com.drew.lang.ReaderInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import java.io.IOException;
import java.util.*;

/**
 * Reads metadata from Netpbm files.
 *
 * @author Drew Noakes https://drewnoakes.com
 * @author Kevin Mott https://github.com/kwhopper
 */
public class NetpbmReader
{
    public void extract(ReaderInfo reader, final @NotNull Metadata metadata) throws IOException, ImageProcessingException
    {
        NetpbmHeaderDirectory directory = new NetpbmHeaderDirectory();

        Iterator<String> words = new IterableWordReader(reader);
        String current = "";
        
        if (!words.hasNext())
            throw new IOException("Unexpected EOF.");
        String magic = words.next();

        if (magic.charAt(0) != 'P')
            throw new ImageProcessingException("Invalid Netpbm magic number");
        int magicNum = magic.charAt(1) - '0';
        if (magicNum < 1 || magicNum > 7)
            throw new ImageProcessingException("Invalid Netpbm magic number");

        directory.setInt(NetpbmHeaderDirectory.TAG_FORMAT_TYPE, magicNum);

        if (!words.hasNext())
            throw new IOException("Unexpected EOF.");

        current = words.next();
        int width;
        try {
            width = Integer.parseInt(current);
        }
        catch(NumberFormatException parseError) {
            throw new IOException("Width is not parseable as an integer.");
        }

        directory.setInt(NetpbmHeaderDirectory.TAG_WIDTH, width);

        if (!words.hasNext())
            throw new IOException("Unexpected EOF.");

        current = words.next();
        int height;
        try {
            height = Integer.parseInt(current);
        }
        catch(NumberFormatException parseError) {
            throw new IOException("Height is not parseable as an integer.");
        }

        directory.setInt(NetpbmHeaderDirectory.TAG_HEIGHT, height);

        if (!words.hasNext())
            throw new IOException("Unexpected EOF.");

        current = words.next();
        if (magicNum != 1 && magicNum != 6)
        {
            int maxValue;
            try {
                maxValue = Integer.parseInt(current);
            }
            catch(NumberFormatException parseError) {
                throw new IOException("MaxValue is not parseable as an integer.");
            }

            directory.setInt(NetpbmHeaderDirectory.TAG_MAXIMUM_VALUE, maxValue);
        }

        metadata.addDirectory(directory);
    }
}
