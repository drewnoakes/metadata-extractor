/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package com.drew.metadata.mkv;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.drew.metadata.mkv.EbmlTestUtil.*;
import static org.junit.Assert.*;

public class MkvTagReaderTest
{
    private static byte[] simple(String name, String value, byte[]... children) throws Exception
    {
        return element(ElementIDs.SIMPLE_TAG, string(ElementIDs.TAG_NAME, name),
            string(ElementIDs.TAG_STRING, value), concat(children));
    }

    @Test
    public void preservesRepeatedNamesAndValuesInFileOrder() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG, element(ElementIDs.TARGETS),
                simple("ARTIST", "First"), simple("ARTIST", "Second"), simple("ARTIST", "First")))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals(3, tags.size());
        assertEquals("First", tags.get(0).getString(MkvSimpleTagDirectory.TAG_STRING));
        assertEquals("Second", tags.get(1).getString(MkvSimpleTagDirectory.TAG_STRING));
        assertEquals("First", tags.get(2).getString(MkvSimpleTagDirectory.TAG_STRING));
        assertSame(tags.get(0).getParent(), tags.get(1).getParent());
        assertFalse(metadata.hasErrors());
    }

    @Test
    public void preservesNestedTagsAndTargetsThatFollowTheirValues() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG,
                simple("ARTIST", "Band",
                    simple("LEAD_PERFORMER", "Singer", simple("DATE_STARTED", "1981-08"))),
                element(ElementIDs.TARGETS,
                    unsigned(ElementIDs.TARGET_TYPE_VALUE, 30),
                    string(ElementIDs.TARGET_TYPE, "TRACK"),
                    unsigned(ElementIDs.TAG_TRACK_UID, 1),
                    unsigned(ElementIDs.TAG_TRACK_UID, -1L),
                    unsigned(ElementIDs.TAG_TRACK_UID, 1),
                    unsigned(ElementIDs.TAG_EDITION_UID, 9),
                    unsigned(ElementIDs.TAG_CHAPTER_UID, 7),
                    unsigned(ElementIDs.TAG_ATTACHMENT_UID, 8))))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        MkvTagDirectory targets = metadata.getFirstDirectoryOfType(MkvTagDirectory.class);
        assertNotNull(targets);
        assertEquals(3, tags.size());
        assertSame(targets, tags.get(0).getParent());
        assertSame(tags.get(0), tags.get(1).getParent());
        assertSame(tags.get(1), tags.get(2).getParent());
        assertSame(targets, tags.get(2).getTargets());
        assertArrayEquals(new Number[] {1L, new BigInteger("18446744073709551615"), 1L},
            (Number[])targets.getObject(MkvTagDirectory.TAG_TRACK_UIDS));
        assertArrayEquals(new Number[] {9L}, (Number[])targets.getObject(MkvTagDirectory.TAG_EDITION_UIDS));
        assertArrayEquals(new Number[] {7L}, (Number[])targets.getObject(MkvTagDirectory.TAG_CHAPTER_UIDS));
        assertArrayEquals(new Number[] {8L}, (Number[])targets.getObject(MkvTagDirectory.TAG_ATTACHMENT_UIDS));
        assertEquals("TRACK", targets.getString(MkvTagDirectory.TAG_TARGET_TYPE));
        assertEquals(30, targets.getInt(MkvTagDirectory.TAG_TARGET_TYPE_VALUE));
        assertTrue(targets.getParent() instanceof MkvTagsDirectory);
        assertTrue(targets.getParent().getParent() instanceof MkvSegmentDirectory);
    }

    @Test
    public void preservesLanguageVariantsAndBcp47Precedence() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG,
                simple("TITLE", "English", string(ElementIDs.TAG_LANGUAGE, "eng")),
                simple("TITLE", "Deutsch", string(ElementIDs.TAG_LANGUAGE, "deu"),
                    string(ElementIDs.TAG_LANGUAGE_BCP47, "de-DE"), unsigned(ElementIDs.TAG_DEFAULT, 0)),
                simple("TITLE", "Undetermined")))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals("eng", tags.get(0).getLanguage());
        assertEquals("de-DE", tags.get(1).getLanguage());
        assertEquals("deu", tags.get(1).getString(MkvSimpleTagDirectory.TAG_LANGUAGE));
        assertEquals("No", tags.get(1).getDescription(MkvSimpleTagDirectory.TAG_DEFAULT));
        assertEquals("und", tags.get(2).getLanguage());
        assertEquals("Yes", tags.get(2).getDescription(MkvSimpleTagDirectory.TAG_DEFAULT));
    }

    @Test
    public void retainsUtf8AndBinaryValuesWithoutConflatingTheirTypes() throws Exception
    {
        byte[] binary = new byte[] {0, 1, (byte)0xFF, 0};
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG,
                simple("TITLE", "Caf\u00e9\0\0"),
                element(ElementIDs.SIMPLE_TAG, string(ElementIDs.TAG_NAME, "BINARY"),
                    element(ElementIDs.TAG_BINARY, binary))))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals("Caf\u00e9", tags.get(0).getString(MkvSimpleTagDirectory.TAG_STRING));
        assertArrayEquals(binary, tags.get(1).getByteArray(MkvSimpleTagDirectory.TAG_BINARY));
        assertFalse(tags.get(1).containsTag(MkvSimpleTagDirectory.TAG_STRING));
    }

    @Test
    public void distinguishesSegmentTargetsFromExplicitAllTracks() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG, element(ElementIDs.TARGETS), simple("TITLE", "Segment")),
            element(ElementIDs.TAG, element(ElementIDs.TARGETS,
                element(ElementIDs.TAG_TRACK_UID)), simple("TITLE", "All Tracks")))));
        List<MkvTagDirectory> targets = new ArrayList<MkvTagDirectory>(
            metadata.getDirectoriesOfType(MkvTagDirectory.class));
        assertEquals(2, targets.size());
        assertEquals(50, targets.get(0).getInt(MkvTagDirectory.TAG_TARGET_TYPE_VALUE));
        assertNull(targets.get(0).getObject(MkvTagDirectory.TAG_TRACK_UIDS));
        assertArrayEquals(new Number[] {0L}, (Number[])targets.get(1).getObject(MkvTagDirectory.TAG_TRACK_UIDS));
        assertEquals("All Tracks", targets.get(1).getDescription(MkvTagDirectory.TAG_TRACK_UIDS));
    }

    @Test
    public void retainsLiveTagUpdatesAsSeparateCollections() throws Exception
    {
        Metadata metadata = extract(concat(header("webm"), unknownElement(ElementIDs.SEGMENT, 8,
            element(ElementIDs.TAGS, element(ElementIDs.TAG, simple("TITLE", "First"))),
            unknownElement(ElementIDs.CLUSTER, 1, unsigned(0xE7, 0)),
            element(ElementIDs.TAGS, element(ElementIDs.TAG, simple("TITLE", "Second"))))));
        List<MkvTagsDirectory> updates = new ArrayList<MkvTagsDirectory>(
            metadata.getDirectoriesOfType(MkvTagsDirectory.class));
        List<MkvSimpleTagDirectory> values = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals(2, updates.size());
        assertEquals(1, updates.get(0).getInt(MkvTagsDirectory.TAG_UPDATE_NUMBER));
        assertEquals(2, updates.get(1).getInt(MkvTagsDirectory.TAG_UPDATE_NUMBER));
        assertSame(updates.get(0), values.get(0).getTargets().getParent());
        assertSame(updates.get(1), values.get(1).getTargets().getParent());
        assertSame(updates.get(0).getParent(), updates.get(1).getParent());
        assertFalse(metadata.hasErrors());
    }

    @Test
    public void reportsMissingNamesAndConflictingValueTypes() throws Exception
    {
        Metadata metadata = extract(document(element(ElementIDs.TAGS,
            element(ElementIDs.TAG,
                element(ElementIDs.SIMPLE_TAG, string(ElementIDs.TAG_STRING, "Missing name")),
                element(ElementIDs.SIMPLE_TAG, string(ElementIDs.TAG_NAME, "CONFLICT"),
                    string(ElementIDs.TAG_STRING, "String"), element(ElementIDs.TAG_BINARY, new byte[] {1}))))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals(2, tags.size());
        assertTrue(tags.get(0).hasErrors());
        assertTrue(tags.get(1).hasErrors());
        assertEquals("String", tags.get(1).getString(MkvSimpleTagDirectory.TAG_STRING));
    }

    @Test
    public void readsDeepNestingWithoutRecursiveParserCalls() throws Exception
    {
        byte[] nested = simple("LEAF", "Value");
        for (int i = 1; i < 512; i++)
            nested = element(ElementIDs.SIMPLE_TAG, string(ElementIDs.TAG_NAME, "PARENT"), nested);
        Metadata metadata = extract(document(element(ElementIDs.TAGS, element(ElementIDs.TAG, nested))));
        List<MkvSimpleTagDirectory> tags = new ArrayList<MkvSimpleTagDirectory>(
            metadata.getDirectoriesOfType(MkvSimpleTagDirectory.class));
        assertEquals(512, tags.size());
        Directory parent = tags.get(511);
        int depth = 0;
        while (parent instanceof MkvSimpleTagDirectory) {
            depth++;
            parent = parent.getParent();
        }
        assertEquals(512, depth);
        assertSame(parent, tags.get(511).getTargets());
        assertFalse(metadata.hasErrors());
    }
}
