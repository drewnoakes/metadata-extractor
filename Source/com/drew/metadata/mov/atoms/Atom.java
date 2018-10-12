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

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html#//apple_ref/doc/uid/TP40000939-CH203-38190
 *
 * @author Payton Garland
 */
public class Atom
{
    public long size;
    public String type;

    public Atom(SequentialReader reader) throws IOException
    {
        this.size = reader.getUInt32();

        /*
		 * A zero size isn't legal for contained atoms, but Canon uses it to
		 * terminate the CNTH atom (EOS 100D, PowerShot SX30 IS)
		 */
		if (size == 0) {
			// previous Atom was terminated by a 32bit zero block
			size = reader.getUInt32();
		}

        this.type = reader.getString(4);
        if (size == 1) {
            size = reader.getInt64();
        } else if (size == 0) {
            size = -1;
        }
    }

    public Atom(Atom atom)
    {
        this.size = atom.size;
        this.type = atom.type;
    }
}
