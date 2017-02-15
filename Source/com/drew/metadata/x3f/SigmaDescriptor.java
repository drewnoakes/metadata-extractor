package com.drew.metadata.x3f;

import com.drew.metadata.TagDescriptor;

//FIXME: This descriptor is entirely unnecessary, just for back compat
class SigmaDescriptor extends TagDescriptor
{
    public SigmaDescriptor(SigmaPropertyDirectory sigmaDirectory)
    {
        super(sigmaDirectory);
    }

    @Override
    public String getDescription(int tagType)
    {
        //FIXME: This circular logic is eliminated with Enum model
        return SigmaPropertyKeys.fromValue(tagType).getDescription(_directory);
    }
}
