package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;

import java.util.HashMap;

public class Mp4UserBoxDirectory extends Mp4MediaDirectory {
    public static final Integer TAG_UUID = 901;
    public static final Integer TAG_LENGTH = 902;
    public static final Integer TAG_USER_DATA = 903;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        Mp4UserBoxDirectory.addMp4MediaTags(_tagNameMap);
        _tagNameMap.put(TAG_UUID, "uuid");
        _tagNameMap.put(TAG_LENGTH, "length");
        _tagNameMap.put(TAG_USER_DATA, "data");
    }

    public Mp4UserBoxDirectory() {
        this.setDescriptor(new Mp4UserBoxDescriptor(this));
    }

    @NotNull
    @Override
    public String getName() {
        return "UserData";
    }

    @NotNull
    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
