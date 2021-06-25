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
package com.drew.metadata;

import com.drew.lang.Rational;
import com.drew.lang.StringUtil;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    @NotNull
    protected final MetadataContext _context;

    // TODO remove constructor once all sub-classes actually use MetadataContext?
    public TagDescriptor(@NotNull T directory)
    {
        // TODO discuss: acceptable to use default here?
        this(directory, new MetadataContext());
    }

    public TagDescriptor(@NotNull T directory, @NotNull MetadataContext context)
    {
        _directory = directory;
        _context = context;
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
                return String.format(getContext().locale(), "[%d values]", length);
            }
        }

        if (object instanceof Date) {
            // Produce a date string having a format that includes the offset in form "+00:00"
            return new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", getContext().locale())
                .format((Date) object)
                .replaceAll("([0-9]{2} [^ ]+)$", ":$1");
        }

        // no special handling required, so use default conversion to a string
        return _directory.getString(tagType);
    }

    /**
     * Gets the {@link MetadataContext}.
     *
     * @return the metadata context.
     */
    @NotNull
    public MetadataContext getContext()
    {
        return _context;
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
        final Long index = _directory.getLongObject(tagType);
        if (index == null)
            return null;
        final long arrayIndex = index - baseIndex;
        if (arrayIndex >= 0 && arrayIndex < (long)descriptions.length) {
            String description = descriptions[(int)arrayIndex];
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
        return String.format(getContext().locale(), "(%d byte%s)", bytes.length, bytes.length == 1 ? "" : "s");
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
        return String.format(getContext().locale(), "%." + decimalPlaces + "f", value.doubleValue());
    }

    @Nullable
    protected String getFormattedInt(final int tagType, @NotNull final String format)
    {
        Integer value = _directory.getInteger(tagType);
        if (value == null)
            return null;
        return String.format(getContext().locale(), format, value);
    }

    @Nullable
    protected String getFormattedFloat(final int tagType, @NotNull final String format)
    {
        Float value = _directory.getFloatObject(tagType);
        if (value == null)
            return null;
        return String.format(getContext().locale(), format, value);
    }

    @Nullable
    protected String getFormattedString(final int tagType, @NotNull final String format)
    {
        String value = _directory.getString(tagType);
        if (value == null)
            return null;
        return String.format(getContext().locale(), format, value);
    }

    @Nullable
    protected String getEpochTimeDescription(final int tagType)
    {
        // TODO have observed a byte[8] here which is likely some kind of date (ticks as long?)
        Long value = _directory.getLongObject(tagType);
        if (value == null)
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
    protected String getStringFromBytes(int tag, Charset cs)
    {
        byte[] values = _directory.getByteArray(tag);

        if (values == null)
            return null;

        try {
            return new String(values, cs.name()).trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // TODO: discuss deprecation
    @Deprecated
    @Nullable
    /**
     * @deprecated Use {@link #getRationalOrDoubleString(int, Locale)}.
     */
    protected String getRationalOrDoubleString(int tagType)
    {
        return getRationalOrDoubleString(tagType, null);
    }

    @Nullable
    protected String getRationalOrDoubleString(int tagType, Locale locale)
    {
        Rational rational = _directory.getRational(tagType);
        if (rational != null)
            return rational.toSimpleString(true);

        Double d = _directory.getDoubleObject(tagType);
        if (d != null) {
            DecimalFormat format = new DecimalFormat("0.###", getDecimalFormatSymbols(locale));
            return format.format(d);
        }

        return null;
    }

    // TODO: discuss deprecation
    @Deprecated
    @Nullable
    /**
     * @deprecated Use {@link #getFStopDescription(double, Locale)}.
     */
    protected static String getFStopDescription(double fStop)
    {
        return getFStopDescription(fStop, null);
    }

    @Nullable
    protected static String getFStopDescription(double fStop, Locale locale)
    {
        DecimalFormat format = new DecimalFormat("0.0", getDecimalFormatSymbols(locale));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return "f/" + format.format(fStop);
    }
    // TODO: discuss deprecation
    @Deprecated
    @Nullable
    /**
     * @deprecated Use {@link #getFocalLengthDescription(double, Locale)}.
     */
    protected static String getFocalLengthDescription(double mm)
    {
        return getFocalLengthDescription(mm, null);
    }

    @Nullable
    protected static String getFocalLengthDescription(double mm, Locale locale)
    {
        DecimalFormat format = new DecimalFormat("0.#", getDecimalFormatSymbols(locale));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(mm) + " mm";
    }

    // TODO: discuss deprecation
    @Deprecated
    @Nullable
    /**
     * @deprecated Use {@link #getLensSpecificationDescription(int, Locale)}.
     */
    protected String getLensSpecificationDescription(int tag)
    {
        return getLensSpecificationDescription(tag, null);
    }

    @Nullable
    protected String getLensSpecificationDescription(int tag, Locale locale)
    {
        Rational[] values = _directory.getRationalArray(tag);

        if (values == null || values.length != 4 || (values[0].isZero() && values[2].isZero()))
            return null;

        StringBuilder sb = new StringBuilder();

        if (values[0].equals(values[1]))
            sb.append(values[0].toSimpleString(true)).append("mm");
        else
            sb.append(values[0].toSimpleString(true)).append('-').append(values[1].toSimpleString(true)).append("mm");

        if (!values[2].isZero()) {
            sb.append(' ');

            DecimalFormat format = new DecimalFormat("0.0", getDecimalFormatSymbols(locale));
            format.setRoundingMode(RoundingMode.HALF_UP);

            if (values[2].equals(values[3]))
                sb.append(getFStopDescription(values[2].doubleValue(), getContext().locale()));
            else
                sb.append("f/").append(format.format(values[2].doubleValue())).append('-').append(format.format(values[3].doubleValue()));
        }

        return sb.toString();
    }

    @Nullable
    protected String getOrientationDescription(int tag)
    {
        return getIndexedDescription(tag, 1,
            "Top, left side (Horizontal / normal)",
            "Top, right side (Mirror horizontal)",
            "Bottom, right side (Rotate 180)",
            "Bottom, left side (Mirror vertical)",
            "Left side, top (Mirror horizontal and rotate 270 CW)",
            "Right side, top (Rotate 90 CW)",
            "Right side, bottom (Mirror horizontal and rotate 90 CW)",
            "Left side, bottom (Rotate 270 CW)");
    }

    // TODO: discuss deprecation
    @Deprecated
    @Nullable
    /**
     * @deprecated Use {@link #getShutterSpeedDescription(int, Locale)}.
     */
    protected String getShutterSpeedDescription(int tag)
    {
        return getShutterSpeedDescription(tag, null);
    }

    @Nullable
    protected String getShutterSpeedDescription(int tag, Locale locale)
    {
        // I believe this method to now be stable, but am leaving some alternative snippets of
        // code in here, to assist anyone who's looking into this (given that I don't have a public CVS).

//        float apexValue = _directory.getFloat(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
//        int apexPower = (int)Math.pow(2.0, apexValue);
//        return "1/" + apexPower + " sec";
        // TODO test this method
        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        Float apexValue = _directory.getFloatObject(tag);
        if (apexValue == null)
            return null;
        if (apexValue <= 1) {
            float apexPower = (float)(1 / (Math.exp(apexValue * Math.log(2))));
            long apexPower10 = Math.round((double)apexPower * 10.0);
            float fApexPower = (float)apexPower10 / 10.0f;
            DecimalFormat format = new DecimalFormat("0.##", getDecimalFormatSymbols(locale));
            format.setRoundingMode(RoundingMode.HALF_UP);
            return format.format(fApexPower) + " sec";
        } else {
            int apexPower = (int)((Math.exp(apexValue * Math.log(2))));
            return "1/" + apexPower + " sec";
        }

/*
        // This alternative implementation offered by Bill Richards
        // TODO determine which is the correct / more-correct implementation
        double apexValue = _directory.getDouble(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
        double apexPower = Math.pow(2.0, apexValue);

        StringBuffer sb = new StringBuffer();
        if (apexPower > 1)
            apexPower = Math.floor(apexPower);

        if (apexPower < 1) {
            sb.append((int)Math.round(1/apexPower));
        } else {
            sb.append("1/");
            sb.append((int)apexPower);
        }
        sb.append(" sec");
        return sb.toString();
*/
    }

    // EXIF UserComment, GPSProcessingMethod and GPSAreaInformation
    @Nullable
    protected String getEncodedTextDescription(int tagType)
    {
        byte[] commentBytes = _directory.getByteArray(tagType);
        if (commentBytes == null)
            return null;
        if (commentBytes.length == 0)
            return "";

        final Map<String, String> encodingMap = new HashMap<String, String>();
        encodingMap.put("ASCII", System.getProperty("file.encoding")); // Someone suggested "ISO-8859-1".
        encodingMap.put("UNICODE", "UTF-16LE");
        encodingMap.put("JIS", "Shift-JIS"); // We assume this charset for now.  Another suggestion is "JIS".

        try {
            if (commentBytes.length >= 10) {
                String firstTenBytesString = new String(commentBytes, 0, 10);

                // try each encoding name
                for (Map.Entry<String, String> pair : encodingMap.entrySet()) {
                    String encodingName = pair.getKey();
                    String charset = pair.getValue();
                    if (firstTenBytesString.startsWith(encodingName)) {
                        // skip any null or blank characters commonly present after the encoding name, up to a limit of 10 from the start
                        for (int j = encodingName.length(); j < 10; j++) {
                            byte b = commentBytes[j];
                            if (b != '\0' && b != ' ')
                                return new String(commentBytes, j, commentBytes.length - j, charset).trim();
                        }
                        return new String(commentBytes, 10, commentBytes.length - 10, charset).trim();
                    }
                }
            }
            // special handling fell through, return a plain string representation
            return new String(commentBytes, System.getProperty("file.encoding")).trim();
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    protected static DecimalFormatSymbols getDecimalFormatSymbols(Locale locale)
    {
        return locale == null ? DecimalFormatSymbols.getInstance() : DecimalFormatSymbols.getInstance(locale);
    }

}
