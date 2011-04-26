/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.imaging.tiff;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;

import java.io.*;

/**
 * Obtains all available metadata from TIFF formatted files.  Note that TIFF files include many digital camera RAW
 * formats, including Canon (CRW, CR2) and Nikon (NEF).
 *
 * @author Darren Salomons
 */
public class TiffMetadataReader
{
	public static Metadata readMetadata(File file) throws TiffProcessingException
    {
		Metadata metadata = new Metadata();
		
		DataInputStream x;
		try {
			x = new DataInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new TiffProcessingException("TIFF file does not exist", e);
		}
		byte[] buffer = new byte[(int)file.length()];
		try {
			x.readFully(buffer);
		} catch (IOException e) {
			throw new TiffProcessingException("Error copying TIFF file contents to byte buffer", e);
		}
		
		new ExifReader(buffer).extractTiff(metadata);

		return metadata;
	}

	public static Metadata readMetadata(InputStream in) throws TiffProcessingException
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		try {
			while((b = in.read()) != -1) {
				out.write(b);
			}
		} catch (IOException e) {
			throw new TiffProcessingException("Error processing TIFF stream", e);
		}
        Metadata metadata = new Metadata();
		new ExifReader(out.toByteArray()).extractTiff(metadata);
		return metadata;
	}
}
