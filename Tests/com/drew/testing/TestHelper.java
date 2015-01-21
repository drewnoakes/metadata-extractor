/*
 * Copyright 2002-2015 Drew Noakes
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
package com.drew.testing;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class TestHelper
{
    public static byte[] skipBytes(byte[] input, int countToSkip)
    {
        if (input.length - countToSkip < 0) {
            throw new IllegalArgumentException("Attempting to skip more bytes than exist in the array.");
        }

        byte[] output = new byte[input.length - countToSkip];
        System.arraycopy(input, countToSkip, output, 0, input.length - countToSkip);
        return output;
    }
}
