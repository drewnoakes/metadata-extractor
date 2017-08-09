package com.drew.metadata.xmp.handlers;

import com.adobe.xmp.*;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.metadata.xmp.XmpHandler;

/**
 * @author Payton Garland
 */
public class InddHandler implements XmpHandler
{
    @Override
    public void process(Metadata metadata)
    {
        XmpDirectory directory = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        if (directory == null) {
            directory.addError("No XMP directory found in directory, aborting XMP process method");
            return;
        }

        XMPMeta xmpMeta = directory.getXMPMeta();
        if (xmpMeta == null) {
            directory.addError("No XMP data found in directory, aborting XMP process method");
            return;
        }

        InddXmpDirectory inddXmpDirectory = new InddXmpDirectory();
        metadata.addDirectory(inddXmpDirectory);

        try {
            for (int i = 1; xmpMeta.doesPropertyExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]") == true; i++) {
                if (xmpMeta.doesStructFieldExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/t/pg/", "PageNumber")) {
                    String pageNumber = xmpMeta.getStructField("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/t/pg/", "PageNumber").getValue();
                    inddXmpDirectory.getTagNameMap().put(inddXmpDirectory.currentKey, "[" + i + "]Page Number");
                    inddXmpDirectory.setString(inddXmpDirectory.currentKey, pageNumber);
                    inddXmpDirectory.currentKey++;
                }
                if (xmpMeta.doesStructFieldExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "format")) {
                    String format = xmpMeta.getStructField("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "format").getValue();
                    inddXmpDirectory.getTagNameMap().put(inddXmpDirectory.currentKey, "[" + i + "]Format");
                    inddXmpDirectory.setString(inddXmpDirectory.currentKey, format);
                    inddXmpDirectory.currentKey++;
                }
                if (xmpMeta.doesStructFieldExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "width")) {
                    String width = xmpMeta.getStructField("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "width").getValue();
                    inddXmpDirectory.getTagNameMap().put(inddXmpDirectory.currentKey, "[" + i + "]Width");
                    inddXmpDirectory.setString(inddXmpDirectory.currentKey, width);
                    inddXmpDirectory.currentKey++;
                }
                if (xmpMeta.doesStructFieldExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "height")) {
                    String height = xmpMeta.getStructField("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "height").getValue();
                    inddXmpDirectory.getTagNameMap().put(inddXmpDirectory.currentKey, "[" + i + "]Height");
                    inddXmpDirectory.setString(inddXmpDirectory.currentKey, height);
                    inddXmpDirectory.currentKey++;
                }
                if (xmpMeta.doesStructFieldExist("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "image")) {
                    byte[] image = xmpMeta.getStructField("http://ns.adobe.com/xap/1.0/", "PageInfo[" + i + "]", "http://ns.adobe.com/xap/1.0/g/img/", "image").getValue().getBytes();
                    inddXmpDirectory.getTagNameMap().put(inddXmpDirectory.currentKey, "[" + i + "]Image");
                    inddXmpDirectory.setByteArray(inddXmpDirectory.currentKey, image);
                    inddXmpDirectory.currentKey++;
                }
            }
        } catch (XMPException ignored) {
            ignored.printStackTrace();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
