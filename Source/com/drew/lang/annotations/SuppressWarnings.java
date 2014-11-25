/*
 * Copyright 2002-2011 Andreas Ziermann
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

package com.drew.lang.annotations;

/**
 * Used to suppress specific code analysis warnings produced by the Findbugs tool.
 *
 * @author Andreas Ziermann
 */
public @interface SuppressWarnings
{
    /**
     * The name of the warning to be suppressed.
     * @return The name of the warning to be suppressed.
     */
    @NotNull String value();

    /**
     * An explanation of why it is valid to suppress the warning in a particular situation/context.
     * @return An explanation of why it is valid to suppress the warning in a particular situation/context.
     */
    @NotNull String justification();
}
