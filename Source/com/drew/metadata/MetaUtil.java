package com.drew.metadata;

import com.drew.lang.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MetaUtil
{
    @Nullable
    public static String getShutterSpeedDescription(Float apexValue)
    {
        // I believe this method to now be stable, but am leaving some alternative snippets of
        // code in here, to assist anyone who's looking into this (given that I don't have a public CVS).

//        float apexValue = _directory.getFloat(ExifSubIFDDirectory.TAG_SHUTTER_SPEED);
//        int apexPower = (int)Math.pow(2.0, apexValue);
//        return "1/" + apexPower + " sec";
        // TODO test this method
        // thanks to Mark Edwards for spotting and patching a bug in the calculation of this
        // description (spotted bug using a Canon EOS 300D)
        // thanks also to Gli Blr for spotting this bug
        if (apexValue == null)
            return null;
        if (apexValue <= 1)
        {
            float apexPower = (float) (1 / (Math.exp(apexValue * Math.log(2))));
            long apexPower10 = Math.round((double) apexPower * 10.0);
            float fApexPower = (float) apexPower10 / 10.0f;
            DecimalFormat format = new DecimalFormat("0.##");
            format.setRoundingMode(RoundingMode.HALF_UP);
            return format.format(fApexPower) + " sec";
        }
        else
        {
            int apexPower = (int) ((Math.exp(apexValue * Math.log(2))));
            return "1/" + apexPower + " sec";
        }
    }
}
