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

package com.drew.imaging.riff;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;

/**
 * An exception class thrown upon unexpected and fatal conditions while processing a RIFF file.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class RiffProcessingException extends ImageProcessingException
{
    private static final long serialVersionUID = -1658134596321487960L;

    public RiffProcessingException(@Nullable String message)
    {
        super(message);
    }

    public RiffProcessingException(@Nullable String message, @Nullable Throwable cause)
    {
        super(message, cause);
    }

    public RiffProcessingException(@Nullable Throwable cause)
    {
        super(cause);
    }
}
