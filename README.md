![metadata-extractor logo](https://cdn.rawgit.com/drewnoakes/metadata-extractor/master/Resources/metadata-extractor-logo.svg)

[![metadata-extractor build status](https://api.travis-ci.org/drewnoakes/metadata-extractor.svg)](https://travis-ci.org/drewnoakes/metadata-extractor)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.drewnoakes/metadata-extractor/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.drewnoakes/metadata-extractor)
[![Issue Stats](http://issuestats.com/github/drewnoakes/metadata-extractor/badge/pr?style=flat)](http://issuestats.com/github/drewnoakes/metadata-extractor)
[![Donate](https://img.shields.io/badge/paypal-donate-yellow.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=TNXDJKCDV5Z2C&lc=GB&item_name=Drew%20Noakes&item_number=metadata%2dextractor&no_note=0&cn=Add%20a%20message%20%28optional%29%3a&no_shipping=1&currency_code=GBP&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted)

_metadata-extractor_ is a straightforward Java library for reading metadata from image files.

## Installation

The easiest way is to install the library via its [Maven package](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.drewnoakes%22%20AND%20a%3A%22metadata-extractor%22).

    <dependency>
      <groupId>com.drewnoakes</groupId>
      <artifactId>metadata-extractor</artifactId>
      <version>2.10.1</version>
    </dependency>

Alternatively, download it from the [releases page](https://github.com/drewnoakes/metadata-extractor/releases).

## Usage

```java
Metadata metadata = ImageMetadataReader.readMetadata(imagePath);
```

With that `Metadata` instance, you can [iterate or query](https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted#2-query-tags) the
[various tag values](https://github.com/drewnoakes/metadata-extractor/wiki/SampleOutput) that were read from the image.

## Features

The library understands several formats of metadata, many of which may be present in a single image:

* [Exif](http://en.wikipedia.org/wiki/Exchangeable_image_file_format)
* [IPTC](http://en.wikipedia.org/wiki/IPTC)
* [XMP](http://en.wikipedia.org/wiki/Extensible_Metadata_Platform)
* [JFIF / JFXX](http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format)
* [ICC Profiles](http://en.wikipedia.org/wiki/ICC_profile)
* [Photoshop](http://en.wikipedia.org/wiki/Photoshop) fields
* [WebP](http://en.wikipedia.org/wiki/WebP) properties
* [PNG](http://en.wikipedia.org/wiki/Portable_Network_Graphics) properties
* [BMP](http://en.wikipedia.org/wiki/BMP_file_format) properties
* [GIF](http://en.wikipedia.org/wiki/Graphics_Interchange_Format) properties
* [ICO](https://en.wikipedia.org/wiki/ICO_(file_format)) properties
* [PCX](http://en.wikipedia.org/wiki/PCX) properties

It will process files of type:

* JPEG
* TIFF
* WebP
* PSD
* PNG
* BMP
* GIF
* ICO
* PCX
* Camera Raw
  * NEF (Nikon)
  * CR2 (Canon)
  * ORF (Olympus)
  * ARW (Sony)
  * RW2 (Panasonic)
  * RWL (Leica)
  * SRW (Samsung)

Camera-specific "makernote" data is decoded for cameras manufactured by:

* Agfa
* Apple
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
* Reconyx
* Sanyo
* Sigma/Foveon
* Sony

Read [getting started](https://github.com/drewnoakes/metadata-extractor/wiki/GettingStarted) for an introduction to the basics of using this library.

## Questions & Feedback

The quickest way to have your questions answered is via [Stack Overflow](http://stackoverflow.com/questions/tagged/metadata-extractor).
Check whether your question has already been asked, and if not, ask a new one tagged with both `metadata-extractor` and `java`.

Bugs and feature requests should be provided via the project's [issue tracker](https://github.com/drewnoakes/metadata-extractor/issues).
Please attach sample images where possible as most issues cannot be investigated without an image.

## Contributing

If you want to get your hands dirty, making a pull request is a great way to enhance the library.
In general it's best to create an issue first that captures the problem you want to address.
You can discuss your proposed solution in that issue.
This gives others a chance to provide feedback before you spend your valuable time working on it.

An easier way to help is to contribute to the [sample image file library](https://github.com/drewnoakes/metadata-extractor-images/wiki) used for research and testing.

## Credits

This library is developed by [Drew Noakes](https://drewnoakes.com/code/exif/).

Thanks are due to the many [users](https://github.com/drewnoakes/metadata-extractor/wiki/UsedBy) who sent in suggestions, bug reports,
[sample images](https://github.com/drewnoakes/metadata-extractor-images/wiki) from their cameras as well as encouragement.
Wherever possible, they have been credited in the source code and commit logs.

## Other languages

- .NET  [metadata-extractor-dotnet](https://github.com/drewnoakes/metadata-extractor-dotnet) is a complete port to C#, maintained alongside this library
- PHP [php-metadata-extractor](https://github.com/gomoob/php-metadata-extractor) wraps this Java project, making it available to users of PHP
- Clojure [exif-processor](https://github.com/joshuamiller/exif-processor) wraps this Java project, returning a subset of data

---

More information about this project is available at:

* https://drewnoakes.com/code/exif/
* https://github.com/drewnoakes/metadata-extractor/
