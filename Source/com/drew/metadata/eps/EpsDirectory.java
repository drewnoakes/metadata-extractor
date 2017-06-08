package com.drew.metadata.eps;


import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class EpsDirectory extends Directory
{
    /**
     * Sources: https://www-cdf.fnal.gov/offline/PostScript/5001.PDF
     *          http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/PostScript.html
     */

    public static final int TAG_DSC_VERSION                                 = 1;
    public static final int TAG_AUTHOR                                      = 2;
    public static final int TAG_BOUNDING_BOX                                = 3;
    public static final int TAG_COPYRIGHT                                   = 4;
    public static final int TAG_CREATION_DATE                               = 5;
    public static final int TAG_CREATOR                                     = 6;
    public static final int TAG_FOR                                         = 7;
    public static final int TAG_IMAGE_DATA                                  = 8;
    public static final int TAG_KEYWORDS                                    = 9;
    public static final int TAG_MODIFY_DATE                                 = 10;
    public static final int TAG_PAGES                                       = 11;
    public static final int TAG_ROUTING                                     = 12;
    public static final int TAG_SUBJECT                                     = 13;
    public static final int TAG_TITLE                                       = 14;
    public static final int TAG_VERSION                                     = 15;
    public static final int TAG_DOCUMENT_DATA                               = 16;
    public static final int TAG_EMULATION                                   = 17;
    public static final int TAG_EXTENSIONS                                  = 18;
    public static final int TAG_LANGUAGE_LEVEL                              = 19;
    public static final int TAG_ORIENTATION                                 = 20;
    public static final int TAG_PAGE_ORDER                                  = 21;
    public static final int TAG_OPERATOR_INTERNVENTION                      = 22;
    public static final int TAG_OPERATOR_MESSAGE                            = 23;
    public static final int TAG_PROOF_MODE                                  = 24;
    public static final int TAG_REQUIREMENTS                                = 25;
    public static final int TAG_VM_LOCATION                                 = 26;
    public static final int TAG_VM_USAGE                                    = 27;
    public static final int TAG_IMAGE_WIDTH                                 = 28;
    public static final int TAG_IMAGE_HEIGHT                                = 29;
    public static final int TAG_COLOR_TYPE                                  = 30;
    public static final int TAG_RAM_SIZE                                    = 31;
    public static final int TAG_TIFF_PREVIEW_SIZE                           = 32;
    public static final int TAG_TIFF_PREVIEW_OFFSET                         = 33;
    public static final int TAG_WMF_PREVIEW_SIZE                            = 34;
    public static final int TAG_WMF_PREVIEW_OFFSET                          = 35;
    public static final int TAG_CONTINUE_LINE                               = 36;

    // Section Markers
//    public static final int TAG_BEGIN_ICC                                   = 37;
//    public static final int TAG_BEGIN_PHOTOSHOP                             = 38;
//    public static final int TAG_BEGIN_XML_PACKET                            = 39;
//    public static final int TAG_BEGIN_BINARY                                = 40;
//    public static final int TAG_BEGIN_DATA                                  = 41;
//    public static final int TAG_AI9_END_PRIVATE_DATA                        = 42;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    @NotNull
    protected static final HashMap<String, Integer> _tagIntegerMap = new HashMap<String, Integer>();

    static {
        _tagIntegerMap.put("%!PS-Adobe-", TAG_DSC_VERSION);
        _tagIntegerMap.put("%%Author", TAG_AUTHOR);
        _tagIntegerMap.put("%%BoundingBox", TAG_BOUNDING_BOX);
        _tagIntegerMap.put("%%Copyright", TAG_COPYRIGHT);
        _tagIntegerMap.put("%%CreationDate", TAG_CREATION_DATE);
        _tagIntegerMap.put("%%Creator", TAG_CREATOR);
        _tagIntegerMap.put("%%For", TAG_FOR);
        _tagIntegerMap.put("%ImageData", TAG_IMAGE_DATA);
        _tagIntegerMap.put("%%Keywords", TAG_KEYWORDS);
        _tagIntegerMap.put("%%ModDate", TAG_MODIFY_DATE);
        _tagIntegerMap.put("%%Pages", TAG_PAGES);
        _tagIntegerMap.put("%%Routing", TAG_ROUTING);
        _tagIntegerMap.put("%%Subject", TAG_SUBJECT);
        _tagIntegerMap.put("%%Title", TAG_TITLE);
        _tagIntegerMap.put("%%Version", TAG_VERSION);
        _tagIntegerMap.put("%%DocumentData", TAG_DOCUMENT_DATA);
        _tagIntegerMap.put("%%Emulation", TAG_EMULATION);
        _tagIntegerMap.put("%%Extensions", TAG_EXTENSIONS);
        _tagIntegerMap.put("%%LanguageLevel", TAG_LANGUAGE_LEVEL);
        _tagIntegerMap.put("%%Orientation", TAG_ORIENTATION);
        _tagIntegerMap.put("%%PageOrder", TAG_PAGE_ORDER);
        _tagIntegerMap.put("%%OperatorIntervention", TAG_OPERATOR_INTERNVENTION);
        _tagIntegerMap.put("%%OperatorMessage", TAG_OPERATOR_MESSAGE);
        _tagIntegerMap.put("%%ProofMode", TAG_PROOF_MODE);
        _tagIntegerMap.put("%%Requirements", TAG_REQUIREMENTS);
        _tagIntegerMap.put("%%VMlocation", TAG_VM_LOCATION);
        _tagIntegerMap.put("%%VMusage", TAG_VM_USAGE);
        _tagIntegerMap.put("Image Width", TAG_IMAGE_WIDTH);
        _tagIntegerMap.put("Image Height", TAG_IMAGE_HEIGHT);
        _tagIntegerMap.put("Color Type", TAG_COLOR_TYPE);
        _tagIntegerMap.put("Ram Size", TAG_RAM_SIZE);
        _tagIntegerMap.put("TIFFPreview", TAG_TIFF_PREVIEW_SIZE);
        _tagIntegerMap.put("TIFFPreviewOffset", TAG_TIFF_PREVIEW_OFFSET);
        _tagIntegerMap.put("WMFPreview", TAG_WMF_PREVIEW_SIZE);
        _tagIntegerMap.put("WMFPreviewOffset", TAG_WMF_PREVIEW_OFFSET);
        _tagIntegerMap.put("%%+", TAG_CONTINUE_LINE);

        _tagNameMap.put(TAG_CONTINUE_LINE, "Line Continuation");
        _tagNameMap.put(TAG_BOUNDING_BOX, "Bounding Box");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_DOCUMENT_DATA, "Document Data");
        _tagNameMap.put(TAG_EMULATION, "Emulation");
        _tagNameMap.put(TAG_EXTENSIONS, "Extensions");
        _tagNameMap.put(TAG_LANGUAGE_LEVEL, "Language Level");
        _tagNameMap.put(TAG_ORIENTATION, "Orientation");
        _tagNameMap.put(TAG_PAGE_ORDER, "Page Order");
        _tagNameMap.put(TAG_VERSION, "Version");
        _tagNameMap.put(TAG_IMAGE_DATA, "Image Data");
        _tagNameMap.put(TAG_IMAGE_WIDTH, "Image Width");
        _tagNameMap.put(TAG_IMAGE_HEIGHT, "Image Height");
        _tagNameMap.put(TAG_COLOR_TYPE, "Color Type");
        _tagNameMap.put(TAG_RAM_SIZE, "Ram Size");
        _tagNameMap.put(TAG_CREATOR, "Creator");
        _tagNameMap.put(TAG_CREATION_DATE, "Creation Date");
        _tagNameMap.put(TAG_FOR, "For");
        _tagNameMap.put(TAG_REQUIREMENTS, "Requirements");
        _tagNameMap.put(TAG_ROUTING, "Routing");
        _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_DSC_VERSION, "DSC Version");
        _tagNameMap.put(TAG_PAGES, "Pages");
        _tagNameMap.put(TAG_OPERATOR_INTERNVENTION, "Operator Intervention");
        _tagNameMap.put(TAG_OPERATOR_MESSAGE, "Operator Message");
        _tagNameMap.put(TAG_PROOF_MODE, "Proof Mode");
        _tagNameMap.put(TAG_VM_LOCATION, "VM Location");
        _tagNameMap.put(TAG_VM_USAGE, "VM Usage");
        _tagNameMap.put(TAG_AUTHOR, "Author");
        _tagNameMap.put(TAG_KEYWORDS, "Keywords");
        _tagNameMap.put(TAG_MODIFY_DATE, "Modify Date");
        _tagNameMap.put(TAG_SUBJECT, "Subject");
        _tagNameMap.put(TAG_TIFF_PREVIEW_SIZE, "TIFF Preview Size");
        _tagNameMap.put(TAG_TIFF_PREVIEW_OFFSET, "TIFF Preview Offset");
        _tagNameMap.put(TAG_WMF_PREVIEW_SIZE, "WMF Preview Size");
        _tagNameMap.put(TAG_WMF_PREVIEW_OFFSET, "WMF Preview Offset");
    }

    public EpsDirectory()
    {
        this.setDescriptor(new EpsDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "EPS";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
