package com.drew.imaging.quicktime;

import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class QtReader {
    private StreamReader reader;
    private int tabCount;

    public void extract(Metadata metadata, InputStream inputStream, QtHandlerSample handler) throws IOException, DataFormatException
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);

        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);
        tabCount = 0;

        boolean printVisited = true;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }
        processAtoms(reader, -1, directory, handler, printVisited);
    }

    private void processAtoms(StreamReader reader, long atomSize, Directory directory, QtHandlerSample qtHandler, boolean printVisited)
    {
        try {
            while ((atomSize == -1) ? true : reader.getPosition() < atomSize) {

                AtomSample atom = qtHandler.getAtom(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (qtHandler.shouldAcceptContainer(atom.type)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + atom.type + "]");
                        tabCount++;
                    }

                    // If the size is 0, that means this atom extends to the end of file
                    if (atom.size == 0) {
                        processAtoms(reader, -1, directory, qtHandler.processContainer(atom.type), printVisited);
                    } else {
                        processAtoms(reader, reader.getPosition() + atom.size - 8, directory, qtHandler.processContainer(atom.type), printVisited);
                    }

                    if (printVisited) {
                        tabCount--;
                    }

                } else if (qtHandler.shouldAcceptAtom(atom.type)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + atom.type);
                    }

                    qtHandler = qtHandler.processAtom(atom.type, reader.getBytes((int)atom.size - 8));
                } else {
                    if (atom.size > 1)
                        reader.skip(atom.size - 8);

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + atom.type + "}");
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

}
