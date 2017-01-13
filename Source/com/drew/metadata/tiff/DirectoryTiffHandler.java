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
package com.drew.metadata.tiff;

import com.drew.imaging.tiff.TiffHandler;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.filter.FilteredDirectory;
import com.drew.metadata.filter.MetadataFilter;

import java.util.Stack;

/**
 * Adapter between the {@link TiffHandler} interface and the {@link Metadata}/{@link Directory} object model.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class DirectoryTiffHandler implements TiffHandler
{
    private final Stack<Directory> _directoryStack = new Stack<Directory>();

    protected Directory _currentDirectory;
    protected final Metadata _metadata;

    protected DirectoryTiffHandler(Metadata metadata)
    {
        _metadata = metadata;
    }

    protected boolean isCurrentInstanceOf(@Nullable Class<? extends Directory> directoryClass) {
        if (directoryClass == null)
            return false;
        if (_currentDirectory instanceof FilteredDirectory) {
            return (((FilteredDirectory) _currentDirectory).getSubstituteFor().isAssignableFrom(directoryClass));
        }
        return (directoryClass.isInstance(_currentDirectory));
    }

    public void endingIFD()
    {
        _currentDirectory = _directoryStack.empty() ? null : _directoryStack.pop();
    }

    protected void pushDirectory(@NotNull Class<? extends Directory> directoryClass, @Nullable final MetadataFilter filter)
    {
        boolean filtered = filter != null && !filter.directoryFilter(directoryClass);
        Directory newDirectory = null;

        if (filtered) {
            newDirectory = new FilteredDirectory(directoryClass);
        } else {
            try {
                newDirectory = directoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (newDirectory != null)
        {
            // If this is the first directory, don't add to the stack
            if (_currentDirectory != null)
            {
                _directoryStack.push(_currentDirectory);
                newDirectory.setParent(_currentDirectory instanceof FilteredDirectory ? _currentDirectory.getParent() : _currentDirectory);
            }
            _currentDirectory = newDirectory;
            if (!filtered)
                _metadata.addDirectory(_currentDirectory);
        }
    }

    public void warn(@NotNull String message)
    {
        _currentDirectory.addError(message);
    }

    public void error(@NotNull String message)
    {
        _currentDirectory.addError(message);
    }

    public void setByteArray(int tagId, @NotNull byte[] bytes)
    {
        _currentDirectory.setByteArray(tagId, bytes, null);
    }

    public void setByteArray(int tagId, @NotNull byte[] bytes, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setByteArray(tagId, bytes, filter);
    }

    public void setString(int tagId, @NotNull StringValue string)
    {
        _currentDirectory.setStringValue(tagId, string, null);
    }

    public void setString(int tagId, @NotNull StringValue string, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setStringValue(tagId, string, filter);
    }

    public void setRational(int tagId, @NotNull Rational rational)
    {
        _currentDirectory.setRational(tagId, rational, null);
    }

    public void setRational(int tagId, @NotNull Rational rational, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setRational(tagId, rational, filter);
    }

    public void setRationalArray(int tagId, @NotNull Rational[] array)
    {
        _currentDirectory.setRationalArray(tagId, array, null);
    }

    public void setRationalArray(int tagId, @NotNull Rational[] array, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setRationalArray(tagId, array, filter);
    }

    public void setFloat(int tagId, float float32)
    {
        _currentDirectory.setFloat(tagId, float32, null);
    }

    public void setFloat(int tagId, float float32, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setFloat(tagId, float32, filter);
    }

    public void setFloatArray(int tagId, @NotNull float[] array)
    {
        _currentDirectory.setFloatArray(tagId, array, null);
    }

    public void setFloatArray(int tagId, @NotNull float[] array, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setFloatArray(tagId, array, filter);
    }

    public void setDouble(int tagId, double double64)
    {
        _currentDirectory.setDouble(tagId, double64, null);
    }

    public void setDouble(int tagId, double double64, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setDouble(tagId, double64, filter);
    }

    public void setDoubleArray(int tagId, @NotNull double[] array)
    {
        _currentDirectory.setDoubleArray(tagId, array, null);
    }

    public void setDoubleArray(int tagId, @NotNull double[] array, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setDoubleArray(tagId, array, filter);
    }

    public void setInt8s(int tagId, byte int8s)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8s, null);
    }

    public void setInt8s(int tagId, byte int8s, @Nullable final MetadataFilter filter)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8s, filter);
    }

    public void setInt8sArray(int tagId, @NotNull byte[] array)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setByteArray(tagId, array, null);
    }

    public void setInt8sArray(int tagId, @NotNull byte[] array, @Nullable final MetadataFilter filter)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setByteArray(tagId, array, filter);
    }

    public void setInt8u(int tagId, short int8u)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8u, null);
    }

    public void setInt8u(int tagId, short int8u, @Nullable final MetadataFilter filter)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8u, filter);
    }

    public void setInt8uArray(int tagId, @NotNull short[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, null);
    }

    public void setInt8uArray(int tagId, @NotNull short[] array, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, filter);
    }

    public void setInt16s(int tagId, int int16s)
    {
        // TODO create and use a proper setter for int16u?
        _currentDirectory.setInt(tagId, int16s, null);
    }

    public void setInt16s(int tagId, int int16s, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for int16u?
        _currentDirectory.setInt(tagId, int16s, filter);
    }

    public void setInt16sArray(int tagId, @NotNull short[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, null);
    }

    public void setInt16sArray(int tagId, @NotNull short[] array, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, filter);
    }

    public void setInt16u(int tagId, int int16u)
    {
        // TODO create and use a proper setter for
        _currentDirectory.setInt(tagId, int16u, null);
    }

    public void setInt16u(int tagId, int int16u, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for
        _currentDirectory.setInt(tagId, int16u, filter);
    }

    public void setInt16uArray(int tagId, @NotNull int[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, null);
    }

    public void setInt16uArray(int tagId, @NotNull int[] array, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, filter);
    }

    public void setInt32s(int tagId, int int32s)
    {
        _currentDirectory.setInt(tagId, int32s, null);
    }

    public void setInt32s(int tagId, int int32s, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setInt(tagId, int32s, filter);
    }

    public void setInt32sArray(int tagId, @NotNull int[] array)
    {
        _currentDirectory.setIntArray(tagId, array, null);
    }

    public void setInt32sArray(int tagId, @NotNull int[] array, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setIntArray(tagId, array, filter);
    }

    public void setInt32u(int tagId, long int32u)
    {
        _currentDirectory.setLong(tagId, int32u, null);
    }

    public void setInt32u(int tagId, long int32u, @Nullable final MetadataFilter filter)
    {
        _currentDirectory.setLong(tagId, int32u, filter);
    }

    public void setInt32uArray(int tagId, @NotNull long[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, null);
    }

    public void setInt32uArray(int tagId, @NotNull long[] array, @Nullable final MetadataFilter filter)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array, filter);
    }
}
