package com.drew.metadata.mkv;

import com.drew.lang.SequentialReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.drew.metadata.mkv.DataParser.decodeInteger;
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
        reader.setMotorolaByteOrder(true);
        Map<Integer, Object> data = new HashMap<>();
        while (reader.available() > 0)
        {
            extractSubContext(reader, data);
        }
        getMetadata(data, metadata);
    }


    private void getMetadata(Map<Integer, Object> data, Metadata metadata) throws IOException
    {
        createDirectories(data, metadata);
        for (Map.Entry<Integer, Object> entry : data.entrySet())
        {
            if (entry.getValue() instanceof List)
            {
                for (Object member : (List<?>) entry.getValue())
                {
                    doCreateDirectory(member, metadata);
                }
            } else
            {
                doCreateDirectory(entry.getValue(), metadata);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doCreateDirectory(Object data, Metadata metadata) throws IOException
    {
        if (data instanceof Map)
        {
            createDirectories((Map<Integer, Object>) data, metadata);
        }
    }

    @SuppressWarnings("unchecked")
    private void createDirectories(Map<Integer, Object> data, Metadata metadata)
    {
        Directory dir = null;
        if (data.containsKey(ElementIDs.TRACK_TYPE))
        {
            switch (((Long) data.get(ElementIDs.TRACK_TYPE)).intValue())
            {
                case 1:
                    dir = new VideoDirectory();
                    break;
                case 2:
                    dir = new AudioDirectory();
                    break;
            }
            if (dir != null)
            {
                mapToDirectory(dir, data);
                metadata.addDirectory(dir);
            }
        }
        if (data.containsKey(ElementIDs.SEGMENT_INFO))
        {
            dir = new SegmentInfoDirectory();
            createDirectories((Map<Integer, Object>) data.get(ElementIDs.SEGMENT_INFO), metadata);
            mapToDirectory(dir, (Map<Integer, Object>) data.get(ElementIDs.SEGMENT_INFO));
            metadata.addDirectory(dir);
        }
        if (data.containsKey(ElementIDs.EBML_HEADER_ELEMENT))
        {
            dir = new EbmlDirectory();
            mapToDirectory(dir, (Map<Integer, Object>) data.get(ElementIDs.EBML_HEADER_ELEMENT));
            metadata.addDirectory(dir);
        }
        if (data.containsKey(ElementIDs.TRACKS))
        {
            createDirectories((Map<Integer, Object>) data.get(ElementIDs.TRACKS), metadata);
        }
        if (data.containsKey(ElementIDs.TRACK_ENTRY))
        {
            if (data.get(ElementIDs.TRACK_ENTRY) instanceof List)
            {
                for (Object entry : (List<?>) data.get(ElementIDs.TRACK_ENTRY))
                {
                    if (entry instanceof Map)
                    {
                        createDirectories((Map<Integer, Object>) entry, metadata);
                    }
                }
            }
        }

    }

    private void mapToDirectory(Directory directory, Map<Integer, Object> data)
    {
        for (Map.Entry<Integer, Object> values : data.entrySet())
        {
            put(directory, values.getKey(), values.getValue());
        }
    }

    private void put(Directory directory, int id, Object value)
    {
        EbmlElement element = ELEMENTS.get(id);
        switch (element.get_type())
        {
            case INTEGER:
            case SIGNED_INTEGER:
                directory.setLong(id, (long) value);
                break;
            case FLOAT:
                directory.setDouble(id, (double) value);
                break;
            case STRING:
            case UTF8:
                directory.setString(id, (String) value);
                break;
            case BINARY:
                directory.setByteArray(id, (byte[]) value);
                break;
        }
    }

    private void extractSubContext(final SequentialReader reader, Map<Integer, Object> data) throws IOException
    {
        reader.setMotorolaByteOrder(true);
        int eid = DataParser.getElementId(reader);
        long size = decodeInteger(reader);
        Object value = null;
        EbmlElement element = ELEMENTS.get(eid);
        if (element == null)
        {
            element = new EbmlElement(String.format("0x%02X [ unknown ]", eid), UNKNOWN);
        }
        switch (element.get_type())
        {
            case STRING:
                value = DataParser.getString(reader, size);
                break;
            case INTEGER:
                value = DataParser.getLong(reader, size);
                break;
            case BINARY:
                value = DataParser.getByteArray(reader, size);
                break;
            case MASTER:
                StreamReader sc = new StreamReader(new ByteArrayInputStream(reader.getBytes((int) size)));
                Map<Integer, Object> subData = new HashMap<>();
                while (sc.available() > 0) extractSubContext(sc, subData);
                value = subData;
                break;
            case UTF8:
                value = new String(reader.getBytes((int) size), StandardCharsets.UTF_8);
                break;
            case VOID:
                reader.skip(size);
                break;
            case FLOAT:
                value = size == 4 ? reader.getFloat32() : reader.getDouble64();
                break;
            case UNKNOWN:
                reader.skip(size);
                return;
        }
        if (ELEMENTS.containsKey(eid) && value != null)
        {
            if (data.containsKey(eid))
            {
                Object previous = data.get(eid);
                List<Object> list = new ArrayList<>();
                list.add(previous);
                list.add(value);
                value = list;
            }
            data.put(eid, value);
        }
    }
}
