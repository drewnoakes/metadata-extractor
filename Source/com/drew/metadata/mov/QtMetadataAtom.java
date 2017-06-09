package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Created by pgarland on 6/9/17.
 */
public class QtMetadataAtom extends QtAtom implements QtLeafAtom {
    public QtMetadataAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }
    @Override
    public void getMetadata(QtDataSource source) throws IOException {
        byte[] buffer = new byte[4];
        source.reset();
        source.skip(offset + 8);

        source.read(buffer);
        int legacyTerminator = ByteUtil.getInt32(buffer, 0, true);
        if (legacyTerminator == 0) {
            offset += 4;
        }
    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {

    }
}
