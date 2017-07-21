package com.drew.metadata.mov;

import java.util.HashMap;

public class QtDictionary
{
    private static HashMap<Integer, HashMap<String, String>> _dictionary = new HashMap<Integer, HashMap<String, String>>();

    private static HashMap<String, String> _majorBrands = new HashMap<String, String>();

    private static HashMap<String, String> _videoCompressionTypes = new HashMap<String, String>();

    private static HashMap<String, String> _soundAudioFormats = new HashMap<String, String>();

    public static HashMap<String, String> _userDataTypes = new HashMap<String, String>();

    public static HashMap<String, String> _metadataKeys = new HashMap<String, String>();

    static {
        _dictionary.put(QtDirectory.TAG_MAJOR_BRAND, _majorBrands);
        _dictionary.put(QtDirectory.TAG_COMPRESSION_TYPE, _videoCompressionTypes);
        _dictionary.put(QtDirectory.TAG_AUDIO_FORMAT, _soundAudioFormats);

        // Video Compression Types
        _videoCompressionTypes.put("cvid", "Cinepak");
        _videoCompressionTypes.put("jpeg", "JPEG");
        _videoCompressionTypes.put("smc ", "Graphics");
        _videoCompressionTypes.put("rle ", "Animation");
        _videoCompressionTypes.put("rpza", "Apple video");
        _videoCompressionTypes.put("kpcd", "Kodak Photo CD");
        _videoCompressionTypes.put("png ", "Portable Network Graphics");
        _videoCompressionTypes.put("mjpa", "Motion-JPEG (format A)");
        _videoCompressionTypes.put("mjpb", "Motion-JPEG (format B)");
        _videoCompressionTypes.put("SVQ1", "Sorenson video, version 1");
        _videoCompressionTypes.put("SVQ3", "Sorenson video 3");
        _videoCompressionTypes.put("mp4v", "MPEG-4 video");
        _videoCompressionTypes.put("avc1", "H.264 video");
        _videoCompressionTypes.put("dvc ", "NTSC DV-25 video");
        _videoCompressionTypes.put("dvcp", "PAL DV-25 video");
        _videoCompressionTypes.put("gif ", "CompuServe Graphics Interchange Format");
        _videoCompressionTypes.put("h263", "H.263 video");
        _videoCompressionTypes.put("tiff", "Tagged Image File Format");
        _videoCompressionTypes.put("raw ", "Uncompressed RGB");
        _videoCompressionTypes.put("2vuY", "Uncompressed Y'CbCr, 8-bit-per-component 4:2:2");
        _videoCompressionTypes.put("yuv2", "Uncompressed Y'CbCr, 8-bit-per-component 4:2:2");
        _videoCompressionTypes.put("v308", "Uncompressed Y'CbCr, 8-bit-per-component 4:4:4");
        _videoCompressionTypes.put("v408", "Uncompressed Y'CbCr, 8-bit-per-component 4:4:4:4");
        _videoCompressionTypes.put("v216", "Uncompressed Y'CbCr, 10, 12, 14, or 16-bit-per-component 4:2:2");
        _videoCompressionTypes.put("v410", "Uncompressed Y'CbCr, 10-bit-per-component 4:4:4");
        _videoCompressionTypes.put("v210", "Uncompressed Y'CbCr, 10-bit-per-component 4:2:2");

        // Sound Audio Formats
        _soundAudioFormats.put("NONE", "");
        _soundAudioFormats.put("raw ", "Uncompressed in offset-binary format");
        _soundAudioFormats.put("twos", "Uncompressed in two's-complement format");
        _soundAudioFormats.put("sowt", "16-bit little-endian, twos-complement");
        _soundAudioFormats.put("MAC3", "MACE 3:1");
        _soundAudioFormats.put("MAC6", "MACE 6:1");
        _soundAudioFormats.put("ima4", "IMA 4:1");
        _soundAudioFormats.put("fl32", "32-bit floating point");
        _soundAudioFormats.put("fl64", "64-bit floating point");
        _soundAudioFormats.put("in24", "24-bit integer");
        _soundAudioFormats.put("in32", "32-bit integer");
        _soundAudioFormats.put("ulaw", "uLaw 2:1");
        _soundAudioFormats.put("alaw", "uLaw 2:1");
        _soundAudioFormats.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x02}), "Microsoft ADPCM-ACM code 2");
        _soundAudioFormats.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x11}), "DVI/Intel IMAADPCM-ACM code 17");
        _soundAudioFormats.put("dvca", "DV Audio");
        _soundAudioFormats.put("QDMC", "QDesign music");
        _soundAudioFormats.put("QDM2", "QDesign music version 2");
        _soundAudioFormats.put("Qclp", "QUALCOMM PureVoice");
        _soundAudioFormats.put(new String(new byte[]{0x6D, 0x73, 0x00, 0x55}), "MPEG-1 layer 3, CBR only (pre-QT4.1)");
        _soundAudioFormats.put(".mp3", "MPEG-1 layer 3, CBR & VBR (QT4.1 and later)");
        _soundAudioFormats.put("mp4a", "MPEG-4, Advanced Audio Coding (AAC)");
        _soundAudioFormats.put("ac-3", "Digital Audio Compression Standard (AC-3, Enhanced AC-3)");

        // Major Brands
        _majorBrands.put("3g2a", "3GPP2 Media (.3G2) compliant with 3GPP2 C.S0050-0 V1.0");
        _majorBrands.put("3g2b", "3GPP2 Media (.3G2) compliant with 3GPP2 C.S0050-A V1.0.0");
        _majorBrands.put("3g2c", "3GPP2 Media (.3G2) compliant with 3GPP2 C.S0050-B v1.0");
        _majorBrands.put("3ge6", "3GPP (.3GP) Release 6 MBMS Extended Presentations");
        _majorBrands.put("3ge7", "3GPP (.3GP) Release 7 MBMS Extended Presentations");
        _majorBrands.put("3gg6", "3GPP Release 6 General Profile");
        _majorBrands.put("3gp1", "3GPP Media (.3GP) Release 1 (probably non-existent)");
        _majorBrands.put("3gp2", "3GPP Media (.3GP) Release 2 (probably non-existent)");
        _majorBrands.put("3gp3", "3GPP Media (.3GP) Release 3 (probably non-existent)");
        _majorBrands.put("3gp4", "3GPP Media (.3GP) Release 4");
        _majorBrands.put("3gp5", "3GPP Media (.3GP) Release 5");
        _majorBrands.put("3gp6", "3GPP Media (.3GP) Release 6 Basic Profile");
        _majorBrands.put("3gp6", "3GPP Media (.3GP) Release 6 Progressive Download");
        _majorBrands.put("3gp6", "3GPP Media (.3GP) Release 6 Streaming Servers");
        _majorBrands.put("3gs7", "3GPP Media (.3GP) Release 7 Streaming Servers");
        _majorBrands.put("avc1", "MP4 Base w/ AVC ext [ISO 14496-12:2005]");
        _majorBrands.put("CAEP", "Canon Digital Camera");
        _majorBrands.put("caqv", "Casio Digital Camera");
        _majorBrands.put("CDes", "Convergent Design");
        _majorBrands.put("da0a", "DMB MAF w/ MPEG Layer II aud, MOT slides, DLS, JPG/PNG/MNG images");
        _majorBrands.put("da0b", "DMB MAF, extending DA0A, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("da1a", "DMB MAF audio with ER-BSAC audio, JPG/PNG/MNG images");
        _majorBrands.put("da1b", "DMB MAF, extending da1a, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("da2a", "DMB MAF aud w/ HE-AAC v2 aud, MOT slides, DLS, JPG/PNG/MNG images");
        _majorBrands.put("da2b", "DMB MAF, extending da2a, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("da3a", "DMB MAF aud with HE-AAC aud, JPG/PNG/MNG images");
        _majorBrands.put("da3b", "DMB MAF, extending da3a w/ BIFS, 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("dmb1", "DMB MAF supporting all the components defined in the specification");
        _majorBrands.put("dmpf", "Digital Media Project");
        _majorBrands.put("drc1", "Dirac (wavelet compression), encapsulated in ISO base media (MP4)");
        _majorBrands.put("dv1a", "DMB MAF vid w/ AVC vid, ER-BSAC aud, BIFS, JPG/PNG/MNG images, TS");
        _majorBrands.put("dv1b", "DMB MAF, extending dv1a, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("dv2a", "DMB MAF vid w/ AVC vid, HE-AAC v2 aud, BIFS, JPG/PNG/MNG images, TS");
        _majorBrands.put("dv2b", "DMB MAF, extending dv2a, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("dv3a", "DMB MAF vid w/ AVC vid, HE-AAC aud, BIFS, JPG/PNG/MNG images, TS");
        _majorBrands.put("dv3b", "DMB MAF, extending dv3a, with 3GPP timed text, DID, TVA, REL, IPMP");
        _majorBrands.put("dvr1", "DVB (.DVB) over RTP");
        _majorBrands.put("dvt1", "DVB (.DVB) over MPEG-2 Transport Stream");
        _majorBrands.put("F4V ", "Video for Adobe Flash Player 9+ (.F4V)");
        _majorBrands.put("F4P ", "Protected Video for Adobe Flash Player 9+ (.F4P)");
        _majorBrands.put("F4A ", "Audio for Adobe Flash Player 9+ (.F4A)");
        _majorBrands.put("F4B ", "Audio Book for Adobe Flash Player 9+ (.F4B)");
        _majorBrands.put("isc2", "ISMACryp 2.0 Encrypted File");
        _majorBrands.put("iso2", "MP4 Base Media v2 [ISO 14496-12:2005]");
        _majorBrands.put("isom", "MP4  Base Media v1 [IS0 14496-12:2003]");
        _majorBrands.put("JP2 ", "JPEG 2000 Image (.JP2) [ISO 15444-1 ?]");
        _majorBrands.put("JP20", "Unknown, from GPAC samples (prob non-existent)");
        _majorBrands.put("jpm ", "JPEG 2000 Compound Image (.JPM) [ISO 15444-6]");
        _majorBrands.put("jpx ", "JPEG 2000 w/ extensions (.JPX) [ISO 15444-2]");
        _majorBrands.put("KDDI", "3GPP2 EZmovie for KDDI 3G cellphones");
        _majorBrands.put("M4A ", "Apple iTunes AAC-LC (.M4A) Audio");
        _majorBrands.put("M4B ", "Apple iTunes AAC-LC (.M4B) Audio Book");
        _majorBrands.put("M4P ", "Apple iTunes AAC-LC (.M4P) AES Protected Audio");
        _majorBrands.put("M4V ", "Apple iTunes Video (.M4V) Video");
        _majorBrands.put("M4VH", "Apple TV (.M4V)");
        _majorBrands.put("M4VP", "Apple iPhone (.M4V)");
        _majorBrands.put("mj2s", "Motion JPEG 2000 [ISO 15444-3] Simple Profile");
        _majorBrands.put("mjp2", "Motion JPEG 2000 [ISO 15444-3] General Profile");
        _majorBrands.put("mmp4", "MPEG-4/3GPP Mobile Profile (.MP4 / .3GP) (for NTT)");
        _majorBrands.put("mp21", "MPEG-21 [ISO/IEC 21000-9]");
        _majorBrands.put("mp41", "MP4 v1 [ISO 14496-1:ch13]");
        _majorBrands.put("mp42", "MP4 v2 [ISO 14496-14]");
        _majorBrands.put("mp71", "MP4 w/ MPEG-7 Metadata [per ISO 14496-12]");
        _majorBrands.put("MPPI", "Photo Player, MAF [ISO/IEC 23000-3]");
        _majorBrands.put("mqt ", "Sony / Mobile QuickTime (.MQV)  US Patent 7,477,830 (Sony Corp)");
        _majorBrands.put("MSNV", "MPEG-4 (.MP4) for SonyPSP");
        _majorBrands.put("NDAS", "MP4 v2 [ISO 14496-14] Nero Digital AAC Audio");
        _majorBrands.put("NDSC", "MPEG-4 (.MP4) Nero Cinema Profile");
        _majorBrands.put("NDSH", "MPEG-4 (.MP4) Nero HDTV Profile");
        _majorBrands.put("NDSM", "MPEG-4 (.MP4) Nero Mobile Profile");
        _majorBrands.put("NDSP", "MPEG-4 (.MP4) Nero Portable Profile");
        _majorBrands.put("NDSS", "MPEG-4 (.MP4) Nero Standard Profile");
        _majorBrands.put("NDXC", "H.264/MPEG-4 AVC (.MP4) Nero Cinema Profile");
        _majorBrands.put("NDXH", "H.264/MPEG-4 AVC (.MP4) Nero HDTV Profile");
        _majorBrands.put("NDXM", "H.264/MPEG-4 AVC (.MP4) Nero Mobile Profile");
        _majorBrands.put("NDXP", "H.264/MPEG-4 AVC (.MP4) Nero Portable Profile");
        _majorBrands.put("NDXS", "H.264/MPEG-4 AVC (.MP4) Nero Standard Profile");
        _majorBrands.put("odcf", "OMA DCF DRM Format 2.0 (OMA-TS-DRM-DCF-V2_0-20060303-A)");
        _majorBrands.put("opf2", "OMA PDCF DRM Format 2.1 (OMA-TS-DRM-DCF-V2_1-20070724-C)");
        _majorBrands.put("opx2", "OMA PDCF DRM + XBS extensions (OMA-TS-DRM_XBS-V1_0-20070529-C)");
        _majorBrands.put("pana", "Panasonic Digital Camera");
        _majorBrands.put("qt  ", "Apple QuickTime (.MOV/QT)");
        _majorBrands.put("ROSS", "Ross Video");
        _majorBrands.put("sdv ", "SD Memory Card Video");
        _majorBrands.put("ssc1", "Samsung stereoscopic, single stream (patent pending, see notes)");
        _majorBrands.put("ssc2", "Samsung stereoscopic, dual stream (patent pending, see notes)");
    }

    public static String lookup(int scope, String lookup)
    {
        String results;
        if (_dictionary.containsKey(scope) && _dictionary.get(scope).containsKey(lookup)) {
            results = _dictionary.get(scope).get(lookup);
        } else {
            results = null;
        }
        return (results == null) ? lookup : results;
    }
}
