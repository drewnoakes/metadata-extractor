/*
 * Copyright 2002-2019 Drew Noakes and contributors
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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Represents a compound exception, as modelled in JDK 1.4, but
 * unavailable in previous versions.  This class allows support
 * of these previous JDK versions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class CompoundException extends Exception
{
    private static final long serialVersionUID = -9207883813472069925L;

    @Nullable
    private final Throwable _innerException;

    public CompoundException(@Nullable String msg)
    {
        this(msg, null);
    }

    public CompoundException(@Nullable Throwable exception)
    {
        this(null, exception);
    }

    public CompoundException(@Nullable String msg, @Nullable Throwable innerException)
    {
        super(msg);
        _innerException = innerException;
    }

    @Nullable
    public Throwable getInnerException()
    {
        return _innerException;
    }

    @Override
    @NotNull
    public String toString()
    {
        StringBuilder string = new StringBuilder();
        string.append(super.toString());
        if (_innerException != null) {
            string.append("\n");
            string.append("--- inner exception ---");
            string.append("\n");
            string.append(_innerException.toString());
        }
        return string.toString();
    }

    @Override
    public void printStackTrace(@NotNull PrintStream s)
    {
        super.printStackTrace(s);
        if (_innerException != null) {
            s.println("--- inner exception ---");
            _innerException.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(@NotNull PrintWriter s)
    {
        super.printStackTrace(s);
        if (_innerException != null) {
            s.println("--- inner exception ---");
            _innerException.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace()
    {
        super.printStackTrace();
        if (_innerException != null) {
            System.err.println("--- inner exception ---");
            _innerException.printStackTrace();
        }
    }
}
