package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.SequentialReader;

import java.io.*;

public class QtDataSource {
    protected byte[] data;
    protected ByteArrayInputStream inStream;
    protected int pos;

    public QtDataSource(InputStream inputStream) throws IOException
    {
        data = getData(inputStream);
        inStream = new ByteArrayInputStream(data);
        pos = 0;
    }

    protected QtDataSource()
    {
        // do nothing
    }

    private byte[] getData(InputStream inputStream) throws IOException
    {
        byte[] buffer = new byte[4];
        //RandomAccessFile qtFile = new RandomAccessFile(file, "r");
        RandomAccessReader qtFile = new RandomAccessStreamReader(inputStream);

        try
        {
            //qtFile.seek(0);

            buffer = qtFile.getBytes(pos, buffer.length);
            pos += buffer.length;
            //qtFile.read(buffer);
            int atomSize = ByteUtil.getInt32(buffer, 0, true);

            //qtFile.read(buffer);
            buffer = qtFile.getBytes(pos, buffer.length);
            pos += buffer.length;

            String atomType = new String(buffer);

            while (!QtAtomTypes.MOVIE_ATOM.equals(atomType) && pos < qtFile.getLength())
            {
                //qtFile.seek(qtFile.getFilePointer() + atomSize - 8);
                pos += (atomSize - 8);

                //qtFile.read(buffer);
                buffer = qtFile.getBytes(pos, buffer.length);
                pos += buffer.length;

                atomSize = ByteUtil.getInt32(buffer, 0, true);
                if (atomSize < 1)
                {
                    throw new RuntimeException("Invalid File structure (non-positive atom size of " + atomSize + ")");
                }

                //qtFile.read(buffer);
                buffer = qtFile.getBytes(pos, buffer.length);
                pos += buffer.length;
                atomType = new String(buffer);
            }

            if ((QtAtomTypes.MOVIE_ATOM).equals(atomType))
            {
                //qtFile.seek(qtFile.getFilePointer() - 8);
                pos -= 8;
                buffer = new byte[atomSize];
                //qtFile.read(buffer);
                buffer = qtFile.getBytes(pos, buffer.length);
            }
            else
            {
                buffer = new byte[0];
            }
        }
        finally
        {
//            try
//            {
//                qtFile.close();
//            }
//            catch (Exception ignored)
//            {
//            }
        }

        return buffer;
    }

    public void read(byte[] buffer) throws IOException
    {
        pos += buffer.length;
        inStream.read(buffer);
    }

    public int readByte()
    {
        pos += 1;
        return inStream.read();
    }

    public void reset()
    {
        pos = 0;
        inStream.reset();
    }

    public int getLength()
    {
        return data.length;
    }

    public void close() throws IOException
    {
        inStream.close();
    }

    public long skip(long index)
    {
        pos += index;
        return inStream.skip(index);
    }
}
