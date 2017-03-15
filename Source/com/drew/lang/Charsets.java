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
package com.drew.lang;

import java.nio.charset.Charset;

/**
 * Holds a set of commonly used character encodings.
 *
 * Newer JDKs include java.nio.charset.StandardCharsets, but we cannot use that in this library.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public final class Charsets
{
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset UTF_16 = Charset.forName("UTF-16");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
    public static final Charset WINDOWS_1252 = Charset.forName("Cp1252");
}
