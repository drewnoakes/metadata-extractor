/*
 * Copyright 2002-2015 Drew Noakes
 * Copyright 2014-2015 Jay Ball
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
 *    
 * More information about the impetus for this class can be found at:
 * 
 *    https://www.veggiespam.com/ils/
 */

package com.drew.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.metadata.xmp.XmpDescriptor;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.iptc.IptcDescriptor;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDirectory;
import com.drew.metadata.exif.makernotes.PanasonicMakernoteDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;


/**
 * Scans images for privacy leakage, such as GPS location or camera serial numbers.
 *
 * @author Jay Ball @veggiespam http://www.veggiespam.com
 * @see http://www.veggiespam.com/ils/ for Whitepaper
 */
public class PrivacyScanner  {

	private static final String EmptyString = "";
	
	/** Tests a Metadata image blob for Location or GPS information and returns each 
	 * instance of image location information as an ArrayList.  If no location 
	 * is detected, the function will return an empty ArrayList.
	 * 
	 * @param md is pre-loaded image Metadata to test, such as a jpeg file.
	 * @return String Array List containing the privacy exposure data or an empty ArrayList indicating exposure detected.
	 */
    public static ArrayList<String>  scanForLocation(Metadata md)   {
    	ArrayList<String> retarr = new ArrayList<String>();
		String prefix  = EmptyString;  // where issue found, consistent format: name_w/o_spaces colon space
		String typefix = "Location ";  // Overall category type.  Location or Privacy (plus space)

		// ** Standard Exif GPS
		prefix = "Exif_GPS: ";
		
		GpsDirectory gpsDir = md.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsDir != null) {
			final GeoLocation geoLocation = gpsDir.getGeoLocation();
			if ( ! (geoLocation == null || geoLocation.isZero()) ) {
				String element = typefix + prefix + geoLocation.toDMSString();
				retarr.add(element);
			}
		}

		// ** IPTC testing
		prefix = "IPTC: ";
		int iptc_tag_list[] = {
			IptcDirectory.TAG_CITY,
			IptcDirectory.TAG_CONTENT_LOCATION_CODE,
			IptcDirectory.TAG_CONTENT_LOCATION_NAME,
			IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_CODE,
			IptcDirectory.TAG_COUNTRY_OR_PRIMARY_LOCATION_NAME,
			IptcDirectory.TAG_DESTINATION,
		};
		
		IptcDirectory iptcDir = md.getFirstDirectoryOfType(IptcDirectory.class);
		if (iptcDir != null) {
			IptcDescriptor iptcDesc = new IptcDescriptor(iptcDir);
			for (int i=0; i< iptc_tag_list.length; i++) {
				String tag = iptcDesc.getDescription(iptc_tag_list[i]);
				if ( ! ( null == tag || tag.equals(EmptyString) )) {
					String element = typefix + prefix + iptcDir.getTagName(iptc_tag_list[i]) + " = " + tag;
					retarr.add(element);
				}
			}
		}

		// ** Proprietary camera: Panasonic / Lumix
		prefix = "Panasonic: ";
		int panasonic_tag_list[] = {
			PanasonicMakernoteDirectory.TAG_CITY,
			PanasonicMakernoteDirectory.TAG_COUNTRY,
			PanasonicMakernoteDirectory.TAG_LANDMARK,
			PanasonicMakernoteDirectory.TAG_LOCATION,
			PanasonicMakernoteDirectory.TAG_STATE,
		};

		PanasonicMakernoteDirectory panasonicDir = md.getFirstDirectoryOfType(PanasonicMakernoteDirectory.class);
		if (panasonicDir != null) {
			PanasonicMakernoteDescriptor panaDesc = new PanasonicMakernoteDescriptor(panasonicDir);
			for (int i=0; i< panasonic_tag_list.length; i++) {
				String tag = panaDesc.getDescription(panasonic_tag_list[i]);
				// Panasonic occationally uses "---" when it cannot find info, we choose to strip it out.
				if ( ! ( null == tag || tag.equals(EmptyString) || tag.equals("---") )) {
					String element = typefix + prefix + panasonicDir.getTagName(panasonic_tag_list[i]) + " = " + tag;
					retarr.add(element);
				}
			}
		}

		return retarr;
    }

	/** Tests a Metadata image blob for other privacy information exposure, like camera
	 * servial number of ages of people in photos and returns each 
	 * instance of information leakage as an ArrayList.  If no privacy exposure
	 * is detected, the function will return an empty ArrayList.
	 * 
	 * @param md is pre-loaded image Metadata to test, such as a jpeg file.
	 * @return String Array List containing the privacy exposure data or an empty ArrayList indicating exposure detected.
	 */
    public static ArrayList<String>  scanForPrivacy(Metadata md)   {
    	ArrayList<String> retarr = new ArrayList<String>();
		String prefix  = EmptyString;  // where issue found, consistent format: name_w/o_spaces colon space
		String typefix = "Privacy ";   // Overall category type.  Location or Privacy (plus space)

		// ** XMP testing
		prefix = "XMP: ";
		int xmp_tag_list[] = {
			XmpDirectory.TAG_CAMERA_SERIAL_NUMBER 
		};
		
		XmpDirectory xmpDir = md.getFirstDirectoryOfType(XmpDirectory.class);
		if (xmpDir != null) {
			XmpDescriptor xmpDesc = new XmpDescriptor(xmpDir);
			for (int i=0; i< xmp_tag_list.length; i++) {
				String tag = xmpDesc.getDescription(xmp_tag_list[i]);
				if ( ! ( null == tag || tag.equals(EmptyString) )) {
					String element = typefix + prefix + xmpDir.getTagName(xmp_tag_list[i]) + " = " + tag;
					retarr.add(element);
				}
			}
		}

		// ** IPTC testing
		prefix = "IPTC: ";
		int iptc_tag_list[] = {
			IptcDirectory.TAG_KEYWORDS,
			IptcDirectory.TAG_LOCAL_CAPTION
			// what about CREDIT   BY_LINE  ...
		};

		IptcDirectory iptcDir = md.getFirstDirectoryOfType(IptcDirectory.class);
		if (iptcDir != null) {
			IptcDescriptor iptcDesc = new IptcDescriptor(iptcDir);
			for (int i=0; i< iptc_tag_list.length; i++) {
				String tag = iptcDesc.getDescription(iptc_tag_list[i]);
				if ( ! ( null == tag || tag.equals(EmptyString) )) {
					String element = typefix + prefix + iptcDir.getTagName(iptc_tag_list[i]) + " = " + tag;
					retarr.add(element);
				}
			}
		}

		// ** Proprietary camera: Panasonic / Lumix
		prefix = "Panasonic: ";
		int panasonic_tag_list[] = {
			PanasonicMakernoteDirectory.TAG_BABY_AGE,
			PanasonicMakernoteDirectory.TAG_BABY_AGE_1,
			PanasonicMakernoteDirectory.TAG_BABY_NAME,
			PanasonicMakernoteDirectory.TAG_INTERNAL_SERIAL_NUMBER,
			PanasonicMakernoteDirectory.TAG_LENS_SERIAL_NUMBER 
			// What about   TAG_TEXT_STAMP_*  TAG_TITLE 
		};

		PanasonicMakernoteDirectory panasonicDir = md.getFirstDirectoryOfType(PanasonicMakernoteDirectory.class);
		if (panasonicDir != null) {
			PanasonicMakernoteDescriptor panaDesc = new PanasonicMakernoteDescriptor(panasonicDir);
			for (int i=0; i< panasonic_tag_list.length; i++) {
				String tag = panaDesc.getDescription(panasonic_tag_list[i]);
				// Panasonic occasionally uses "---" when it cannot find info, we choose to strip it out.
				if ( ! ( null == tag || tag.equals(EmptyString) || tag.equals("---") )) {
					String element = typefix + prefix + panasonicDir.getTagName(panasonic_tag_list[i]) + " = " + tag;
					retarr.add(element);
				}
			}
		}

		return retarr;
    }

    /** Scans for all types of data leakage and combines into one array, currently this is Location and Privacy. */
    public static ArrayList<String> scanForDataLeakage(Metadata md)   {
		ArrayList<String> retarr = new ArrayList<String>();

		retarr = scanForLocation(md);
		retarr.addAll(scanForPrivacy(md));
		
		return retarr;
    }
	
	/** Tests a data blob for data exposure and returns the image location
	 * information as an ArrayList.  If no location is present or there is an error,
	 * the function will return an empty string of "".  This function will eat the Stream
	 * and image loading exceptions without returning an error.
	 * 
	 * @param data is a byte array that is an image file to test, such as entire jpeg file.
	 * @return String containing the Location data or an empty String indicating no GPS data found.
	 */
    public static ArrayList<String> scanImagePrivacy(byte[] data)   {
		ArrayList<String> retarr = new ArrayList<String>();

		try {
			BufferedInputStream is = new BufferedInputStream(new ByteArrayInputStream(data, 0, data.length));
			Metadata md = ImageMetadataReader.readMetadata(is);

			retarr = scanForDataLeakage(md);

    	} catch (ImageProcessingException e) {
    		// bad image, just ignore processing exceptions
    		// DEBUG: return new String("ImageProcessingException " + e.toString());
    	} catch (IOException e) {
    		// bad file or something, just ignore 
    		// DEBUG: return new String("IOException " + e.toString());
    	}

    	return retarr;
	}
    
	/** Convenience function that produces a pretty list from the array list with prefix/suffix to each line.  
	 * Hint: end-line can be \r\n and also HTML codes like br.  Startline might be "    " or HTML table cells.  Etc.
	 * 
	 * @param list to concatenate.
	 * @param startline is at the start. 
	 * @param endline appended to each line
	 * @return String version of the array list.
	 */
    public static String makeListToString(ArrayList<String> list, String startline, String endline) {
    	StringBuffer ret = new StringBuffer(500);
    	if (null == startline) {
    		startline = EmptyString;
    	}
    	if (null == endline) {
    		endline = EmptyString;
    	}
    	for (String s : list) {
    		ret.append(startline);
    		ret.append(s);
    		ret.append(endline);
    	}
    	return ret.toString();
    }
    

    /** A static main function that scans images on the command line.  */
    public static void main(String[] args) throws Exception {
    	if (args.length == 0){
    		System.out.println("Metadata Extractor Image Privacy Scanner");
    		System.out.println("Usage: java com.drew.metadata.PrivacyScanner file1.jpg file2.png file3.tiff [...]");
    		System.out.println("   This scans for both location and privacy leaks in images.");
    		return;
    	}
    	for (String s: args) {
            try {
				System.out.println("Processing " + s + " : ");

				File f = new File(s);
				FileInputStream fis = new FileInputStream(f);
				long size = f.length();
				byte[] data = new byte[(int) size];
				fis.read(data);
				fis.close();
				
				ArrayList<String>  resarr = scanImagePrivacy(data);
				String res = makeListToString(resarr, "    ", "\n");
				System.out.println(res);
        	} catch (IOException e) {
        		System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
}
