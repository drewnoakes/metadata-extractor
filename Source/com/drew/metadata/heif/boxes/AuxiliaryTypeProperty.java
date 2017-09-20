package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Payton Garland
 */
public class AuxiliaryTypeProperty extends FullBox
{
    String auxType;

    public AuxiliaryTypeProperty(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        auxType = getZeroTerminatedString((int)box.size - 12, reader);
    }

    private String getZeroTerminatedString(int maxLengthBytes, SequentialReader reader) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < maxLengthBytes; i++) {
            stringBuilder.append((char)reader.getByte());
            if (stringBuilder.charAt(stringBuilder.length() - 1) == 0) {
                break;
            }
        }
        return stringBuilder.toString().trim();
    }
}
