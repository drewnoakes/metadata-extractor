/*
 * Copyright 2002-2012 Drew Noakes
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
 *    http://drewnoakes.com/code/exif/
 *    http://code.google.com/p/metadata-extractor/
 */
package com.drew.imaging.jpeg;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;

/**
 * An exception class thrown upon unexpected and fatal conditions while processing a Jpeg file.
 *
 * @author Drew Noakes http://drewnoakes.com
 */
public class JpegProcessingException extends ImageProcessingException
{
    private static final long serialVersionUID = -7870179776125450158L;

    public JpegProcessingException(@Nullable String message)
    {
        super(message);
    }

    public JpegProcessingException(@Nullable String message, @Nullable Throwable cause)
    {
        super(message, cause);
    }

    public JpegProcessingException(@Nullable Throwable cause)
    {
        super(cause);
    }
}
