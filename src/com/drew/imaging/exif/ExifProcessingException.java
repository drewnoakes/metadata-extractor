/*
 * ExifProcessingException.java
 *
 * Created on 29 April 2002, 00:33
 */

package com.drew.imaging.exif;

/**
 *
 * @author  Drewski
 */
public class ExifProcessingException extends java.lang.Exception {

    /**
     * Creates a new instance of <code>ExifProcessingException</code> without detail message.
     */
    public ExifProcessingException() {
    }


    /**
     * Constructs an instance of <code>ExifProcessingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExifProcessingException(String msg) {
        super(msg);
    }
}


