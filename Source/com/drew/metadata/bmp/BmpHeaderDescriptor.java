package com.drew.metadata.bmp;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.TagDescriptor;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class BmpHeaderDescriptor extends TagDescriptor<BmpHeaderDirectory>
{
    public BmpHeaderDescriptor(@NotNull BmpHeaderDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case BmpHeaderDirectory.TAG_COMPRESSION:
                return getCompressionDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    @Nullable
    public String getCompressionDescription()
    {
        // 0 = None
        // 1 = RLE 8-bit/pixel
        // 2 = RLE 4-bit/pixel
        // 3 = Bit field (or Huffman 1D if BITMAPCOREHEADER2 (size 64))
        // 4 = JPEG (or RLE-24 if BITMAPCOREHEADER2 (size 64))
        // 5 = PNG
        // 6 = Bit field
        try {
            Integer value = _directory.getInteger(BmpHeaderDirectory.TAG_COMPRESSION);
            if (value == null)
                return null;
            Integer headerSize = _directory.getInteger(BmpHeaderDirectory.TAG_HEADER_SIZE);
            if (headerSize == null)
                return null;

            switch (value) {
                case 0: return "None";
                case 1: return "RLE 8-bit/pixel";
                case 2: return "RLE 4-bit/pixel";
                case 3: return headerSize == 64 ? "Bit field" : "Huffman 1D";
                case 4: return headerSize == 64 ? "JPEG" : "RLE-24";
                case 5: return "PNG";
                case 6: return "Bit field";
                default:
                    return super.getDescription(BmpHeaderDirectory.TAG_COMPRESSION);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
