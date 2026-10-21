/*
 * Copyright 2026 Drew Noakes and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Metadata for Matroska and WebM, which use EBML encoding. Support includes
 * EBML header and segment fields, track properties, video colour information,
 * and structured tags.
 *
 * <p>Tag structure is represented by a
 * {@link com.drew.metadata.mkv.MkvTagsDirectory} for each Tags update,
 * {@link com.drew.metadata.mkv.MkvTagDirectory} target groups, and one
 * {@link com.drew.metadata.mkv.MkvSimpleTagDirectory} per SimpleTag.
 * Directory parent links preserve targets and nesting; repeated tag names,
 * language variants, and binary values remain separate directories.
 * Targets may occur before or after values.
 *
 * <p>Media packets are not decoded. Chapter and attachment contents are not
 * extracted, but tag target identifiers referring to them are retained.
 */
package com.drew.metadata.mkv;
