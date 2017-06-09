package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Created by pgarland on 6/9/17.
 */
public class QtUserDataItemAtom extends QtAtom implements QtLeafAtom {

    String text = "";

    public QtUserDataItemAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    @Override
    public void getMetadata(QtDataSource source) throws IOException {
        byte[] buffer = new byte[4];
        source.reset();
        source.read(buffer);

        int length = ByteUtil.getInt32(buffer, 0, true);

        length -= 8;

        source.read(buffer);

        if (new String(buffer).equals("desc")) {

            buffer = new byte[length];

            source.read(buffer);

            text = new String(buffer);
        }

    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {
        if (!text.equals(""))
            directory.setString(QtDirectory.TAG_USER_DATA_TEXT, text);
    }
}

