package com.drew.metadata.mov;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.ParseOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.drew.imaging.quicktime.QuickTimeReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class XmpAtomTest {

    @Test
    public void extractXmpTest() throws IOException, XMPException {
        Metadata metadata = new Metadata();
        QuickTimeReader.extract(new FileInputStream("Tests/Data/xmp480qt.mov"), new QuickTimeAtomHandler(metadata));

        String title = "";
        String description = "";
        Set<String> keywords = new HashSet<String>();
        for (Directory d : metadata.getDirectories()) {
            QuickTimeDirectory quickTimeDirectory = (QuickTimeDirectory) d;
            if (quickTimeDirectory.getName().equals("QuickTime")) {
                String xmp = quickTimeDirectory.getString(0x3000);
                XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmp, new ParseOptions().setOmitNormalization(true));
                XMPProperty xmpTitle = xmpMeta.getProperty(XMPConst.NS_PHOTOSHOP, "Headline");
                XMPProperty xmpDescription = xmpMeta.getArrayItem(XMPConst.NS_DC, "description", 1);
                title = xmpTitle != null ? xmpTitle.getValue() : "";
                description = xmpDescription != null ? xmpDescription.getValue() : "";

                int k = 1;
                while (true) {
                    XMPProperty keyword = xmpMeta.getArrayItem(XMPConst.NS_DC, "subject", k++);
                    if (keyword != null) {
                        keywords.add(keyword.getValue());
                    } else {
                        break;
                    }
                }
            }

            assertEquals("Baltic sea timelapse title", title);
            assertEquals("Baltic sea timelapse description", description);
            assertEquals(3, keywords.size());
        }
    }
}
