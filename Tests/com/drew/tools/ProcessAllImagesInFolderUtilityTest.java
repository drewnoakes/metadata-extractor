/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.tools;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class ProcessAllImagesInFolderUtilityTest
{
    @Test
    public void includesMatroskaAndWebmInRegressionProcessing()
    {
        ProcessAllImagesInFolderUtility.FileHandlerBase handler =
            new ProcessAllImagesInFolderUtility.FileHandlerBase() {};
        for (String extension : new String[] {"mkv", "mka", "mks", "mk3d", "webm", "WEBM"})
            assertTrue(extension, handler.shouldProcess(new File("sample." + extension)));
        assertFalse(handler.shouldProcess(new File("sample.webm.LICENSE")));
    }
}
