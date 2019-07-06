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
package com.drew.metadata.icc;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Yuri Binev
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class IccDirectory extends Directory
{
    // These (smaller valued) tags have an integer value that's equal to their offset within the ICC data buffer.

    public static final int TAG_PROFILE_BYTE_COUNT = 0;
    public static final int TAG_CMM_TYPE = 4;
    public static final int TAG_PROFILE_VERSION = 8;
    public static final int TAG_PROFILE_CLASS = 12;
    public static final int TAG_COLOR_SPACE = 16;
    public static final int TAG_PROFILE_CONNECTION_SPACE = 20;
    public static final int TAG_PROFILE_DATETIME = 24;
    public static final int TAG_SIGNATURE = 36;
    public static final int TAG_PLATFORM = 40;
    public static final int TAG_CMM_FLAGS = 44;
    public static final int TAG_DEVICE_MAKE = 48;
    public static final int TAG_DEVICE_MODEL = 52;
    public static final int TAG_DEVICE_ATTR = 56;
    public static final int TAG_RENDERING_INTENT = 64;
    public static final int TAG_XYZ_VALUES = 68;
    public static final int TAG_PROFILE_CREATOR = 80;
    public static final int TAG_TAG_COUNT = 128;

    // These tag values

    public static final int TAG_TAG_A2B0 = 0x41324230;
    public static final int TAG_TAG_A2B1 = 0x41324231;
    public static final int TAG_TAG_A2B2 = 0x41324232;
    public static final int TAG_TAG_bXYZ = 0x6258595A;
    public static final int TAG_TAG_bTRC = 0x62545243;
    public static final int TAG_TAG_B2A0 = 0x42324130;
    public static final int TAG_TAG_B2A1 = 0x42324131;
    public static final int TAG_TAG_B2A2 = 0x42324132;
    public static final int TAG_TAG_calt = 0x63616C74;
    public static final int TAG_TAG_targ = 0x74617267;
    public static final int TAG_TAG_chad = 0x63686164;
    public static final int TAG_TAG_chrm = 0x6368726D;
    public static final int TAG_TAG_cprt = 0x63707274;
    public static final int TAG_TAG_crdi = 0x63726469;
    public static final int TAG_TAG_dmnd = 0x646D6E64;
    public static final int TAG_TAG_dmdd = 0x646D6464;
    public static final int TAG_TAG_devs = 0x64657673;
    public static final int TAG_TAG_gamt = 0x67616D74;
    public static final int TAG_TAG_kTRC = 0x6B545243;
    public static final int TAG_TAG_gXYZ = 0x6758595A;
    public static final int TAG_TAG_gTRC = 0x67545243;
    public static final int TAG_TAG_lumi = 0x6C756D69;
    public static final int TAG_TAG_meas = 0x6D656173;
    public static final int TAG_TAG_bkpt = 0x626B7074;
    public static final int TAG_TAG_wtpt = 0x77747074;
    public static final int TAG_TAG_ncol = 0x6E636F6C;
    public static final int TAG_TAG_ncl2 = 0x6E636C32;
    public static final int TAG_TAG_resp = 0x72657370;
    public static final int TAG_TAG_pre0 = 0x70726530;
    public static final int TAG_TAG_pre1 = 0x70726531;
    public static final int TAG_TAG_pre2 = 0x70726532;
    public static final int TAG_TAG_desc = 0x64657363;
    public static final int TAG_TAG_pseq = 0x70736571;
    public static final int TAG_TAG_psd0 = 0x70736430;
    public static final int TAG_TAG_psd1 = 0x70736431;
    public static final int TAG_TAG_psd2 = 0x70736432;
    public static final int TAG_TAG_psd3 = 0x70736433;
    public static final int TAG_TAG_ps2s = 0x70733273;
    public static final int TAG_TAG_ps2i = 0x70733269;
    public static final int TAG_TAG_rXYZ = 0x7258595A;
    public static final int TAG_TAG_rTRC = 0x72545243;
    public static final int TAG_TAG_scrd = 0x73637264;
    public static final int TAG_TAG_scrn = 0x7363726E;
    public static final int TAG_TAG_tech = 0x74656368;
    public static final int TAG_TAG_bfd = 0x62666420;
    public static final int TAG_TAG_vued = 0x76756564;
    public static final int TAG_TAG_view = 0x76696577;

    public static final int TAG_TAG_aabg = 0x61616267;
    public static final int TAG_TAG_aagg = 0x61616767;
    public static final int TAG_TAG_aarg = 0x61617267;
    public static final int TAG_TAG_mmod = 0x6D6D6F64;
    public static final int TAG_TAG_ndin = 0x6E64696E;
    public static final int TAG_TAG_vcgt = 0x76636774;
    public static final int TAG_APPLE_MULTI_LANGUAGE_PROFILE_NAME = 0x6473636d;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_PROFILE_BYTE_COUNT, "Profile Size");
        _tagNameMap.put(TAG_CMM_TYPE, "CMM Type");
        _tagNameMap.put(TAG_PROFILE_VERSION, "Version");
        _tagNameMap.put(TAG_PROFILE_CLASS, "Class");
        _tagNameMap.put(TAG_COLOR_SPACE, "Color space");
        _tagNameMap.put(TAG_PROFILE_CONNECTION_SPACE, "Profile Connection Space");
        _tagNameMap.put(TAG_PROFILE_DATETIME, "Profile Date/Time");
        _tagNameMap.put(TAG_SIGNATURE, "Signature");
        _tagNameMap.put(TAG_PLATFORM, "Primary Platform");
        _tagNameMap.put(TAG_CMM_FLAGS, "CMM Flags");
        _tagNameMap.put(TAG_DEVICE_MAKE, "Device manufacturer");
        _tagNameMap.put(TAG_DEVICE_MODEL, "Device model");
        _tagNameMap.put(TAG_DEVICE_ATTR, "Device attributes");
        _tagNameMap.put(TAG_RENDERING_INTENT, "Rendering Intent");
        _tagNameMap.put(TAG_XYZ_VALUES, "XYZ values");
        _tagNameMap.put(TAG_PROFILE_CREATOR, "Profile Creator");
        _tagNameMap.put(TAG_TAG_COUNT, "Tag Count");
        _tagNameMap.put(TAG_TAG_A2B0, "AToB 0");
        _tagNameMap.put(TAG_TAG_A2B1, "AToB 1");
        _tagNameMap.put(TAG_TAG_A2B2, "AToB 2");
        _tagNameMap.put(TAG_TAG_bXYZ, "Blue Colorant");
        _tagNameMap.put(TAG_TAG_bTRC, "Blue TRC");
        _tagNameMap.put(TAG_TAG_B2A0, "BToA 0");
        _tagNameMap.put(TAG_TAG_B2A1, "BToA 1");
        _tagNameMap.put(TAG_TAG_B2A2, "BToA 2");
        _tagNameMap.put(TAG_TAG_calt, "Calibration Date/Time");
        _tagNameMap.put(TAG_TAG_targ, "Char Target");
        _tagNameMap.put(TAG_TAG_chad, "Chromatic Adaptation");
        _tagNameMap.put(TAG_TAG_chrm, "Chromaticity");
        _tagNameMap.put(TAG_TAG_cprt, "Copyright");
        _tagNameMap.put(TAG_TAG_crdi, "CrdInfo");
        _tagNameMap.put(TAG_TAG_dmnd, "Device Mfg Description");
        _tagNameMap.put(TAG_TAG_dmdd, "Device Model Description");
        _tagNameMap.put(TAG_TAG_devs, "Device Settings");
        _tagNameMap.put(TAG_TAG_gamt, "Gamut");
        _tagNameMap.put(TAG_TAG_kTRC, "Gray TRC");
        _tagNameMap.put(TAG_TAG_gXYZ, "Green Colorant");
        _tagNameMap.put(TAG_TAG_gTRC, "Green TRC");
        _tagNameMap.put(TAG_TAG_lumi, "Luminance");
        _tagNameMap.put(TAG_TAG_meas, "Measurement");
        _tagNameMap.put(TAG_TAG_bkpt, "Media Black Point");
        _tagNameMap.put(TAG_TAG_wtpt, "Media White Point");
        _tagNameMap.put(TAG_TAG_ncol, "Named Color");
        _tagNameMap.put(TAG_TAG_ncl2, "Named Color 2");
        _tagNameMap.put(TAG_TAG_resp, "Output Response");
        _tagNameMap.put(TAG_TAG_pre0, "Preview 0");
        _tagNameMap.put(TAG_TAG_pre1, "Preview 1");
        _tagNameMap.put(TAG_TAG_pre2, "Preview 2");
        _tagNameMap.put(TAG_TAG_desc, "Profile Description");
        _tagNameMap.put(TAG_TAG_pseq, "Profile Sequence Description");
        _tagNameMap.put(TAG_TAG_psd0, "Ps2 CRD 0");
        _tagNameMap.put(TAG_TAG_psd1, "Ps2 CRD 1");
        _tagNameMap.put(TAG_TAG_psd2, "Ps2 CRD 2");
        _tagNameMap.put(TAG_TAG_psd3, "Ps2 CRD 3");
        _tagNameMap.put(TAG_TAG_ps2s, "Ps2 CSA");
        _tagNameMap.put(TAG_TAG_ps2i, "Ps2 Rendering Intent");
        _tagNameMap.put(TAG_TAG_rXYZ, "Red Colorant");
        _tagNameMap.put(TAG_TAG_rTRC, "Red TRC");
        _tagNameMap.put(TAG_TAG_scrd, "Screening Desc");
        _tagNameMap.put(TAG_TAG_scrn, "Screening");
        _tagNameMap.put(TAG_TAG_tech, "Technology");
        _tagNameMap.put(TAG_TAG_bfd, "Ucrbg");
        _tagNameMap.put(TAG_TAG_vued, "Viewing Conditions Description");
        _tagNameMap.put(TAG_TAG_view, "Viewing Conditions");
        _tagNameMap.put(TAG_TAG_aabg, "Blue Parametric TRC");
        _tagNameMap.put(TAG_TAG_aagg, "Green Parametric TRC");
        _tagNameMap.put(TAG_TAG_aarg, "Red Parametric TRC");
        _tagNameMap.put(TAG_TAG_mmod, "Make And Model");
        _tagNameMap.put(TAG_TAG_ndin, "Native Display Information");
        _tagNameMap.put(TAG_TAG_vcgt, "Video Card Gamma");
        _tagNameMap.put(TAG_APPLE_MULTI_LANGUAGE_PROFILE_NAME, "Apple Multi-language Profile Name");
    }

    public IccDirectory()
    {
        this.setDescriptor(new IccDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "ICC Profile";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
