/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew.metadata.heif;

import com.drew.imaging.heif.HeifHandler;
import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.boxes.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Payton Garland
 */
public class HeifPictureHandler extends HeifHandler<HeifDirectory>
{
    ItemProtectionBox itemProtectionBox;
    PrimaryItemBox primaryItemBox;
    ItemInfoBox itemInfoBox;
    ItemLocationBox itemLocationBox;

    public HeifPictureHandler(Metadata metadata)
    {
        super(metadata);

        itemProtectionBox = null;
        primaryItemBox = null;
        itemInfoBox = null;
        itemLocationBox = null;
    }

    @Override
    protected boolean shouldAcceptBox(Box box)
    {
        List<String> boxes = Arrays.asList(HeifBoxTypes.BOX_ITEM_PROTECTION,
            HeifBoxTypes.BOX_PRIMARY_ITEM,
            HeifBoxTypes.BOX_ITEM_INFO,
            HeifBoxTypes.BOX_ITEM_LOCATION,
            HeifBoxTypes.BOX_IMAGE_SPATIAL_EXTENTS,
            HeifBoxTypes.BOX_AUXILIARY_TYPE_PROPERTY,
            HeifBoxTypes.BOX_IMAGE_ROTATION,
            HeifBoxTypes.BOX_COLOUR_INFO,
            HeifBoxTypes.BOX_PIXEL_INFORMATION);

        return boxes.contains(box.type);
    }

    @Override
    protected boolean shouldAcceptContainer(Box box)
    {
        return box.type.equals(HeifContainerTypes.BOX_IMAGE_PROPERTY)
            || box.type.equals(HeifContainerTypes.BOX_ITEM_PROPERTY);
    }

    @Override
    protected HeifHandler processBox(Box box, byte[] payload) throws IOException
    {
        SequentialReader reader = new SequentialByteArrayReader(payload);
        if (box.type.equals(HeifBoxTypes.BOX_ITEM_PROTECTION)) {
            itemProtectionBox = new ItemProtectionBox(reader, box);
        } else if (box.type.equals(HeifBoxTypes.BOX_PRIMARY_ITEM)) {
            primaryItemBox = new PrimaryItemBox(reader, box);
        } else if (box.type.equals(HeifBoxTypes.BOX_ITEM_INFO)) {
            itemInfoBox = new ItemInfoBox(reader, box);
            itemInfoBox.addMetadata(directory);
        } else if (box.type.equals(HeifBoxTypes.BOX_ITEM_LOCATION)) {
            itemLocationBox = new ItemLocationBox(reader, box);
        } else if (box.type.equals(HeifBoxTypes.BOX_IMAGE_SPATIAL_EXTENTS)) {
            ImageSpatialExtentsProperty imageSpatialExtentsProperty = new ImageSpatialExtentsProperty(reader, box);
            imageSpatialExtentsProperty.addMetadata(directory);
        } else if (box.type.equals(HeifBoxTypes.BOX_AUXILIARY_TYPE_PROPERTY)) {
            AuxiliaryTypeProperty auxiliaryTypeProperty = new AuxiliaryTypeProperty(reader, box);
        } else if (box.type.equals(HeifBoxTypes.BOX_IMAGE_ROTATION)) {
            ImageRotationBox imageRotationBox = new ImageRotationBox(reader, box);
            imageRotationBox.addMetadata(directory);
        } else if (box.type.equals(HeifBoxTypes.BOX_COLOUR_INFO)) {
            ColourInformationBox colourInformationBox = new ColourInformationBox(reader, box, metadata);
            colourInformationBox.addMetadata(directory);
        } else if (box.type.equals(HeifBoxTypes.BOX_PIXEL_INFORMATION)) {
            PixelInformationBox pixelInformationBox = new PixelInformationBox(reader, box);
            pixelInformationBox.addMetadata(directory);
        }
        return this;
    }

    @Override
    protected void processContainer(Box box, SequentialReader reader) throws IOException
    {

    }

    @Override
    protected HeifDirectory getDirectory()
    {
        return new HeifDirectory();
    }
}
