package com.drew.lang;

import com.drew.lang.annotations.NotNull;

/**
 * Models a key/value pair, where both are non-null {@link String} objects.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class KeyValuePair
{
    private final String _key;
    private final String _value;

    public KeyValuePair(@NotNull String key, @NotNull String value)
    {
        _key = key;
        _value = value;
    }

    @NotNull
    public String getKey()
    {
        return _key;
    }

    @NotNull
    public String getValue()
    {
        return _value;
    }
}
