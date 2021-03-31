package com.drew.metadata.xmp;

import java.io.OutputStream;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.SerializeOptions;
import com.drew.metadata.Metadata;

public class XmpWriter
{
    /**
     * Serializes the XmpDirectory component of <code>Metadata</code> into an <code>OutputStream</code>
     * @param os Destination for the xmp data
     * @param data populated metadata
     * @return serialize success
     */
    public static boolean write(OutputStream os, Metadata data) throws XMPException
    {
        XmpDirectory dir = data.getFirstDirectoryOfType(XmpDirectory.class);
        if (dir == null)
            return false;
        XMPMeta meta = dir.getXMPMeta();
        SerializeOptions so = new SerializeOptions().setOmitPacketWrapper(true);
        XMPMetaFactory.serialize(meta, os, so);
        return true;
    }
}
