package com.drew.metadata.xmp;

import com.adobe.xmp.XMPMeta;
import com.drew.metadata.Metadata;

/**
 * @author Payton Garland
 */
public interface XmpHandler
{
    void process(Metadata metadata);
}
