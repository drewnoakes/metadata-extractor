package com.drew.metadata.exif;

/**
 * Contains tags which are shared across {@link ExifSubIFDDirectory}, {@link ExifIFD0Directory} and {@link ExifThumbnailDirectory}.
 */
public final class ExifCommonDirectoryTags {

	private ExifCommonDirectoryTags() throws UnsupportedOperationException{
		throw new UnsupportedOperationException("not intended for instation");
	}

	/**
	 * Shows the color space of the image data components.
	 * 0 = WhiteIsZero
	 * 1 = BlackIsZero
	 * 2 = RGB
	 * 3 = RGB Palette
	 * 4 = Transparency Mask
	 * 5 = CMYK
	 * 6 = YCbCr
	 * 8 = CIELab
	 * 9 = ICCLab
	 * 10 = ITULab
	 * 32803 = Color Filter Array
	 * 32844 = Pixar LogL
	 * 32845 = Pixar LogLuv
	 * 34892 = Linear Raw
	 */
	public static final int TAG_PHOTOMETRIC_INTERPRETATION = 0x0106;

	/**
	 * The position in the file of raster data.
	 */
	public static final int TAG_STRIP_OFFSETS = 0x0111;

	/**
	 * Each pixel is composed of this many samples.
	 */
	public static final int TAG_SAMPLES_PER_PIXEL = 0x0115;
	/**
	 * When image format is no compression, this value shows the number of bits
	 * per component for each pixel. Usually this value is '8,8,8'.
	 */
	public static final int TAG_BITS_PER_SAMPLE = 0x0102;
	public static final int TAG_ORIENTATION = 0x0112;
	/** The raster is codified by a single block of data holding this many rows. */
	public static final int TAG_ROWS_PER_STRIP = 0x116;
	/** The size of the raster data in bytes. */
	public static final int TAG_STRIP_BYTE_COUNTS = 0x0117;
	public static final int TAG_X_RESOLUTION = 0x011A;
	public static final int TAG_Y_RESOLUTION = 0x011B;
	/**
	 * When image format is no compression YCbCr, this value shows byte aligns of
	 * YCbCr data. If value is '1', Y/Cb/Cr value is chunky format, contiguous for
	 * each subsampling pixel. If value is '2', Y/Cb/Cr value is separated and
	 * stored to Y plane/Cb plane/Cr plane format.
	 */
	public static final int TAG_PLANAR_CONFIGURATION = 0x011C;
	public static final int TAG_RESOLUTION_UNIT = 0x0128;
	public static final int TAG_YCBCR_COEFFICIENTS = 0x0211;
	public static final int TAG_YCBCR_SUBSAMPLING = 0x0212;
	public static final int TAG_YCBCR_POSITIONING = 0x0213;
	public static final int TAG_REFERENCE_BLACK_WHITE = 0x0214;

}
