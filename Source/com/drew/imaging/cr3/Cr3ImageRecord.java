/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.cr3;

import com.drew.imaging.isobmff.IsoBox;
import com.drew.imaging.isobmff.IsoBoxContext;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.cr3.Cr3Directory;

import java.io.EOFException;
import java.io.IOException;

/**
 * Reads a {@code THMB} or {@code PRVW} record: the dimensions of its image and, when the image
 * data is present, where it lies. The image data starts 16 bytes into the payload, after a
 * header that holds the width, height and length at record-specific positions. It is skipped,
 * or handed to an {@link Cr3MetadataReader.ImageCallback}. A record without usable image data
 * does not replace an earlier one that has it, so the dimensions and the location always
 * describe the same image.
 */
final class Cr3ImageRecord
{
    static final Cr3ImageRecord THUMBNAIL = new Cr3ImageRecord(4, 8,
        Cr3Directory.TAG_THUMBNAIL_WIDTH, Cr3Directory.TAG_THUMBNAIL_HEIGHT,
        Cr3Directory.TAG_THUMBNAIL_OFFSET, Cr3Directory.TAG_THUMBNAIL_LENGTH);
    static final Cr3ImageRecord PREVIEW = new Cr3ImageRecord(6, 12,
        Cr3Directory.TAG_PREVIEW_WIDTH, Cr3Directory.TAG_PREVIEW_HEIGHT,
        Cr3Directory.TAG_PREVIEW_OFFSET, Cr3Directory.TAG_PREVIEW_LENGTH);

    private static final int IMAGE_START = 16;

    private final int _dimensionsAt;
    private final int _lengthAt;
    private final int _widthTag;
    private final int _heightTag;
    private final int _offsetTag;
    private final int _lengthTag;

    private Cr3ImageRecord(int dimensionsAt, int lengthAt, int widthTag, int heightTag, int offsetTag, int lengthTag)
    {
        _dimensionsAt = dimensionsAt;
        _lengthAt = lengthAt;
        _widthTag = widthTag;
        _heightTag = heightTag;
        _offsetTag = offsetTag;
        _lengthTag = lengthTag;
    }

    void read(@NotNull IsoBoxContext context, @NotNull Cr3Directory directory, @Nullable Cr3MetadataReader.ImageCallback callback)
        throws IOException
    {
        IsoBox box = context.box();
        SequentialReader payload = context.payload();
        payload.skip(_dimensionsAt);
        int width = payload.getUInt16();
        int height = payload.getUInt16();
        boolean earlierLocated = directory.containsTag(_offsetTag);
        long length = 0;
        if (box.payloadSize == -1 || box.payloadSize >= IMAGE_START) {
            payload.skip(_lengthAt - _dimensionsAt - 4);
            length = payload.getUInt32();
        }
        boolean fits = length == 0 || box.payloadSize == -1 || length <= box.payloadSize - IMAGE_START;
        if (length == 0 || !fits) {
            if (!fits)
                directory.addError(box.type + " image length " + length + " exceeds its box");
            if (!earlierLocated)
                setDimensions(directory, width, height);
            return;
        }
        try {
            payload.skip(IMAGE_START - _lengthAt - 4);
            long start = payload.getPosition();
            if (callback != null)
                callback.image(context, new ImageReader(payload, start + length));
            long left = start + length - payload.getPosition();
            if (left > 0)
                payload.skip(left);
        } catch (IOException e) {
            if (!earlierLocated)
                setDimensions(directory, width, height);
            throw e;
        }
        setDimensions(directory, width, height);
        directory.setLong(_offsetTag, box.payloadOffset + IMAGE_START);
        directory.setLong(_lengthTag, length);
    }

    private void setDimensions(@NotNull Cr3Directory directory, int width, int height)
    {
        directory.setInt(_widthTag, width);
        directory.setInt(_heightTag, height);
    }

    /** A view of the payload that ends where the image data ends. */
    private static final class ImageReader extends SequentialReader
    {
        @NotNull
        private final SequentialReader _payload;
        private final long _end;

        ImageReader(@NotNull SequentialReader payload, long end)
        {
            _payload = payload;
            _end = end;
            setMotorolaByteOrder(payload.isMotorolaByteOrder());
        }

        private void require(long count) throws IOException
        {
            if (count < 0)
                throw new IllegalArgumentException("count must be zero or greater.");
            if (count > _end - _payload.getPosition())
                throw new EOFException("Read of " + count + " bytes passes the end of the image data");
        }

        @Override
        public long getPosition() throws IOException
        {
            return _payload.getPosition();
        }

        @Override
        public byte getByte() throws IOException
        {
            require(1);
            return _payload.getByte();
        }

        @NotNull
        @Override
        public byte[] getBytes(int count) throws IOException
        {
            require(count);
            return _payload.getBytes(count);
        }

        @Override
        public void getBytes(@NotNull byte[] buffer, int offset, int count) throws IOException
        {
            require(count);
            _payload.getBytes(buffer, offset, count);
        }

        @Override
        public void skip(long n) throws IOException
        {
            require(n);
            _payload.skip(n);
        }

        @Override
        public boolean trySkip(long n) throws IOException
        {
            if (n < 0)
                throw new IllegalArgumentException("n must be zero or greater.");
            long remaining = _end - _payload.getPosition();
            return _payload.trySkip(Math.min(n, remaining)) && n <= remaining;
        }

        @Override
        public int available()
        {
            try {
                return (int) Math.min(_payload.available(), _end - _payload.getPosition());
            } catch (IOException e) {
                return 0;
            }
        }
    }
}
