package com.drew.metadata.wav;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Holds basic metadata from Wav files including some ID3 tags
 *
 * @author Payton Garland
 */
public class WavDirectory extends Directory
{
    public static final int TAG_FORMAT = 1;
    public static final int TAG_CHANNELS = 2;
    public static final int TAG_SAMPLES_PER_SEC = 3;
    public static final int TAG_BYTES_PER_SEC = 4;
    public static final int TAG_BLOCK_ALIGNMENT = 5;
    public static final int TAG_BITS_PER_SAMPLE = 6;
    public static final int TAG_ARTIST = 7;
    public static final int TAG_TITLE = 8;
    public static final int TAG_PRODUCT = 9;
    public static final int TAG_TRACK_NUMBER = 10;
    public static final int TAG_DATE_CREATED = 11;
    public static final int TAG_GENRE = 12;
    public static final int TAG_COMMENTS = 13;
    public static final int TAG_COPYRIGHT = 14;
    public static final int TAG_SOFTWARE = 15;
    public static final int TAG_DURATION = 16;

    public static final String CHUNK_FORMAT = "fmt ";
    public static final String CHUNK_DATA = "data";

    public static final String LIST_INFO = "INFO";

    public static final String FORMAT = "WAVE";

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    @NotNull
    protected transient static final HashMap<String, Integer> _tagIntegerMap = new HashMap<String, Integer>();

    @NotNull
    protected transient static final HashMap<Integer, String> _audioEncodingMap = new HashMap<Integer, String>();

    static {
        _tagIntegerMap.put("IART", TAG_ARTIST);
        _tagIntegerMap.put("INAM", TAG_TITLE);
        _tagIntegerMap.put("IPRD", TAG_PRODUCT);
        _tagIntegerMap.put("ITRK", TAG_TRACK_NUMBER);
        _tagIntegerMap.put("ICRD", TAG_DATE_CREATED);
        _tagIntegerMap.put("IGNR", TAG_GENRE);
        _tagIntegerMap.put("ICMT", TAG_COMMENTS);
        _tagIntegerMap.put("ICOP", TAG_COPYRIGHT);
        _tagIntegerMap.put("ISFT", TAG_SOFTWARE);

        _tagNameMap.put(TAG_FORMAT, "Format");
        _tagNameMap.put(TAG_CHANNELS, "Channels");
        _tagNameMap.put(TAG_SAMPLES_PER_SEC, "Samples Per Second");
        _tagNameMap.put(TAG_BYTES_PER_SEC, "Bytes Per Second");
        _tagNameMap.put(TAG_BLOCK_ALIGNMENT, "Block Alignment");
        _tagNameMap.put(TAG_BITS_PER_SAMPLE, "Bits Per Sample");
        _tagNameMap.put(TAG_ARTIST, "Artist");
        _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_PRODUCT, "Product");
        _tagNameMap.put(TAG_TRACK_NUMBER, "Track Number");
        _tagNameMap.put(TAG_DATE_CREATED, "Date Created");
        _tagNameMap.put(TAG_GENRE, "Genre");
        _tagNameMap.put(TAG_COMMENTS, "Comments");
        _tagNameMap.put(TAG_COPYRIGHT, "Copyright");
        _tagNameMap.put(TAG_SOFTWARE, "Software");
        _tagNameMap.put(TAG_DURATION, "Duration");

        // Audio encoding tags from http://www.sno.phy.queensu.ca/~phil/exiftool/TagNames/RIFF.html#AudioEncoding
        _audioEncodingMap.put(0x1, "Microsoft PCM");
        _audioEncodingMap.put(0x2, "Microsoft ADPCM");
        _audioEncodingMap.put(0x3, "Microsoft IEEE float");
        _audioEncodingMap.put(0x4, "Compaq VSELP");
        _audioEncodingMap.put(0x5, "IBM CVSD");
        _audioEncodingMap.put(0x6, "Microsoft a-Law");
        _audioEncodingMap.put(0x7, "Microsoft u-Law");
        _audioEncodingMap.put(0x8, "Microsoft DTS");
        _audioEncodingMap.put(0x9, "DRM");
        _audioEncodingMap.put(0xa, "WMA 9 Speech");
        _audioEncodingMap.put(0xb, "Microsoft Windows Media RT Voice");
        _audioEncodingMap.put(0x10, "OKI-ADPCM");
        _audioEncodingMap.put(0x11, "Intel IMA/DVI-ADPCM");
        _audioEncodingMap.put(0x12, "Videologic Mediaspace ADPCM");
        _audioEncodingMap.put(0x13, "Sierra ADPCM");
        _audioEncodingMap.put(0x14, "Antex G.723 ADPCM");
        _audioEncodingMap.put(0x15, "DSP Solutions DIGISTD");
        _audioEncodingMap.put(0x16, "DSP Solutions DIGIFIX");
        _audioEncodingMap.put(0x17, "Dialoic OKI ADPCM");
        _audioEncodingMap.put(0x18, "Media Vision ADPCM");
        _audioEncodingMap.put(0x19, "HP CU");
        _audioEncodingMap.put(0x1a, "HP Dynamic Voice");
        _audioEncodingMap.put(0x20, "Yamaha ADPCM");
        _audioEncodingMap.put(0x21, "SONARC Speech Compression");
        _audioEncodingMap.put(0x22, "DSP Group True Speech");
        _audioEncodingMap.put(0x23, "Echo Speech Corp.");
        _audioEncodingMap.put(0x24, "Virtual Music Audiofile AF36");
        _audioEncodingMap.put(0x25, "Audio Processing Tech.");
        _audioEncodingMap.put(0x26, "Virtual Music Audiofile AF10");
        _audioEncodingMap.put(0x27, "Aculab Prosody 1612");
        _audioEncodingMap.put(0x28, "Merging Tech. LRC");
        _audioEncodingMap.put(0x30, "Dolby AC2");
        _audioEncodingMap.put(0x31, "Microsoft GSM610");
        _audioEncodingMap.put(0x32, "MSN Audio");
        _audioEncodingMap.put(0x33, "Antex ADPCME");
        _audioEncodingMap.put(0x34, "Control Resources VQLPC");
        _audioEncodingMap.put(0x35, "DSP Solutions DIGIREAL");
        _audioEncodingMap.put(0x36, "DSP Solutions DIGIADPCM");
        _audioEncodingMap.put(0x37, "Control Resources CR10");
        _audioEncodingMap.put(0x38, "Natural MicroSystems VBX ADPCM");
        _audioEncodingMap.put(0x39, "Crystal Semiconductor IMA ADPCM");
        _audioEncodingMap.put(0x3a, "Echo Speech ECHOSC3");
        _audioEncodingMap.put(0x3b, "Rockwell ADPCM");
        _audioEncodingMap.put(0x3c, "Rockwell DIGITALK");
        _audioEncodingMap.put(0x3d, "Xebec Multimedia");
        _audioEncodingMap.put(0x40, "Antex G.721 ADPCM");
        _audioEncodingMap.put(0x41, "Antex G.728 CELP");
        _audioEncodingMap.put(0x42, "Microsoft MSG723");
        _audioEncodingMap.put(0x43, "IBM AVC ADPCM");
        _audioEncodingMap.put(0x45, "ITU-T G.726");
        _audioEncodingMap.put(0x50, "Microsoft MPEG");
        _audioEncodingMap.put(0x51, "RT23 or PAC");
        _audioEncodingMap.put(0x52, "InSoft RT24");
        _audioEncodingMap.put(0x53, "InSoft PAC");
        _audioEncodingMap.put(0x55, "MP3");
        _audioEncodingMap.put(0x59, "Cirrus");
        _audioEncodingMap.put(0x60, "Cirrus Logic");
        _audioEncodingMap.put(0x61, "ESS Tech. PCM");
        _audioEncodingMap.put(0x62, "Voxware Inc.");
        _audioEncodingMap.put(0x63, "Canopus ATRAC");
        _audioEncodingMap.put(0x64, "APICOM G.726 ADPCM");
        _audioEncodingMap.put(0x65, "APICOM G.722 ADPCM");
        _audioEncodingMap.put(0x66, "Microsoft DSAT");
        _audioEncodingMap.put(0x67, "Micorsoft DSAT DISPLAY");
        _audioEncodingMap.put(0x69, "Voxware Byte Aligned");
        _audioEncodingMap.put(0x70, "Voxware AC8");
        _audioEncodingMap.put(0x71, "Voxware AC10");
        _audioEncodingMap.put(0x72, "Voxware AC16");
        _audioEncodingMap.put(0x73, "Voxware AC20");
        _audioEncodingMap.put(0x74, "Voxware MetaVoice");
        _audioEncodingMap.put(0x75, "Voxware MetaSound");
        _audioEncodingMap.put(0x76, "Voxware RT29HW");
        _audioEncodingMap.put(0x77, "Voxware VR12");
        _audioEncodingMap.put(0x78, "Voxware VR18");
        _audioEncodingMap.put(0x79, "Voxware TQ40");
        _audioEncodingMap.put(0x7a, "Voxware SC3");
        _audioEncodingMap.put(0x7b, "Voxware SC3");
        _audioEncodingMap.put(0x80, "Soundsoft");
        _audioEncodingMap.put(0x81, "Voxware TQ60");
        _audioEncodingMap.put(0x82, "Microsoft MSRT24");
        _audioEncodingMap.put(0x83, "AT&T G.729A");
        _audioEncodingMap.put(0x84, "Motion Pixels MVI MV12");
        _audioEncodingMap.put(0x85, "DataFusion G.726");
        _audioEncodingMap.put(0x86, "DataFusion GSM610");
        _audioEncodingMap.put(0x88, "Iterated Systems Audio");
        _audioEncodingMap.put(0x89, "Onlive");
        _audioEncodingMap.put(0x8a, "Multitude, Inc. FT SX20");
        _audioEncodingMap.put(0x8b, "Infocom ITS A/S G.721 ADPCM");
        _audioEncodingMap.put(0x8c, "Convedia G729");
        _audioEncodingMap.put(0x8d, "Not specified congruency, Inc.");
        _audioEncodingMap.put(0x91, "Siemens SBC24");
        _audioEncodingMap.put(0x92, "Sonic Foundry Dolby AC3 APDIF");
        _audioEncodingMap.put(0x93, "MediaSonic G.723");
        _audioEncodingMap.put(0x94, "Aculab Prosody 8kbps");
        _audioEncodingMap.put(0x97, "ZyXEL ADPCM");
        _audioEncodingMap.put(0x98, "Philips LPCBB");
        _audioEncodingMap.put(0x99, "Studer Professional Audio Packed");
        _audioEncodingMap.put(0xa0, "Malden PhonyTalk");
        _audioEncodingMap.put(0xa1, "Racal Recorder GSM");
        _audioEncodingMap.put(0xa2, "Racal Recorder G720.a");
        _audioEncodingMap.put(0xa3, "Racal G723.1");
        _audioEncodingMap.put(0xa4, "Racal Tetra ACELP");
        _audioEncodingMap.put(0xb0, "NEC AAC NEC Corporation");
        _audioEncodingMap.put(0xff, "AAC");
        _audioEncodingMap.put(0x100, "Rhetorex ADPCM");
        _audioEncodingMap.put(0x101, "IBM u-Law");
        _audioEncodingMap.put(0x102, "IBM a-Law");
        _audioEncodingMap.put(0x103, "IBM ADPCM");
        _audioEncodingMap.put(0x111, "Vivo G.723");
        _audioEncodingMap.put(0x112, "Vivo Siren");
        _audioEncodingMap.put(0x120, "Philips Speech Processing CELP");
        _audioEncodingMap.put(0x121, "Philips Speech Processing GRUNDIG");
        _audioEncodingMap.put(0x123, "Digital G.723");
        _audioEncodingMap.put(0x125, "Sanyo LD ADPCM");
        _audioEncodingMap.put(0x130, "Sipro Lab ACEPLNET");
        _audioEncodingMap.put(0x131, "Sipro Lab ACELP4800");
        _audioEncodingMap.put(0x132, "Sipro Lab ACELP8V3");
        _audioEncodingMap.put(0x133, "Sipro Lab G.729");
        _audioEncodingMap.put(0x134, "Sipro Lab G.729A");
        _audioEncodingMap.put(0x135, "Sipro Lab Kelvin");
        _audioEncodingMap.put(0x136, "VoiceAge AMR");
        _audioEncodingMap.put(0x140, "Dictaphone G.726 ADPCM");
        _audioEncodingMap.put(0x150, "Qualcomm PureVoice");
        _audioEncodingMap.put(0x151, "Qualcomm HalfRate");
        _audioEncodingMap.put(0x155, "Ring Zero Systems TUBGSM");
        _audioEncodingMap.put(0x160, "Microsoft Audio1");
        _audioEncodingMap.put(0x161, "Windows Media Audio V2 V7 V8 V9 / DivX audio (WMA) / Alex AC3 Audio");
        _audioEncodingMap.put(0x162, "Windows Media Audio Professional V9");
        _audioEncodingMap.put(0x163, "Windows Media Audio Lossless V9");
        _audioEncodingMap.put(0x164, "WMA Pro over S/PDIF");
        _audioEncodingMap.put(0x170, "UNISYS NAP ADPCM");
        _audioEncodingMap.put(0x171, "UNISYS NAP ULAW");
        _audioEncodingMap.put(0x172, "UNISYS NAP ALAW");
        _audioEncodingMap.put(0x173, "UNISYS NAP 16K");
        _audioEncodingMap.put(0x174, "MM SYCOM ACM SYC008 SyCom Technologies");
        _audioEncodingMap.put(0x175, "MM SYCOM ACM SYC701 G726L SyCom Technologies");
        _audioEncodingMap.put(0x176, "MM SYCOM ACM SYC701 CELP54 SyCom Technologies");
        _audioEncodingMap.put(0x177, "MM SYCOM ACM SYC701 CELP68 SyCom Technologies");
        _audioEncodingMap.put(0x178, "Knowledge Adventure ADPCM");
        _audioEncodingMap.put(0x180, "Fraunhofer IIS MPEG2AAC");
        _audioEncodingMap.put(0x190, "Digital Theater Systems DTS DS");
        _audioEncodingMap.put(0x200, "Creative Labs ADPCM");
        _audioEncodingMap.put(0x202, "Creative Labs FASTSPEECH8");
        _audioEncodingMap.put(0x203, "Creative Labs FASTSPEECH10");
        _audioEncodingMap.put(0x210, "UHER ADPCM");
        _audioEncodingMap.put(0x215, "Ulead DV ACM");
        _audioEncodingMap.put(0x216, "Ulead DV ACM");
        _audioEncodingMap.put(0x220, "Quarterdeck Corp.");
        _audioEncodingMap.put(0x230, "I-Link VC");
        _audioEncodingMap.put(0x240, "Aureal Semiconductor Raw Sport");
        _audioEncodingMap.put(0x241, "ESST AC3");
        _audioEncodingMap.put(0x250, "Interactive Products HSX");
        _audioEncodingMap.put(0x251, "Interactive Products RPELP");
        _audioEncodingMap.put(0x260, "Consistent CS2");
        _audioEncodingMap.put(0x270, "Sony SCX");
        _audioEncodingMap.put(0x271, "Sony SCY");
        _audioEncodingMap.put(0x272, "Sony ATRAC3");
        _audioEncodingMap.put(0x273, "Sony SPC");
        _audioEncodingMap.put(0x280, "TELUM Telum Inc.");
        _audioEncodingMap.put(0x281, "TELUMIA Telum Inc.");
        _audioEncodingMap.put(0x285, "Norcom Voice Systems ADPCM");
        _audioEncodingMap.put(0x300, "Fujitsu FM TOWNS SND");
        _audioEncodingMap.put(0x301, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x302, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x303, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x304, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x305, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x306, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x307, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x308, "Fujitsu (not specified)");
        _audioEncodingMap.put(0x350, "Micronas Semiconductors, Inc. Development");
        _audioEncodingMap.put(0x351, "Micronas Semiconductors, Inc. CELP833");
        _audioEncodingMap.put(0x400, "Brooktree Digital");
        _audioEncodingMap.put(0x401, "Intel Music Coder (IMC)");
        _audioEncodingMap.put(0x402, "Ligos Indeo Audio");
        _audioEncodingMap.put(0x450, "QDesign Music");
        _audioEncodingMap.put(0x500, "On2 VP7 On2 Technologies");
        _audioEncodingMap.put(0x501, "On2 VP6 On2 Technologies");
        _audioEncodingMap.put(0x680, "AT&T VME VMPCM");
        _audioEncodingMap.put(0x681, "AT&T TCP");
        _audioEncodingMap.put(0x700, "YMPEG Alpha (dummy for MPEG-2 compressor)");
        _audioEncodingMap.put(0x8ae, "ClearJump LiteWave (lossless)");
        _audioEncodingMap.put(0x1000, "Olivetti GSM");
        _audioEncodingMap.put(0x1001, "Olivetti ADPCM");
        _audioEncodingMap.put(0x1002, "Olivetti CELP");
        _audioEncodingMap.put(0x1003, "Olivetti SBC");
        _audioEncodingMap.put(0x1004, "Olivetti OPR");
        _audioEncodingMap.put(0x1100, "Lernout & Hauspie");
        _audioEncodingMap.put(0x1101, "Lernout & Hauspie CELP codec");
        _audioEncodingMap.put(0x1102, "Lernout & Hauspie SBC codec");
        _audioEncodingMap.put(0x1103, "Lernout & Hauspie SBC codec");
        _audioEncodingMap.put(0x1104, "Lernout & Hauspie SBC codec");
        _audioEncodingMap.put(0x1400, "Norris Comm. Inc.");
        _audioEncodingMap.put(0x1401, "ISIAudio");
        _audioEncodingMap.put(0x1500, "AT&T Soundspace Music Compression");
        _audioEncodingMap.put(0x181c, "VoxWare RT24 speech codec");
        _audioEncodingMap.put(0x181e, "Lucent elemedia AX24000P Music codec");
        _audioEncodingMap.put(0x1971, "Sonic Foundry LOSSLESS");
        _audioEncodingMap.put(0x1979, "Innings Telecom Inc. ADPCM");
        _audioEncodingMap.put(0x1c07, "Lucent SX8300P speech codec");
        _audioEncodingMap.put(0x1c0c, "Lucent SX5363S G.723 compliant codec");
        _audioEncodingMap.put(0x1f03, "CUseeMe DigiTalk (ex-Rocwell)");
        _audioEncodingMap.put(0x1fc4, "NCT Soft ALF2CD ACM");
        _audioEncodingMap.put(0x2000, "FAST Multimedia DVM");
        _audioEncodingMap.put(0x2001, "Dolby DTS (Digital Theater System)");
        _audioEncodingMap.put(0x2002, "RealAudio 1 / 2 14.4");
        _audioEncodingMap.put(0x2003, "RealAudio 1 / 2 28.8");
        _audioEncodingMap.put(0x2004, "RealAudio G2 / 8 Cook (low bitrate)");
        _audioEncodingMap.put(0x2005, "RealAudio 3 / 4 / 5 Music (DNET)");
        _audioEncodingMap.put(0x2006, "RealAudio 10 AAC (RAAC)");
        _audioEncodingMap.put(0x2007, "RealAudio 10 AAC+ (RACP)");
        _audioEncodingMap.put(0x2500, "Reserved range to 0x2600 Microsoft");
        _audioEncodingMap.put(0x3313, "makeAVIS (ffvfw fake AVI sound from AviSynth scripts)");
        _audioEncodingMap.put(0x4143, "Divio MPEG-4 AAC audio");
        _audioEncodingMap.put(0x4201, "Nokia adaptive multirate");
        _audioEncodingMap.put(0x4243, "Divio G726 Divio, Inc.");
        _audioEncodingMap.put(0x434c, "LEAD Speech");
        _audioEncodingMap.put(0x564c, "LEAD Vorbis");
        _audioEncodingMap.put(0x5756, "WavPack Audio");
        _audioEncodingMap.put(0x674f, "Ogg Vorbis (mode 1)");
        _audioEncodingMap.put(0x6750, "Ogg Vorbis (mode 2)");
        _audioEncodingMap.put(0x6751, "Ogg Vorbis (mode 3)");
        _audioEncodingMap.put(0x676f, "Ogg Vorbis (mode 1+)");
        _audioEncodingMap.put(0x6770, "Ogg Vorbis (mode 2+)");
        _audioEncodingMap.put(0x6771, "Ogg Vorbis (mode 3+)");
        _audioEncodingMap.put(0x7000, "3COM NBX 3Com Corporation");
        _audioEncodingMap.put(0x706d, "FAAD AAC");
        _audioEncodingMap.put(0x7a21, "GSM-AMR (CBR, no SID)");
        _audioEncodingMap.put(0x7a22, "GSM-AMR (VBR, including SID)");
        _audioEncodingMap.put(0xa100, "Comverse Infosys Ltd. G723 1");
        _audioEncodingMap.put(0xa101, "Comverse Infosys Ltd. AVQSBC");
        _audioEncodingMap.put(0xa102, "Comverse Infosys Ltd. OLDSBC");
        _audioEncodingMap.put(0xa103, "Symbol Technologies G729A");
        _audioEncodingMap.put(0xa104, "VoiceAge AMR WB VoiceAge Corporation");
        _audioEncodingMap.put(0xa105, "Ingenient Technologies Inc. G726");
        _audioEncodingMap.put(0xa106, "ISO/MPEG-4 advanced audio Coding");
        _audioEncodingMap.put(0xa107, "Encore Software Ltd G726");
        _audioEncodingMap.put(0xa109, "Speex ACM Codec xiph.org");
        _audioEncodingMap.put(0xdfac, "DebugMode SonicFoundry Vegas FrameServer ACM Codec");
        _audioEncodingMap.put(0xe708, "Unknown -");
        _audioEncodingMap.put(0xf1ac, "Free Lossless Audio Codec FLAC");
        _audioEncodingMap.put(0xfffe, "Extensible");
        _audioEncodingMap.put(0xffff, "Development");
    }

    public WavDirectory()
    {
        this.setDescriptor(new WavDescriptor(this));
    }

    @NotNull
    @Override
    public String getName()
    {
        return "WAV";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
