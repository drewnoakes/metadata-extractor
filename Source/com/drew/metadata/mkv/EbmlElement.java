package com.drew.metadata.mkv;

public class EbmlElement
{
    private final String _name;
    private final Type _type;
    private final DirectoryType _directory;

    public EbmlElement(String _name, Type type)
    {
        this(_name, type, DirectoryType.UNKNOWN);
    }

    public EbmlElement(String name, Type type, DirectoryType directory)
    {
        _name = name;
        _type = type;
        _directory = directory;
    }

    public String toString()
    {
        return _name;
    }

    public String get_name()
    {
        return _name;
    }

    public Type get_type()
    {
        return _type;
    }

    public DirectoryType getDirectory()
    {
        return _directory;
    }

    public enum Type
    {
        MASTER, STRING, INTEGER, SIGNED_INTEGER, UTF8, BINARY, VOID, UNKNOWN, FLOAT
    }

    public enum DirectoryType
    {
        EBML, SEGMENT, VIDEO, AUDIO, UNKNOWN
    }
}
