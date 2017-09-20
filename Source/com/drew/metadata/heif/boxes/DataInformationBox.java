package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class DataInformationBox extends Box
{

    public DataInformationBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);
    }
}
