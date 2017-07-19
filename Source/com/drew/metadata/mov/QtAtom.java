package com.drew.metadata.mov;

public class QtAtom
{
    private long size;
    private String fourCC;
    private byte[] payload;

    public QtAtom(long size, String fourCC, byte[] payload) {
        this.size = size;
        this.fourCC = fourCC;
        this.payload = payload;
    }
}
