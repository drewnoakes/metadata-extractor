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
package com.drew.metadata.tiff;

import com.drew.imaging.tiff.TiffHandler;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.ErrorDirectory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;

import java.util.Stack;

/**
 * Adapter between the {@link TiffHandler} interface and the {@link Metadata}/{@link Directory} object model.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class DirectoryTiffHandler implements TiffHandler
{
    private final Stack<Directory> _directoryStack = new Stack<Directory>();

    @Nullable private Directory _rootParentDirectory;
    @Nullable protected Directory _currentDirectory;
    protected final Metadata _metadata;

    protected DirectoryTiffHandler(Metadata metadata, @Nullable Directory parentDirectory)
    {
        _metadata = metadata;
        _rootParentDirectory = parentDirectory;
    }

    public void endingIFD()
    {
        _currentDirectory = _directoryStack.empty() ? null : _directoryStack.pop();
    }

    protected void pushDirectory(@NotNull Class<? extends Directory> directoryClass)
    {
        Directory newDirectory;

        try {
            newDirectory = directoryClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // If this is the first directory, don't add to the stack
        if (_currentDirectory == null) {
            // Apply any pending root parent to this new directory
            if (_rootParentDirectory != null) {
                newDirectory.setParent(_rootParentDirectory);
                _rootParentDirectory = null;
            }
        }
        else {
            // The current directory is pushed onto the stack, and set as the new directory's parent
            _directoryStack.push(_currentDirectory);
            newDirectory.setParent(_currentDirectory);
        }

        _currentDirectory = newDirectory;
        _metadata.addDirectory(_currentDirectory);
    }

    public void warn(@NotNull String message)
    {
        getCurrentOrErrorDirectory().addError(message);
    }

    public void error(@NotNull String message)
    {
        getCurrentOrErrorDirectory().addError(message);
    }

    @NotNull
    private Directory getCurrentOrErrorDirectory()
    {
        if (_currentDirectory != null)
            return _currentDirectory;
        ErrorDirectory error = _metadata.getFirstDirectoryOfType(ErrorDirectory.class);
        if (error != null)
            return error;
        pushDirectory(ErrorDirectory.class);
        return _currentDirectory;
    }

    public void setByteArray(int tagId, @NotNull byte[] bytes)
    {
        _currentDirectory.setByteArray(tagId, bytes);
    }

    public void setString(int tagId, @NotNull StringValue string)
    {
        _currentDirectory.setStringValue(tagId, string);
    }

    public void setRational(int tagId, @NotNull Rational rational)
    {
        _currentDirectory.setRational(tagId, rational);
    }

    public void setRationalArray(int tagId, @NotNull Rational[] array)
    {
        _currentDirectory.setRationalArray(tagId, array);
    }

    public void setFloat(int tagId, float float32)
    {
        _currentDirectory.setFloat(tagId, float32);
    }

    public void setFloatArray(int tagId, @NotNull float[] array)
    {
        _currentDirectory.setFloatArray(tagId, array);
    }

    public void setDouble(int tagId, double double64)
    {
        _currentDirectory.setDouble(tagId, double64);
    }

    public void setDoubleArray(int tagId, @NotNull double[] array)
    {
        _currentDirectory.setDoubleArray(tagId, array);
    }

    public void setInt8s(int tagId, byte int8s)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8s);
    }

    public void setInt8sArray(int tagId, @NotNull byte[] array)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setByteArray(tagId, array);
    }

    public void setInt8u(int tagId, short int8u)
    {
        // NOTE Directory stores all integral types as int32s, except for int32u and long
        _currentDirectory.setInt(tagId, int8u);
    }

    public void setInt8uArray(int tagId, @NotNull short[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array);
    }

    public void setInt16s(int tagId, int int16s)
    {
        // TODO create and use a proper setter for int16u?
        _currentDirectory.setInt(tagId, int16s);
    }

    public void setInt16sArray(int tagId, @NotNull short[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array);
    }

    public void setInt16u(int tagId, int int16u)
    {
        // TODO create and use a proper setter for
        _currentDirectory.setInt(tagId, int16u);
    }

    public void setInt16uArray(int tagId, @NotNull int[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array);
    }

    public void setInt32s(int tagId, int int32s)
    {
        _currentDirectory.setInt(tagId, int32s);
    }

    public void setInt32sArray(int tagId, @NotNull int[] array)
    {
        _currentDirectory.setIntArray(tagId, array);
    }

    public void setInt32u(int tagId, long int32u)
    {
        _currentDirectory.setLong(tagId, int32u);
    }

    public void setInt32uArray(int tagId, @NotNull long[] array)
    {
        // TODO create and use a proper setter for short[]
        _currentDirectory.setObjectArray(tagId, array);
    }
}
