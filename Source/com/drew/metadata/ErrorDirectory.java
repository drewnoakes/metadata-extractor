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
package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import java.util.*;

/**
 * A directory to use for the reporting of errors. No values may be added to this directory, only warnings and errors.
 *
 * @author Drew Noakes https://drewnoakes.com
 */

public final class ErrorDirectory extends Directory
{

    public ErrorDirectory()
    {}

    public ErrorDirectory(String error)
    {
        super.addError(error);
    }

    @Override
    @NotNull
    public String getName()
    {
        return "Error";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return new HashMap<Integer, String>();
    }

    @Override
    @NotNull
    public String getTagName(int tagType)
    {
        return "";
    }

    @Override
    public boolean hasTagName(int tagType)
    {
        return false;
    }

    @Override
    public void setObject(int tagType, @NotNull Object value)
    {
        throw new UnsupportedOperationException(String.format("Cannot add value to %s.", ErrorDirectory.class.getName()));
    }
}
