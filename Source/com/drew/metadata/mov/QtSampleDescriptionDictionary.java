package com.drew.metadata.mov;

import java.util.HashMap;

public class QtSampleDescriptionDictionary
{
    public static HashMap<String, String> _dictionary = new HashMap<String, String>();

    static {
        // Video Compression Types
        _dictionary.put("cvid", "Cinepak");
        _dictionary.put("jpeg", "JPEG");
        _dictionary.put("smc ", "Graphics");
        _dictionary.put("rle ", "Animation");
        _dictionary.put("rpza", "Apple video");
        _dictionary.put("kpcd", "Kodak Photo CD");
        _dictionary.put("png ", "Portable Network Graphics");
        _dictionary.put("mjpa", "Motion-JPEG (format A)");
        _dictionary.put("mjpb", "Motion-JPEG (format B)");
        _dictionary.put("SVQ1", "Sorenson video, version 1");
        _dictionary.put("SVQ3", "Sorenson video 3");
        _dictionary.put("mp4v", "MPEG-4 video");
        _dictionary.put("avc1", "H.264 video");
        _dictionary.put("dvc ", "NTSC DV-25 video");
        _dictionary.put("dvcp", "PAL DV-25 video");
        _dictionary.put("gif ", "CompuServe Graphics Interchange Format");
        _dictionary.put("h263", "H.263 video");
        _dictionary.put("tiff", "Tagged Image File Format");
        _dictionary.put("raw ", "Uncompressed RGB");
        _dictionary.put("2vuY", "Uncompressed Y'CbCr, 8-bit-per-component 4:2:2");
        _dictionary.put("yuv2", "Uncompressed Y'CbCr, 8-bit-per-component 4:2:2");
        _dictionary.put("v308", "Uncompressed Y'CbCr, 8-bit-per-component 4:4:4");
        _dictionary.put("v408", "Uncompressed Y'CbCr, 8-bit-per-component 4:4:4:4");
        _dictionary.put("v216", "Uncompressed Y'CbCr, 10, 12, 14, or 16-bit-per-component 4:2:2");
        _dictionary.put("v410", "Uncompressed Y'CbCr, 10-bit-per-component 4:4:4");
        _dictionary.put("v210", "Uncompressed Y'CbCr, 10-bit-per-component 4:2:2");

        // Sound Audio Formats
        _dictionary.put("NONE", "");
        _dictionary.put("raw ", "Uncompressed in offset-binary format");
        _dictionary.put("twos", "Uncompressed in two's-complement format");
        _dictionary.put("sowt", "16-bit little-endian, twos-complement");
        _dictionary.put("MAC3", "MACE 3:1");
        _dictionary.put("MAC6", "MACE 6:1");
        _dictionary.put("ima4", "IMA 4:1");
        _dictionary.put("fl32", "32-bit floating point");
        _dictionary.put("fl64", "64-bit floating point");
        _dictionary.put("in24", "24-bit integer");
        _dictionary.put("in32", "32-bit integer");
        _dictionary.put("ulaw", "uLaw 2:1");
        _dictionary.put("alaw", "uLaw 2:1");
        _dictionary.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x02}), "Microsoft ADPCM-ACM code 2");
        _dictionary.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x11}), "DVI/Intel IMAADPCM-ACM code 17");
        _dictionary.put("dvca", "DV Audio");
        _dictionary.put("QDMC", "QDesign music");
        _dictionary.put("QDM2", "QDesign music version 2");
        _dictionary.put("Qclp", "QUALCOMM PureVoice");
        _dictionary.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x55}), "MPEG-1 layer 3, CBR only (pre-QT4.1)");
        _dictionary.put(".mp3", "MPEG-1 layer 3, CBR & VBR (QT4.1 and later)");
        _dictionary.put("mp4a", "MPEG-4, Advanced Audio Coding (AAC)");
        _dictionary.put("ac-3", "Digital Audio Compression Standard (AC-3, Enhanced AC-3)");
    }
}
