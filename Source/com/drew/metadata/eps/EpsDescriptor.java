package com.drew.metadata.eps;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;
import javafx.geometry.BoundingBox;

import java.util.Scanner;

import static com.drew.metadata.eps.EpsDirectory.*;

/**
 * @author Payton Garland
 */
public class EpsDescriptor extends TagDescriptor<EpsDirectory>
{

    public EpsDescriptor(@NotNull EpsDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }

}
