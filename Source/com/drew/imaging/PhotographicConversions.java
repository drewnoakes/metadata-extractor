/*
 * This is public domain software - that is, you can do whatever you want
 * with it, and include it software that is licensed under the GNU or the
 * BSD license, or whatever other licence you choose, including proprietary
 * closed source licenses.  I do ask that you leave this header in tact.
 *
 * If you make modifications to this code that you think would benefit the
 * wider community, please send me a copy and I'll post it on my site.
 *
 * If you make use of this code, I'd appreciate hearing about it.
 *   metadata_extractor [at] drewnoakes [dot] com
 * Latest version of this software kept at
 *   http://drewnoakes.com/
 */
package com.drew.imaging;

/**
 * Contains helper methods that perform photographic conversions.
 */
public class PhotographicConversions
{
    public final static double ROOT_TWO = Math.sqrt(2);

    private PhotographicConversions()
    {}

    /**
     * Converts an aperture value to its corresponding F-stop number.
     * @param aperture the aperture value to convert
     * @return the F-stop number of the specified aperture
     */
    public static double apertureToFStop(double aperture)
    {
        double fStop = Math.pow(ROOT_TWO, aperture);
        return fStop;

        // Puzzle?!
        // jhead uses a different calculation as far as i can tell...  this confuses me...
        // fStop = (float)Math.exp(aperture * Math.log(2) * 0.5));
    }

    /**
     * Converts a shutter speed to an exposure time.
     * @param shutterSpeed the shutter speed to convert
     * @return the exposure time of the specified shutter speed
     */
    public static double shutterSpeedToExposureTime(double shutterSpeed)
    {
        return (float)(1 / Math.exp(shutterSpeed * Math.log(2)));
    }
}
