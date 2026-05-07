// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////

package com.drew.metadata.fuzzer;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.drew.imaging.eps.EpsMetadataReader;
import com.drew.imaging.ImageProcessingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpsFuzzer {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        try {
            byte[] input = generateEps(data);
            com.drew.metadata.Metadata metadata = EpsMetadataReader.readMetadata(
                new ByteArrayInputStream(input));
            FuzzInputBuilder.touchMetadata(metadata);
        } catch (IOException e) {
        } catch (Exception e) {
        }
    }

    private static byte[] generateEps(FuzzedDataProvider data) throws IOException {
        int type = data.consumeInt(0, 2);
        
        if (type == 0) {
            return generateSimpleEps(data);
        } else {
            return generateStructuredEps(data);
        }
    }

    private static byte[] generateSimpleEps(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write("%!PS-Adobe-3.0 EPSF-3.0\n".getBytes(StandardCharsets.ISO_8859_1));
        
        baos.write("%%BoundingBox: 0 0 100 100\n".getBytes(StandardCharsets.ISO_8859_1));
        
        if (data.consumeBoolean()) {
            baos.write("%%Creator: Fuzzer\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        
        int numEntries = data.consumeInt(0, 20);
        for (int i = 0; i < numEntries; i++) {
            String key = "%%" + data.consumeString(10);
            String value = data.consumeString(20);
            baos.write((key + ": " + value + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        
        byte[] prolog = data.consumeBytes(100);
        baos.write(prolog);
        
        byte[] trailer = data.consumeBytes(50);
        baos.write(trailer);
        
        baos.write("%%EOF\n".getBytes(StandardCharsets.ISO_8859_1));
        
        return baos.toByteArray();
    }

    private static byte[] generateStructuredEps(FuzzedDataProvider data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        baos.write("%!PS-Adobe-3.0 EPSF-3.0\n".getBytes(StandardCharsets.ISO_8859_1));
        
        baos.write(("%%BoundingBox: " + data.consumeInt(0, 100) + " " + data.consumeInt(0, 100) + " " + 
            data.consumeInt(100, 500) + " " + data.consumeInt(100, 500) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        
        if (data.consumeBoolean()) {
            baos.write(("%%Creator: " + data.consumeString(20) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        if (data.consumeBoolean()) {
            baos.write(("%%Title: " + data.consumeString(30) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        if (data.consumeBoolean()) {
            baos.write(("%%CreationDate: " + data.consumeString(20) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        
        int numEntries = data.consumeInt(0, 5);
        for (int i = 0; i < numEntries; i++) {
            String key = "%%" + data.consumeString(8);
            String value = data.consumeString(15);
            baos.write((key + ": " + value + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        
        if (data.consumeBoolean()) {
            baos.write("%%BeginProlog\n".getBytes(StandardCharsets.ISO_8859_1));
            baos.write(data.consumeBytes(30));
            baos.write("%%EndProlog\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        
        if (data.consumeBoolean()) {
            baos.write("%%BeginSetup\n".getBytes(StandardCharsets.ISO_8859_1));
            baos.write(data.consumeBytes(30));
            baos.write("%%EndSetup\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        
        baos.write("%%BeginPageSetup\n".getBytes(StandardCharsets.ISO_8859_1));
        if (data.consumeBoolean()) {
            baos.write(("%%PageBoundingBox: " + data.consumeInt(0, 50) + " " + data.consumeInt(0, 50) + " " + 
                data.consumeInt(200, 500) + " " + data.consumeInt(200, 500) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        baos.write("%%EndPageSetup\n".getBytes(StandardCharsets.ISO_8859_1));
        
        baos.write(data.consumeBytes(50));
        
        if (data.consumeBoolean()) {
            baos.write("%%PageTrailer\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        
        baos.write("%%Trailer\n".getBytes(StandardCharsets.ISO_8859_1));
        baos.write("%%EOF\n".getBytes(StandardCharsets.ISO_8859_1));
        
        return baos.toByteArray();
    }
}
