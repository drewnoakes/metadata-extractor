package com.drew.metadata.mkv;

public class EbmlElement {
    private final String name;
    private final Type type;
    private final DirectoryType directory;

    public String toString(){
        return name;
    }
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public EbmlElement(String name, Type type) {
        this(name, type, DirectoryType.UNKNOWN);
    }
    public EbmlElement(String name, Type type, DirectoryType directory) {
        this.name = name;
        this.type = type;
        this.directory = directory;
    }

    public DirectoryType getDirectory() {
        return directory;
    }

    public enum Type{
        MASTER, STRING, INTEGER, SIGNED_INTEGER, UTF8, BINARY, VOID, UNKNOWN, FLOAT
    }

    public enum DirectoryType{
        EBML, SEGMENT, VIDEO, AUDIO, UNKNOWN
    }
}
