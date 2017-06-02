package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class QtCompressedDataSource extends QtDataSource {
    private int decompressedDataSize;
    private byte[] compressedData;

    public QtCompressedDataSource(QtDataSource compressedSource) throws IOException, DataFormatException
    {
        compressedData = getCompressedData(compressedSource);

        data = new byte[decompressedDataSize];

        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);
        inflater.inflate(data);

        inStream = new ByteArrayInputStream(data);
    }


    private byte[] getCompressedData(QtDataSource compressedSource) throws IOException
    {
        byte[] buffer = new byte[4];

        compressedSource.reset();

        compressedSource.skip(8);  // skip moov header type and size
        // multiple unused reads left in for clarity - as
        compressedSource.read(buffer);
//		long cmovSize = ByteUtil.getUnsignedInt32(buffer, 0, true);   // cmov size

        compressedSource.read(buffer);
//		String atomType = new String(buffer);   // cmov type

        compressedSource.read(buffer);
//		long dcomSize = ByteUtil.getUnsignedInt32(buffer, 0, true);  // dcom size

        compressedSource.read(buffer); 			// dcom type
//		atomType = new String(buffer);

        compressedSource.read(buffer);
//		String decompressor = new String(buffer);    // decompressor type - this should be zlib

        compressedSource.read(buffer);
        int compressedDataSize = ByteUtil.getInt32(buffer, 0, true);

        compressedSource.read(buffer);
//		atomType = new String(buffer);   // cmvd type

        compressedSource.read(buffer);
        decompressedDataSize = ByteUtil.getInt32(buffer, 0, true);    // the size of our data after decompression

        buffer = new byte[compressedDataSize];     // the next compressedDataSize bytes are the compressed data

        compressedSource.read(buffer);

        return buffer;
    }

}
