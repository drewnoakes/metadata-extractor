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

package com.drew.metadata.iptc;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("ConstantConditions")
public class IptcDirectoryTest
{
    private IptcDirectory _directory;

    @Before
    public void setUp()
    {
        _directory = new IptcDirectory();
    }

    @Test
    public void testGetDateSent()
    {
        _directory.setString(IptcDirectory.TAG_DATE_SENT, "20101212");
        _directory.setString(IptcDirectory.TAG_TIME_SENT, "124135+0100");
        final Date actual = _directory.getDateSent();

        Calendar calendar = new GregorianCalendar(2010, 12 - 1, 12, 12, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        assertEquals(calendar.getTime(), actual);
        assertEquals(1292154095000L, actual.getTime());
    }

    @Test
    public void testGetReleaseDate()
    {
        _directory.setString(IptcDirectory.TAG_RELEASE_DATE, "20101212");
        _directory.setString(IptcDirectory.TAG_RELEASE_TIME, "124135+0100");
        final Date actual = _directory.getReleaseDate();

        Calendar calendar = new GregorianCalendar(2010, 12 - 1, 12, 12, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        assertEquals(calendar.getTime(), actual);
        assertEquals(1292154095000L, actual.getTime());
    }

    @Test
    public void testGetExpirationDate()
    {
        _directory.setString(IptcDirectory.TAG_EXPIRATION_DATE, "20101212");
        _directory.setString(IptcDirectory.TAG_EXPIRATION_TIME, "124135+0100");
        final Date actual = _directory.getExpirationDate();

        Calendar calendar = new GregorianCalendar(2010, 12 - 1, 12, 12, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        assertEquals(calendar.getTime(), actual);
        assertEquals(1292154095000L, actual.getTime());
    }

    @Test
    public void testGetDateCreated()
    {
        _directory.setString(IptcDirectory.TAG_DATE_CREATED, "20101212");
        _directory.setString(IptcDirectory.TAG_TIME_CREATED, "124135+0100");
        final Date actual = _directory.getDateCreated();

        Calendar calendar = new GregorianCalendar(2010, 12 - 1, 12, 12, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        assertEquals(calendar.getTime(), actual);
        assertEquals(1292154095000L, actual.getTime());
    }

    @Test
    public void testGetDigitalDateCreated()
    {
        _directory.setString(IptcDirectory.TAG_DIGITAL_DATE_CREATED, "20101212");
        _directory.setString(IptcDirectory.TAG_DIGITAL_TIME_CREATED, "124135+0100");
        final Date actual = _directory.getDigitalDateCreated();

        Calendar calendar = new GregorianCalendar(2010, 12 - 1, 12, 12, 41, 35);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        assertEquals(calendar.getTime(), actual);
        assertEquals(1292154095000L, actual.getTime());
    }
}
