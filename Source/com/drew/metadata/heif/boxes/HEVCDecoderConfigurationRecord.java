package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Payton Garland
 */
public class HEVCDecoderConfigurationRecord extends Box
{
    int holder;

    int configurationVersion;
    int generalProfileSpace;
    int generalTierFlag;
    int generalProfileIdc;
    long generalProfileCompatibilityFlags;
    long generalConstraintIndicatorFlags;
    int generalLevelIdc;
    // 4-bits Reserved
    int minSpatialSegmentationIdc;
    // 6-bits Reserved
    int parallelismType;
    // 6-bits Reserved
    int chromaFormat;
    // 5-bits Reserved
    int bitDepthLumaMinus8;
    // 5-bits Reserved
    int bitDepthChromaMinus8;
    int avgFrameRate;
    int constasntFrameRate;
    int numTemporalLayers;
    int temporalIdNested;
    int lengthSizeMinusOne;
    int numOfArrays;
    NalUnitContainer nalUnitContainer;

    public HEVCDecoderConfigurationRecord(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        configurationVersion = reader.getUInt8();
        holder = reader.getUInt8();
        generalProfileSpace = (holder & 0xC0) >> 6;
        generalTierFlag = (holder & 0x20) >> 5;
        generalProfileIdc = (holder & 0x1F);
        generalProfileCompatibilityFlags = reader.getUInt32();
        generalConstraintIndicatorFlags = (reader.getUInt32() << 16) + reader.getUInt16();
        generalLevelIdc = reader.getUInt8();
        minSpatialSegmentationIdc = reader.getUInt16() & 0x0FFF;
        parallelismType = reader.getUInt8() & 0x03;
        chromaFormat = reader.getUInt8() & 0x03;
        bitDepthLumaMinus8 = reader.getUInt8() & 0x07;
        bitDepthChromaMinus8 = reader.getUInt8() & 0x07;
        avgFrameRate = reader.getInt16();
        holder = reader.getUInt8();
        constasntFrameRate = (holder & 0xC0) >> 6;
        numTemporalLayers = (holder & 0x38) >> 3;
        temporalIdNested = (holder & 0x04) >> 2;
        lengthSizeMinusOne = (holder & 0x03);
        numOfArrays = reader.getUInt8();

        for (int i = 0 ; i < numOfArrays; i++) {
            nalUnitContainer = new NalUnitContainer(reader);
        }
    }

    private class NalUnitContainer
    {
        int holder;
        int arrayCompleteness;
        // 1-bit Reserved
        int nalUnitType;
        int numNalus;
        ArrayList<byte[]> nalUnits;

        public NalUnitContainer(SequentialReader reader) throws IOException
        {
            holder = reader.getUInt8();
            nalUnits = new ArrayList<byte[]>();
            arrayCompleteness = (holder & 0x80) >> 7;
            nalUnitType = (holder & 0x3F);

            

            numNalus = reader.getUInt16();
            for (int i = 0; i < numNalus; i++) {
                int nalUnitLength = reader.getUInt16();
                nalUnits.add(reader.getBytes(nalUnitLength));
            }
        }
    }
}
