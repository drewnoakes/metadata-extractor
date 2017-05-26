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

    public static final int TAG_DSC_VERSION                                 = "%!PS-Adobe-".hashCode();
    public static final int TAG_AUTHOR                                      = "%%Author:".hashCode();
    public static final int TAG_BOUNDING_BOX                                = "%%BoundingBox:".hashCode();
    public static final int TAG_COPYRIGHT                                   = "%%Copyright:".hashCode();
    public static final int TAG_CREATION_DATE                               = "%%CreationDate:".hashCode();
    public static final int TAG_CREATOR                                     = "%%Creator:".hashCode();
    public static final int TAG_FOR                                         = "%%For:".hashCode();
    public static final int TAG_IMAGE_DATA                                  = "%ImageData:".hashCode();
    public static final int TAG_KEYWORDS                                    = "%%Keywords:".hashCode();
    public static final int TAG_MODIFY_DATE                                 = "%%ModDate:".hashCode();
    public static final int TAG_PAGES                                       = "%%Pages:".hashCode();
    public static final int TAG_ROUTING                                     = "%%Routing:".hashCode();
    public static final int TAG_SUBJECT                                     = "%%Subject:".hashCode();
    public static final int TAG_TIFF_PREVIEW                                = "TIFFPreview".hashCode();
    public static final int TAG_TIFF_PREVIEW_BYTES                          = "TIFFPreviewBytes".hashCode();
    public static final int TAG_WMF_PREVIEW                                 = "WMFPreview".hashCode();
    public static final int TAG_WMF_PREVIEW_BYTES                           = "WMFPreviewBytes".hashCode();
    public static final int TAG_TITLE                                       = "%%Title:".hashCode();
    public static final int TAG_VERSION                                     = "%%Version:".hashCode();
    public static final int TAG_DOCUMENT_DATA                               = "%%DocumentData:".hashCode();
    public static final int TAG_EMULATION                                   = "%%Emulation:".hashCode();
    public static final int TAG_EXTENSIONS                                  = "%%Extensions:".hashCode();
    public static final int TAG_LANGUAGE_LEVEL                              = "%%LanguageLevel:".hashCode();
    public static final int TAG_ORIENTATION                                 = "%%Orientation:".hashCode();
    public static final int TAG_PAGE_ORDER                                  = "%%PageOrder:".hashCode();
    public static final int TAG_OPERATOR_INTERNVENTION                      = "%%OperatorIntervention:".hashCode();
    public static final int TAG_OPERATOR_MESSAGE                            = "%%OperatorMessage:".hashCode();
    public static final int TAG_PROOF_MODE                                  = "%%ProofMode:".hashCode();
    public static final int TAG_REQUIREMENTS                                = "%%Requirements:".hashCode();
    public static final int TAG_VM_LOCATION                                 = "%%VMlocation:".hashCode();
    public static final int TAG_VM_USAGE                                    = "%%VMusage:".hashCode();
    public static final int TAG_IMAGE_WIDTH_PX                              = "Image Width".hashCode();
    public static final int TAG_IMAGE_HEIGHT_PX                             = "Image Height".hashCode();
    public static final int TAG_COLOR_TYPE                                  = "Color Type".hashCode();
    public static final int TAG_RAM_SIZE                                    = "Ram Size".hashCode();

    // Section Markers
    public static final int TAG_BEGIN_ICC                                   = "%%BeginICCProfile:".hashCode();
    public static final int TAG_BEGIN_PHOTOSHOP                             = "%BeginPhotoshop:".hashCode();
    public static final int TAG_BEGIN_XML_PACKET                            = "%begin_xml_packet:".hashCode();
    public static final int TAG_BEGIN_BINARY                                = "%%BeginBinary:".hashCode();
    public static final int TAG_BEGIN_DATA                                  = "%%BeginData:".hashCode();
    public static final int TAG_AI9_BEGIN_PRIVATE_DATA                      = "%AI9_PrivateDataBegin".hashCode();
    public static final int TAG_AI9_END_PRIVATE_DATA                        = "%AI9_PrivateDataEnd".hashCode();
    public static final int TAG_END_OF_FILE                                 = "%%EOF".hashCode();
    public static final int TAG_TRAILER                                     = "%%Trailer".hashCode();

//    public static final int TAG_PAGE_BOUNDING_BOX                           = "%%PageBoundingBox:".hashCode();
//    public static final int TAG_PAGE_CUSTOM_COLORS                          = "%%PageCustomColors:".hashCode();
//    public static final int TAG_PAGE_MEDIA                                  = "%%PageMedia:".hashCode();
//    public static final int TAG_PAGE_ORIENTATION                            = "%%PageOrientation:".hashCode();
//    public static final int TAG_PAGE_PROCESS_COLORS                         = "%%PageProcessColors:".hashCode();
//    public static final int TAG_PAGE_REQUIREMENTS                           = "%%PageRequirements:".hashCode();
//    public static final int TAG_PAGE_RESOURCES                              = "%%PageResources:".hashCode();
//    public static final int TAG_BEGIN_EMULATION                             = "%%BeginEmulation:".hashCode();
//    public static final int TAG_BEGIN_PREVIEW                               = "%%BeginPreview:".hashCode();
//    public static final int TAG_PAGE                                        = "%%Page:".hashCode();
//    public static final int TAG_DOCUMENT_MEDIA                              = "%%DocumentMedia:".hashCode();
//    public static final int TAG_DOCUMENT_NEEDED_RESOURCES                   = "%%DocumentNeededResources:".hashCode();
//    public static final int TAG_DOCUMENT_SUPPLIED_RESOURCES                 = "%%DocumentSuppliedResources:".hashCode();
//    public static final int TAG_DOCUMENT_PRINTER_REQUIRED                   = "%%DocumentPrinterRequired:".hashCode();
//    public static final int TAG_DOCUMENT_NEEDED_FILES                       = "%%DocumentNeededFiles:".hashCode();
//    public static final int TAG_DOCUMENT_SUPPLIED_FILES                     = "%%DocumentSuppliedFiles:".hashCode();
//    public static final int TAG_DOCUMENT_FONTS                              = "%%DocumentFonts:".hashCode();
//    public static final int TAG_DOCUMENT_NEEDED_FONTS                       = "%%DocumentNeededFonts:".hashCode();
//    public static final int TAG_DOCUMENT_SUPPLIED_FONTS                     = "%%DocumentSuppliedFonts:".hashCode();
//    public static final int TAG_DOCUMENT_PROC_SETS                          = "%%DocumentProcSets:".hashCode();
//    public static final int TAG_DOCUMENT_NEEDED_PROC_SETS                   = "%%DocumentNeededProcSets:".hashCode();
//    public static final int TAG_DOCUMENT_SUPPLIED_PROC_SETS                 = "%%DocumentSuppliedProcSets:".hashCode();

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_BOUNDING_BOX, "Bounding Box");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_DOCUMENT_DATA, "Document Data");
        _tagNameMap.put(TAG_EMULATION, "Emulation");
        _tagNameMap.put(TAG_EXTENSIONS, "Extensions");
        _tagNameMap.put(TAG_LANGUAGE_LEVEL, "Language Level");
        _tagNameMap.put(TAG_ORIENTATION, "Orientation");
        _tagNameMap.put(TAG_PAGE_ORDER, "Page Order");
        _tagNameMap.put(TAG_VERSION, "Version");
        _tagNameMap.put(TAG_BEGIN_BINARY, "Begin Binary");
        _tagNameMap.put(TAG_BEGIN_DATA, "Begin Data");
        _tagNameMap.put(TAG_IMAGE_DATA, "Image Data");
        _tagNameMap.put(TAG_IMAGE_WIDTH_PX, "Image Width (pixels)");
        _tagNameMap.put(TAG_IMAGE_HEIGHT_PX, "Image Height (pixels)");
        _tagNameMap.put(TAG_COLOR_TYPE, "Color Type");
        _tagNameMap.put(TAG_RAM_SIZE, "Ram Size");
        _tagNameMap.put(TAG_BEGIN_PHOTOSHOP, "Begin Photoshop");
        _tagNameMap.put(TAG_BEGIN_ICC, "Begin ICC");
        _tagNameMap.put(TAG_BEGIN_XML_PACKET, "Begin XML Packet");
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
        _tagNameMap.put(TAG_TIFF_PREVIEW, "TIFF Preview Size");
        _tagNameMap.put(TAG_TIFF_PREVIEW_BYTES, "TIFF Preview Bytes");
        _tagNameMap.put(TAG_WMF_PREVIEW, "WMF Preview Size");
        _tagNameMap.put(TAG_WMF_PREVIEW_BYTES, "WMF Preview Bytes");
        _tagNameMap.put(TAG_AI9_BEGIN_PRIVATE_DATA, "AI9 Private Data Begin");
        _tagNameMap.put(TAG_AI9_END_PRIVATE_DATA, "AI9 Private Data End");
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
