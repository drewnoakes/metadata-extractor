package com.drew.metadata.mov;

import java.util.HashMap;
import java.util.Map;

public class CodecTypes {

    private static String[][] audioCodecList = new String[][] {
        {".mp3","MPEG3 alias"},
        {"aac ","ISO/IEC 14496-3 AAC"},
        {"agsm","Apple GSM 10:1"},
        {"alac","Apple Lossless Audio Codec"},
        {"alaw","A-law 2:1"},
        {"conv","Sample Format"},
        {"dvca","DV"},
        {"dvi ","DV 4:1"},
        {"eqal","Frequency Equalizer"},
        {"fl32","32-bit Floating Point"},
        {"fl64","64-bit Floating Point"},
        {"ima4","Interactive Multimedia Association 4:1"},
        {"in24","24-bit Integer"},
        {"in32","32-bit Integer"},
        {"lpc ","LPC 23:1"},
        {"MAC3","(MACE) 3:1"},
        {"MAC6","(MACE) 6:1"},
        {"mixb","8-bit Mixer"},
        {"mixw","16-bit Mixer"},
        {"mp4a","AAC"},
//		{"ms" + 0x00 + "U" ,"Microsoft ADPCM"},
        {new String(new byte[] {0x6d, 0x73, 0x00, 0x11}), "DV IMA"},
        {new String(new byte[] {0x6d, 0x73, 0x00, 0x02}), "Microsoft ADPCM"},
        {new String(new byte[] {0x6d, 0x73, 0x00, 0x55}), "MPEG3"},
        {new String(new byte[] {0x4d, 0x53, 0x00, 0x02}), "Microsoft ADPCM"},
        {new String(new byte[] {0x4d, 0x53, 0x00, 0x11}), "DV IMA"},
        {new String(new byte[] {0x4d, 0x53, 0x00, 0x55}), "MPEG3"},
//		{"MS'."\x00\x02"] = 'Microsoft ADPCM"},
//		{"MS'."\x00\x11"] = 'DV IMA"},
//		{"MS'."\x00\x55"] = 'Fraunhofer MPEG Layer III"},
        {"NONE","No Encoding"},
        {"Qclp","Qualcomm PureVoice"},
        {"QDM2","QDesign Music 2"},
        {"QDMC","QDesign Music 1"},
        {"ratb","8-bit Rate"},
        {"ratw","16-bit Rate"},
        {"raw ","raw PCM"},
        {"sour","Sound Source"},
        {"sowt","16 bit (Little Endian)"},
        {"str1","Iomega MPEG layer II"},
        {"str2","Iomega MPEG *layer II"},
        {"str3","Iomega MPEG **layer II"},
        {"str4","Iomega MPEG ***layer II"},
        {"twos","16 bit (Big Endian)"},
        {"ulaw","mu-law 2:1"}
    };

    private static String[][] videoCodecList = new String[][] {
        {"3IVX","3ivx MPEG-4"},
        {"3IV1","3ivx MPEG-4 v1"},
        {"3IV2","3ivx MPEG-4 v2"},
        {"avr ","AVR-JPEG"},
        {"base","Base"},
        {"WRLE","BMP"},
        {"cvid","Cinepak"},
        {"clou","Cloud"},
        {"cmyk","CMYK"},
        {"yuv2","ComponentVideo"},
        {"yuvu","ComponentVideoSigned"},
        {"yuvs","ComponentVideoUnsigned"},
        {"dvc ","DVC-NTSC"},
        {"dvcp","DVC-PAL"},
        {"dvpn","DVCPro-NTSC"},
        {"dvpp","DVCPro-PAL"},
        {"fire","Fire"},
        {"flic","FLC"},
        {"b48r","48RGB"},
        {"gif ","GIF"},
        {"smc ","Graphics"},
        {"h261"," Apple H261"},
        {"h263","Apple VC H.263"},
        {"IV41","Indeo4"},
        {"jpeg","JPEG"},
        {"PNTG","MacPaint"},
        {"msvc","Microsoft Video1"},
        {"mjpa","Apple Motion JPEG-A"},
        {"mjpb","Apple Motion JPEG-B"},
        {"myuv","MPEG YUV420"},
        {"dmb1","OpenDML JPEG"},
        {"kpcd","PhotoCD"},
        {"8BPS","Planar RGB"},
        {"png ","PNG"},
        {"qdrw","QuickDraw"},
        {"qdgx","QuickDrawGX"},
        {"raw ","RAW"},
        {".SGI","SGI"},
        {"b16g","16Gray"},
        {"b64a","64ARGB"},
        {"SVQ1","Sorenson Video 1"},
        {"SVQ3","Sorenson Video 3"},
        {"syv9","Sorenson YUV9"},
        {"tga ","Targa"},
        {"b32a","32AlphaGray"},
        {"tiff","TIFF"},
        {"path","Vector"},
        {"rpza","Video (Road Pizza)"},
        {"ripl","WaterRipple"},
        {"WRAW","Windows RAW"},
        {"y420","YUV420"},
        {"avc1","H.264"},
        {"mp4v","MPEG-4"},
        {"MP4V","MPEG-4"},
        {"dvhp","DVCPRO HD 720p60"},
        {"hdv2","HDV 1080i60"},
        {"dvc+","DV/DVCPRO - NTSC"},

        {"mx5p","MPEG2 IMX 635/50 50mb/s"},
        {"mx3n","MPEG2 IMX 635/50 30mb/s"},
        {"dv5p","DVCPRO50"},
        {"hdv3","HDV Final Cut Pro"},
        {"rle ","Animation"},
        {"rle ","Animation"},
        {"2vuY","Uncompressed Y'CbCr, 8-bit-per-component 4:2:2"},
        {"v308","Uncompressed Y'CbCr, 8-bit-per-component 4:4:4"},
        {"v408","Uncompressed Y'CbCr, 8-bit-per-component 4:4:4:4"},
        {"v216","Uncompressed Y'CbCr, 10, 12, 14, or 16-bit-per-component 4:2:2"},
        {"v410","Uncompressed Y'CbCr, 10-bit-per-component 4:4:4"},
        {"v210","Uncompressed Y'CbCr, 10-bit-per-component 4:2:2"}

    };

    public static Map<String, String> audioCodecs = new HashMap<String, String>();
    static {
        for (int i=0; i<audioCodecList.length; i++)
        {
            audioCodecs.put(audioCodecList[i][0], audioCodecList[i][1]);
        }
    };

    public static Map<String, String> videoCodecs = new HashMap<String, String>();
    static {
        for (int i=0; i<videoCodecList.length; i++)
        {
            videoCodecs.put(videoCodecList[i][0], videoCodecList[i][1]);
        }
    };

}
