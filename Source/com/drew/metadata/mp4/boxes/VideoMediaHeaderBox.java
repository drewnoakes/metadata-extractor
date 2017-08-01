package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;

public class VideoMediaHeaderBox extends FullBox
{
    int graphicsmode;
    int[] opcolor;

    public VideoMediaHeaderBox(SequentialReader reader) throws IOException
    {
        super(reader);

        graphicsmode = reader.getUInt16();
        opcolor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
        directory.setIntArray(Mp4VideoDirectory.TAG_OPCOLOR, opcolor);

        switch (graphicsmode) {
            case (0x00):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Copy");
                break;
            case (0x40):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Dither copy");
                break;
            case (0x20):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Blend");
                break;
            case (0x24):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Transparent");
                break;
            case (0x100):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha");
                break;
            case (0x101):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Premul white alpha");
                break;
            case (0x102):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Premul black alpha");
                break;
            case (0x104):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Straight alpha blend");
                break;
            case (0x103):
                directory.setString(Mp4VideoDirectory.TAG_GRAPHICS_MODE, "Composition (dither copy)");
                break;
            default:
        }
    }
}
