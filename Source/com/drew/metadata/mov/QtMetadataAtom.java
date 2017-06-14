package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * Created by pgarland on 6/9/17.
 */
public class QtMetadataAtom extends QtAtom implements QtLeafAtom {

    private String[] keyNames;
    private String[] keyValues;

    public QtMetadataAtom(long size, String type, long offset)
    {
        super(size, type, offset);
    }

    @Override
    public void getMetadata(QtDataSource source) throws IOException {
        byte[] buffer = new byte[4];
        source.reset();
        source.skip(offset + 8);
        int totalLength = 0;

        while (source.pos < (size + offset))
        {
            source.read(buffer);
            int length = ByteUtil.getInt32(buffer, 0, true);

            source.read(buffer);
            String type = new String(buffer);

            if (type.equals("hdlr")) {
            } else if (type.equals("mhdr")) {
                System.out.println("Header");
            } else if (type.equals("keys")) {
                keys(source, length);
            } else if (type.equals("ilst")) {
                list(source, length, (offset + 8 + totalLength));
            }

            totalLength += length;

            source.reset();
            source.skip(offset + 8 + totalLength);
        }

        for (int i = 1; i < keyNames.length; i++) {
            System.out.println(keyNames[i] + ": " + keyValues[i]);
        }

        System.out.println("Meta get source");
    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {
        if (keyNames.length != 0) {
            for (int i = 1; i < keyNames.length; i++) {
                directory.setString(keyNames[i].hashCode(), keyValues[i]);
            }
        }

    }

    public void data(QtDataSource source, int length, int index) throws IOException {
        byte[] buffer = new byte[4];

//        source.read(buffer);
//        int index = ByteUtil.getInt32(buffer, 0, true);

        source.skip(8);

        buffer = new byte[length - 16];
        source.read(buffer);
        keyValues[index] = new String(buffer);
        System.out.println(keyValues[index]);
    }

    public void list(QtDataSource source, long length, long offset) throws IOException {
        byte[] buffer = new byte[4];

        while (source.pos < (length + offset)) {
            source.skip(4);

            source.read(buffer);
            int index = ByteUtil.getInt32(buffer, 0, true);

            source.read(buffer);
            int dataLength = ByteUtil.getInt32(buffer, 0, true);

            source.skip(4);
            data(source, dataLength, index);
        }

    }

    public void keys(QtDataSource source, long length) throws IOException {
        byte[] buffer = new byte[4];
        source.skip(4);

        source.read(buffer);
        int entryCount = ByteUtil.getInt32(buffer, 0, true);

        keyNames = new String[entryCount + 1];
        keyValues = new String[entryCount + 1];

        for (int i = 0; i < entryCount; i++) {
            buffer = new byte[4];
            source.read(buffer);
            int keySize = ByteUtil.getInt32(buffer, 0, true);

            source.read(buffer);
            String keyType = new String(buffer);

            buffer = new byte[keySize - 8];
            source.read(buffer);
            String namespace = new String(buffer);
            keyNames[i + 1] = namespace;
        }
    }
}
