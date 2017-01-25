/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.metadata.exif.makernotes;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * Describes tags specific to Nikon (type 2) cameras.  Type-2 applies to the E990 and D-series cameras such as the E990, D1,
 * D70 and D100.
 * <p>
 * Thanks to Fabrizio Giudici for publishing his reverse-engineering of the D100 makernote data.
 * http://www.timelesswanderings.net/equipment/D100/NEF.html
 * <p>
 * Note that the camera implements image protection (locking images) via the file's 'readonly' attribute.  Similarly
 * image hiding uses the 'hidden' attribute (observed on the D70).  Consequently, these values are not available here.
 * <p>
 * Additional sample images have been observed, and their tag values recorded in javadoc comments for each tag's field.
 * New tags have subsequently been added since Fabrizio's observations.
 * <p>
 * In earlier models (such as the E990 and D1), this directory begins at the first byte of the makernote IFD.  In
 * later models, the IFD was given the standard prefix to indicate the camera models (most other manufacturers also
 * provide this prefix to aid in software decoding).
 *
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class NikonType2MakernoteDirectory extends Directory
{
    /**
     * Values observed
     * - 0200 (D70)
     * - 0200 (D1X)
     */
    public static final int TAG_FIRMWARE_VERSION = 0x0001;

    /**
     * Values observed
     * - 0 250
     * - 0 400
     */
    public static final int TAG_ISO_1 = 0x0002;

    /**
     * The camera's color mode, as an uppercase string.  Examples include:
     * <ul>
     * <li><code>B &amp; W</code></li>
     * <li><code>COLOR</code></li>
     * <li><code>COOL</code></li>
     * <li><code>SEPIA</code></li>
     * <li><code>VIVID</code></li>
     * </ul>
     */
    public static final int TAG_COLOR_MODE = 0x0003;

    /**
     * The camera's quality setting, as an uppercase string.  Examples include:
     * <ul>
     * <li><code>BASIC</code></li>
     * <li><code>FINE</code></li>
     * <li><code>NORMAL</code></li>
     * <li><code>RAW</code></li>
     * <li><code>RAW2.7M</code></li>
     * </ul>
     */
    public static final int TAG_QUALITY_AND_FILE_FORMAT = 0x0004;

    /**
     * The camera's white balance setting, as an uppercase string.  Examples include:
     *
     * <ul>
     * <li><code>AUTO</code></li>
     * <li><code>CLOUDY</code></li>
     * <li><code>FLASH</code></li>
     * <li><code>FLUORESCENT</code></li>
     * <li><code>INCANDESCENT</code></li>
     * <li><code>PRESET</code></li>
     * <li><code>PRESET0</code></li>
     * <li><code>PRESET1</code></li>
     * <li><code>PRESET3</code></li>
     * <li><code>SUNNY</code></li>
     * <li><code>WHITE PRESET</code></li>
     * <li><code>4350K</code></li>
     * <li><code>5000K</code></li>
     * <li><code>DAY WHITE FL</code></li>
     * <li><code>SHADE</code></li>
     * </ul>
     */
    public static final int TAG_CAMERA_WHITE_BALANCE  = 0x0005;

    /**
     * The camera's sharpening setting, as an uppercase string.  Examples include:
     *
     * <ul>
     * <li><code>AUTO</code></li>
     * <li><code>HIGH</code></li>
     * <li><code>LOW</code></li>
     * <li><code>NONE</code></li>
     * <li><code>NORMAL</code></li>
     * <li><code>MED.H</code></li>
     * <li><code>MED.L</code></li>
     * </ul>
     */
    public static final int TAG_CAMERA_SHARPENING = 0x0006;

    /**
     * The camera's auto-focus mode, as an uppercase string.  Examples include:
     *
     * <ul>
     * <li><code>AF-C</code></li>
     * <li><code>AF-S</code></li>
     * <li><code>MANUAL</code></li>
     * <li><code>AF-A</code></li>
     * </ul>
     */
    public static final int TAG_AF_TYPE = 0x0007;

    /**
     * The camera's flash setting, as an uppercase string.  Examples include:
     *
     * <ul>
     * <li><code></code></li>
     * <li><code>NORMAL</code></li>
     * <li><code>RED-EYE</code></li>
     * <li><code>SLOW</code></li>
     * <li><code>NEW_TTL</code></li>
     * <li><code>REAR</code></li>
     * <li><code>REAR SLOW</code></li>
     * </ul>
     * Note: when TAG_AUTO_FLASH_MODE is blank (whitespace), Nikon Browser displays "Flash Sync Mode: Not Attached"
     */
    public static final int TAG_FLASH_SYNC_MODE = 0x0008;

    /**
     * The type of flash used in the photograph, as a string.  Examples include:
     *
     * <ul>
     * <li><code></code></li>
     * <li><code>Built-in,TTL</code></li>
     * <li><code>NEW_TTL</code> Nikon Browser interprets as "D-TTL"</li>
     * <li><code>Built-in,M</code></li>
     * <li><code>Optional,TTL</code> with speedlight SB800, flash sync mode as "NORMAL"</li>
     * </ul>
     */
    public static final int TAG_AUTO_FLASH_MODE = 0x0009;

    /**
     * An unknown tag, as a rational.  Several values given here:
     * http://gvsoft.homedns.org/exif/makernote-nikon-type2.html#0x000b
     */
    public static final int TAG_UNKNOWN_34 = 0x000A;

    /**
     * The camera's white balance bias setting, as an uint16 array having either one or two elements.
     *
     * <ul>
     * <li><code>0</code></li>
     * <li><code>1</code></li>
     * <li><code>-3</code></li>
     * <li><code>-2</code></li>
     * <li><code>-1</code></li>
     * <li><code>0,0</code></li>
     * <li><code>1,0</code></li>
     * <li><code>5,-5</code></li>
     * </ul>
     */
    public static final int TAG_CAMERA_WHITE_BALANCE_FINE = 0x000B;

    /**
     * The first two numbers are coefficients to multiply red and blue channels according to white balance as set in the
     * camera. The meaning of the third and the fourth numbers is unknown.
     *
     * Values observed
     * - 2.25882352 1.76078431 0.0 0.0
     * - 10242/1 34305/1 0/1 0/1
     * - 234765625/100000000 1140625/1000000 1/1 1/1
     */
    public static final int TAG_CAMERA_WHITE_BALANCE_RB_COEFF = 0x000C;

    /**
     * The camera's program shift setting, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>0,1,3,0</code> = 0 EV</li>
     * <li><code>1,1,3,0</code> = 0.33 EV</li>
     * <li><code>-3,1,3,0</code> = -1 EV</li>
     * <li><code>1,1,2,0</code> = 0.5 EV</li>
     * <li><code>2,1,6,0</code> = 0.33 EV</li>
     * </ul>
     */
    public static final int TAG_PROGRAM_SHIFT = 0x000D;

    /**
     * The exposure difference, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>-105,1,12,0</code> = -8.75 EV</li>
     * <li><code>-72,1,12,0</code> = -6.00 EV</li>
     * <li><code>-11,1,12,0</code> = -0.92 EV</li>
     * </ul>
     */
    public static final int TAG_EXPOSURE_DIFFERENCE = 0x000E;

    /**
     * The camera's ISO mode, as an uppercase string.
     *
     * <ul>
     * <li><code>AUTO</code></li>
     * <li><code>MANUAL</code></li>
     * </ul>
     */
    public static final int TAG_ISO_MODE = 0x000F;

    /**
     * Added during merge of Type2 &amp; Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_DATA_DUMP = 0x0010;

    /**
     * Preview to another IFD (?)
     * <p>
     * Details here: http://gvsoft.homedns.org/exif/makernote-nikon-2-tag0x0011.html
     * // TODO if this is another IFD, decode it
     */
    public static final int TAG_PREVIEW_IFD = 0x0011;

    /**
     * The flash compensation, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>-18,1,6,0</code> = -3 EV</li>
     * <li><code>4,1,6,0</code> = 0.67 EV</li>
     * <li><code>6,1,6,0</code> = 1 EV</li>
     * </ul>
     */
    public static final int TAG_AUTO_FLASH_COMPENSATION = 0x0012;

    /**
     * The requested ISO value, as an array of two integers.
     *
     * <ul>
     * <li><code>0,0</code></li>
     * <li><code>0,125</code></li>
     * <li><code>1,2500</code></li>
     * </ul>
     */
    public static final int TAG_ISO_REQUESTED = 0x0013;

    /**
     * Defines the photo corner coordinates, in 8 bytes.  Treated as four 16-bit integers, they
     * decode as: top-left (x,y); bot-right (x,y)
     * - 0 0 49163 53255
     * - 0 0 3008 2000 (the image dimensions were 3008x2000) (D70)
     * <ul>
     * <li><code>0,0,4288,2848</code> The max resolution of the D300 camera</li>
     * <li><code>0,0,3008,2000</code> The max resolution of the D70 camera</li>
     * <li><code>0,0,4256,2832</code> The max resolution of the D3 camera</li>
     * </ul>
     */
    public static final int TAG_IMAGE_BOUNDARY = 0x0016;

    /**
     * The flash exposure compensation, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>0,0,0,0</code> = 0 EV</li>
     * <li><code>0,1,6,0</code> = 0 EV</li>
     * <li><code>4,1,6,0</code> = 0.67 EV</li>
     * </ul>
     */
    public static final int TAG_FLASH_EXPOSURE_COMPENSATION = 0x0017;

    /**
     * The flash bracket compensation, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>0,0,0,0</code> = 0 EV</li>
     * <li><code>0,1,6,0</code> = 0 EV</li>
     * <li><code>4,1,6,0</code> = 0.67 EV</li>
     * </ul>
     */
    public static final int TAG_FLASH_BRACKET_COMPENSATION = 0x0018;

    /**
     * The AE bracket compensation, as a rational number.
     *
     * <ul>
     * <li><code>0/0</code></li>
     * <li><code>0/1</code></li>
     * <li><code>0/6</code></li>
     * <li><code>4/6</code></li>
     * <li><code>6/6</code></li>
     * </ul>
     */
    public static final int TAG_AE_BRACKET_COMPENSATION = 0x0019;

    /**
     * Flash mode, as a string.
     *
     * <ul>
     * <li><code></code></li>
     * <li><code>Red Eye Reduction</code></li>
     * <li><code>D-Lighting</code></li>
     * <li><code>Distortion control</code></li>
     * </ul>
     */
    public static final int TAG_FLASH_MODE = 0x001a;

    public static final int TAG_CROP_HIGH_SPEED = 0x001b;
    public static final int TAG_EXPOSURE_TUNING = 0x001c;

    /**
     * The camera's serial number, as a string.
     * Note that D200 is always blank, and D50 is always <code>"D50"</code>.
     */
    public static final int TAG_CAMERA_SERIAL_NUMBER = 0x001d;

    /**
     * The camera's color space setting.
     *
     * <ul>
     * <li><code>1</code> sRGB</li>
     * <li><code>2</code> Adobe RGB</li>
     * </ul>
     */
    public static final int TAG_COLOR_SPACE = 0x001e;
    public static final int TAG_VR_INFO = 0x001f;
    public static final int TAG_IMAGE_AUTHENTICATION = 0x0020;
    public static final int TAG_UNKNOWN_35 = 0x0021;

    /**
     * The active D-Lighting setting.
     *
     * <ul>
     * <li><code>0</code> Off</li>
     * <li><code>1</code> Low</li>
     * <li><code>3</code> Normal</li>
     * <li><code>5</code> High</li>
     * <li><code>7</code> Extra High</li>
     * <li><code>65535</code> Auto</li>
     * </ul>
     */
    public static final int TAG_ACTIVE_D_LIGHTING = 0x0022;
    public static final int TAG_PICTURE_CONTROL = 0x0023;
    public static final int TAG_WORLD_TIME = 0x0024;
    public static final int TAG_ISO_INFO = 0x0025;
    public static final int TAG_UNKNOWN_36 = 0x0026;
    public static final int TAG_UNKNOWN_37 = 0x0027;
    public static final int TAG_UNKNOWN_38 = 0x0028;
    public static final int TAG_UNKNOWN_39 = 0x0029;

    /**
     * The camera's vignette control setting.
     *
     * <ul>
     * <li><code>0</code> Off</li>
     * <li><code>1</code> Low</li>
     * <li><code>3</code> Normal</li>
     * <li><code>5</code> High</li>
     * </ul>
     */
    public static final int TAG_VIGNETTE_CONTROL = 0x002a;
    public static final int TAG_UNKNOWN_40 = 0x002b;
    public static final int TAG_UNKNOWN_41 = 0x002c;
    public static final int TAG_UNKNOWN_42 = 0x002d;
    public static final int TAG_UNKNOWN_43 = 0x002e;
    public static final int TAG_UNKNOWN_44 = 0x002f;
    public static final int TAG_UNKNOWN_45 = 0x0030;
    public static final int TAG_UNKNOWN_46 = 0x0031;

    /**
     * The camera's image adjustment setting, as a string.
     *
     * <ul>
     * <li><code>AUTO</code></li>
     * <li><code>CONTRAST(+)</code></li>
     * <li><code>CONTRAST(-)</code></li>
     * <li><code>NORMAL</code></li>
     * <li><code>B &amp; W</code></li>
     * <li><code>BRIGHTNESS(+)</code></li>
     * <li><code>BRIGHTNESS(-)</code></li>
     * <li><code>SEPIA</code></li>
     * </ul>
     */
    public static final int TAG_IMAGE_ADJUSTMENT = 0x0080;

    /**
     * The camera's tone compensation setting, as a string.
     *
     * <ul>
     * <li><code>NORMAL</code></li>
     * <li><code>LOW</code></li>
     * <li><code>MED.L</code></li>
     * <li><code>MED.H</code></li>
     * <li><code>HIGH</code></li>
     * <li><code>AUTO</code></li>
     * </ul>
     */
    public static final int TAG_CAMERA_TONE_COMPENSATION = 0x0081;

    /**
     * A description of any auxiliary lens, as a string.
     *
     * <ul>
     * <li><code>OFF</code></li>
     * <li><code>FISHEYE 1</code></li>
     * <li><code>FISHEYE 2</code></li>
     * <li><code>TELEPHOTO 2</code></li>
     * <li><code>WIDE ADAPTER</code></li>
     * </ul>
     */
    public static final int TAG_ADAPTER = 0x0082;

    /**
     * The type of lens used, as a byte.
     *
     * <ul>
     * <li><code>0x00</code> AF</li>
     * <li><code>0x01</code> MF</li>
     * <li><code>0x02</code> D</li>
     * <li><code>0x06</code> G, D</li>
     * <li><code>0x08</code> VR</li>
     * <li><code>0x0a</code> VR, D</li>
     * <li><code>0x0e</code> VR, G, D</li>
     * </ul>
     */
    public static final int TAG_LENS_TYPE = 0x0083;

    /**
     * A pair of focal/max-fstop values that describe the lens used.
     *
     * Values observed
     * - 180.0,180.0,2.8,2.8 (D100)
     * - 240/10 850/10 35/10 45/10
     * - 18-70mm f/3.5-4.5 (D70)
     * - 17-35mm f/2.8-2.8 (D1X)
     * - 70-200mm f/2.8-2.8 (D70)
     *
     * Nikon Browser identifies the lens as "18-70mm F/3.5-4.5 G" which
     * is identical to metadata extractor, except for the "G".  This must
     * be coming from another tag...
     */
    public static final int TAG_LENS = 0x0084;

    /**
     * Added during merge of Type2 &amp; Type3.  May apply to earlier models, such as E990 and D1.
     */
    public static final int TAG_MANUAL_FOCUS_DISTANCE = 0x0085;

    /**
     * The amount of digital zoom used.
     */
    public static final int TAG_DIGITAL_ZOOM = 0x0086;

    /**
     * Whether the flash was used in this image.
     *
     * <ul>
     * <li><code>0</code> Flash Not Used</li>
     * <li><code>1</code> Manual Flash</li>
     * <li><code>3</code> Flash Not Ready</li>
     * <li><code>7</code> External Flash</li>
     * <li><code>8</code> Fired, Commander Mode</li>
     * <li><code>9</code> Fired, TTL Mode</li>
     * </ul>
     */
    public static final int TAG_FLASH_USED = 0x0087;

    /**
     * The position of the autofocus target.
     */
    public static final int TAG_AF_FOCUS_POSITION = 0x0088;

    /**
     * The camera's shooting mode.
     * <p>
     * A bit-array with:
     * <ul>
     * <li><code>0</code> Single Frame</li>
     * <li><code>1</code> Continuous</li>
     * <li><code>2</code> Delay</li>
     * <li><code>8</code> PC Control</li>
     * <li><code>16</code> Exposure Bracketing</li>
     * <li><code>32</code> Auto ISO</li>
     * <li><code>64</code> White-Balance Bracketing</li>
     * <li><code>128</code> IR Control</li>
     * </ul>
     */
    public static final int TAG_SHOOTING_MODE = 0x0089;

    public static final int TAG_UNKNOWN_20 = 0x008A;

    /**
     * Lens stops, as an array of four integers.
     * The value, in EV, is calculated as <code>a*b/c</code>.
     *
     * <ul>
     * <li><code>64,1,12,0</code> = 5.33 EV</li>
     * <li><code>72,1,12,0</code> = 6 EV</li>
     * </ul>
     */
    public static final int TAG_LENS_STOPS = 0x008B;

    public static final int TAG_CONTRAST_CURVE = 0x008C;

    /**
     * The color space as set in the camera, as a string.
     *
     * <ul>
     * <li><code>MODE1</code> = Mode 1 (sRGB)</li>
     * <li><code>MODE1a</code> = Mode 1 (sRGB)</li>
     * <li><code>MODE2</code> = Mode 2 (Adobe RGB)</li>
     * <li><code>MODE3</code> = Mode 2 (sRGB): Higher Saturation</li>
     * <li><code>MODE3a</code> = Mode 2 (sRGB): Higher Saturation</li>
     * <li><code>B &amp; W</code> = B &amp; W</li>
     * </ul>
     */
    public static final int TAG_CAMERA_COLOR_MODE = 0x008D;
    public static final int TAG_UNKNOWN_47 = 0x008E;

    /**
     * The camera's scene mode, as a string.  Examples include:
     * <ul>
     * <li><code>BEACH/SNOW</code></li>
     * <li><code>CLOSE UP</code></li>
     * <li><code>NIGHT PORTRAIT</code></li>
     * <li><code>PORTRAIT</code></li>
     * <li><code>ANTI-SHAKE</code></li>
     * <li><code>BACK LIGHT</code></li>
     * <li><code>BEST FACE</code></li>
     * <li><code>BEST</code></li>
     * <li><code>COPY</code></li>
     * <li><code>DAWN/DUSK</code></li>
     * <li><code>FACE-PRIORITY</code></li>
     * <li><code>FIREWORKS</code></li>
     * <li><code>FOOD</code></li>
     * <li><code>HIGH SENS.</code></li>
     * <li><code>LAND SCAPE</code></li>
     * <li><code>MUSEUM</code></li>
     * <li><code>PANORAMA ASSIST</code></li>
     * <li><code>PARTY/INDOOR</code></li>
     * <li><code>SCENE AUTO</code></li>
     * <li><code>SMILE</code></li>
     * <li><code>SPORT</code></li>
     * <li><code>SPORT CONT.</code></li>
     * <li><code>SUNSET</code></li>
     * </ul>
     */
    public static final int TAG_SCENE_MODE = 0x008F;

    /**
     * The lighting type, as a string.  Examples include:
     * <ul>
     * <li><code></code></li>
     * <li><code>NATURAL</code></li>
     * <li><code>SPEEDLIGHT</code></li>
     * <li><code>COLORED</code></li>
     * <li><code>MIXED</code></li>
     * <li><code>NORMAL</code></li>
     * </ul>
     */
    public static final int TAG_LIGHT_SOURCE = 0x0090;

    /**
     * Advertised as ASCII, but actually isn't.  A variable number of bytes (eg. 18 to 533).  Actual number of bytes
     * appears fixed for a given camera model.
     */
    public static final int TAG_SHOT_INFO = 0x0091;

    /**
     * The hue adjustment as set in the camera.  Values observed are either 0 or 3.
     */
    public static final int TAG_CAMERA_HUE_ADJUSTMENT = 0x0092;
    /**
     * The NEF (RAW) compression.  Examples include:
     * <ul>
     * <li><code>1</code> Lossy (Type 1)</li>
     * <li><code>2</code> Uncompressed</li>
     * <li><code>3</code> Lossless</li>
     * <li><code>4</code> Lossy (Type 2)</li>
     * </ul>
     */
    public static final int TAG_NEF_COMPRESSION = 0x0093;

    /**
     * The saturation level, as a signed integer.  Examples include:
     * <ul>
     * <li><code>+3</code></li>
     * <li><code>+2</code></li>
     * <li><code>+1</code></li>
     * <li><code>0</code> Normal</li>
     * <li><code>-1</code></li>
     * <li><code>-2</code></li>
     * <li><code>-3</code> (B&amp;W)</li>
     * </ul>
     */
    public static final int TAG_SATURATION = 0x0094;

    /**
     * The type of noise reduction, as a string.  Examples include:
     * <ul>
     * <li><code>OFF</code></li>
     * <li><code>FPNR</code></li>
     * </ul>
     */
    public static final int TAG_NOISE_REDUCTION = 0x0095;
    public static final int TAG_LINEARIZATION_TABLE = 0x0096;
    public static final int TAG_COLOR_BALANCE = 0x0097;
    public static final int TAG_LENS_DATA = 0x0098;

    /** The NEF (RAW) thumbnail size, as an integer array with two items representing [width,height]. */
    public static final int TAG_NEF_THUMBNAIL_SIZE = 0x0099;

    /** The sensor pixel size, as a pair of rational numbers. */
    public static final int TAG_SENSOR_PIXEL_SIZE = 0x009A;
    public static final int TAG_UNKNOWN_10 = 0x009B;
    public static final int TAG_SCENE_ASSIST = 0x009C;
    public static final int TAG_UNKNOWN_11 = 0x009D;
    public static final int TAG_RETOUCH_HISTORY = 0x009E;
    public static final int TAG_UNKNOWN_12 = 0x009F;

    /**
     * The camera serial number, as a string.
     * <ul>
     * <li><code>NO= 00002539</code></li>
     * <li><code>NO= -1000d71</code></li>
     * <li><code>PKG597230621263</code></li>
     * <li><code>PKG5995671330625116</code></li>
     * <li><code>PKG49981281631130677</code></li>
     * <li><code>BU672230725063</code></li>
     * <li><code>NO= 200332c7</code></li>
     * <li><code>NO= 30045efe</code></li>
     * </ul>
     */
    public static final int TAG_CAMERA_SERIAL_NUMBER_2 = 0x00A0;

    public static final int TAG_IMAGE_DATA_SIZE = 0x00A2;

    public static final int TAG_UNKNOWN_27 = 0x00A3;
    public static final int TAG_UNKNOWN_28 = 0x00A4;
    public static final int TAG_IMAGE_COUNT = 0x00A5;
    public static final int TAG_DELETED_IMAGE_COUNT = 0x00A6;

    /** The number of total shutter releases.  This value increments for each exposure (observed on D70). */
    public static final int TAG_EXPOSURE_SEQUENCE_NUMBER = 0x00A7;

    public static final int TAG_FLASH_INFO = 0x00A8;
    /**
     * The camera's image optimisation, as a string.
     * <ul>
     *     <li><code></code></li>
     *     <li><code>NORMAL</code></li>
     *     <li><code>CUSTOM</code></li>
     *     <li><code>BLACK AND WHITE</code></li>
     *     <li><code>LAND SCAPE</code></li>
     *     <li><code>MORE VIVID</code></li>
     *     <li><code>PORTRAIT</code></li>
     *     <li><code>SOFT</code></li>
     *     <li><code>VIVID</code></li>
     * </ul>
     */
    public static final int TAG_IMAGE_OPTIMISATION = 0x00A9;

    /**
     * The camera's saturation level, as a string.
     * <ul>
     *     <li><code></code></li>
     *     <li><code>NORMAL</code></li>
     *     <li><code>AUTO</code></li>
     *     <li><code>ENHANCED</code></li>
     *     <li><code>MODERATE</code></li>
     * </ul>
     */
    public static final int TAG_SATURATION_2 = 0x00AA;

    /**
     * The camera's digital vari-program setting, as a string.
     * <ul>
     *     <li><code></code></li>
     *     <li><code>AUTO</code></li>
     *     <li><code>AUTO(FLASH OFF)</code></li>
     *     <li><code>CLOSE UP</code></li>
     *     <li><code>LANDSCAPE</code></li>
     *     <li><code>NIGHT PORTRAIT</code></li>
     *     <li><code>PORTRAIT</code></li>
     *     <li><code>SPORT</code></li>
     * </ul>
     */
    public static final int TAG_DIGITAL_VARI_PROGRAM = 0x00AB;

    /**
     * The camera's digital vari-program setting, as a string.
     * <ul>
     *     <li><code></code></li>
     *     <li><code>VR-ON</code></li>
     *     <li><code>VR-OFF</code></li>
     *     <li><code>VR-HYBRID</code></li>
     *     <li><code>VR-ACTIVE</code></li>
     * </ul>
     */
    public static final int TAG_IMAGE_STABILISATION = 0x00AC;

    /**
     * The camera's digital vari-program setting, as a string.
     * <ul>
     *     <li><code></code></li>
     *     <li><code>HYBRID</code></li>
     *     <li><code>STANDARD</code></li>
     * </ul>
     */
    public static final int TAG_AF_RESPONSE = 0x00AD;
    public static final int TAG_UNKNOWN_29 = 0x00AE;
    public static final int TAG_UNKNOWN_30 = 0x00AF;
    public static final int TAG_MULTI_EXPOSURE = 0x00B0;

    /**
     * The camera's high ISO noise reduction setting, as an integer.
     * <ul>
     *     <li><code>0</code> Off</li>
     *     <li><code>1</code> Minimal</li>
     *     <li><code>2</code> Low</li>
     *     <li><code>4</code> Normal</li>
     *     <li><code>6</code> High</li>
     * </ul>
     */
    public static final int TAG_HIGH_ISO_NOISE_REDUCTION = 0x00B1;
    public static final int TAG_UNKNOWN_31 = 0x00B2;
    public static final int TAG_UNKNOWN_32 = 0x00B3;
    public static final int TAG_UNKNOWN_33 = 0x00B4;
    public static final int TAG_UNKNOWN_48 = 0x00B5;
    public static final int TAG_POWER_UP_TIME = 0x00B6;
    public static final int TAG_AF_INFO_2 = 0x00B7;
    public static final int TAG_FILE_INFO = 0x00B8;
    public static final int TAG_AF_TUNE = 0x00B9;
    public static final int TAG_UNKNOWN_49 = 0x00BB;
    public static final int TAG_UNKNOWN_50 = 0x00BD;
    public static final int TAG_UNKNOWN_51 = 0x0103;
    public static final int TAG_PRINT_IMAGE_MATCHING_INFO = 0x0E00;

    /**
     * Data about changes set by Nikon Capture Editor.
     *
     * Values observed
     */
    public static final int TAG_NIKON_CAPTURE_DATA = 0x0E01;
    public static final int TAG_UNKNOWN_52 = 0x0E05;
    public static final int TAG_UNKNOWN_53 = 0x0E08;
    public static final int TAG_NIKON_CAPTURE_VERSION = 0x0E09;
    public static final int TAG_NIKON_CAPTURE_OFFSETS = 0x0E0E;
    public static final int TAG_NIKON_SCAN = 0x0E10;
    public static final int TAG_UNKNOWN_54 = 0x0E19;
    public static final int TAG_NEF_BIT_DEPTH = 0x0E22;
    public static final int TAG_UNKNOWN_55 = 0x0E23;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static
    {
        _tagNameMap.put(TAG_FIRMWARE_VERSION, "Firmware Version");
        _tagNameMap.put(TAG_ISO_1, "ISO");
        _tagNameMap.put(TAG_QUALITY_AND_FILE_FORMAT, "Quality & File Format");
        _tagNameMap.put(TAG_CAMERA_WHITE_BALANCE, "White Balance");
        _tagNameMap.put(TAG_CAMERA_SHARPENING, "Sharpening");
        _tagNameMap.put(TAG_AF_TYPE, "AF Type");
        _tagNameMap.put(TAG_CAMERA_WHITE_BALANCE_FINE, "White Balance Fine");
        _tagNameMap.put(TAG_CAMERA_WHITE_BALANCE_RB_COEFF, "White Balance RB Coefficients");
        _tagNameMap.put(TAG_ISO_REQUESTED, "ISO");
        _tagNameMap.put(TAG_ISO_MODE, "ISO Mode");
        _tagNameMap.put(TAG_DATA_DUMP, "Data Dump");

        _tagNameMap.put(TAG_PROGRAM_SHIFT, "Program Shift");
        _tagNameMap.put(TAG_EXPOSURE_DIFFERENCE, "Exposure Difference");
        _tagNameMap.put(TAG_PREVIEW_IFD, "Preview IFD");
        _tagNameMap.put(TAG_LENS_TYPE, "Lens Type");
        _tagNameMap.put(TAG_FLASH_USED, "Flash Used");
        _tagNameMap.put(TAG_AF_FOCUS_POSITION, "AF Focus Position");
        _tagNameMap.put(TAG_SHOOTING_MODE, "Shooting Mode");
        _tagNameMap.put(TAG_LENS_STOPS, "Lens Stops");
        _tagNameMap.put(TAG_CONTRAST_CURVE, "Contrast Curve");
        _tagNameMap.put(TAG_LIGHT_SOURCE, "Light source");
        _tagNameMap.put(TAG_SHOT_INFO, "Shot Info");
        _tagNameMap.put(TAG_COLOR_BALANCE, "Color Balance");
        _tagNameMap.put(TAG_LENS_DATA, "Lens Data");
        _tagNameMap.put(TAG_NEF_THUMBNAIL_SIZE, "NEF Thumbnail Size");
        _tagNameMap.put(TAG_SENSOR_PIXEL_SIZE, "Sensor Pixel Size");
        _tagNameMap.put(TAG_UNKNOWN_10, "Unknown 10");
        _tagNameMap.put(TAG_SCENE_ASSIST, "Scene Assist");
        _tagNameMap.put(TAG_UNKNOWN_11, "Unknown 11");
        _tagNameMap.put(TAG_RETOUCH_HISTORY, "Retouch History");
        _tagNameMap.put(TAG_UNKNOWN_12, "Unknown 12");
        _tagNameMap.put(TAG_FLASH_SYNC_MODE, "Flash Sync Mode");
        _tagNameMap.put(TAG_AUTO_FLASH_MODE, "Auto Flash Mode");
        _tagNameMap.put(TAG_AUTO_FLASH_COMPENSATION, "Auto Flash Compensation");
        _tagNameMap.put(TAG_EXPOSURE_SEQUENCE_NUMBER, "Exposure Sequence Number");
        _tagNameMap.put(TAG_COLOR_MODE, "Color Mode");

        _tagNameMap.put(TAG_UNKNOWN_20, "Unknown 20");
        _tagNameMap.put(TAG_IMAGE_BOUNDARY, "Image Boundary");
        _tagNameMap.put(TAG_FLASH_EXPOSURE_COMPENSATION, "Flash Exposure Compensation");
        _tagNameMap.put(TAG_FLASH_BRACKET_COMPENSATION, "Flash Bracket Compensation");
        _tagNameMap.put(TAG_AE_BRACKET_COMPENSATION, "AE Bracket Compensation");
        _tagNameMap.put(TAG_FLASH_MODE, "Flash Mode");
        _tagNameMap.put(TAG_CROP_HIGH_SPEED, "Crop High Speed");
        _tagNameMap.put(TAG_EXPOSURE_TUNING, "Exposure Tuning");
        _tagNameMap.put(TAG_CAMERA_SERIAL_NUMBER, "Camera Serial Number");
        _tagNameMap.put(TAG_COLOR_SPACE, "Color Space");
        _tagNameMap.put(TAG_VR_INFO, "VR Info");
        _tagNameMap.put(TAG_IMAGE_AUTHENTICATION, "Image Authentication");
        _tagNameMap.put(TAG_UNKNOWN_35, "Unknown 35");
        _tagNameMap.put(TAG_ACTIVE_D_LIGHTING, "Active D-Lighting");
        _tagNameMap.put(TAG_PICTURE_CONTROL, "Picture Control");
        _tagNameMap.put(TAG_WORLD_TIME, "World Time");
        _tagNameMap.put(TAG_ISO_INFO, "ISO Info");
        _tagNameMap.put(TAG_UNKNOWN_36, "Unknown 36");
        _tagNameMap.put(TAG_UNKNOWN_37, "Unknown 37");
        _tagNameMap.put(TAG_UNKNOWN_38, "Unknown 38");
        _tagNameMap.put(TAG_UNKNOWN_39, "Unknown 39");
        _tagNameMap.put(TAG_VIGNETTE_CONTROL, "Vignette Control");
        _tagNameMap.put(TAG_UNKNOWN_40, "Unknown 40");
        _tagNameMap.put(TAG_UNKNOWN_41, "Unknown 41");
        _tagNameMap.put(TAG_UNKNOWN_42, "Unknown 42");
        _tagNameMap.put(TAG_UNKNOWN_43, "Unknown 43");
        _tagNameMap.put(TAG_UNKNOWN_44, "Unknown 44");
        _tagNameMap.put(TAG_UNKNOWN_45, "Unknown 45");
        _tagNameMap.put(TAG_UNKNOWN_46, "Unknown 46");
        _tagNameMap.put(TAG_UNKNOWN_47, "Unknown 47");
        _tagNameMap.put(TAG_SCENE_MODE, "Scene Mode");

        _tagNameMap.put(TAG_CAMERA_SERIAL_NUMBER_2, "Camera Serial Number");
        _tagNameMap.put(TAG_IMAGE_DATA_SIZE, "Image Data Size");
        _tagNameMap.put(TAG_UNKNOWN_27, "Unknown 27");
        _tagNameMap.put(TAG_UNKNOWN_28, "Unknown 28");
        _tagNameMap.put(TAG_IMAGE_COUNT, "Image Count");
        _tagNameMap.put(TAG_DELETED_IMAGE_COUNT, "Deleted Image Count");
        _tagNameMap.put(TAG_SATURATION_2, "Saturation");
        _tagNameMap.put(TAG_DIGITAL_VARI_PROGRAM, "Digital Vari Program");
        _tagNameMap.put(TAG_IMAGE_STABILISATION, "Image Stabilisation");
        _tagNameMap.put(TAG_AF_RESPONSE, "AF Response");
        _tagNameMap.put(TAG_UNKNOWN_29, "Unknown 29");
        _tagNameMap.put(TAG_UNKNOWN_30, "Unknown 30");
        _tagNameMap.put(TAG_MULTI_EXPOSURE, "Multi Exposure");
        _tagNameMap.put(TAG_HIGH_ISO_NOISE_REDUCTION, "High ISO Noise Reduction");
        _tagNameMap.put(TAG_UNKNOWN_31, "Unknown 31");
        _tagNameMap.put(TAG_UNKNOWN_32, "Unknown 32");
        _tagNameMap.put(TAG_UNKNOWN_33, "Unknown 33");
        _tagNameMap.put(TAG_UNKNOWN_48, "Unknown 48");
        _tagNameMap.put(TAG_POWER_UP_TIME, "Power Up Time");
        _tagNameMap.put(TAG_AF_INFO_2, "AF Info 2");
        _tagNameMap.put(TAG_FILE_INFO, "File Info");
        _tagNameMap.put(TAG_AF_TUNE, "AF Tune");
        _tagNameMap.put(TAG_FLASH_INFO, "Flash Info");
        _tagNameMap.put(TAG_IMAGE_OPTIMISATION, "Image Optimisation");

        _tagNameMap.put(TAG_IMAGE_ADJUSTMENT, "Image Adjustment");
        _tagNameMap.put(TAG_CAMERA_TONE_COMPENSATION, "Tone Compensation");
        _tagNameMap.put(TAG_ADAPTER, "Adapter");
        _tagNameMap.put(TAG_LENS, "Lens");
        _tagNameMap.put(TAG_MANUAL_FOCUS_DISTANCE, "Manual Focus Distance");
        _tagNameMap.put(TAG_DIGITAL_ZOOM, "Digital Zoom");
        _tagNameMap.put(TAG_CAMERA_COLOR_MODE, "Colour Mode");
        _tagNameMap.put(TAG_CAMERA_HUE_ADJUSTMENT, "Camera Hue Adjustment");
        _tagNameMap.put(TAG_NEF_COMPRESSION, "NEF Compression");
        _tagNameMap.put(TAG_SATURATION, "Saturation");
        _tagNameMap.put(TAG_NOISE_REDUCTION, "Noise Reduction");
        _tagNameMap.put(TAG_LINEARIZATION_TABLE, "Linearization Table");
        _tagNameMap.put(TAG_NIKON_CAPTURE_DATA, "Nikon Capture Data");
        _tagNameMap.put(TAG_UNKNOWN_49, "Unknown 49");
        _tagNameMap.put(TAG_UNKNOWN_50, "Unknown 50");
        _tagNameMap.put(TAG_UNKNOWN_51, "Unknown 51");
        _tagNameMap.put(TAG_PRINT_IMAGE_MATCHING_INFO, "Print IM");
        _tagNameMap.put(TAG_UNKNOWN_52, "Unknown 52");
        _tagNameMap.put(TAG_UNKNOWN_53, "Unknown 53");
        _tagNameMap.put(TAG_NIKON_CAPTURE_VERSION, "Nikon Capture Version");
        _tagNameMap.put(TAG_NIKON_CAPTURE_OFFSETS, "Nikon Capture Offsets");
        _tagNameMap.put(TAG_NIKON_SCAN, "Nikon Scan");
        _tagNameMap.put(TAG_UNKNOWN_54, "Unknown 54");
        _tagNameMap.put(TAG_NEF_BIT_DEPTH, "NEF Bit Depth");
        _tagNameMap.put(TAG_UNKNOWN_55, "Unknown 55");
    }

    public NikonType2MakernoteDirectory()
    {
        this.setDescriptor(new NikonType2MakernoteDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Nikon Makernote";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
