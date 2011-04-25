// =================================================================================================
// ADOBE SYSTEMS INCORPORATED
// Copyright 2006 Adobe Systems Incorporated
// All Rights Reserved
//
// NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the terms
// of the Adobe license agreement accompanying it.
// =================================================================================================

package com.adobe.xmp;

/**
 * XMP Toolkit Version Information.
 * <p>
 * Version information for the XMP toolkit is stored in the jar-library and available through a
 * runtime call, {@link XMPMetaFactory#getVersionInfo()},  addition static version numbers are
 * defined in "version.properties". 
 * 
 * @since 23.01.2006
 */
public interface XMPVersionInfo
{
	/** @return Returns the primary release number, the "1" in version "1.2.3". */
	int getMajor();


	/** @return Returns the secondary release number, the "2" in version "1.2.3". */
	int getMinor();


	/** @return Returns the tertiary release number, the "3" in version "1.2.3". */
	int getMicro();


	/** @return Returns a rolling build number, monotonically increasing in a release. */
	int getBuild();


	/** @return Returns true if this is a debug build. */
	boolean isDebug();
	
	
	/** @return Returns a comprehensive version information string. */
	String getMessage();
}