package com.drew.metadata.mov;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class QtReader {
    private StreamReader reader;
    private int tabCount;

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);

        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);
        tabCount = 0;

        boolean printVisited = false;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }
        processAtoms(reader, -1, directory, new QtAtomHandler(metadata), printVisited);
    }

    private void processAtoms(StreamReader reader, long atomSize, QtDirectory directory, QtHandler qtHandler, boolean printVisited)
    {
        try {
            while ((atomSize == -1) ? true : reader.getPosition() < atomSize) {

                long size = reader.getUInt32();

                if (size == 1) {
                    size = reader.getInt64();
                } else if (size == 0) {
                    size = reader.getInt32();
                }

                String fourCC = reader.getString(4);

                if (qtHandler.shouldAcceptContainer(fourCC)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + fourCC + "]");
                        tabCount++;
                    }

                    if (size == 0) {
                        processAtoms(reader, -1, directory, qtHandler.processContainer(fourCC), printVisited);
                    } else {
                        processAtoms(reader, reader.getPosition() + size - 8, directory, qtHandler.processContainer(fourCC), printVisited);
                    }

                    if (printVisited) {
                        tabCount--;
                    }

                } else if (qtHandler.shouldAcceptAtom(fourCC)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + fourCC);
                    }

                    qtHandler = qtHandler.processAtom(fourCC, reader.getBytes((int)size - 8));
                } else {
                    if (size > 1)
                        reader.skip(size - 8);

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + fourCC + "}");
                    }
                }
            }
        } catch (IOException ex) {
            directory.addError("Error reading file stream");
        }
    }

}
