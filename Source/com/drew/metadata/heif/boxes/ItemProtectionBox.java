package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class ItemProtectionBox extends FullBox
{
    int protectionCount;
    ArrayList<ProtectionSchemeInfoBox> protectionSchemes;

    public ItemProtectionBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        protectionCount = reader.getUInt16();
        protectionSchemes = new ArrayList<ProtectionSchemeInfoBox>(protectionCount);
        for (int i = 1; i <= protectionCount; i++) {
            protectionSchemes.add(new ProtectionSchemeInfoBox(reader, box));
        }
    }

    class ProtectionSchemeInfoBox extends Box
    {
        public ProtectionSchemeInfoBox(SequentialReader reader, Box box) throws IOException
        {
            super(box);
        }

        class OriginalFormatBox extends Box
        {
            String dataFormat;

            public OriginalFormatBox(SequentialReader reader, Box box) throws IOException
            {
                super(reader);

                dataFormat = reader.getString(4);
            }
        }
    }
}
