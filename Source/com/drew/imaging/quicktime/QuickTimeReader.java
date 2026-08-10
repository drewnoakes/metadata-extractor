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
package com.drew.imaging.quicktime;

import com.drew.imaging.FileType;
import com.drew.imaging.tiff.TiffProcessingException;
import com.drew.imaging.tiff.TiffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.exif.makernotes.CanonMakernoteDirectory;
import com.drew.metadata.mov.QuickTimeContainerTypes;
import com.drew.metadata.mov.QuickTimeContext;
import com.drew.metadata.mov.atoms.Atom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Payton Garland
 */
public class QuickTimeReader
{
    // 8 bytes length for an atom header
    private static final long ATOM_HEADER_LENGTH = 8;

    private QuickTimeReader() {}

    public static void extract(@NotNull InputStream inputStream, @NotNull QuickTimeHandler<?> handler, FileType fileType)
    {
        StreamReader reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        QuickTimeContext context = new QuickTimeContext();

        processAtoms(reader, -1, handler, context, fileType);
    }

    private static void processAtoms(StreamReader reader, long atomEnd, QuickTimeHandler<?> handler, QuickTimeContext context, FileType fileType)
    {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                // Determine if fourCC is container/atom and process accordingly.
                // Unknown atoms will be skipped

                if (atom.size > Integer.MAX_VALUE) {
                    handler.addError("Atom size too large.");
                    break;
                }

                if (atom.size < 8) {
                    handler.addError("Atom size too small.");
                    break;
                }

                if (FileType.Crx.equals(fileType) && atom.type.equals(QuickTimeContainerTypes.ATOM_UUID)) {
                    byte[] cr3 = new byte[]{(byte) 0x85, (byte) 0xc0, (byte) 0xb6, (byte) 0x87, (byte) 0x82, 0x0f, 0x11, (byte) 0xe0, (byte) 0x81, 0x11, (byte) 0xf4, (byte) 0xce, 0x46, 0x2b, 0x6a, 0x48};
                    try {
                        byte[] uuid = reader.getBytes(cr3.length);
                        if (Arrays.equals(cr3, uuid)) {
                            processUuidAtoms(reader, atom.size + reader.getPosition() - ATOM_HEADER_LENGTH, handler.processContainer(atom, context));
                        }
                    } catch (IOException ex) {
                        handler.addError("IOException at crx uuid header: " + ex.getMessage());
                    }
                } else if (handler.shouldAcceptContainer(atom)) {
                    processAtoms(reader, atom.size + reader.getPosition() - ATOM_HEADER_LENGTH, handler.processContainer(atom, context), context, fileType);
                } else if (handler.shouldAcceptAtom(atom)) {
                    handler = handler.processAtom(atom, reader.getBytes((int) (atom.size - ATOM_HEADER_LENGTH)), context);
                } else if (atom.size > 8) {
                    reader.skip(atom.size - ATOM_HEADER_LENGTH);
                } else if (atom.size == -1) {
                    break;
                }
            }
        } catch (IOException e) {
            handler.addError(e.getMessage());
        }
    }

    private static void processUuidAtoms(StreamReader reader, long atomEnd, QuickTimeHandler<?> handler) {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);
                // Unknown atoms will be skipped

                if (atom.size > Integer.MAX_VALUE) {
                    handler.addError("Atom size too large.");
                    continue;
                }

                if (atom.size < 8) {
                    handler.addError("Atom size too small.");
                    continue;
                }
                switch (atom.type) {
                    case "CMT1":
                    {
                        QuickTimeTiffHandler tiffHandler = new QuickTimeTiffHandler(ExifIFD0Directory.class, handler.metadata, handler.directory, 0);
                        new TiffReader().processTiff(reader.asByteArrayReader((int) (atom.size - ATOM_HEADER_LENGTH)), tiffHandler, 0);
                        break;
                    }
                    case "CMT2":
                    {
                        QuickTimeTiffHandler tiffHandler = new QuickTimeTiffHandler(ExifSubIFDDirectory.class, handler.metadata, handler.directory, 0);
                        new TiffReader().processTiff(reader.asByteArrayReader((int) (atom.size - ATOM_HEADER_LENGTH)), tiffHandler, 0);
                        break;
                    }
                    case "CMT3":
                    {
                        QuickTimeTiffHandler tiffHandler = new QuickTimeTiffHandler(CanonMakernoteDirectory.class, handler.metadata, handler.directory, 0);
                        new TiffReader().processTiff(reader.asByteArrayReader((int) (atom.size - ATOM_HEADER_LENGTH)), tiffHandler, 0);
                        break;
                    }
                    case "CMT4":
                    {
                        QuickTimeTiffHandler tiffHandler = new QuickTimeTiffHandler(GpsDirectory.class, handler.metadata, handler.directory, 0);
                        new TiffReader().processTiff(reader.asByteArrayReader((int) atom.size - 8), tiffHandler, 0);
                        break;
                    }
                    default:
                        reader.skip(atom.size - ATOM_HEADER_LENGTH);

                }
            }
        } catch (IOException | TiffProcessingException e) {
            handler.addError(e.getMessage());
        }
    }
}
