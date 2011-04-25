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
 * @since   21.09.2006
 */
public interface XMPError
{
	/** */
	int UNKNOWN = 0;
	/** */
	int BADPARAM = 4;
	/** */
	int BADVALUE = 5;
	/** */
	int INTERNALFAILURE = 9;
	/** */
	int BADSCHEMA = 101;
	/** */
	int BADXPATH = 102;
	/** */
	int BADOPTIONS = 103;
	/** */
	int BADINDEX = 104;
	/** */
	int BADSERIALIZE = 107;
	/** */
	int BADXML = 201;
	/** */
	int BADRDF = 202;
	/** */
	int BADXMP = 203;
	/** <em>Note:</em> This is an error code introduced by Java. */
	int BADSTREAM = 204;
}
