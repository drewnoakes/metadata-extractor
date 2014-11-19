package com.drew.metadata.png;

import com.drew.imaging.png.PngColorType;
import com.drew.lang.KeyValuePair;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.io.IOException;
import java.util.List;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class PngDescriptor extends TagDescriptor<PngDirectory>
{
    public PngDescriptor(@NotNull PngDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case PngDirectory.TAG_COLOR_TYPE:
                return getColorTypeDescription();
            case PngDirectory.TAG_COMPRESSION_TYPE:
                return getCompressionTypeDescription();
            case PngDirectory.TAG_FILTER_METHOD:
                return getFilterMethodDescription();
            case PngDirectory.TAG_INTERLACE_METHOD:
                return getInterlaceMethodDescription();
            case PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY:
                return getPaletteHasTransparencyDescription();
            case PngDirectory.TAG_SRGB_RENDERING_INTENT:
                return getIsSrgbColorSpaceDescription();
            case PngDirectory.TAG_TEXTUAL_DATA:
                return getTextualDataDescription();
            case PngDirectory.TAG_BACKGROUND_COLOR:
                return getBackgroundColorDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getColorTypeDescription()
    {
        Integer value = _directory.getInteger(PngDirectory.TAG_COLOR_TYPE);
        if (value == null)
            return null;
        PngColorType colorType = PngColorType.fromNumericValue(value);
        if (colorType == null)
            return null;
        return colorType.getDescription();
    }

    @Nullable
    public String getCompressionTypeDescription()
    {
        return getIndexedDescription(PngDirectory.TAG_COMPRESSION_TYPE, "Deflate");
    }

    @Nullable
    public String getFilterMethodDescription()
    {
        return getIndexedDescription(PngDirectory.TAG_FILTER_METHOD, "Adaptive");
    }

    @Nullable
    public String getInterlaceMethodDescription()
    {
        return getIndexedDescription(PngDirectory.TAG_INTERLACE_METHOD, "No Interlace", "Adam7 Interlace");
    }

    @Nullable
    public String getPaletteHasTransparencyDescription()
    {
        return getIndexedDescription(PngDirectory.TAG_PALETTE_HAS_TRANSPARENCY, null, "Yes");
    }

    @Nullable
    public String getIsSrgbColorSpaceDescription()
    {
        return getIndexedDescription(
            PngDirectory.TAG_SRGB_RENDERING_INTENT,
            "Perceptual",
            "Relative Colorimetric",
            "Saturation",
            "Absolute Colorimetric"
        );
    }

    @Nullable
    public String getTextualDataDescription()
    {
        Object object = _directory.getObject(PngDirectory.TAG_TEXTUAL_DATA);
        if (object == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<KeyValuePair> keyValues = (List<KeyValuePair>)object;
        StringBuilder sb = new StringBuilder();
        for (KeyValuePair keyValue : keyValues) {
            sb.append(String.format("%s: %s\n", keyValue.getKey(), keyValue.getValue()));
        }
        return sb.toString();
    }

    @Nullable
    public String getBackgroundColorDescription()
    {
        byte[] bytes = _directory.getByteArray(PngDirectory.TAG_BACKGROUND_COLOR);
        Integer colorType = _directory.getInteger(PngDirectory.TAG_COLOR_TYPE);
        if (bytes == null || colorType == null) {
            return null;
        }
        SequentialReader reader = new SequentialByteArrayReader(bytes);
        try {
            // TODO do we need to normalise these based upon the bit depth?
            switch (colorType) {
                case 0:
                case 4:
                    return String.format("Greyscale Level %d", reader.getUInt16());
                case 2:
                case 6:
                    return String.format("R %d, G %d, B %d", reader.getUInt16(), reader.getUInt16(), reader.getUInt16());
                case 3:
                    return String.format("Palette Index %d", reader.getUInt8());
            }
        } catch (IOException ex) {
            return null;
        }
        return null;
    }
}
