package com.drew.metadata.mp4;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.*;

import java.io.IOException;

/**
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
    public boolean shouldAcceptBox(@NotNull Box box)
    {
        return box.type.equals(Mp4BoxTypes.BOX_FILE_TYPE)
            || box.type.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)
            || box.type.equals(Mp4BoxTypes.BOX_HANDLER)
            || box.type.equals(Mp4BoxTypes.BOX_MEDIA_HEADER);
    }

    @Override
    public boolean shouldAcceptContainer(Box box)
    {
        return box.type.equals(Mp4ContainerTypes.BOX_TRACK)
            || box.type.equals(Mp4ContainerTypes.BOX_USER_DATA)
            || box.type.equals(Mp4ContainerTypes.BOX_METADATA)
            || box.type.equals(Mp4ContainerTypes.BOX_MOVIE)
            || box.type.equals(Mp4ContainerTypes.BOX_MEDIA);
    }

    @Override
    public Mp4Handler processBox(@NotNull Box box, @NotNull byte[] payload) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (box.type.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)) {
                processMovieHeader(reader, box);
            } else if (box.type.equals(Mp4BoxTypes.BOX_FILE_TYPE)) {
                processFileType(reader, box);
            } else if (box.type.equals(Mp4BoxTypes.BOX_HANDLER)) {
                HandlerBox handlerBox = new HandlerBox(reader, box);
                return handlerFactory.getHandler(handlerBox, metadata);
            } else if (box.type.equals(Mp4BoxTypes.BOX_MEDIA_HEADER)) {
                processMediaHeader(reader, box);
            }
        } else {
            if (box.type.equals(Mp4ContainerTypes.BOX_COMPRESSED_MOVIE)) {
                directory.addError("Compressed MP4 movies not supported");
            }
        }
        return this;
    }

    private void processFileType(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        FileTypeBox fileTypeBox = new FileTypeBox(reader, box);
        fileTypeBox.addMetadata(directory);
    }

    private void processMovieHeader(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        MovieHeaderBox movieHeaderBox = new MovieHeaderBox(reader, box);
        movieHeaderBox.addMetadata(directory);
    }

    private void processMediaHeader(@NotNull SequentialReader reader, @NotNull Box box) throws IOException
    {
        MediaHeaderBox mediaHeaderBox = new MediaHeaderBox(reader, box);
    }
}
