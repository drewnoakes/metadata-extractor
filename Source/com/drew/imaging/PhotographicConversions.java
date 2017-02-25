/*
 * Copyright 2002-2017 Drew Noakes
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
package com.drew.imaging;

/**
 * Contains helper methods that perform photographic conversions.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public final class PhotographicConversions
{
    public final static double ROOT_TWO = Math.sqrt(2);

    private PhotographicConversions() throws Exception
    {
        throw new Exception("Not intended for instantiation.");
    }

    /**
     * Converts an aperture value to its corresponding F-stop number.
     *
     * @param aperture the aperture value to convert
     * @return the F-stop number of the specified aperture
     */
    public static double apertureToFStop(double aperture)
    {
        return Math.pow(ROOT_TWO, aperture);

        // NOTE jhead uses a different calculation as far as i can tell...  this confuses me...
        // fStop = (float)Math.exp(aperture * Math.log(2) * 0.5));
    }

    /**
     * Converts a shutter speed to an exposure time.
     *
     * @param shutterSpeed the shutter speed to convert
     * @return the exposure time of the specified shutter speed
     */
    public static double shutterSpeedToExposureTime(double shutterSpeed)
    {
        return (float) (1 / Math.exp(shutterSpeed * Math.log(2)));
    }
}
