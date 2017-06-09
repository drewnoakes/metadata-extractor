package com.drew.metadata.mov;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Created by pgarland on 6/9/17.
 */
public class QtUserDataAtom extends QtAtom implements QtLeafAtom {

    public QtUserDataAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    @Override
    public void getMetadata(QtDataSource source) throws IOException {
        byte[] buffer = new byte[4];

        source.reset();
        source.skip(offset);
    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {

    }
}
