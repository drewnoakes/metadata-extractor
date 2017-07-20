package com.drew.metadata.mov;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.DataFormatException;

public class QtReader {
    private StreamReader reader;
    private QtDirectory directory;
    private QtContainerHandler qtContainerHandler;
    private QtAtomHandler qtAtomHandler;
    private ArrayList<String> history = new ArrayList<String>();

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        metadata = metadata;
        reader = new StreamReader(inputStream);
        directory = new QtDirectory();
        qtContainerHandler = new QtContainerHandler();
        qtAtomHandler = new QtAtomHandler();
        metadata.addDirectory(directory);
        reader.setMotorolaByteOrder(true);

        processAtoms(reader, -1, directory);
    }

    public void processAtoms(StreamReader reader, long atomSize, QtDirectory directory)
    {
        try {
            while ((atomSize == -1) ? true : reader.getPosition() < atomSize) {
                long size = reader.getInt32();
                if (size == 1) {
                    size = reader.getInt64();
                }

                if (size == 0 && history.get(history.size() - 1).equals("meta")) {
                    // This is free space for future metadata additions
                    size = reader.getInt32();
                }

                String fourCC = new String(reader.getBytes(4));

                if (qtContainerHandler.shouldAcceptContainer(fourCC)) {
                    history.add(fourCC);
                    if (size == 0) {
                        processAtoms(reader, -1, directory);
                    } else {
                        processAtoms(reader, reader.getPosition() + size - 8, directory);
                    }
                    history.remove(history.size() - 1);
                } else if (qtAtomHandler.shouldAcceptAtom(fourCC)) {
                    qtAtomHandler.processAtom(fourCC, reader.getBytes((int)size - 8), directory, history);
                } else {
                     if (size > 1)
                        reader.skip(size - 8);
                }
                System.out.println(Arrays.toString(history.toArray()) + ": " + fourCC);

            }
        } catch (IOException e) {
            System.out.println("End of data reached");
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
    }

}
