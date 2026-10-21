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
 * Matroska and WebM metadata. Tag structure is represented by a
 * {@link com.drew.metadata.mkv.MkvTagsDirectory} for each Tags update,
 * {@link com.drew.metadata.mkv.MkvTagDirectory} target groups, and one
 * {@link com.drew.metadata.mkv.MkvSimpleTagDirectory} per SimpleTag.
 * Directory parent links preserve nesting; repeated tag names and values
 * remain separate directories. Targets may occur before or after values.
 */
package com.drew.metadata.mkv;
