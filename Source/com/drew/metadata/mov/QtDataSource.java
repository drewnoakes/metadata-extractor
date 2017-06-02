package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class QtDataSource {
    protected byte[] data;
    protected ByteArrayInputStream inStream;

    public QtDataSource(File file) throws IOException
    {
        data = getData(file);
        inStream = new ByteArrayInputStream(data);
    }

    protected QtDataSource()
    {
        // do nothing
    }

    private byte[] getData(File file) throws IOException
    {
        byte[] buffer = new byte[4];
        RandomAccessFile qtFile = new RandomAccessFile(file, "r");

        try
        {
            qtFile.seek(0);

            qtFile.read(buffer);
            int atomSize = ByteUtil.getInt32(buffer, 0, true);

            qtFile.read(buffer);
            String atomType = new String(buffer);

            while (!QtAtomTypes.MOVIE_ATOM.equals(atomType) && qtFile.getFilePointer() < qtFile.length())
            {
                qtFile.seek(qtFile.getFilePointer() + atomSize - 8);

                qtFile.read(buffer);
                atomSize = ByteUtil.getInt32(buffer, 0, true);
                if (atomSize < 1)
                {
                    throw new RuntimeException("Invalid File structure (non-positive atom size of " + atomSize + ")");
                }

                qtFile.read(buffer);
                atomType = new String(buffer);
            }

            if ((QtAtomTypes.MOVIE_ATOM).equals(atomType))
            {
                qtFile.seek(qtFile.getFilePointer() - 8);
                buffer = new byte[atomSize];
                qtFile.read(buffer);
            }
            else
            {
                buffer = new byte[0];
            }
        }
        finally
        {
            try
            {
                qtFile.close();
            }
            catch (Exception ignored)
            {
            }
        }

        return buffer;
    }

    public void read(byte[] buffer) throws IOException
    {
        inStream.read(buffer);
    }

    public int readByte()
    {
        return inStream.read();
    }

    public void reset()
    {
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
        return inStream.skip(index);
    }
}
