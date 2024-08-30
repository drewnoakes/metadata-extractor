package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;
import static com.drew.metadata.mkv.ElementIDs.*;

public class EbmlDirectory extends Directory {

    private static final HashMap<Integer, String> _tagNameMap = new HashMap<>();
    static {
        _tagNameMap.put(EBML_VERSION, "Version");
        _tagNameMap.put(EBML_READ_VERSION, "Read version");
        _tagNameMap.put(EBML_MAX_ID_LENGTH, "Maximum ID length");
        _tagNameMap.put(EBML_MAX_SIZE_LENGTH, "Maximum size length");
        _tagNameMap.put(DOCTYPE, "Doctype");
        _tagNameMap.put(DOCTYPE_VERSION, "Doctype version");
        _tagNameMap.put(DOCTYPE_READ_VERSION, "Doctype read version");
    }

    public EbmlDirectory() {
        this.setDescriptor(new TagDescriptor<Directory>(this));
    }

    @Override
    public String getName() {
        return "EBML";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
