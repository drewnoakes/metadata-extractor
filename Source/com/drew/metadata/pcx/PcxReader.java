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
package com.drew.metadata.pcx;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

/**
 * Reads PCX image file metadata.
 *
 * <ul>
 *   <li>https://courses.engr.illinois.edu/ece390/books/labmanual/graphics-pcx.html</li>
 *   <li>http://www.fileformat.info/format/pcx/egff.htm</li>
 *   <li>http://fileformats.archiveteam.org/wiki/PCX</li>
 * </ul>
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class PcxReader
{
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        reader.setMotorolaByteOrder(false);

        PcxDirectory directory = new PcxDirectory();
        metadata.addDirectory(directory);

        try {
            byte identifier = reader.getInt8();
            if (identifier != 0x0A)
                throw new ImageProcessingException("Invalid PCX identifier byte");

            directory.setInt(PcxDirectory.TAG_VERSION, reader.getInt8());

            byte encoding = reader.getInt8();
            if (encoding != 0x01)
                throw new ImageProcessingException("Invalid PCX encoding byte");

            directory.setInt(PcxDirectory.TAG_BITS_PER_PIXEL, reader.getUInt8());
            directory.setInt(PcxDirectory.TAG_XMIN,           reader.getUInt16());
            directory.setInt(PcxDirectory.TAG_YMIN,           reader.getUInt16());
            directory.setInt(PcxDirectory.TAG_XMAX,           reader.getUInt16());
            directory.setInt(PcxDirectory.TAG_YMAX,           reader.getUInt16());
            directory.setInt(PcxDirectory.TAG_HORIZONTAL_DPI, reader.getUInt16());
            directory.setInt(PcxDirectory.TAG_VERTICAL_DPI,   reader.getUInt16());
            directory.setByteArray(PcxDirectory.TAG_PALETTE,  reader.getBytes(48));
            reader.skip(1);
            directory.setInt(PcxDirectory.TAG_COLOR_PLANES,   reader.getUInt8());
            directory.setInt(PcxDirectory.TAG_BYTES_PER_LINE, reader.getUInt16());

            int paletteType = reader.getUInt16();
            if (paletteType != 0)
                directory.setInt(PcxDirectory.TAG_PALETTE_TYPE, paletteType);

            int hScrSize = reader.getUInt16();
            if (hScrSize != 0)
                directory.setInt(PcxDirectory.TAG_HSCR_SIZE, hScrSize);

            int vScrSize = reader.getUInt16();
            if (vScrSize != 0)
                directory.setInt(PcxDirectory.TAG_VSCR_SIZE, vScrSize);

        } catch (Exception ex) {
            directory.addError("Exception reading PCX file metadata: " + ex.getMessage());
        }
    }
}
