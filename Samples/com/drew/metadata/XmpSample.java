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
package com.drew.metadata;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.xmp.XmpDirectory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Shows basic extraction and iteration of XMP data.
 * <p>
 * For more information, see the project wiki: https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class XmpSample
{
    private static void xmpSample(InputStream imageStream) throws XMPException, ImageProcessingException, IOException
    {
        // Extract metadata from the image
        Metadata metadata = ImageMetadataReader.readMetadata(imageStream);

        // Iterate through any XMP directories we may have received
        for (XmpDirectory xmpDirectory : metadata.getDirectoriesOfType(XmpDirectory.class)) {

            // Usually with metadata-extractor, you iterate a directory's tags. However XMP has
            // a complex structure with many potentially unknown properties. This doesn't map
            // well to metadata-extractor's directory-and-tag model.
            //
            // If you need to use XMP data, access the XMPMeta object directly.
            XMPMeta xmpMeta = xmpDirectory.getXMPMeta();

            XMPIterator itr = xmpMeta.iterator();

            // Iterate XMP properties
            while (itr.hasNext()) {

                XMPPropertyInfo property = (XMPPropertyInfo) itr.next();

                // Print details of the property
                System.out.println(property.getPath() + ": " + property.getValue());
            }
        }
    }
}
