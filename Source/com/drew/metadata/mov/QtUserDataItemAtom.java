package com.drew.metadata.mov;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Created by pgarland on 6/9/17.
 */
public class QtUserDataItemAtom extends QtAtom implements QtLeafAtom {

    public QtUserDataItemAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    @Override
    public void getMetadata(QtDataSource source) throws IOException {

    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {

    }
}
