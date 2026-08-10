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
package com.drew.imaging.isobmff;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents an ISO Base Media File Format (ISOBMFF) box header.
 * <p>
 * ISO/IEC 14496-12 section 4.2 defines the box structure:
 * <ul>
 *   <li>4 bytes: size (or 1 for large-size, 0 for EOF-terminated)</li>
 *   <li>4 bytes: type (fourCC)</li>
 *   <li>8 bytes: large size (only when size == 1)</li>
 *   <li>16 bytes: usertype UUID (only when type == "uuid")</li>
 * </ul>
 */
public final class IsoBox
{
    /** Total size of the box in bytes including the header, or -1 if the box extends to EOF. */
    public final long size;

    /** Four-character box type code. */
    public final String type;

    /** 16-byte user-defined type for {@code uuid} boxes; {@code null} otherwise. */
    public final byte[] usertype;

    /** Byte count of the payload (data after the full header). */
    final long payloadSize;

    IsoBox(SequentialReader reader) throws IOException
    {
        int headerSize = 8; // minimum: 4 (size field) + 4 (type field)

        long rawSize = reader.getUInt32();
        this.type = reader.getString(4);

        long resolvedSize;
        if (rawSize == 1) {
            resolvedSize = reader.getInt64();
            headerSize += 8;
        } else if (rawSize == 0) {
            resolvedSize = -1; // extends to EOF
        } else {
            resolvedSize = rawSize;
        }
        this.size = resolvedSize;

        if ("uuid".equals(this.type)) {
            this.usertype = reader.getBytes(16);
            headerSize += 16;
        } else {
            this.usertype = null;
        }

        this.payloadSize = (resolvedSize == -1) ? -1 : resolvedSize - headerSize;
    }

    /** Returns {@code true} if this is a {@code uuid} box whose usertype matches the given 16 bytes. */
    public boolean usertypeMatches(byte[] expected)
    {
        return usertype != null && Arrays.equals(usertype, expected);
    }
}
