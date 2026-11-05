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
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.util.Arrays;

/**
 * One box during a walk by {@link IsoBoxReaders}, or the file itself at the root.
 * <p>
 * Each context holds state keyed by class, visible to the box and everything inside it. A
 * reader stores what later boxes need in the container it belongs to: a {@code hdlr} reader
 * puts the handler type into {@code parent()}, so the rest of that {@code mdia} sees it, and
 * per-file state goes into {@link #root()}.
 */
public final class IsoBoxContext
{
    @Nullable
    private final IsoBoxContext _parent;
    @NotNull
    private final IsoBoxContext _root;
    @Nullable
    private final IsoBox _box;
    @Nullable
    private String _path;
    @NotNull
    private final Metadata _metadata;
    @NotNull
    private final ErrorHandler _errors;
    @Nullable
    private Object[] _values;
    @Nullable
    private SequentialReader _payload;
    @Nullable
    private IsoBoxIterator _iterator;
    @Nullable
    private byte[] _bytes;

    /** Receives the problems of a walk. */
    public interface ErrorHandler
    {
        /** @param cause the exception behind the problem, or {@code null} for a reader's own message */
        void error(@NotNull String message, @Nullable IOException cause);
    }

    /** The context of a file, where per-file state can be stored before a walk. */
    @NotNull
    public static IsoBoxContext root(@NotNull Metadata metadata, @NotNull final Directory errors)
    {
        return root(metadata, new ErrorHandler()
        {
            @Override
            public void error(@NotNull String message, @Nullable IOException cause)
            {
                errors.addError(message);
            }
        });
    }

    /** The context of a file, with problems sent to {@code errors}. */
    @NotNull
    public static IsoBoxContext root(@NotNull Metadata metadata, @NotNull ErrorHandler errors)
    {
        return new IsoBoxContext(metadata, errors);
    }

    private IsoBoxContext(@NotNull Metadata metadata, @NotNull ErrorHandler errors)
    {
        _parent = null;
        _root = this;
        _box = null;
        _path = "";
        _metadata = metadata;
        _errors = errors;
    }

    IsoBoxContext(@NotNull IsoBoxContext parent, @NotNull IsoBox box)
    {
        _parent = parent;
        _root = parent._root;
        _box = box;
        _metadata = parent._metadata;
        _errors = parent._errors;
    }

    /** The box; throws at the root. */
    @NotNull
    public IsoBox box()
    {
        if (_box == null)
            throw new IllegalStateException("The root has no box");
        return _box;
    }

    /** The types from the top level down to this box, separated by {@code /}; for tests. */
    @NotNull
    String path()
    {
        if (_path == null)
            _path = _parent._parent == null ? _box.type : _parent.path() + "/" + _box.type;
        return _path;
    }

    @Nullable
    public IsoBoxContext parent()
    {
        return _parent;
    }

    @NotNull
    public IsoBoxContext root()
    {
        return _root;
    }

    /** The nearest enclosing box of {@code type}, including this one, or {@code null}. */
    @Nullable
    public IsoBoxContext ancestor(@NotNull String type)
    {
        for (IsoBoxContext context = this; context != null; context = context._parent) {
            if (context._box != null && type.equals(context._box.type))
                return context;
        }
        return null;
    }

    @NotNull
    public Metadata metadata()
    {
        return _metadata;
    }

    public void addError(@NotNull String message)
    {
        _errors.error(message, null);
    }

    void addError(@NotNull String message, @NotNull IOException cause)
    {
        _errors.error(message, cause);
    }

    /**
     * The payload, from its start, for the reader being called. Positions start at 0 at the
     * beginning of the payload, whether it is streamed or was read into memory;
     * {@link IsoBox#payloadOffset} says where that is in the input.
     */
    @NotNull
    public SequentialReader payload()
    {
        if (_payload == null)
            throw new IllegalStateException("No payload outside readBox or enterContainer");
        return _payload;
    }

    /**
     * A reader over the next {@code length} bytes of the payload, such as a record's image data,
     * with positions starting at 0. Reading it advances the payload; reads past its end fail.
     */
    @NotNull
    public SequentialReader slice(long length) throws IOException
    {
        SequentialReader payload = payload();
        long start = payload.getPosition();
        return new BoundedSequentialReader(payload, start, start + length, box());
    }

    /**
     * The boxes inside the payload, from wherever reading has got to, for a reader that walks
     * them itself (to take only the first child, for example). Their offsets are input offsets.
     */
    @NotNull
    public IsoBoxIterator children() throws IOException
    {
        if (_iterator != null)
            return _iterator.children();
        if (_bytes != null) {
            int depth = depth() + 1;
            if (depth > IsoBoxIterator.MAX_DEPTH)
                throw new IOException("Maximum box nesting depth exceeded");
            long offset = box().payloadOffset;
            return new IsoBoxIterator(new OffsetByteArrayReader(_bytes, (int) payload().getPosition(), offset), offset + _bytes.length, depth);
        }
        throw new IllegalStateException("No payload outside readBox or enterContainer");
    }

    /** The number of boxes this box is nested in: 0 for a box at the top level. */
    private int depth()
    {
        int depth = -1;
        for (IsoBoxContext context = this; context._parent != null; context = context._parent)
            depth++;
        return depth;
    }

    /** The rest of the payload as an array. */
    @NotNull
    public byte[] payloadBytes() throws IOException
    {
        if (_iterator != null)
            return _iterator.payloadBytes();
        SequentialReader payload = payload();
        return payload.getBytes((int) (_bytes.length - payload.getPosition()));
    }

    void stream(@NotNull IsoBoxIterator iterator) throws IOException
    {
        _iterator = iterator;
        _bytes = null;
        _payload = iterator.directPayload();
    }

    void buffer(@NotNull byte[] bytes)
    {
        _iterator = null;
        _bytes = bytes;
        _payload = new SequentialByteArrayReader(bytes);
    }

    void release()
    {
        _iterator = null;
        _bytes = null;
        _payload = null;
    }

    /** The value stored under {@code key} here or in the nearest enclosing box, or {@code null}. */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull Class<T> key)
    {
        for (IsoBoxContext context = this; context != null; context = context._parent) {
            Object[] values = context._values;
            if (values == null)
                continue;
            for (int i = 0; i < values.length && values[i] != null; i += 2) {
                if (values[i] == key)
                    return (T) values[i + 1];
            }
        }
        return null;
    }

    /** Stores {@code value} on this box, visible to it and everything inside it. */
    public <T> void put(@NotNull Class<T> key, @NotNull T value)
    {
        if (_values == null)
            _values = new Object[4];
        int i = 0;
        while (i < _values.length && _values[i] != null && _values[i] != key)
            i += 2;
        if (i == _values.length)
            _values = Arrays.copyOf(_values, _values.length * 2);
        _values[i] = key;
        _values[i + 1] = value;
    }
}
