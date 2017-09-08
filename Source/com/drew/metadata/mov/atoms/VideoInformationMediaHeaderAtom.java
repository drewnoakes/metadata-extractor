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
package com.drew.metadata.mov.atoms;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.media.QtVideoDirectory;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-25638
 */
public class VideoInformationMediaHeaderAtom extends FullAtom
{
    int graphicsMode;
    int[] opcolor;

    public VideoInformationMediaHeaderAtom(SequentialReader reader, Atom atom) throws IOException
    {
        super(reader, atom);

        graphicsMode = reader.getUInt16();
        opcolor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
    }

    public void addMetadata(QtVideoDirectory directory)
    {
        directory.setIntArray(QtVideoDirectory.TAG_OPCOLOR, opcolor);

        switch (graphicsMode) {
            case (0x00):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Copy");
                break;
            case (0x40):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Dither copy");
                break;
            case (0x20):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Blend");
                break;
            case (0x24):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Transparent");
                break;
            case (0x100):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha");
                break;
            case (0x101):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Premul white alpha");
                break;
            case (0x102):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Premul black alpha");
                break;
            case (0x104):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha blend");
                break;
            case (0x103):
                directory.setString(QtVideoDirectory.TAG_GRAPHICS_MODE, "Composition (dither copy)");
                break;
            default:
        }
    }
}
