package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;
import com.drew.lang.Charsets;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static com.drew.metadata.mkv.EbmlElement.DirectoryType.EBML;
import static com.drew.metadata.mkv.EbmlElement.Type.*;

public class MkvReader
{

    private static final Map<Integer, EbmlElement> ELEMENTS = new HashMap<>();

    static
    {
        // VOID is legit element type in Matroska spec, we're abusing it here to skip parts we don't need
        ELEMENTS.put(ElementIDs.EBML_HEADER_ELEMENT, new EbmlElement("EBML_HEADER", MASTER));
        ELEMENTS.put(ElementIDs.EBML_VERSION, new EbmlElement("EBML_VERSION", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.EBML_READ_VERSION, new EbmlElement("EBML_READ_VERSION", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.EBML_MAX_ID_LENGTH, new EbmlElement("EBML_MAX_ID_LENGTH", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.EBML_MAX_SIZE_LENGTH, new EbmlElement("EBML_MAX_SIZE_LENGTH", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.DOCTYPE, new EbmlElement("DOCTYPE", STRING, EBML));
        ELEMENTS.put(ElementIDs.DOCTYPE_VERSION, new EbmlElement("DOCTYPE_VERSION", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.DOCTYPE_READ_VERSION, new EbmlElement("DOCTYPE_READ_VERSION", INTEGER, EBML));
        ELEMENTS.put(ElementIDs.SEGMENT, new EbmlElement("SEGMENT", MASTER));
        ELEMENTS.put(ElementIDs.SEGMENT_INFO, new EbmlElement("SEGMENT_INFO", MASTER));
        ELEMENTS.put(ElementIDs.SEEK_HEAD, new EbmlElement("SEEK_HEAD", VOID));
        ELEMENTS.put(ElementIDs.SEEK, new EbmlElement("SEEK", MASTER));
        ELEMENTS.put(ElementIDs.SEEK_ID, new EbmlElement("SEEK_ID", BINARY));
        ELEMENTS.put(ElementIDs.SEEK_POSITION, new EbmlElement("SEEK_POSITION", INTEGER));
        ELEMENTS.put(ElementIDs.MUXING_APP, new EbmlElement("MUXING_APP", UTF8));
        ELEMENTS.put(ElementIDs.WRITING_APP, new EbmlElement("WRITING_APP", UTF8));
        ELEMENTS.put(ElementIDs.CODEC_ID, new EbmlElement("CODEC_ID", STRING));
        ELEMENTS.put(ElementIDs.VOID_ELEMENT, new EbmlElement("VOID", VOID));
        ELEMENTS.put(ElementIDs.TIMESTAMP_SCALE, new EbmlElement("TIMESTAMP_SCALE", INTEGER));
        ELEMENTS.put(ElementIDs.DURATION, new EbmlElement("DURATION", FLOAT));
        ELEMENTS.put(ElementIDs.CLUSTER, new EbmlElement("CLUSTER", VOID));
        ELEMENTS.put(ElementIDs.SEGMENT_UUID, new EbmlElement("SEGMENT_UUID", BINARY));
        ELEMENTS.put(ElementIDs.TRACKS, new EbmlElement("TRACKS", MASTER));
        ELEMENTS.put(ElementIDs.TRACK_ENTRY, new EbmlElement("TRACK_ENTRY", MASTER));
        ELEMENTS.put(ElementIDs.TRACK_NUMBER, new EbmlElement("TRACK_NUMBER", INTEGER));
        ELEMENTS.put(ElementIDs.TRACK_UID, new EbmlElement("TRACK_UID", INTEGER));
        ELEMENTS.put(ElementIDs.TRACK_TYPE, new EbmlElement("TRACK_TYPE", INTEGER));
        ELEMENTS.put(ElementIDs.TAG_LACING, new EbmlElement("TAG_LACING", INTEGER));
        ELEMENTS.put(ElementIDs.AUDIO, new EbmlElement("AUDIO", MASTER));
        ELEMENTS.put(ElementIDs.CHANNELS, new EbmlElement("CHANNELS", INTEGER));
        ELEMENTS.put(ElementIDs.SAMPLING_FREQUENCY, new EbmlElement("SAMPLING_FREQUENCY", FLOAT));
        ELEMENTS.put(ElementIDs.BIT_DEPTH, new EbmlElement("BIT_DEPTH", INTEGER));
        ELEMENTS.put(ElementIDs.CODEC_PRIVATE, new EbmlElement("CODEC_PRIVATE", VOID));
        ELEMENTS.put(ElementIDs.CUES, new EbmlElement("CUES", VOID));
        ELEMENTS.put(ElementIDs.LANGUAGE, new EbmlElement("LANGUAGE", STRING));
        ELEMENTS.put(ElementIDs.LANGUAGE_BCP47, new EbmlElement("LANGUAGE_BCP47", STRING));
        ELEMENTS.put(ElementIDs.DEFAULT_DURATION, new EbmlElement("DEFAULT_DURATION", INTEGER));
        ELEMENTS.put(ElementIDs.VIDEO, new EbmlElement("VIDEO", MASTER));
        ELEMENTS.put(ElementIDs.DISPLAY_WIDTH, new EbmlElement("DISPLAY_WIDTH", INTEGER));
        ELEMENTS.put(ElementIDs.DISPLAY_HEIGHT, new EbmlElement("DISPLAY_HEIGHT", INTEGER));
        ELEMENTS.put(ElementIDs.DISPLAY_UNIT, new EbmlElement("DISPLAY_UNIT", INTEGER));
        ELEMENTS.put(ElementIDs.PIXEL_WIDTH, new EbmlElement("PIXEL_WIDTH", INTEGER));
        ELEMENTS.put(ElementIDs.PIXEL_HEIGHT, new EbmlElement("PIXEL_HEIGHT", INTEGER));
        ELEMENTS.put(ElementIDs.FLAG_INTERLACED, new EbmlElement("FLAG_INTERLACED", INTEGER));
        ELEMENTS.put(ElementIDs.COLOR, new EbmlElement("COLOR", MASTER));
        ELEMENTS.put(ElementIDs.TRANSFER_CHARACTERISTICS, new EbmlElement("TRANSFER_CHARACTERISTICS", INTEGER));
        ELEMENTS.put(ElementIDs.MATRIX_COEFFICIENTS, new EbmlElement("MATRIX_COEFFICIENTS", INTEGER));
        ELEMENTS.put(ElementIDs.PRIMARIES, new EbmlElement("PRIMARIES", INTEGER));
        ELEMENTS.put(ElementIDs.RANGE, new EbmlElement("RANGE", INTEGER));
        ELEMENTS.put(ElementIDs.CHROMA_SITING_HORZ, new EbmlElement("CHROMA_SITING_HORZ", INTEGER));
        ELEMENTS.put(ElementIDs.CHROMA_SITING_VERT, new EbmlElement("CHROMA_SITING_VERT", INTEGER));
        ELEMENTS.put(ElementIDs.CODEC_DELAY, new EbmlElement("CODEC_DELAY", INTEGER));
        ELEMENTS.put(ElementIDs.SEEK_PRE_ROLL, new EbmlElement("SEEK_PRE_ROLL", INTEGER));
        ELEMENTS.put(ElementIDs.TAGS, new EbmlElement("TAGS", MASTER));
        ELEMENTS.put(ElementIDs.TAG, new EbmlElement("TAG", MASTER));
        ELEMENTS.put(ElementIDs.TARGETS, new EbmlElement("TARGETS", MASTER));
        ELEMENTS.put(ElementIDs.SIMPLE_TAG, new EbmlElement("SIMPLE_TAG", MASTER));
        ELEMENTS.put(ElementIDs.TAG_NAME, new EbmlElement("TAG_NAME", UTF8));
        ELEMENTS.put(ElementIDs.TAG_LANGUAGE, new EbmlElement("TAG_LANGUAGE", STRING));
        ELEMENTS.put(ElementIDs.TAG_STRING, new EbmlElement("TAG_STRING", UTF8));
        ELEMENTS.put(ElementIDs.TAG_LANGUAGE_BCP47, new EbmlElement("TAG_LANGUAGE_BCP47", STRING));
        ELEMENTS.put(ElementIDs.TAG_TRACK_UID, new EbmlElement("TAG_TRACK_UID", INTEGER));
    }

    public void extract(final SequentialReader reader, Metadata metadata) throws IOException
    {
        new Parser(reader, metadata).extract();
    }

    private static final class Context
    {
        final int id;
        final long end;
        final boolean unknownSize;
        final Directory directory;

        Context(int id, long end, boolean unknownSize, Directory directory)
        {
            this.id = id;
            this.end = end;
            this.unknownSize = unknownSize;
            this.directory = directory;
        }
    }

    private static final class Parser
    {
        private final SequentialReader _reader;
        private final Metadata _metadata;
        private final Deque<Context> _contexts = new ArrayDeque<Context>();
        private EbmlDirectory _header;

        Parser(SequentialReader reader, Metadata metadata)
        {
            _reader = reader;
            _metadata = metadata;
            _reader.setMotorolaByteOrder(true);
            _contexts.push(new Context(0, -1, true, null));
        }

        void extract() throws IOException
        {
            while (true) {
                while (_contexts.size() > 1 && _contexts.peek().end == _reader.getPosition())
                    closeContext();

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
                long size = DataParser.getElementSize(_reader);
                long position = _reader.getPosition();
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

                EbmlElement element = ELEMENTS.get(id);
                if (parent.directory != null && accepts(parent, id) && element != null) {
                    Object value = readValue(id, size, element.get_type());
                    if (value != null)
                        parent.directory.setObject(id, value);
                } else {
                    skip(size);
                }
            }
            while (_contexts.size() > 1)
                closeContext();
        }

        private void endUnknownContexts(int id)
        {
            Context context = _contexts.peek();
            if (context.id == ElementIDs.CLUSTER && context.unknownSize
                && (isSegmentChild(id) || id == ElementIDs.EBML_HEADER_ELEMENT)) {
                closeContext();
                context = _contexts.peek();
            }
            if (context.id == ElementIDs.SEGMENT && context.unknownSize
                && id == ElementIDs.EBML_HEADER_ELEMENT)
                closeContext();
        }

        private static boolean isSegmentChild(int id)
        {
            return id == ElementIDs.SEEK_HEAD || id == ElementIDs.SEGMENT_INFO
                || id == ElementIDs.TRACKS || id == ElementIDs.CLUSTER || id == ElementIDs.CUES
                || id == ElementIDs.TAGS || id == 0x1941A469 || id == 0x1043A770;
        }

        private Context createContext(int id, long end, boolean unknownSize, Context parent)
        {
            Directory directory;
            if (parent.id == 0 && id == ElementIDs.EBML_HEADER_ELEMENT) {
                _header = new EbmlDirectory();
                _metadata.addDirectory(_header);
                return new Context(id, end, unknownSize, _header);
            }
            if (parent.id == 0 && id == ElementIDs.SEGMENT) {
                directory = new MkvSegmentDirectory();
                if (_header != null)
                    directory.setParent(_header);
                _metadata.addDirectory(directory);
                return new Context(id, end, unknownSize, directory);
            }
            if ((parent.id == ElementIDs.SEGMENT && (id == ElementIDs.SEGMENT_INFO || id == ElementIDs.TRACKS))
                || (parent.id == ElementIDs.SEGMENT && id == ElementIDs.CLUSTER && unknownSize))
                return new Context(id, end, unknownSize, parent.directory);
            if (parent.id == ElementIDs.TRACKS && id == ElementIDs.TRACK_ENTRY)
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
                && parent.id != ElementIDs.CLUSTER && parent.directory.hasTagName(id);
        }

        private void closeContext()
        {
            Context context = _contexts.pop();
            Context parent = _contexts.peek();
            if (context.id == ElementIDs.VIDEO || context.id == ElementIDs.AUDIO) {
                copyTags(context.directory, parent.directory);
            } else if (context.id == ElementIDs.TRACK_ENTRY) {
                Object type = context.directory.getObject(ElementIDs.TRACK_TYPE);
                Directory track = Long.valueOf(1).equals(type) ? new MkvVideoDirectory()
                    : Long.valueOf(2).equals(type) ? new MkvAudioDirectory() : context.directory;
                if (parent.directory != null)
                    track.setParent(parent.directory);
                copyTags(context.directory, track);
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

        private Object readValue(int id, long size, EbmlElement.Type type) throws IOException
        {
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
