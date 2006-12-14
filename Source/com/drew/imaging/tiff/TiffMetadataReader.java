/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 *
 * Created by Darren Salomons & Drew Noakes.
 */
package com.drew.imaging.tiff;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;

/**
 * Obtains all available metadata from TIFF formatted files.  Note that TIFF files include many digital camera RAW
 * formats, including Canon (CRW, CR2) and Nikon (NEF).
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
			throw new TiffProcessingException("JPEG file does not exist", e);
		}
		byte[] buffer = new byte[(int)file.length()];
		try {
			x.readFully(buffer);
		} catch (IOException e) {
			throw new TiffProcessingException("Error copying file contents to byte buffer", e);
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
			throw new TiffProcessingException("Error processing tiff stream", e);
		}
        Metadata metadata = new Metadata();
		new ExifReader(out.toByteArray()).extractTiff(metadata);
		return metadata;
	}
}
