/*
 * Copyright 2002-2022 Drew Noakes and contributors
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
package com.drew.metadata.mp4.media;

import com.drew.imaging.mp4.Mp4Handler;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.ByteTrie;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4Context;
import com.drew.metadata.photoshop.PhotoshopReader;
import com.drew.metadata.xmp.XmpReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import static com.drew.metadata.mp4.media.Mp4UuidBoxDirectory.TAG_USER_DATA;
import static com.drew.metadata.mp4.media.Mp4UuidBoxDirectory.TAG_UUID;

public class Mp4UuidBoxHandler extends Mp4Handler<Mp4UuidBoxDirectory>
{
    // http://xhelmboyx.tripod.com/formats/mp4-layout.txt
    // http://fileformats.archiveteam.org/wiki/Boxes/atoms_format#UUID_boxes

    private enum UuidType
    {
        Unknown,
        Exif,                                  // 0537cdab-9d0c-4431-a72a-fa561f2a113e
        PhotoshopImageResources,               // 2c4c0100-8504-40b9-a03e-562148d6dfeb
        IptcIim,                               // 33c7a4d2-b81d-4723-a0ba-f1a3e097ad38
        PiffTrackEncryptionBox,                // 8974dbce-7be7-4c51-84f9-7148f9882554
        GeoJp2WorldFileBox,                    // 96a9f1f1-dc98-402d-a7ae-d68e34451809
        PiffSampleEncryptionBox,               // a2394f52-5a9b-4f14-a244-6c427c648df4
        GeoJp2GeoTiffBox,                      // b14bf8bd-083d-4b43-a5ae-8cd7d5a6ce03
        Xmp,                                   // be7acfcb-97a9-42e8-9c71-999491e3afac
        PiffProtectionSystemSpecificHeaderBox, // d08a4f18-10f3-4a82-b6c8-32d8aba183d3
    }

    private final static ByteTrie<UuidType> _uuidLookup;

    static
    {
        _uuidLookup = new ByteTrie<UuidType>();
        _uuidLookup.setDefaultValue(UuidType.Unknown);

        _uuidLookup.addPath(UuidType.Exif, new byte[] { 0x05, 0x37, (byte)0xcd, (byte)0xab, (byte)0x9d, 0x0c, 0x44, 0x31, (byte)0xa7, 0x2a, (byte)0xfa, 0x56, 0x1f, 0x2a, 0x11, 0x3e });
        _uuidLookup.addPath(UuidType.PhotoshopImageResources, new byte[] { 0x2c, 0x4c, 0x01, 0x00, (byte)0x85, 0x04, 0x40, (byte)0xb9, (byte)0xa0, 0x3e, 0x56, 0x21, 0x48, (byte)0xd6, (byte)0xdf, (byte)0xeb });
        _uuidLookup.addPath(UuidType.IptcIim, new byte[] { 0x33, (byte)0xc7, (byte)0xa4, (byte)0xd2, (byte)0xb8, 0x1d, 0x47, 0x23, (byte)0xa0, (byte)0xba, (byte)0xf1, (byte)0xa3, (byte)0xe0, (byte)0x97, (byte)0xad, 0x38 });
        _uuidLookup.addPath(UuidType.PiffTrackEncryptionBox, new byte[] { (byte)0x89, 0x74, (byte)0xdb, (byte)0xce, 0x7b, (byte)0xe7, 0x4c, 0x51, (byte)0x84, (byte)0xf9, 0x71, 0x48, (byte)0xf9, (byte)0x88, 0x25, 0x54 });
        _uuidLookup.addPath(UuidType.GeoJp2WorldFileBox, new byte[] { (byte)0x96, (byte)0xa9, (byte)0xf1, (byte)0xf1, (byte)0xdc, (byte)0x98, 0x40, 0x2d, (byte)0xa7, (byte)0xae, (byte)0xd6, (byte)0x8e, 0x34, 0x45, 0x18, 0x09 });
        _uuidLookup.addPath(UuidType.PiffSampleEncryptionBox, new byte[] { (byte)0xa2, 0x39, 0x4f, 0x52, 0x5a, (byte)0x9b, 0x4f, 0x14, (byte)0xa2, 0x44, 0x6c, 0x42, 0x7c, 0x64, (byte)0x8d, (byte)0xf4 });
        _uuidLookup.addPath(UuidType.GeoJp2GeoTiffBox, new byte[] { (byte)0xb1, 0x4b, (byte)0xf8, (byte)0xbd, 0x08, 0x3d, 0x4b, 0x43, (byte)0xa5, (byte)0xae, (byte)0x8c, (byte)0xd7, (byte)0xd5, (byte)0xa6, (byte)0xce, 0x03 });
        _uuidLookup.addPath(UuidType.Xmp, new byte[] { (byte)0xbe, 0x7a, (byte)0xcf, (byte)0xcb, (byte)0x97, (byte)0xa9, 0x42, (byte)0xe8, (byte)0x9c, 0x71, (byte)0x99, (byte)0x94, (byte)0x91, (byte)0xe3, (byte)0xaf, (byte)0xac });
        _uuidLookup.addPath(UuidType.PiffProtectionSystemSpecificHeaderBox, new byte[] { (byte)0xd0, (byte)0x8a, 0x4f, 0x18, 0x10, (byte)0xf3, 0x4a, (byte)0x82, (byte)0xb6, (byte)0xc8, 0x32, (byte)0xd8, (byte)0xab, (byte)0xa1, (byte)0x83, (byte)0xd3 });
    }

    public Mp4UuidBoxHandler(Metadata metadata)
    {
        super(metadata);
    }

    @NotNull
    @Override
    protected Mp4UuidBoxDirectory getDirectory()
    {
        return new Mp4UuidBoxDirectory();
    }

    @Override
    protected boolean shouldAcceptBox(@NotNull String type)
    {
        return type.equals(Mp4BoxTypes.BOX_USER_DEFINED);
    }

    @Override
    protected boolean shouldAcceptContainer(@NotNull String type)
    {
        return false;
    }

    @Override
    public Mp4Handler<?> processBox(@NotNull String type, byte[] payload, long boxSize, Mp4Context context) throws IOException
    {
        if (payload != null && payload.length >= 16) {
            UuidType uuidType = _uuidLookup.find(payload);

            if (uuidType == null) {
                return this;
            }

            switch (uuidType) {
                case Exif:
                    new ExifReader().extract(new ByteArrayReader(payload, 16), metadata, directory, 0);
                    break;
                case IptcIim:
                    new IptcReader().extract(new SequentialByteArrayReader(payload, 16), metadata, payload.length - 16, directory);
                    break;
                case PhotoshopImageResources:
                    new PhotoshopReader().extract(new SequentialByteArrayReader(payload, 16), payload.length - 16,  metadata, directory);
                    break;
                case Xmp:
                    new XmpReader().extract(payload, 16, payload.length - 16,  metadata, directory);
                    break;
                default:
                    SequentialReader reader = new SequentialByteArrayReader(payload);
                    String usertype = getUuid(reader.getBytes(16));
                    byte[] userData = reader.getBytes(reader.available());
                    directory.setString(TAG_UUID, usertype);
                    directory.setByteArray(TAG_USER_DATA, userData);
                    break;
            }
        }

        return this;
    }

    private static String getUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());

        return uuid.toString();
    }
}
