package com.drew.metadata.mov;

import com.drew.metadata.Directory;
import com.drew.metadata.MetadataException;

import java.io.IOException;

public abstract interface QtLeafAtom {
    public abstract void getMetadata(QtDataSource source) throws IOException;
    public abstract void populateMetadata(Directory directory) throws MetadataException;
}
