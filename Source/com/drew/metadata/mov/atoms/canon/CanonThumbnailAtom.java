package com.drew.metadata.mov.atoms.canon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentData;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.atoms.Atom;

public class CanonThumbnailAtom extends Atom {

	private String dateTime;

	public CanonThumbnailAtom(SequentialReader reader) throws IOException {
		super(reader);
		readCNDA(reader);
	}

	// public CanonThumbnailAtom(Atom atom) {
	// super(atom);
	// // TODO Auto-generated constructor stub
	// }

	/**
	 * Canon Data Block (Exif/TIFF ThumbnailImage)
	 * 
	 * @param reader
	 * @throws IOException
	 */
	private void readCNDA(SequentialReader reader) throws IOException {
		// Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.INFO,
		// cndaAtom.type + " " + cndaAtom.size + " " + reader.getPosition());

		if (this.type.compareTo("CNDA") == 0) {
			// Taken From JpegMetadataReader
			JpegSegmentMetadataReader exifReader = new ExifReader();
			InputStream exifStream = new ByteArrayInputStream(reader.getBytes((int) this.size));
			Set<JpegSegmentType> segmentTypes = new HashSet<JpegSegmentType>();
			for (JpegSegmentType type : exifReader.getSegmentTypes()) {
				segmentTypes.add(type);
			}
			JpegSegmentData segmentData = null;
			try {
				segmentData = JpegSegmentReader.readSegments(new StreamReader(exifStream), segmentTypes);
			} catch (JpegProcessingException e) {
				Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.SEVERE,
						e.getLocalizedMessage());
				e.printStackTrace();
			}

			Metadata metadata = new Metadata();
			for (JpegSegmentType segmentType : exifReader.getSegmentTypes()) {
				exifReader.readJpegSegments(segmentData.getSegments(segmentType), metadata, segmentType);
			}

			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			for (Tag tag : directory.getTags()) {
//				 Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.INFO,
//				 "'"+tag.getTagName()+"' ("+tag.getTagType()+")'"+tag.getDescription()+"'");
				if (tag.getTagType() == ExifDirectoryBase.TAG_DATETIME) {
//					Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.INFO,
//							tag.getDescription());
					dateTime = tag.getDescription();
				}
			}
		} else {
			Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.WARNING, "Not CNDA: " + this.type+ " ("+this.size + ")" );
		}

	}

	public void addMetadata(QuickTimeDirectory directory) {
//		Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName()).log(Level.INFO,
//				"\t+CanonThumbnailAtom_addMetadata: " + directory.getName() + " " + dateTime);
		 directory.setString(QuickTimeDirectory.TAG_CANON_THUMBNAIL_DT, dateTime);
	}

}