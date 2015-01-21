/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.metadata;

import com.drew.lang.Rational;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base class for all tag descriptor classes.  Implementations are responsible for
 * providing the human-readable string representation of tag values stored in a directory.
 * The directory is provided to the tag descriptor via its constructor.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class TagDescriptor<T extends Directory>
{
    @NotNull
    protected final T _directory;

    public TagDescriptor(@NotNull T directory)
    {
        _directory = directory;
    }

    /**
     * Returns a descriptive value of the specified tag for this image.
     * Where possible, known values will be substituted here in place of the raw
     * tokens actually kept in the metadata segment.  If no substitution is
     * available, the value provided by <code>getString(tagType)</code> will be returned.
     *
     * @param tagType the tag to find a description for
     * @return a description of the image's value for the specified tag, or
     *         <code>null</code> if the tag hasn't been defined.
     */
    @Nullable
    public String getDescription(int tagType)
    {
        Object object = _directory.getObject(tagType);

        if (object == null)
            return null;

        // special presentation for long arrays
        if (object.getClass().isArray()) {
            final int length = Array.getLength(object);
            if (length > 16) {
                final String componentTypeName = object.getClass().getComponentType().getName();
                return String.format("[%d %s%s]", length, componentTypeName, length == 1 ? "" : "s");
            }
        }

        // no special handling required, so use default conversion to a string
        return _directory.getString(tagType);
    }

    /**
     * Takes a series of 4 bytes from the specified offset, and converts these to a
     * well-known version number, where possible.
     * <p>
     * Two different formats are processed:
     * <ul>
     * <li>[30 32 31 30] -&gt; 2.10</li>
     * <li>[0 1 0 0] -&gt; 1.00</li>
     * </ul>
     *
     * @param components  the four version values
     * @param majorDigits the number of components to be
     * @return the version as a string of form "2.10" or null if the argument cannot be converted
     */
    @Nullable
    public static String convertBytesToVersionString(@Nullable int[] components, final int majorDigits)
    {
        if (components == null)
            return null;
        StringBuilder version = new StringBuilder();
        for (int i = 0; i < 4 && i < components.length; i++) {
            if (i == majorDigits)
                version.append('.');
            char c = (char)components[i];
            if (c < '0')
                c += '0';
            if (i == 0 && c == '0')
                continue;
            version.append(c);
        }
        return version.toString();
    }

    @Nullable
    protected String getVersionBytesDescription(final int tagType, int majorDigits)
    {
        int[] values = _directory.getIntArray(tagType);
        return values == null ? null : convertBytesToVersionString(values, majorDigits);
    }

    @Nullable
    protected String getIndexedDescription(final int tagType, @NotNull String... descriptions)
    {
        return getIndexedDescription(tagType, 0, descriptions);
    }

    @Nullable
    protected String getIndexedDescription(final int tagType, final int baseIndex, @NotNull String... descriptions)
    {
        final Integer index = _directory.getInteger(tagType);
        if (index == null)
            return null;
        final int arrayIndex = index - baseIndex;
        if (arrayIndex >= 0 && arrayIndex < descriptions.length) {
            String description = descriptions[arrayIndex];
            if (description != null)
                return description;
        }
        return "Unknown (" + index + ")";
    }

    @Nullable
    protected String getByteLengthDescription(final int tagType)
    {
        byte[] bytes = _directory.getByteArray(tagType);
        if (bytes == null)
            return null;
        return String.format("(%d byte%s)", bytes.length, bytes.length == 1 ? "" : "s");
    }

    @Nullable
    protected String getSimpleRational(final int tagType)
    {
        Rational value = _directory.getRational(tagType);
        if (value == null)
            return null;
        return value.toSimpleString(true);
    }

    @Nullable
    protected String getDecimalRational(final int tagType, final int decimalPlaces)
    {
        Rational value = _directory.getRational(tagType);
        if (value == null)
            return null;
        return String.format("%." + decimalPlaces + "f", value.doubleValue());
    }

    @Nullable
    protected String getFormattedInt(final int tagType, @NotNull final String format)
    {
        Integer value = _directory.getInteger(tagType);
        if (value == null)
            return null;
        return String.format(format, value);
    }

    @Nullable
    protected String getFormattedFloat(final int tagType, @NotNull final String format)
    {
        Float value = _directory.getFloatObject(tagType);
        if (value == null)
            return null;
        return String.format(format, value);
    }

    @Nullable
    protected String getFormattedString(final int tagType, @NotNull final String format)
    {
        String value = _directory.getString(tagType);
        if (value == null)
            return null;
        return String.format(format, value);
    }

    @Nullable
    protected String getEpochTimeDescription(final int tagType)
    {
        // TODO have observed a byte[8] here which is likely some kind of date (ticks as long?)
        Long value = _directory.getLongObject(tagType);
        if (value==null)
            return null;
        return new Date(value).toString();
    }

    /**
     * LSB first. Labels may be null, a String, or a String[2] with (low label,high label) values.
     */
    @Nullable
    protected String getBitFlagDescription(final int tagType, @NotNull final Object... labels)
    {
        Integer value = _directory.getInteger(tagType);

        if (value == null)
            return null;

        List<String> parts = new ArrayList<String>();

        int bitIndex = 0;
        while (labels.length > bitIndex) {
            Object labelObj = labels[bitIndex];
            if (labelObj != null) {
                boolean isBitSet = (value & 1) == 1;
                if (labelObj instanceof String[]) {
                    String[] labelPair = (String[])labelObj;
                    assert(labelPair.length == 2);
                    parts.add(labelPair[isBitSet ? 1 : 0]);
                } else if (isBitSet && labelObj instanceof String) {
                    parts.add((String)labelObj);
                }
            }
            value >>= 1;
            bitIndex++;
        }

        return StringUtil.join(parts, ", ");
    }

    @Nullable
    protected String get7BitStringFromBytes(final int tagType)
    {
        final byte[] bytes = _directory.getByteArray(tagType);

        if (bytes == null)
            return null;

        int length = bytes.length;
        for (int index = 0; index < bytes.length; index++) {
            int i = bytes[index] & 0xFF;
            if (i == 0 || i > 0x7F) {
                length = index;
                break;
            }
        }

        return new String(bytes, 0, length);
    }

    @Nullable
    protected String getAsciiStringFromBytes(int tag)
    {
        byte[] values = _directory.getByteArray(tag);

        if (values == null)
            return null;

        try {
            return new String(values, "ASCII").trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
