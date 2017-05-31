package com.drew.metadata.mov;

import java.io.IOException;

public abstract interface QtLeafAtom {
    public abstract void getMetadata(QtDataSource source) throws IOException;
    public abstract void populateMetadata(FileInfo fileId);
}
