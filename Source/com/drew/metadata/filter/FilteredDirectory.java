/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.metadata.filter;

import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.StringValue;

import java.util.Date;
import java.util.HashMap;


/**
 * A dummy class that doesn't store any values. It is used in place of
 * instances that have been filtered out.
 */
public class FilteredDirectory extends Directory {

    protected final Class<? extends Directory> _substituteFor;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    public FilteredDirectory(@Nullable Class<? extends Directory> substituteFor) {
        _substituteFor = substituteFor;
    }

    @Nullable
    public Class<? extends Directory> getSubstituteFor() {
        return _substituteFor;
    }

    @Override
    @NotNull
    public String getName() {
        return "Filtered";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }

    @Override
    public boolean containsTag(int tagType) {
        return false;
    }

    @Override
    public void addError(String message) {
    }

    @Override
    @Nullable
    public Directory getParent() {
        if (super.getParent() == null)
            return null;

        if (super.getParent() instanceof FilteredDirectory)
            return super.getParent().getParent();

        return super.getParent();
    }

    @Override
    public void setInt(int tagType, int value, MetadataFilter filter) {
    }

    @Override
    public void setIntArray(int tagType, int[] ints, MetadataFilter filter) {
    }

    @Override
    public void setFloat(int tagType, float value, MetadataFilter filter) {
    }

    @Override
    public void setFloatArray(int tagType, float[] floats, MetadataFilter filter) {
    }

    @Override
    public void setDouble(int tagType, double value, MetadataFilter filter) {
    }

    @Override
    public void setDoubleArray(int tagType, double[] doubles, MetadataFilter filter) {
    }

    @Override
    public void setStringValue(int tagType, StringValue value, MetadataFilter filter) {
    }

    @Override
    public void setString(int tagType, String value, MetadataFilter filter) {
    }

    @Override
    public void setStringArray(int tagType, String[] strings, MetadataFilter filter) {
    }

    @Override
    public void setStringValueArray(int tagType, StringValue[] strings, MetadataFilter filter) {
    }

    @Override
    public void setBoolean(int tagType, boolean value, MetadataFilter filter) {
    }

    @Override
    public void setLong(int tagType, long value, MetadataFilter filter) {
    }

    @Override
    public void setDate(int tagType, Date value, MetadataFilter filter) {
    }

    @Override
    public void setRational(int tagType, Rational rational, MetadataFilter filter) {
    }

    @Override
    public void setRationalArray(int tagType, Rational[] rationals, MetadataFilter filter) {
    }

    @Override
    public void setByteArray(int tagType, byte[] bytes, MetadataFilter filter) {
    }

    @Override
    public void setObject(int tagType, Object value, MetadataFilter filter) {
    }

    @Override
    public void setObjectArray(int tagType, Object array, MetadataFilter filter) {
    }
}
