# FLIR FFF records (.NET)

The .NET FLIR reader combines FLIR JPEG APP1 segments into an FFF payload.
Its header identifies an index table whose records include raw thermal image
data and camera information.

Java does not currently have a corresponding FLIR FFF or Exif FLIR maker-note reader.
The .NET Exif FLIR maker-note reader is a separate path from FFF records.

## References

- [ExifTool FLIR tag reference](https://exiftool.org/TagNames/FLIR.html)
