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

import com.drew.lang.Charsets;
import com.drew.lang.ReaderInfo;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-BBCIBHFD
 *
 * @author Payton Garland
 */
public class HandlerReferenceAtom extends FullAtom
{
    public String getComponentType()
    {
        return componentSubtype;
    }

    String componentType;
    String componentSubtype;
    String componentName;

    public HandlerReferenceAtom(ReaderInfo reader, Atom atom) throws IOException
    {
        super(reader, atom);

        componentType = reader.getString(4, Charsets.UTF_8);
        componentSubtype = reader.getString(4, Charsets.UTF_8);
        reader.skip(4); // Reserved
        reader.skip(4); // Reserved
        reader.skip(4); // Reserved
        componentName = reader.getString(reader.getUInt8(), Charsets.UTF_8);
    }
}
