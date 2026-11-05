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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Iterates an ISO Base Media File Format (ISOBMFF) box sequence.
 * <p>
 * Handles standard 32-bit sizes, 64-bit large sizes ({@code size == 1}), boxes that extend
 * to the end of their container or input ({@code size == 0}), and {@code uuid} boxes whose
 * 16-byte usertype is parsed into {@link IsoBox#usertype}.
 * <p>
 * Nothing of a box's payload is read unless the caller asks for it: {@link #payload()} gives
 * a reader limited to the payload, {@link #payloadBytes()} reads it into an array, and
 * {@link #children()} iterates the boxes inside it. Whatever is left unread is skipped by the
 * next call to {@link #next()}.
 * <p>
 * Any {@link IOException} means the sequence cannot be read further at this level. The
 * enclosing iterator stays usable: its next call to {@link #next()} skips to the end of the
 * container.
 */
public final class IsoBoxIterator
{
    /** The deepest nesting {@link #children()} allows. */
    public static final int MAX_DEPTH = 64;
    private static final int MAX_EOF_PAYLOAD_SIZE = 16 * 1024 * 1024;

    @NotNull
    private final SequentialReader _reader;
    private final long _end;
    private final int _depth;

    @Nullable
    private IsoBox _box;
    private long _payloadEnd;
    @Nullable
    private SequentialReader _payload;
    private boolean _done;

    /**
     * Iterates the boxes in {@code inputStream} up to its end. The stream is read with
     * Motorola (big-endian) byte order, which is mandatory for ISOBMFF.
     */
    public IsoBoxIterator(@NotNull InputStream inputStream)
    {
        this(motorola(new StreamReader(inputStream)), -1, 0);
    }

    /**
     * Iterates the boxes in {@code bytes} from index {@code from} up to index {@code to}.
     * Box offsets are indexes into {@code bytes}.
     */
    public IsoBoxIterator(@NotNull byte[] bytes, int from, int to)
    {
        this(new SequentialByteArrayReader(bytes, from), to, 0);
    }

    /**
     * Iterates the boxes from the current position of {@code reader} up to the absolute
     * position {@code end}, or to the end of input when {@code end == -1}.
     */
    public IsoBoxIterator(@NotNull SequentialReader reader, long end)
    {
        this(reader, end, 0);
    }

    private IsoBoxIterator(@NotNull SequentialReader reader, long end, int depth)
    {
        _reader = reader;
        _end = end;
        _depth = depth;
    }

    private static SequentialReader motorola(SequentialReader reader)
    {
        reader.setMotorolaByteOrder(true);
        return reader;
    }

    /**
     * Moves to the next box, skipping what is left of the current one.
     *
     * @return the next box, or {@code null} at the end of the sequence
     * @throws IOException if a box header is truncated or invalid, or the input fails
     */
    @Nullable
    public IsoBox next() throws IOException
    {
        if (_done)
            return null;
        if (_box != null) {
            IsoBox previous = _box;
            _box = null;
            _payload = null;
            if (_payloadEnd == -1) {
                _done = true;
                return null;
            }
            long remaining = _payloadEnd - _reader.getPosition();
            if (remaining > 0) {
                _done = true;
                try {
                    _reader.skip(remaining);
                } catch (EOFException e) {
                    throw truncated(previous);
                }
                _done = false;
            }
        }
        long start = _reader.getPosition();
        if (_end != -1 && start >= _end) {
            _done = true;
            return null;
        }
        _done = true;
        IsoBox box;
        try {
            box = new IsoBox(_reader, _end);
        } catch (EOFException e) {
            if (_end == -1 && _reader.getPosition() == start)
                return null;
            throw e;
        }
        if (box.payloadSize < 0 && box.size != -1)
            throw new IOException("Box payload size is negative for type: " + box.type);
        long position = _reader.getPosition();
        long payloadSize = box.payloadSize;
        if (payloadSize != -1 && (payloadSize > Long.MAX_VALUE - position
            || (_end != -1 && payloadSize > _end - position)))
            throw new IOException("Box extends beyond its container: " + box.type);
        _done = false;
        _box = box;
        _payloadEnd = payloadSize == -1 ? -1 : position + payloadSize;
        return box;
    }

    /**
     * A reader over the current box's payload, from wherever reading has got to. Reads past
     * the payload fail; a payload that extends to the end of input has no limit.
     */
    @NotNull
    public SequentialReader payload()
    {
        IsoBox box = current();
        if (_payload == null)
            _payload = new BoundedSequentialReader(_reader, _payloadEnd, box);
        return _payload;
    }

    /**
     * Reads the rest of the current box's payload. The array grows as data arrives, so a
     * declared size larger than the input fails at end of input rather than being allocated.
     *
     * @throws IOException if the payload is larger than {@code Integer.MAX_VALUE} or, when it
     *                     extends to the end of input, larger than 16 MiB
     */
    @NotNull
    public byte[] payloadBytes() throws IOException
    {
        IsoBox box = current();
        SequentialReader payload = payload();
        if (_payloadEnd == -1)
            return readToEnd(payload);
        long remaining = _payloadEnd - _reader.getPosition();
        if (remaining > Integer.MAX_VALUE)
            throw new IOException("Box payload too large to read into memory: " + box.type);
        return payload.getBytes((int) remaining);
    }

    /**
     * Iterates the boxes inside the current box's payload, from wherever reading has got to.
     *
     * @throws IOException if that would nest deeper than {@link #MAX_DEPTH}
     */
    @NotNull
    public IsoBoxIterator children() throws IOException
    {
        current();
        if (_depth >= MAX_DEPTH)
            throw new IOException("Maximum box nesting depth exceeded");
        return new IsoBoxIterator(_reader, _payloadEnd, _depth + 1);
    }

    /** The error for a payload that the input ends inside of. */
    @NotNull
    static EOFException truncated(@NotNull IsoBox box)
    {
        return new EOFException("Box '" + box.type + "' declares " + box.payloadSize
            + " bytes of payload, but the input ends before that");
    }

    @NotNull
    private IsoBox current()
    {
        if (_box == null)
            throw new IllegalStateException("No current box; call next() first");
        return _box;
    }

    @NotNull
    private static byte[] readToEnd(@NotNull SequentialReader reader) throws IOException
    {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        while (true) {
            byte value;
            try {
                value = reader.getByte();
            } catch (EOFException e) {
                return payload.toByteArray();
            }
            if (payload.size() == MAX_EOF_PAYLOAD_SIZE)
                throw new IOException("EOF-terminated box payload exceeds the 16 MiB limit");
            payload.write(value);
        }
    }
}
