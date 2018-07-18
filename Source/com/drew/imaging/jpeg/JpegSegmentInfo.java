package com.drew.imaging.jpeg;

public class JpegSegmentInfo {
    public byte[] bytes;
    public long fileOffset;

    public JpegSegmentInfo() {

    }

    public JpegSegmentInfo(byte[] bytes) {
        this.bytes = bytes;
        this.fileOffset = 0;
    }

    public JpegSegmentInfo(byte[] bytes, long fileOffset) {
        this.bytes = bytes;
        this.fileOffset = fileOffset;
    }
}
