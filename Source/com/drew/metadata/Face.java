/*
 * Copyright 2002-2011 Drew Noakes
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
package com.drew.metadata;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * Class to hold information about a detected or recognized face in a photo.
 *
 * @author psandhaus, Drew Noakes
 */
public class Face
{
    private final int _x;
    private final int _y;
    private final int _width;
    private final int _height;
    @Nullable
    private final String _name;
    @Nullable
    private final String _age;

    public Face(int x, int y, int width, int height, @Nullable String name, @Nullable String age)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _name = name;
        _age = age;
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (!(obj instanceof Face))
            return false;
        
        Face face = (Face)obj;
        boolean nameAndAge = true;
        if (_name != null && face._name != null)
            nameAndAge = _name.equalsIgnoreCase(face._name);
        if (nameAndAge && _age != null && face._age != null)
            nameAndAge = _age.equalsIgnoreCase(face._age);

        return face._x == _x &&
               face._y == _y &&
               face._width == _width &&
               face._height == _height &&
               nameAndAge;
    }

    @NotNull
    public String toString()
    {
        StringBuilder result = new StringBuilder().append("x: ").append(_x).append(" y: ").append(_y).append(" width: ").append(_width).append(" height: ").append(_height);
        if (_name != null)
            result.append(" name: ").append(_name);
        if (_age != null)
            result.append(" age: ").append(_age);
        return result.toString();
    }
}
