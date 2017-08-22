package com.drew.metadata.indd;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.indd.structs.ContiguousObjectStreamHeaderOrTrailer;
import com.drew.metadata.indd.structs.MasterPage;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.metadata.xmp.XmpReader;
import com.drew.metadata.xmp.handlers.InddHandler;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class InddReader
{
    public void extract(@NotNull final SequentialReader reader, @NotNull final Metadata metadata)
    {
        try {
            MasterPage masterPage;
            try {
                masterPage = new MasterPage(reader);
            } catch (ImageProcessingException e) {
                return;
            }

            InddDirectory directory = new InddDirectory();
            metadata.addDirectory(directory);
            masterPage.addMetadata(directory);

            reader.skip(4096 * (masterPage.getfFilePages() - 1));

            ContiguousObjectStreamHeaderOrTrailer contiguousObjectStreamHeaderOrTrailer = new ContiguousObjectStreamHeaderOrTrailer(reader);

            long packetSize = reader.getUInt32();

            // 4-Byte size before XMP Packet begins
            XmpReader xmpReader = new XmpReader();
            xmpReader.extract(reader.getBytes((int)packetSize), metadata);

            xmpReader.processXMPMeta(new InddHandler(), metadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
