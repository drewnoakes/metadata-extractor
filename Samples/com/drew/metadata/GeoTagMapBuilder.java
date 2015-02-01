/*
 * Copyright 2002-2015 Drew Noakes
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

package com.drew.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A sample application that creates HTML that shows image positions on a Google Map according to the GPS data
 * contained within each {@link GpsDirectory}, if available.
 */
public class GeoTagMapBuilder
{
    public static void main(String args[]) throws ImageProcessingException, IOException
    {
        if (args.length != 1)
        {
            System.err.println("Expected a single argument of the path to the photographs to be processed.");
            System.exit(1);
        }

        File path = new File(args[0]);
        final String[] acceptedExtensions = new String[] { ".jpg", ".jpeg" };

        // TODO process subdirectories recursively?

        final File[] files = path.listFiles(new FileFilter()
        {
            public boolean accept(final File file)
            {
                if (file.isDirectory())
                    return false;
                for (String extension : acceptedExtensions) {
                    if (file.getName().toLowerCase().endsWith(extension))
                        return true;
                }
                return false;
            }
        });

        Collection<PhotoLocation> photoLocations = new ArrayList<PhotoLocation>();
        for (File file : files)
        {
            // Read all metadata from the image
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            // See whether it has GPS data
            Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
            if (gpsDirectories == null)
                continue;
            for (GpsDirectory gpsDirectory : gpsDirectories) {
                // Try to read out the location, making sure it's non-zero
                GeoLocation geoLocation = gpsDirectory.getGeoLocation();
                if (geoLocation != null && !geoLocation.isZero()) {
                    // Add to our collection for use below
                    photoLocations.add(new PhotoLocation(geoLocation, file));
                    break;
                }
            }
        }

        // Write output to the console.
        // You can pipe this to a file if you like, or alternatively modify the output stream here
        // to be a file or network stream.
        PrintStream ps = new PrintStream(System.out);

        writeHtml(ps, photoLocations);

        // Make sure we flush the stream before exiting.  If you use a different type of stream, you
        // may need to close it here instead.
        ps.flush();
    }

    /**
     * Simple tuple type, which pairs an image file with its {@link GeoLocation}.
     */
    public static class PhotoLocation
    {
        public final GeoLocation location;
        public final File file;

        public PhotoLocation(final GeoLocation location, final File file)
        {
            this.location = location;
            this.file = file;
        }
    }

    private static void writeHtml(PrintStream ps, Iterable<PhotoLocation> photoLocations)
    {
        ps.println("<!DOCTYPE html>");
        ps.println("<html>");
        ps.println("<head>");
        ps.println("<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />");
        ps.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        ps.println("<style>html,body{height:100%;margin:0;padding:0;}#map_canvas{height:100%;}</style>");
        ps.println("<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?sensor=false\"></script>");
        ps.println("<script type=\"text/javascript\">");
        ps.println("function initialise() {");
        ps.println("    var options = { zoom:2, mapTypeId:google.maps.MapTypeId.ROADMAP, center:new google.maps.LatLng(0.0, 0.0)};");
        ps.println("    var map = new google.maps.Map(document.getElementById('map_canvas'), options);");
        ps.println("    var marker;");

        for (PhotoLocation photoLocation : photoLocations)
        {
            final String fullPath = photoLocation.file.getAbsoluteFile().toString().trim().replace("\\", "\\\\");

            ps.println("    marker = new google.maps.Marker({");
            ps.println("        position:new google.maps.LatLng(" + photoLocation.location + "),");
            ps.println("        map:map,");
            ps.println("        title:\"" + fullPath + "\"});");
            ps.println("    google.maps.event.addListener(marker, 'click', function() { document.location = \"" + fullPath + "\"; });");
        }

        ps.println("}");
        ps.println("</script>");
        ps.println("</head>");
        ps.println("<body onload=\"initialise()\">");
        ps.println("<div id=\"map_canvas\"></div>");
        ps.println("</body>");
        ps.println("</html>");
    }
}
