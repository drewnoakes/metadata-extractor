package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;
import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

/**
 * @author Payton Garland
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

        // Loop through entire atom
        while (source.pos < (size + offset))
        {
            // Get length
            source.read(buffer);
            int length = ByteUtil.getInt32(buffer, 0, true);

            // Get atom type
            source.read(buffer);
            String type = new String(buffer);

            // Separate work accordingly
            if (type.equals("hdlr")) {
                // Do nothing, information not relevant
            } else if (type.equals("mhdr")) {
                // Do nothing, information not relevant
            } else if (type.equals("keys")) {
                keys(source, length);
            } else if (type.equals("ilst")) {
                list(source, length, (offset + 8 + totalLength));
            }

            // Skip to end of metadata atom
            totalLength += length;
            source.reset();
            source.skip(offset + 8 + totalLength);
        }
    }

    @Override
    public void populateMetadata(Directory directory) throws MetadataException {
        if (keyNames.length != 0) {
            for (int i = 1; i < keyNames.length; i++) {
                directory.setString(keyNames[i].hashCode(), keyValues[i]);
            }
        }

    }

    /**
     * Adds each individual data atom to the array of values.  Each data atom
     * aligns with a key name.
     *
     * @param source a QtDataSource to pull data from
     * @param length the size of this data atom
     * @param index the current index in the file (and of QtDataSource)
     * @throws IOException
     */
    public void data(QtDataSource source, int length, int index) throws IOException {
        source.skip(8);

        // Read in string to keyValues
        byte[] buffer = buffer = new byte[length - 16];
        source.read(buffer);
        keyValues[index] = new String(buffer);
    }

    /**
     * Gathers atom data from the list of data atoms utilizign theh data method.
     *
     * @param source a QtDataSource to pull data from
     * @param length the size of this data atom
     * @param offset the current offset in the file (and of QtDataSource)
     * @throws IOException
     */
    public void list(QtDataSource source, long length, long offset) throws IOException {
        byte[] buffer = new byte[4];

        // Read through list until end and process each data atom encountered
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

    /**
     * Gathers key name values from keys atom within meta
     *
     * @param source a QtDataSource to pull data from
     * @param length the size of this data atom
     * @throws IOException
     */
    public void keys(QtDataSource source, long length) throws IOException {
        byte[] buffer = new byte[4];
        source.skip(4);

        source.read(buffer);
        int entryCount = ByteUtil.getInt32(buffer, 0, true);

        // Size arrays according to entry count
        keyNames = new String[entryCount + 1];
        keyValues = new String[entryCount + 1];

        // Loop through each entry and get String value
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
