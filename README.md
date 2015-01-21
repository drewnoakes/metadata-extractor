![metadata-extractor logo](https://raw.githubusercontent.com/drewnoakes/metadata-extractor/master/Resources/metadata-extractor-logo-500x123.png)

[![metadata-extractor build status](https://api.travis-ci.org/drewnoakes/metadata-extractor.svg)](https://travis-ci.org/drewnoakes/metadata-extractor)

_metadata-extractor_ is a straightforward Java library for reading metadata from image files.

    Metadata metadata = ImageMetadataReader.readMetadata(imagePath);

With that `metadata` object, you can [iterate or query](https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted#2._Query_Tag_s) the
[various tag values](https://github.com/drewnoakes/metadata-extractor/wiki/SampleOutput) that were read from the image.

The library understands several formats of metadata, many of which may be present in a single image:

* [Exif](http://en.wikipedia.org/wiki/Exchangeable_image_file_format)
* [IPTC](http://en.wikipedia.org/wiki/IPTC)
* [XMP](http://en.wikipedia.org/wiki/Extensible_Metadata_Platform)
* [JFIF / JFXX](http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format)
* [ICC Profiles](http://en.wikipedia.org/wiki/ICC_profile)
* [Photoshop](http://en.wikipedia.org/wiki/Photoshop) fields
* [PNG](http://en.wikipedia.org/wiki/Portable_Network_Graphics) properties
* [BMP](http://en.wikipedia.org/wiki/BMP_file_format) properties
* [GIF](http://en.wikipedia.org/wiki/Graphics_Interchange_Format) properties

It will process files of type:

* JPEG
* TIFF
* PSD
* PNG
* BMP
* GIF
* Camera Raw (NEF/CR2/ORF/ARW/RW2/...)

Special camera-specific data is decoded for most cameras manufactured by:

* Agfa
* Canon
* Casio
* Epson
* Fujifilm
* Kodak
* Kyocera
* Leica
* Minolta
* Nikon
* Olympus
* Panasonic
* Pentax
* Sanyo
* Sigma/Foveon
* Sony

Read [getting started](https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted) for an introduction to the basics of using this library.

# Mailing Lists

Three mailing lists exist:

* [metadata-extractor-announce](http://groups.google.com/group/metadata-extractor-announce) for read-only announcements of new releases
* [metadata-extractor-dev](http://groups.google.com/group/metadata-extractor-dev) for discussion about development and notifications of changes to issues and source code
* [metadata-extractor-changes](http://groups.google.com/group/metadata-extractor-changes) for automated emails when code, issues or the wiki are changed

# Credits

This library is developed by [Drew Noakes](https://drewnoakes.com/code/exif/).

Thanks are due to the many [users](https://github.com/drewnoakes/metadata-extractor/wiki/UsedBy) who sent in suggestions, bug reports,
[sample images](https://github.com/drewnoakes/metadata-extractor/wiki/ImageDatabase) from their cameras as well as encouragement.
Wherever possible, they have been credited in the source code and commit logs.

# Feedback

Have questions or ideas? Try the [mailing list](http://groups.google.com/group/metadata-extractor-dev).

Found a bug or have a patch? Search the issue list and if it isn't already there, create it.

# Contribute

The easiest way to help is to contribute to the [sample image file library](https://github.com/drewnoakes/metadata-extractor/wiki/ImageDatabase)
used for research and testing.

If you want to get your hands dirty, clone this repository, enhance the library and let us know
to pull from your clone. Ask around on the mailing list to avoid duplication of work.

# License

Copyright 2002-2015 Drew Noakes

> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

More information about this project is available at:

* https://drewnoakes.com/code/exif/
* https://github.com/drewnoakes/metadata-extractor/
