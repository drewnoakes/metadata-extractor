package com.drew.metadata.heif.boxes;

import com.drew.lang.ByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.HeifDirectory;
import com.drew.metadata.icc.IccReader;

import java.io.IOException;

/**
 * ISO/IEC 14496-12:2015 pg.159
 */
public class ColourInformationBox extends Box
{
    String colourType;
    int colourPrimaries;
    int transferCharacteristics;
    int matrixCoefficients;
    int fullRangeFlag;

    public ColourInformationBox(SequentialReader reader, Box box, Metadata metadata) throws IOException
    {
        super(box);

        colourType = reader.getString(4);
        if (colourType.equals("nclx")) {
            colourPrimaries = reader.getUInt16();
            transferCharacteristics = reader.getUInt16();
            matrixCoefficients = reader.getUInt16();
            // Last 7 bits are reserved
            fullRangeFlag = (reader.getUInt8() & 0x80) >> 7;
        } else if (colourType.equals("rICC")) {
            byte[] buffer = reader.getBytes((int)(size - 12));
            new IccReader().extract(new ByteArrayReader(buffer), metadata);
        } else if (colourType.equals("prof")) {
            byte[] buffer = reader.getBytes((int)(size - 12));
            new IccReader().extract(new ByteArrayReader(buffer), metadata);
        }
    }

    public void addMetadata(HeifDirectory directory)
    {

    }
}
