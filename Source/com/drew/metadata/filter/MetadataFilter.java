/*
 * Copyright 2002-2016 Drew Noakes
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
package com.drew.metadata.filter;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;


public interface MetadataFilter {

    /**
     * This method is invoked before every tag is written. Use it to filter
     * which tags to keep and which to filter out.
     *
     * @param directory the {@link Directory} being written to.
     * @param tagType the tag being written.
     * @return {@code True} to include the tag or {@code false} to exclude it.
     */
    public boolean tagFilter(@NotNull Directory directory, int tagType);

    /**
     * This method is invoked before every {@link Directory} is added to the
     * {@link Metadata} instance. Use it to filter which directories to keep
     * and which to filter out.
     *
     * @param directory the {@link Class} of the directory to be added.
     * @return {@code True} to include the {@link Directory} or {@code false}
     *         to exclude it.
     */
    public boolean directoryFilter(@NotNull Class<? extends Directory> directory);

}
