package com.drew.metadata.mp4;

import com.adobe.internal.xmp.XMPConst;
import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.ParseOptions;
import com.adobe.internal.xmp.properties.XMPProperty;
import com.drew.imaging.mp4.Mp4Reader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import static com.drew.metadata.mp4.media.Mp4UuidBoxDirectory.TAG_USER_DATA;
import static com.drew.metadata.mp4.media.Mp4UuidBoxDirectory.TAG_UUID;
import static org.junit.Assert.assertEquals;

public class UuidBoxTest {

    @Test
    public void extractUuidBox() throws FileNotFoundException, XMPException
    {
        //https://wwwimages2.adobe.com/content/dam/acom/en/devnet/xmp/pdfs/XMP%20SDK%20Release%20cc-2016-08/XMPSpecificationPart3.pdf
        //1.2.7.1 Placement of XMP
        String XMP = "be7acfcb-97a9-42e8-9c71-999491e3afac";

        Metadata metadata = new Metadata();
        Mp4Reader.extract(new FileInputStream("Tests/Data/uuid540.mp4"), new Mp4BoxHandler(metadata));

        String title = "";
        String description = "";
        Set<String> keywords = new HashSet<String>();
        for (Directory d : metadata.getDirectories()) {
            if (d instanceof Mp4Directory) {
                Mp4Directory mp4Directory = (Mp4Directory) d;
                if (mp4Directory.getName().equals("UUID")) {
                    if (XMP.equals(mp4Directory.getString(TAG_UUID))) {
                        String xmp = new String(mp4Directory.getByteArray(TAG_USER_DATA));
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
                }
            }
        }

        assertEquals("Title", title);
        assertEquals("Description", description);
        assertEquals(3, keywords.size());
    }
}
