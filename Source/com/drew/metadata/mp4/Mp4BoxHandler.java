package com.drew.metadata.mp4;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.*;

import java.io.IOException;

/**
 * Source: http://l.web.umkc.edu/lizhu/teaching/2016sp.video-communication/ref/mp4.pdf
 *
 * @author Payton Garland
 */
public class Mp4BoxHandler extends Mp4Handler<Mp4Directory>
{
    private Mp4HandlerFactory handlerFactory = new Mp4HandlerFactory(this);

    public Mp4BoxHandler(Metadata metadata)
    {
        super(metadata);
    }

    @Override
    protected Mp4Directory getDirectory()
    {
        return new Mp4Directory();
    }

    @Override
    public boolean shouldAcceptAtom(@NotNull String fourCC)
    {
        return fourCC.equals(Mp4BoxTypes.BOX_FILE_TYPE)
            || fourCC.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)
            || fourCC.equals(Mp4BoxTypes.BOX_HANDLER)
            || fourCC.equals(Mp4BoxTypes.BOX_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(String fourCC)
    {
        return fourCC.equals(Mp4ContainerTypes.BOX_TRACK)
            || fourCC.equals(Mp4ContainerTypes.BOX_USER_DATA)
            || fourCC.equals(Mp4ContainerTypes.BOX_METADATA)
            || fourCC.equals(Mp4ContainerTypes.BOX_MOVIE)
            || fourCC.equals(Mp4ContainerTypes.BOX_MEDIA);
    }

    @Override
    public Mp4Handler processAtom(@NotNull String fourCC, @NotNull byte[] payload) throws IOException
    {
        SequentialReader reader = new SequentialByteArrayReader(payload);
        if (fourCC.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)) {
            processMovieHeader(directory, new SequentialByteArrayReader(payload));
        } else if (fourCC.equals(Mp4BoxTypes.BOX_FILE_TYPE)) {
            processFileType(directory, reader, payload.length);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_HANDLER)) {
            BoxHandler box = new BoxHandler(reader, baseAtom);
            return handlerFactory.getHandler(box, metadata);
        } else if (fourCC.equals(Mp4BoxTypes.BOX_MEDIA_HEADER)) {
            processMediaHeader(new SequentialByteArrayReader(payload));
        }
        return this;
    }

    @Override
    public Mp4Handler processContainer(String fourCC)
    {
        if (fourCC.equals(Mp4ContainerTypes.BOX_COMPRESSED_MOVIE)) {
            directory.addError("Compressed QuickTime movies not supported");
        }
        return this;
    }

    /**
     * Extracts data from the 'ftyp' atom
     * Index 0 is after size and type
     */
    private void processFileType(@NotNull Mp4Directory directory, @NotNull SequentialReader reader, @NotNull long size) throws IOException
    {
        BoxFileType box = new BoxFileType(reader, baseAtom);
        box.addMetadata(directory);
    }

    /**
     * Extracts data from the 'moov' atom's movie header marked by the fourCC 'mvhd'
     */
    private void processMovieHeader(@NotNull Mp4Directory directory, @NotNull SequentialReader reader) throws IOException
    {
        BoxHeaderMovie box = new BoxHeaderMovie(reader, baseAtom);
        box.addMetadata(directory);
    }

    private void processMediaHeader(@NotNull SequentialReader reader) throws IOException
    {
        BoxHeaderMedia box = new BoxHeaderMedia(reader, baseAtom);
    }
}
