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
package com.drew.metadata.file;

import com.drew.imaging.FileType;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland https://github.com/PaytonGarland
 */
@SuppressWarnings("WeakerAccess")
public class FileTypeDirectory extends Directory
{
    public static final int TAG_DETECTED_FILE_TYPE_NAME = 1;
    public static final int TAG_DETECTED_FILE_TYPE_LONG_NAME = 2;
    public static final int TAG_DETECTED_FILE_MIME_TYPE = 3;
    public static final int TAG_EXPECTED_FILE_NAME_EXTENSION = 4;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_DETECTED_FILE_TYPE_NAME, "Detected File Type Name");
        _tagNameMap.put(TAG_DETECTED_FILE_TYPE_LONG_NAME, "Detected File Type Long Name");
        _tagNameMap.put(TAG_DETECTED_FILE_MIME_TYPE, "Detected MIME Type");
        _tagNameMap.put(TAG_EXPECTED_FILE_NAME_EXTENSION, "Expected File Name Extension");
    }

    public FileTypeDirectory(FileType fileType)
    {
        this.setDescriptor(new FileTypeDescriptor(this));

        setString(TAG_DETECTED_FILE_TYPE_NAME, fileType.getName());
        setString(TAG_DETECTED_FILE_TYPE_LONG_NAME, fileType.getLongName());

        if (fileType.getMimeType() != null)
            setString(TAG_DETECTED_FILE_MIME_TYPE, fileType.getMimeType());

        if (fileType.getCommonExtension() != null)
            setString(TAG_EXPECTED_FILE_NAME_EXTENSION, fileType.getCommonExtension());
    }

    @Override
    @NotNull
    public String getName()
    {
        return "File Type";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
