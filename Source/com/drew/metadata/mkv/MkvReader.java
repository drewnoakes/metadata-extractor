/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;
import com.drew.lang.Charsets;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads the supported metadata subset of Matroska and WebM without buffering
 * media payloads. See RFC 8794 and https://www.matroska.org/technical/elements.html.
 */
public class MkvReader
{
    private enum ValueType
    {
        INTEGER, FLOAT, STRING, UTF8, BINARY
    }

    @NotNull
    private static final Map<Integer, ValueType> _elements = new HashMap<Integer, ValueType>();

    static
    {
        addElements(ValueType.INTEGER,
            ElementIDs.EBML_VERSION, ElementIDs.EBML_READ_VERSION, ElementIDs.EBML_MAX_ID_LENGTH,
            ElementIDs.EBML_MAX_SIZE_LENGTH, ElementIDs.DOCTYPE_VERSION, ElementIDs.DOCTYPE_READ_VERSION,
            ElementIDs.TIMESTAMP_SCALE, ElementIDs.TRACK_NUMBER, ElementIDs.TRACK_UID,
            ElementIDs.TRACK_TYPE, ElementIDs.TAG_LACING, ElementIDs.CHANNELS, ElementIDs.BIT_DEPTH,
            ElementIDs.DEFAULT_DURATION, ElementIDs.DISPLAY_WIDTH, ElementIDs.DISPLAY_HEIGHT,
            ElementIDs.DISPLAY_UNIT, ElementIDs.PIXEL_WIDTH, ElementIDs.PIXEL_HEIGHT,
            ElementIDs.FLAG_INTERLACED, ElementIDs.TRANSFER_CHARACTERISTICS,
            ElementIDs.MATRIX_COEFFICIENTS, ElementIDs.PRIMARIES, ElementIDs.RANGE,
            ElementIDs.CHROMA_SITING_HORZ, ElementIDs.CHROMA_SITING_VERT,
            ElementIDs.CODEC_DELAY, ElementIDs.SEEK_PRE_ROLL, ElementIDs.TAG_TRACK_UID,
            ElementIDs.TARGET_TYPE_VALUE, ElementIDs.TAG_EDITION_UID, ElementIDs.TAG_CHAPTER_UID,
            ElementIDs.TAG_ATTACHMENT_UID, ElementIDs.TAG_DEFAULT);
        addElements(ValueType.STRING, ElementIDs.DOCTYPE, ElementIDs.CODEC_ID, ElementIDs.LANGUAGE,
            ElementIDs.LANGUAGE_BCP47, ElementIDs.TAG_LANGUAGE, ElementIDs.TAG_LANGUAGE_BCP47, ElementIDs.TARGET_TYPE);
        addElements(ValueType.UTF8, ElementIDs.MUXING_APP, ElementIDs.WRITING_APP, ElementIDs.TAG_NAME, ElementIDs.TAG_STRING);
        addElements(ValueType.FLOAT, ElementIDs.DURATION, ElementIDs.SAMPLING_FREQUENCY);
        addElements(ValueType.BINARY, ElementIDs.SEGMENT_UUID, ElementIDs.TAG_BINARY);
    }

    private static void addElements(ValueType type, int... ids)
    {
        for (int id : ids)
            _elements.put(id, type);
    }

    public void extract(@NotNull final SequentialReader reader, @NotNull Metadata metadata) throws IOException
    {
        new Parser(reader, metadata, false).extract();
    }

    public void extractHeader(@NotNull final SequentialReader reader, @NotNull Metadata metadata) throws IOException
    {
        new Parser(reader, metadata, true).extract();
    }

    private static class Context
    {
        final int id;
        final long end;
        final boolean unknownSize;
        final Directory directory;
        int tagUpdateCount;

        Context(int id, long end, boolean unknownSize, Directory directory)
        {
            this.id = id;
            this.end = end;
            this.unknownSize = unknownSize;
            this.directory = directory;
        }
    }

    private static final class TargetsContext extends Context
    {
        final Map<Integer, List<Number>> uids = new LinkedHashMap<Integer, List<Number>>();

        TargetsContext(long end, Directory directory)
        {
            super(ElementIDs.TARGETS, end, false, directory);
        }
    }

    private static final class Parser
    {
        @NotNull private final SequentialReader _reader;
        @NotNull private final Metadata _metadata;
        @NotNull private final Deque<Context> _contexts = new ArrayDeque<Context>();
        @Nullable private EbmlDirectory _header;
        private final boolean _headerOnly;
        private boolean _headerComplete;
        private boolean _validHeader;
        private long _maxSizeLength = 8;

        Parser(SequentialReader reader, Metadata metadata, boolean headerOnly)
        {
            _reader = reader;
            _metadata = metadata;
            _headerOnly = headerOnly;
            _reader.setMotorolaByteOrder(true);
            _contexts.push(new Context(0, -1, true, null));
        }

        void extract() throws IOException
        {
            try {
                process();
            } catch (IOException e) {
                Directory directory = null;
                for (Context context : _contexts) {
                    if (context.directory != null) {
                        directory = context.directory;
                        break;
                    }
                }
                if (directory == null) {
                    if (_header == null) {
                        _header = new EbmlDirectory();
                        _metadata.addDirectory(_header);
                    }
                    directory = _header;
                }
                directory.addError("Error reading Matroska data: "
                    + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
            } finally {
                while (_contexts.size() > 1)
                    closeContext();
            }
        }

        private void process() throws IOException
        {
            while (true) {
                while (_contexts.size() > 1 && _contexts.peek().end == _reader.getPosition())
                    closeContext();
                if (_headerOnly && _headerComplete)
                    return;

                long start = _reader.getPosition();
                int id;
                try {
                    id = DataParser.getElementId(_reader);
                } catch (EOFException e) {
                    if (_reader.getPosition() != start)
                        throw e;
                    for (Context context : _contexts) {
                        if (context.end >= 0 && start < context.end)
                            throw new EOFException("Unexpected end of an EBML master element.");
                    }
                    break;
                }

                endUnknownContexts(id);
                Context parent = _contexts.peek();
                if (parent.end >= 0 && _reader.getPosition() >= parent.end)
                    throw new IOException("EBML element header exceeds its parent boundary.");
                long sizeStart = _reader.getPosition();
                long size = DataParser.getElementSize(_reader);
                long position = _reader.getPosition();
                if (_headerComplete && id != ElementIDs.EBML_HEADER_ELEMENT
                    && position - sizeStart > _maxSizeLength)
                    throw new IOException("EBML element size exceeds EBMLMaxSizeLength.");
                if (parent.end >= 0 && (position > parent.end || size > parent.end - position))
                    throw new IOException("EBML element exceeds its parent boundary.");
                if (size > Long.MAX_VALUE - position)
                    throw new IOException("EBML element size overflows its position.");
                if (size < 0 && id != ElementIDs.SEGMENT && id != ElementIDs.CLUSTER)
                    throw new IOException("Unknown size is only supported for Segment and Cluster elements.");

                long end = size < 0 ? parent.end : position + size;
                Context child = createContext(id, end, size < 0, parent);
                if (child != null) {
                    _contexts.push(child);
                    continue;
                }
                if (size < 0)
                    throw new IOException("Unexpected unknown-sized EBML element.");

                ValueType element = _elements.get(id);
                if (parent.directory != null && accepts(parent, id) && element != null) {
                    if (parent instanceof TargetsContext && isTargetUid(id)) {
                        TargetsContext targets = (TargetsContext)parent;
                        List<Number> uids = targets.uids.get(id);
                        if (uids == null) {
                            uids = new ArrayList<Number>();
                            targets.uids.put(id, uids);
                        }
                        uids.add(DataParser.getUnsignedInteger(_reader, size));
                        continue;
                    }
                    Object value = readValue(id, size, element);
                    if (value != null) {
                        if (parent.directory.containsTag(id))
                            parent.directory.addError(String.format("Duplicate EBML element 0x%X.", id));
                        parent.directory.setObject(id, value);
                    }
                } else {
                    skip(size);
                }
            }
        }

        private void endUnknownContexts(int id)
        {
            Context context = _contexts.peek();
            if (context.id == ElementIDs.CLUSTER && context.unknownSize
                && (isSegmentChild(id) || id == ElementIDs.EBML_HEADER_ELEMENT || id == ElementIDs.SEGMENT)) {
                closeContext();
                context = _contexts.peek();
            }
            if (context.id == ElementIDs.SEGMENT && context.unknownSize
                && (id == ElementIDs.EBML_HEADER_ELEMENT || id == ElementIDs.SEGMENT))
                closeContext();
        }

        private static boolean isSegmentChild(int id)
        {
            return id == ElementIDs.SEEK_HEAD || id == ElementIDs.SEGMENT_INFO
                || id == ElementIDs.TRACKS || id == ElementIDs.CLUSTER || id == ElementIDs.CUES
                || id == ElementIDs.TAGS || id == 0x1941A469 || id == 0x1043A770;
        }

        private Context createContext(int id, long end, boolean unknownSize, Context parent) throws IOException
        {
            Directory directory;
            if (parent.id == 0 && id == ElementIDs.EBML_HEADER_ELEMENT) {
                _header = new EbmlDirectory();
                _headerComplete = false;
                _validHeader = false;
                _metadata.addDirectory(_header);
                return new Context(id, end, unknownSize, _header);
            }
            if (parent.id == 0 && id == ElementIDs.SEGMENT) {
                if (!_validHeader)
                    throw new IOException("A valid Matroska or WebM EBML header is required before Segment.");
                directory = new MkvSegmentDirectory();
                if (_header != null)
                    directory.setParent(_header);
                _metadata.addDirectory(directory);
                return new Context(id, end, unknownSize, directory);
            }
            if ((parent.id == ElementIDs.SEGMENT && (id == ElementIDs.SEGMENT_INFO || id == ElementIDs.TRACKS))
                || (parent.id == ElementIDs.SEGMENT && id == ElementIDs.CLUSTER && unknownSize)
                || (parent.id == ElementIDs.VIDEO && id == ElementIDs.COLOR))
                return new Context(id, end, unknownSize, parent.directory);
            if (parent.id == ElementIDs.SEGMENT && id == ElementIDs.TAGS) {
                directory = new MkvTagsDirectory();
                directory.setInt(MkvTagsDirectory.TAG_UPDATE_NUMBER, ++parent.tagUpdateCount);
                _metadata.addDirectory(directory);
            } else if (parent.id == ElementIDs.TAGS && id == ElementIDs.TAG) {
                directory = new MkvTagDirectory();
                _metadata.addDirectory(directory);
            } else if (parent.id == ElementIDs.TAG && id == ElementIDs.TARGETS) {
                return new TargetsContext(end, parent.directory);
            } else if ((parent.id == ElementIDs.TAG || parent.id == ElementIDs.SIMPLE_TAG)
                && id == ElementIDs.SIMPLE_TAG) {
                directory = new MkvSimpleTagDirectory();
                _metadata.addDirectory(directory);
            } else if (parent.id == ElementIDs.TRACKS && id == ElementIDs.TRACK_ENTRY)
                directory = new MkvTrackDirectory();
            else if (parent.id == ElementIDs.TRACK_ENTRY && id == ElementIDs.VIDEO)
                directory = new MkvVideoDirectory();
            else if (parent.id == ElementIDs.TRACK_ENTRY && id == ElementIDs.AUDIO)
                directory = new MkvAudioDirectory();
            else
                return null;
            if (parent.directory != null)
                directory.setParent(parent.directory);
            return new Context(id, end, unknownSize, directory);
        }

        private static boolean accepts(Context parent, int id)
        {
            return parent.id != ElementIDs.SEGMENT && parent.id != ElementIDs.TRACKS
                && parent.id != ElementIDs.CLUSTER && parent.id != ElementIDs.TAGS
                && parent.id != ElementIDs.TAG && parent.directory.hasTagName(id);
        }

        private static boolean isTargetUid(int id)
        {
            return id == ElementIDs.TAG_TRACK_UID || id == ElementIDs.TAG_EDITION_UID
                || id == ElementIDs.TAG_CHAPTER_UID || id == ElementIDs.TAG_ATTACHMENT_UID;
        }

        private void closeContext()
        {
            Context context = _contexts.pop();
            Context parent = _contexts.peek();
            if (context instanceof TargetsContext) {
                for (Map.Entry<Integer, List<Number>> entry : ((TargetsContext)context).uids.entrySet())
                    context.directory.setObjectArray(entry.getKey(), entry.getValue().toArray(new Number[0]));
            } else if (context.id == ElementIDs.TAG) {
                applyDefaults(context.directory, ElementIDs.TARGET_TYPE_VALUE);
            } else if (context.id == ElementIDs.SIMPLE_TAG) {
                applyDefaults(context.directory, ElementIDs.TAG_LANGUAGE, ElementIDs.TAG_DEFAULT);
                if (!context.directory.containsTag(ElementIDs.TAG_NAME))
                    context.directory.addError("SimpleTag is missing its TagName.");
                if (context.directory.containsTag(ElementIDs.TAG_STRING)
                    && context.directory.containsTag(ElementIDs.TAG_BINARY))
                    context.directory.addError("SimpleTag must not contain both TagString and TagBinary.");
            } else if (context.id == ElementIDs.EBML_HEADER_ELEMENT) {
                applyDefaults(context.directory, ElementIDs.EBML_VERSION, ElementIDs.EBML_READ_VERSION,
                    ElementIDs.EBML_MAX_ID_LENGTH, ElementIDs.EBML_MAX_SIZE_LENGTH,
                    ElementIDs.DOCTYPE_VERSION, ElementIDs.DOCTYPE_READ_VERSION);
                validateHeader();
            } else if (context.id == ElementIDs.SEGMENT_INFO || context.id == ElementIDs.SEGMENT) {
                applyDefaults(context.directory, ElementIDs.TIMESTAMP_SCALE);
                if (context.id == ElementIDs.SEGMENT) {
                    Double duration = context.directory.getDoubleObject(ElementIDs.DURATION);
                    Double scale = context.directory.getDoubleObject(ElementIDs.TIMESTAMP_SCALE);
                    if (duration != null && scale != null)
                        context.directory.setDouble(MkvSegmentDirectory.TAG_DURATION_SECONDS,
                            duration * scale / 1000000000.0);
                }
            } else if (context.id == ElementIDs.AUDIO) {
                applyDefaults(context.directory, ElementIDs.SAMPLING_FREQUENCY, ElementIDs.CHANNELS);
            } else if (context.id == ElementIDs.VIDEO) {
                applyDefaults(context.directory, ElementIDs.FLAG_INTERLACED, ElementIDs.DISPLAY_UNIT);
            } else if (context.id == ElementIDs.COLOR) {
                applyDefaults(context.directory, ElementIDs.MATRIX_COEFFICIENTS,
                    ElementIDs.TRANSFER_CHARACTERISTICS, ElementIDs.PRIMARIES, ElementIDs.RANGE,
                    ElementIDs.CHROMA_SITING_HORZ, ElementIDs.CHROMA_SITING_VERT);
            }
            if (context.id == ElementIDs.VIDEO || context.id == ElementIDs.AUDIO) {
                copyTags(context.directory, parent.directory);
            } else if (context.id == ElementIDs.TRACK_ENTRY) {
                applyDefaults(context.directory, ElementIDs.TAG_LACING, ElementIDs.LANGUAGE,
                    ElementIDs.CODEC_DELAY, ElementIDs.SEEK_PRE_ROLL);
                Object type = context.directory.getObject(ElementIDs.TRACK_TYPE);
                Directory track = Long.valueOf(1).equals(type) ? new MkvVideoDirectory()
                    : Long.valueOf(2).equals(type) ? new MkvAudioDirectory() : context.directory;
                if (parent.directory != null)
                    track.setParent(parent.directory);
                copyTags(context.directory, track);
                Double duration = track.getDoubleObject(ElementIDs.DEFAULT_DURATION);
                if (track instanceof MkvVideoDirectory && duration != null && duration > 0)
                    track.setDouble(MkvVideoDirectory.TAG_FRAME_RATE, 1000000000.0 / duration);
                _metadata.addDirectory(track);
            }
        }

        private static void copyTags(Directory source, Directory destination)
        {
            for (Tag tag : source.getTags()) {
                Object value = source.getObject(tag.getTagType());
                if (value != null)
                    destination.setObject(tag.getTagType(), value);
            }
            if (source != destination) {
                for (String error : source.getErrors())
                    destination.addError(error);
            }
        }

        private void validateHeader()
        {
            _headerComplete = true;
            String docType = _header.getString(ElementIDs.DOCTYPE);
            if (!"matroska".equals(docType) && !"webm".equals(docType))
                _header.addError("Unsupported EBML document type: " + docType);
            Long ebmlRead = _header.getLongObject(ElementIDs.EBML_READ_VERSION);
            Long docRead = _header.getLongObject(ElementIDs.DOCTYPE_READ_VERSION);
            Long maxId = _header.getLongObject(ElementIDs.EBML_MAX_ID_LENGTH);
            Long maxSize = _header.getLongObject(ElementIDs.EBML_MAX_SIZE_LENGTH);
            if (!Long.valueOf(1).equals(ebmlRead))
                _header.addError("Unsupported EBML read version.");
            if (docRead == null || docRead < 1 || docRead > 4)
                _header.addError("Unsupported Matroska document read version.");
            if (!Long.valueOf(4).equals(maxId))
                _header.addError("Matroska requires EBMLMaxIDLength to be four.");
            if (maxSize == null || maxSize < 1 || maxSize > 8)
                _header.addError("Unsupported EBMLMaxSizeLength.");
            else
                _maxSizeLength = maxSize;
            if (!versionAtLeast(ElementIDs.EBML_VERSION, ebmlRead)
                || !versionAtLeast(ElementIDs.DOCTYPE_VERSION, docRead))
                _header.addError("EBML version must not be lower than its read version.");
            _validHeader = !_header.hasErrors();
        }

        private boolean versionAtLeast(int tag, Long minimum)
        {
            Object value = _header.getObject(tag);
            return value instanceof Number && minimum != null
                && new BigInteger(value.toString()).compareTo(BigInteger.valueOf(minimum)) >= 0;
        }

        @Nullable
        private Object readValue(int id, long size, ValueType type) throws IOException
        {
            if (size == 0) {
                Object value = getDefaultValue(id);
                if (value != null)
                    return value;
            }
            switch (type) {
                case INTEGER:
                    return DataParser.getUnsignedInteger(_reader, size);
                case FLOAT:
                    return size == 0 && id == ElementIDs.SAMPLING_FREQUENCY
                        ? 8000.0 : DataParser.getDouble(_reader, size);
                case STRING:
                    return DataParser.getString(_reader, size);
                case UTF8:
                    return DataParser.getString(_reader, size, Charsets.UTF_8);
                case BINARY:
                    return DataParser.getByteArray(_reader, size);
                default:
                    skip(size);
                    return null;
            }
        }

        private static void applyDefaults(Directory directory, int... ids)
        {
            for (int id : ids) {
                if (!directory.containsTag(id)) {
                    Object value = getDefaultValue(id);
                    if (value == null)
                        throw new IllegalArgumentException("No default is defined for EBML element " + id);
                    directory.setObject(id, value);
                }
            }
        }

        @Nullable
        private static Object getDefaultValue(int id)
        {
            switch (id) {
                case ElementIDs.EBML_VERSION:
                case ElementIDs.EBML_READ_VERSION:
                case ElementIDs.DOCTYPE_VERSION:
                case ElementIDs.DOCTYPE_READ_VERSION:
                case ElementIDs.TAG_LACING:
                case ElementIDs.CHANNELS:
                case ElementIDs.TAG_DEFAULT:
                    return 1L;
                case ElementIDs.TARGET_TYPE_VALUE:
                    return 50L;
                case ElementIDs.EBML_MAX_ID_LENGTH:
                    return 4L;
                case ElementIDs.EBML_MAX_SIZE_LENGTH:
                    return 8L;
                case ElementIDs.TIMESTAMP_SCALE:
                    return 1000000L;
                case ElementIDs.SAMPLING_FREQUENCY:
                    return 8000.0;
                case ElementIDs.LANGUAGE:
                    return "eng";
                case ElementIDs.TAG_LANGUAGE:
                    return "und";
                case ElementIDs.MATRIX_COEFFICIENTS:
                case ElementIDs.TRANSFER_CHARACTERISTICS:
                case ElementIDs.PRIMARIES:
                    return 2L;
                case ElementIDs.FLAG_INTERLACED:
                case ElementIDs.DISPLAY_UNIT:
                case ElementIDs.CODEC_DELAY:
                case ElementIDs.SEEK_PRE_ROLL:
                case ElementIDs.RANGE:
                case ElementIDs.CHROMA_SITING_HORZ:
                case ElementIDs.CHROMA_SITING_VERT:
                    return 0L;
                default:
                    return null;
            }
        }

        private void skip(long size) throws IOException
        {
            while (size > 0) {
                long start = _reader.getPosition();
                if (_reader.trySkip(size))
                    return;
                size -= _reader.getPosition() - start;
                if (size > 0) {
                    _reader.getByte();
                    size--;
                }
            }
        }
    }
}
