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
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.metadata.mp4;

import static com.drew.metadata.mp4.Mp4Directory.TAG_CATEGORY;
import static com.drew.metadata.mp4.Mp4Directory.TAG_COMMENT;
import static com.drew.metadata.mp4.Mp4Directory.TAG_LATITUDE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_LONGITUDE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_MOOD;
import static com.drew.metadata.mp4.Mp4Directory.TAG_SUBTITLE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_TITLE;
import static com.drew.metadata.mp4.Mp4Directory.TAG_USER_RATING;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.DateUtil;
import com.drew.lang.Rational;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.media.Mp4HintHandler;
import com.drew.metadata.mp4.media.Mp4MetaHandler;
import com.drew.metadata.mp4.media.Mp4SoundHandler;
import com.drew.metadata.mp4.media.Mp4TextHandler;
import com.drew.metadata.mp4.media.Mp4UuidBoxHandler;
import com.drew.metadata.mp4.media.Mp4VideoHandler;

/**
 * @author Payton Garland
 */
public class Mp4BoxHandler extends Mp4Handler<Mp4Directory>
{
    public Mp4BoxHandler(Metadata metadata)
    {
        super(metadata);
    }

    @NotNull
    @Override
    protected Mp4Directory getDirectory()
    {
        return new Mp4Directory();
    }

    @Override
    public boolean shouldAcceptBox(@NotNull String type)
    {
        return type.equals(Mp4BoxTypes.BOX_FILE_TYPE)
            || type.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)
            || type.equals(Mp4BoxTypes.BOX_HANDLER)
            || type.equals(Mp4BoxTypes.BOX_MEDIA_HEADER)
            || type.equals(Mp4BoxTypes.BOX_TRACK_HEADER)
            || type.equals(Mp4BoxTypes.BOX_USER_DATA)
            || type.equals(Mp4BoxTypes.BOX_USER_DEFINED);
    }

    @Override
    public boolean shouldAcceptContainer(@NotNull String type)
    {
        return type.equals(Mp4ContainerTypes.BOX_TRACK)
            || type.equals(Mp4ContainerTypes.BOX_METADATA)
            || type.equals(Mp4ContainerTypes.BOX_MOVIE)
            || type.equals(Mp4ContainerTypes.BOX_MEDIA);
    }

    @Override
    public Mp4Handler<?> processBox(@NotNull String type, @Nullable byte[] payload, long boxSize, Mp4Context context) throws IOException
    {
        if (payload != null) {
            SequentialReader reader = new SequentialByteArrayReader(payload);
            if (type.equals(Mp4BoxTypes.BOX_MOVIE_HEADER)) {
                processMovieHeader(reader);
            } else if (type.equals(Mp4BoxTypes.BOX_FILE_TYPE)) {
                processFileType(reader, boxSize);
            } else if (type.equals(Mp4BoxTypes.BOX_HANDLER)) {

                // ISO/IED 14496-12:2015 pg.7

                reader.skip(4); // one byte version, three bytes flags

                // ISO/IED 14496-12:2015 pg.30

                reader.skip(4); // Pre-defined
                String handlerType = reader.getString(4);
                reader.skip(12); // Reserved
                String name = reader.getNullTerminatedString((int)boxSize - 32, Charset.defaultCharset(), false);

                final String HANDLER_SOUND_MEDIA             = "soun";
                final String HANDLER_VIDEO_MEDIA             = "vide";
                final String HANDLER_HINT_MEDIA              = "hint";
                final String HANDLER_TEXT_MEDIA              = "text";
                final String HANDLER_META_MEDIA              = "meta";

                if (handlerType.equals(HANDLER_SOUND_MEDIA)) {
                    return new Mp4SoundHandler(metadata, context);
                } else if (handlerType.equals(HANDLER_VIDEO_MEDIA)) {
                    return new Mp4VideoHandler(metadata, context);
                } else if (handlerType.equals(HANDLER_HINT_MEDIA)) {
                    return new Mp4HintHandler(metadata, context);
                } else if (handlerType.equals(HANDLER_TEXT_MEDIA)) {
                    return new Mp4TextHandler(metadata, context);
                } else if (handlerType.equals(HANDLER_META_MEDIA)) {
                    return new Mp4MetaHandler(metadata, context);
                }
                return this;
            } else if (type.equals(Mp4BoxTypes.BOX_MEDIA_HEADER)) {
                processMediaHeader(reader, context);
            } else if (type.equals(Mp4BoxTypes.BOX_TRACK_HEADER)) {
                processTrackHeader(reader);
            } else if (type.equals(Mp4BoxTypes.BOX_USER_DEFINED)) {
                Mp4UuidBoxHandler userBoxHandler = new Mp4UuidBoxHandler(metadata);
                userBoxHandler.processBox(type, payload, boxSize, context);
            } else if (type.equals(Mp4BoxTypes.BOX_USER_DATA)) {
                processUserData(reader, payload.length);
            }
        } else {
            if (type.equals(Mp4ContainerTypes.BOX_COMPRESSED_MOVIE)) {
                directory.addError("Compressed MP4 movies not supported");
            }
        }
        return this;
    }

    private static final Pattern COORDINATE_PATTERN = Pattern.compile("([+-]\\d+\\.\\d+)([+-]\\d+\\.\\d+)");

    private void processUserData(@NotNull SequentialReader reader, int length) throws IOException
    {
        final int LOCATION_CODE = 0xA978797A; // "©xyz"
		final int META_TYPE = 0x6D657461; // "meta"
		final int XTRA_TYPE = 0x58747261; // "Xtra"

        String coordinateString = null;

        while (reader.getPosition() < length) {
            long size = reader.getUInt32();
            if (size <= 4)
                break;
            int kind = reader.getInt32();
            if (kind == LOCATION_CODE) {
                int xyzLength = reader.getUInt16();
                reader.skip(2);
                coordinateString = reader.getString(xyzLength, "UTF-8");
            } else if (kind == META_TYPE && size > 16) {
				reader.skip(4);
				processUserDataMeta(reader, length, size - 12);
			} else if (kind == XTRA_TYPE && size > 16) {
				processUserDataMetaXtra(reader, length, size - 8);
			} else if (size >= 8) {
				reader.skip(size - 8);
			} else {
				return;
			}
        }

        if (coordinateString != null) {
            final Matcher matcher = COORDINATE_PATTERN.matcher(coordinateString);
            if (matcher.find()) {
                final double latitude = Double.parseDouble(matcher.group(1));
                final double longitude = Double.parseDouble(matcher.group(2));

                directory.setDouble(TAG_LATITUDE, latitude);
                directory.setDouble(TAG_LONGITUDE, longitude);
            }
        }
    }
    
    private void processUserDataMeta(@NotNull SequentialReader reader, int length, long blockSize) throws IOException {
		final int HDLR_TYPE = 0x68646C72; // "hdlr"
		final int ILST_TYPE = 0x696C7374; // "ilst"

		long initialPosition = reader.getPosition();

		while (reader.getPosition() < length && (reader.getPosition() - initialPosition) < blockSize) {
			long size = reader.getUInt32();
			if (size <= 4)
				break;
			int kind = reader.getInt32();
			if (kind == HDLR_TYPE) {
				// nothing
				reader.skip(size - 8);
			} else if (kind == ILST_TYPE && size > 16) {
				processUserDataMetaIList(reader, length, size - 8);
			}
		}
	}

	private void processUserDataMetaIList(@NotNull SequentialReader reader, int length, long blockSize)
			throws IOException {
		final int CNAM_TYPE = 0xA96E616D; // "©nam"
		final int CCMT_TYPE = 0xA9636D74; // "©cmt"
		long initialPosition = reader.getPosition();

		while (reader.getPosition() < length && (reader.getPosition() - initialPosition) < blockSize) {
			long size = reader.getUInt32();
			if (size <= 4)
				break;
			int kind = reader.getInt32();
			if (kind == CNAM_TYPE) {
				long cnamSize = reader.getUInt32();
				if (cnamSize > 16) {
					reader.skip(12);
					directory.setString(TAG_TITLE, reader.getString((int) cnamSize - 16, "UTF-8"));
				}
			} else if (kind == CCMT_TYPE) {
				long ccmtSize = reader.getUInt32();
				if (ccmtSize > 16) {
					reader.skip(12);
					directory.setString(TAG_COMMENT, reader.getString((int) ccmtSize - 16, "UTF-8"));
				}
			} else {
				// nothing
			}
		}
	}

	private void processUserDataMetaXtra(@NotNull SequentialReader reader, int length, long blockSize)
			throws IOException {
		long initialPosition = reader.getPosition();
		while (reader.getPosition() < length && (reader.getPosition() - initialPosition) < blockSize) {
			long key_size = reader.getUInt32();
			long key_name_size = reader.getUInt32();
			String key_name = reader.getString((int) key_name_size, "UTF-8");
			long entry_count = reader.getUInt32();
			if (key_name.equals("WM/SubTitle")) {
				String value = getProcessUserDataMetaXtraValue(reader, entry_count);
				directory.setString(TAG_SUBTITLE, value);
			} else if (key_name.equals("WM/SharedUserRating")) {
				long value_size = reader.getUInt32();
				int value_type = reader.getUInt16();
				directory.setLong(TAG_USER_RATING, reader.getInt64());
			} else if (key_name.equals("WM/Category")) {
				String value = getProcessUserDataMetaXtraValue(reader, entry_count);
				directory.setString(TAG_CATEGORY, value);
			} else if (key_name.equals("WM/Mood")) {
				String value = getProcessUserDataMetaXtraValue(reader, entry_count);
				directory.setString(TAG_MOOD, value);
			}
		}
	}

	private String getProcessUserDataMetaXtraValue(@NotNull SequentialReader reader, long entry_count)
			throws IOException {
		List<String> result = new ArrayList<>();
		for (long i = 0; i < entry_count; ++i) {
			long value_size = reader.getUInt32();
			int val_type = reader.getUInt16();
			result.add(reader.getString((int) value_size - 6, "UTF-8"));
		}
		return String.join(" | ", result);
	}

    private void processFileType(@NotNull SequentialReader reader, long boxSize) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.8

        String majorBrand = reader.getString(4);
        long minorVersion = reader.getUInt32();

        // TODO avoid array list
        ArrayList<String> compatibleBrands = new ArrayList<String>();
        for (int i = 16; i < boxSize; i += 4) {
            compatibleBrands.add(reader.getString(4));
        }

        directory.setString(Mp4Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Mp4Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[compatibleBrands.size()]));
    }

    private void processMovieHeader(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.23

        short version = reader.getUInt8();

        reader.skip(3); // flags

        long creationTime;
        long modificationTime;
        long timescale;
        long duration;

        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            timescale = reader.getUInt32();
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            timescale = reader.getUInt32();
            duration = reader.getUInt32();
        }

        int rate = reader.getInt32();
        int volume = reader.getInt16();
        reader.skip(2); // Reserved
        reader.skip(8); // Reserved
        int[] matrix = new int[]{
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        reader.skip(24); // Pre-defined
        long nextTrackID = reader.getUInt32();

        // Get creation/modification times
        directory.setDate(Mp4Directory.TAG_CREATION_TIME, DateUtil.get1Jan1904EpochDate(creationTime));
        directory.setDate(Mp4Directory.TAG_MODIFICATION_TIME, DateUtil.get1Jan1904EpochDate(modificationTime));

        // Get duration and time scale
        directory.setLong(Mp4Directory.TAG_DURATION, duration);
        directory.setLong(Mp4Directory.TAG_TIME_SCALE, timescale);
        directory.setRational(Mp4Directory.TAG_DURATION_SECONDS, new Rational(duration, timescale));

        directory.setIntArray(Mp4Directory.TAG_TRANSFORMATION_MATRIX, matrix);

        // Calculate preferred rate fixed point
        double preferredRateInteger = (rate & 0xFFFF0000) >> 16;
        double preferredRateFraction = (rate & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4Directory.TAG_PREFERRED_RATE, preferredRateInteger + preferredRateFraction);

        // Calculate preferred volume fixed point
        double preferredVolumeInteger = (volume & 0xFF00) >> 8;
        double preferredVolumeFraction = (volume & 0x00FF) / Math.pow(2, 2);
        directory.setDouble(Mp4Directory.TAG_PREFERRED_VOLUME, preferredVolumeInteger + preferredVolumeFraction);

        directory.setLong(Mp4Directory.TAG_NEXT_TRACK_ID, nextTrackID);
    }

    private void processMediaHeader(@NotNull SequentialReader reader, Mp4Context context) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        int version = reader.getUInt8();

        reader.skip(3); // flags

        // ISO/IED 14496-12:2015 pg.29

        if (version == 1) {
            context.creationTime = reader.getInt64();
            context.modificationTime = reader.getInt64();
            context.timeScale = (long)reader.getInt32();
            context.duration = reader.getInt64();
        } else {
            context.creationTime = reader.getUInt32();
            context.modificationTime = reader.getUInt32();
            context.timeScale = reader.getUInt32();
            context.duration = reader.getUInt32();
        }

        int languageBits = reader.getInt16();

        context.language = new String(new char[]
            {
                (char)(((languageBits & 0x7C00) >> 10) + 0x60),
                (char)(((languageBits & 0x03E0) >> 5) + 0x60),
                (char)((languageBits & 0x001F) + 0x60)
            });
    }

    private void processTrackHeader(@NotNull SequentialReader reader) throws IOException
    {
        // ISO/IED 14496-12:2015 pg.7

        int version = reader.getUInt8();

        reader.skip(3); // flags

        // ISO/IED 14496-12:2005 pg.17-18

        long creationTime;
        long modificationTime;
        long trackID;
        long duration;

        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            trackID = reader.getInt32();
            reader.skip(4); // reserved
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            trackID = reader.getUInt32();
            reader.skip(4);
            duration = reader.getUInt32();
        }

        reader.skip(8); // reserved

        int layer = reader.getInt16();
        int alternateGroup = reader.getInt16();
        int volume = reader.getInt16();

        reader.skip(2); // reserved

        int[] matrix = new int[9];
        for (int i = 0; i < 9; i++) {
            matrix[i] = reader.getInt32();
        }

        long width = reader.getInt32();
        long height = reader.getInt32();

        // TODO seems wrong to only set this once
        if (width != 0 && height != 0 && directory.getDoubleObject(Mp4Directory.TAG_ROTATION) == null) {
            int x = matrix[1] + matrix[4];
            int y = matrix[0] + matrix[3];
            double theta = Math.atan2(y, x);
            double degree = Math.toDegrees(theta);
            degree -= 45;
            directory.setDouble(Mp4Directory.TAG_ROTATION, degree);
        }
    }
}
