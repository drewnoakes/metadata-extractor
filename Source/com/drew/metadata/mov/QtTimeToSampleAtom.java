package com.drew.metadata.mov;

import java.io.IOException;

public class QtTimeToSampleAtom extends QtAtom implements QtLeafAtom {
    private int sampleDuration;
    private int numberOfEntries;
    private int numberOfSamples;

    public QtTimeToSampleAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    public void getMetadata(QtDataSource source) throws IOException
    {
        byte[] buffer = new byte[4];

        source.reset();

        source.skip(offset + 12);

        source.read(buffer);
        numberOfEntries = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        numberOfSamples = ByteUtil.getInt32(buffer, 0, true);

        source.read(buffer);
        sampleDuration = ByteUtil.getInt32(buffer, 0, true);
    }

    public void populateMetadata(FileInfo fileId)
    {
        double timeScale = 0;

        IntegerValue mediaTimeScale = (IntegerValue)fileId.getMetadata(StandardMetadata.MEDIA_TIMESCALE);

        if (mediaTimeScale != null)
        {
            timeScale = mediaTimeScale.getValue();
        }

        if (sampleDuration != 0)
        {
            double frameRate = timeScale/sampleDuration;
            fileId.addMetadata(StandardMetadata.FRAME_RATE, frameRate);
        }
    }

    public int getSampleDuration()
    {
        return sampleDuration;
    }

    public String toString()
    {
        return new String(type + " (" + size + " bytes)(offset:" + offset + ")\n Number Of Entries: " + numberOfEntries +
            "\n Number of Samples: " + numberOfSamples) +
            "\n SampleDuration: " + sampleDuration;
    }
}
