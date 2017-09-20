package com.drew.metadata.heif;

import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class HeifContainerTypes
{
    public static final String BOX_METADATA                         = "meta";
    public static final String BOX_IMAGE_PROPERTY                   = "iprp";
    public static final String BOX_ITEM_PROPERTY                    = "ipco";

    public static ArrayList<String> _containerList = new ArrayList<String>();

    static {
        _containerList.add(BOX_METADATA);
        _containerList.add(BOX_IMAGE_PROPERTY);
        _containerList.add(BOX_ITEM_PROPERTY);
    }
}
