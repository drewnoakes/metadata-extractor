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

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.impl.XMPMetaImpl;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Wraps an instance of Adobe's {@link XMPMeta} object, which holds XMP data.
 * <p />
 * XMP uses a namespace and path format for identifying values, which does not map to metadata-extractor's
 * integer based tag identifiers. Therefore, XMP data is extracted and exposed via {@link XmpDirectory#getXMPMeta()}
 * which returns an instance of Adobe's {@link XMPMeta} which exposes the full XMP data set.
 *
 * @author Torsten Skadell
 * @author Drew Noakes https://drewnoakes.com
 */
@SuppressWarnings("WeakerAccess")
public class XmpDirectory extends Directory
{
    public static final int TAG_XMP_VALUE_COUNT = 0xFFFF;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_XMP_VALUE_COUNT, "XMP Value Count");
    }

    @Nullable
    private XMPMeta _xmpMeta;

    public XmpDirectory()
    {
        this.setDescriptor(new XmpDescriptor(this));
    }

    @Override
    @NotNull
    public String getName()
    {
        return "XMP";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }

    /**
     * Gets a map of all XMP properties in this directory.
     * <p>
     * This is required because XMP properties are represented as strings, whereas the rest of this library
     * uses integers for keys.
     */
    @NotNull
    public Map<String, String> getXmpProperties()
    {
        Map<String, String> propertyValueByPath = new HashMap<String, String>();

        if (_xmpMeta != null)
        {
            try {
                for (Iterator i = _xmpMeta.iterator(); i.hasNext(); ) {
                    XMPPropertyInfo prop = (XMPPropertyInfo)i.next();
                    String path = prop.getPath();
                    String value = prop.getValue();
                    if (path != null && value != null) {
                        propertyValueByPath.put(path, value);
                    }
                }
            } catch (XMPException ignored) {
            }
        }

        return Collections.unmodifiableMap(propertyValueByPath);
    }

    public void setXMPMeta(@NotNull XMPMeta xmpMeta)
    {
        _xmpMeta = xmpMeta;

        try {
            int valueCount = 0;
            for (Iterator i = _xmpMeta.iterator(); i.hasNext(); ) {
                XMPPropertyInfo prop = (XMPPropertyInfo)i.next();
                if (prop.getPath() != null) {
                    valueCount++;
                }
            }
            setInt(TAG_XMP_VALUE_COUNT, valueCount);
        } catch (XMPException ignored) {
        }
    }

    /**
     * Gets the XMPMeta object used to populate this directory. It can be used for more XMP-oriented operations.
     * If one does not exist it will be created.
     */
    @NotNull
    public XMPMeta getXMPMeta()
    {
        if (_xmpMeta == null)
            _xmpMeta = new XMPMetaImpl();
        return _xmpMeta;
    }
}
