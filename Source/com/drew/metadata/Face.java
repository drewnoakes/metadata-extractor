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
import com.drew.lang.annotations.Nullable;

/**
 * Class to hold information about a detected or recognized face in a photo.
 * <p>
 * When a face is <em>detected</em>, the camera believes that a face is present at a given location in
 * the image, but is not sure whose face it is.  When a face is <em>recognised</em>, then the face is
 * both detected and identified as belonging to a known person.
 *
 * @author Philipp Sandhaus, Drew Noakes
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
    private final Age _age;

    public Face(int x, int y, int width, int height, @Nullable String name, @Nullable Age age)
    {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _name = name;
        _age = age;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getWidth()
    {
        return _width;
    }

    public int getHeight()
    {
        return _height;
    }

    @Nullable
    public String getName()
    {
        return _name;
    }

    @Nullable
    public Age getAge()
    {
        return _age;
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Face face = (Face)o;

        if (_height != face._height) return false;
        if (_width != face._width) return false;
        if (_x != face._x) return false;
        if (_y != face._y) return false;
        if (_age != null ? !_age.equals(face._age) : face._age != null) return false;
        if (_name != null ? !_name.equals(face._name) : face._name != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = _x;
        result = 31 * result + _y;
        result = 31 * result + _width;
        result = 31 * result + _height;
        result = 31 * result + (_name != null ? _name.hashCode() : 0);
        result = 31 * result + (_age != null ? _age.hashCode() : 0);
        return result;
    }

    @Override
    @NotNull
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("x: ").append(_x);
        result.append(" y: ").append(_y);
        result.append(" width: ").append(_width);
        result.append(" height: ").append(_height);
        if (_name != null)
            result.append(" name: ").append(_name);
        if (_age != null)
            result.append(" age: ").append(_age.toFriendlyString());
        return result.toString();
    }
}
