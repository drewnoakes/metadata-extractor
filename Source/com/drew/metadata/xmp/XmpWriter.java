package com.drew.metadata.xmp;

import java.io.OutputStream;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.SerializeOptions;
import com.drew.metadata.Metadata;

public class XmpWriter
{
    /**
     * Serializes the XmpDirectory component of <code>Metadata</code> into an <code>OutputStream</code>
     * @param os Destination for the xmp data
     * @param data populated metadata
     * @return serialize success
     */
    public static boolean write(OutputStream os, Metadata data)
    {
        XmpDirectory dir = data.getFirstDirectoryOfType(XmpDirectory.class);
        if (dir == null)
            return false;
        XMPMeta meta = dir.getXMPMeta();
        try
        {
            SerializeOptions so = new SerializeOptions().setOmitPacketWrapper(true);
            XMPMetaFactory.serialize(meta, os, so);
        }
        catch (XMPException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
