/**
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
 *
 * Created by dnoakes on Dec 15, 2002 3:30:02 PM using IntelliJ IDEA.
 */
package com.drew.lang.test;

import com.drew.lang.NullOutputStream;
import junit.framework.TestCase;

import java.io.OutputStream;

public class NullOutputStreamTest extends TestCase
{
    public NullOutputStreamTest(String s)
    {
        super(s);
    }

    public void testCreateNullOutputStream() throws Exception
    {
        OutputStream out = new NullOutputStream();
        out.write(1);
    }
}
