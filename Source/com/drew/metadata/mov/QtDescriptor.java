package com.drew.metadata.mov;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

public class QtDescriptor extends TagDescriptor<QtDirectory> {

    public QtDescriptor(@NotNull QtDirectory directory) { super(directory); }

    @Override
    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }
}
