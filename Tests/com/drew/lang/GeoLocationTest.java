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

package com.drew.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class GeoLocationTest
{
    @Test
    public void testDecimalToDegreesMinutesSeconds() throws Exception
    {
        double[] dms = GeoLocation.decimalToDegreesMinutesSeconds(1);
        assertEquals(1.0, dms[0], 0.0001);
        assertEquals(0.0, dms[1], 0.0001);
        assertEquals(0.0, dms[2], 0.0001);

        dms = GeoLocation.decimalToDegreesMinutesSeconds(-12.3216);
        assertEquals(-12.0, dms[0], 0.0001);
        assertEquals(19.0, dms[1], 0.0001);
        assertEquals(17.76, dms[2], 0.0001);

        dms = GeoLocation.decimalToDegreesMinutesSeconds(32.698);
        assertEquals(32.0, dms[0], 0.0001);
        assertEquals(41.0, dms[1], 0.0001);
        assertEquals(52.8, dms[2], 0.0001);
    }
}
