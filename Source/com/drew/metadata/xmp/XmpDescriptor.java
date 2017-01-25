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
package com.drew.metadata.xmp;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * Contains all logic for the presentation of xmp data, as stored in Xmp-Segment.  Use
 * this class to provide human-readable descriptions of tag values.
 *
 * @author Torsten Skadell, Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class XmpDescriptor extends TagDescriptor<XmpDirectory>
{
    public XmpDescriptor(@NotNull XmpDirectory directory)
    {
        super(directory);
    }
}
