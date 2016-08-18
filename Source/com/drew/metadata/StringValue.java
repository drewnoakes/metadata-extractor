/*
 * Copyright 2016 eperson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.drew.metadata;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eperson
 */
public class StringValue {
    public static final Charset DefaultEncoding = java.nio.charset.StandardCharsets.US_ASCII;
    public byte[] bytes;
    public Charset charset = DefaultEncoding;

    public StringValue(byte[] _bytes) {
        bytes = _bytes;
    }
        public StringValue(byte[] _bytes, Charset encoding) {
        bytes = _bytes;
        charset = encoding;
    }

    public String toString() {
        try {
            return new String(bytes, charset.name());
        } catch (UnsupportedEncodingException ex) {
            return new String(bytes);
        }
    }
    public String toString(Charset encoding) {
        try {
            return new String(bytes, encoding.name());
        } catch (UnsupportedEncodingException ex) {
            return new String(bytes);
        }
    }
    
    public int length() {
        return bytes.length;
    }
    
}
