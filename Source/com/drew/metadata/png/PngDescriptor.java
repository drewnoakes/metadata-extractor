package com.drew.metadata.png;

import com.drew.imaging.png.PngColorType;
import com.drew.lang.KeyValuePair;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

import java.util.List;

/**
 * @author Drew Noakes http://drewnoakes.com
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
        return getIndexedDescription(PngDirectory.TAG_INTERLACE_METHOD, null, "Yes");
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
        if (object == null)
            return null;
        List<KeyValuePair> keyValues = (List<KeyValuePair>)object;
        StringBuilder sb = new StringBuilder();
        for (KeyValuePair keyValue : keyValues) {
            sb.append(String.format("%s: %s\n", keyValue.getKey(), keyValue.getValue()));
        }
        return sb.toString();
    }
}
