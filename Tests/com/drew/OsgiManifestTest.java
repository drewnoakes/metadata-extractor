/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
package com.drew;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

/**
 * Verifies that the OSGi {@code Export-Package} manifest entry in {@code pom.xml} stays in sync
 * with the actual source packages. If a new package is added to (or removed from) the source tree,
 * this test will fail until {@code pom.xml} and {@code build.gradle} are updated accordingly.
 */
public class OsgiManifestTest
{
    /** Internal package excluded from OSGi exports. */
    private static final String TOOLS_PACKAGE = "com.drew.tools";

    @Test
    public void exportPackageDeclarationCoversAllPublicPackages() throws Exception
    {
        File sourceDir = new File("Source");
        assertTrue("Source directory not found at: " + sourceDir.getAbsolutePath(), sourceDir.isDirectory());

        File pomFile = new File("pom.xml");
        assertTrue("pom.xml not found at: " + pomFile.getAbsolutePath(), pomFile.isFile());

        Set<String> sourcePackages = findSourcePackages(sourceDir);
        Set<String> exportedPackages = readExportedPackages(pomFile);

        // Packages present in source but missing from Export-Package
        Set<String> missing = new TreeSet<>(sourcePackages);
        missing.removeAll(exportedPackages);

        // Packages in Export-Package that no longer exist in source
        Set<String> extra = new TreeSet<>(exportedPackages);
        extra.removeAll(sourcePackages);

        StringBuilder message = new StringBuilder();
        if (!missing.isEmpty()) {
            message.append("\nThe following packages exist in Source but are missing from Export-Package in pom.xml and build.gradle:\n");
            for (String pkg : missing) {
                message.append("  ").append(pkg).append('\n');
            }
        }
        if (!extra.isEmpty()) {
            message.append("\nThe following packages are declared in Export-Package but no longer exist in Source:\n");
            for (String pkg : extra) {
                message.append("  ").append(pkg).append('\n');
            }
        }

        assertTrue("OSGi Export-Package is out of sync. Please update the Export-Package manifest entry in pom.xml and build.gradle:" + message,
            missing.isEmpty() && extra.isEmpty());
    }

    /**
     * Walks the given source root directory and returns all Java package names
     * that contain at least one {@code .java} file, excluding the internal tools package.
     */
    private static Set<String> findSourcePackages(File sourceRoot)
    {
        Set<String> packages = new TreeSet<>();
        collectPackages(sourceRoot, "", packages);
        return packages;
    }

    private static void collectPackages(File dir, String packageName, Set<String> packages)
    {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }

        boolean hasJavaFiles = Arrays.stream(children)
            .anyMatch(f -> f.isFile() && f.getName().endsWith(".java"));

        if (hasJavaFiles && !packageName.isEmpty() && !packageName.equals(TOOLS_PACKAGE)) {
            packages.add(packageName);
        }

        for (File child : children) {
            if (child.isDirectory()) {
                String subPackage = packageName.isEmpty() ? child.getName() : packageName + "." + child.getName();
                collectPackages(child, subPackage, packages);
            }
        }
    }

    /**
     * Reads the {@code Export-Package} element from {@code pom.xml} and returns the set of
     * package names declared there (version attributes stripped).
     */
    private static Set<String> readExportedPackages(File pomFile) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pomFile);

        NodeList nodes = doc.getElementsByTagName("Export-Package");
        assertTrue("Export-Package element not found in pom.xml", nodes.getLength() > 0);

        String exportPackageValue = nodes.item(0).getTextContent();

        Set<String> packages = new TreeSet<>();
        for (String entry : exportPackageValue.split(",")) {
            // Strip OSGi attributes such as ";version=..." -> keep only the package name
            String packageName = entry.trim().split(";")[0].trim();
            if (!packageName.isEmpty()) {
                packages.add(packageName);
            }
        }
        return packages;
    }
}
