package com.drew.metadata.mov;

import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.DataFormatException;

public class QtReader {
    private Metadata metadata;
    private RandomAccessStreamReader reader;
    private QtDirectory directory;
    private QtContainerHandler qtContainerHandler;
    private QtAtomHandler qtAtomHandler;
    private QtAtomTree qtAtomTree;

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        this.metadata = metadata;
        this.reader = new RandomAccessStreamReader(inputStream);
        this.directory = new QtDirectory();
        this.qtContainerHandler = new QtContainerHandler();
        this.qtAtomHandler = new QtAtomHandler();
        this.qtAtomTree = new QtAtomTree();
        metadata.addDirectory(directory);
        reader.setMotorolaByteOrder(true);

        processAtoms(reader, reader.getLength(), directory, 0);
    }

    public void processAtoms(RandomAccessStreamReader reader, long atomSize, QtDirectory directory, int pos)
    {
        try {
            int offset = pos;
            while (pos < atomSize) {
                long size = reader.getInt32(pos);
                pos += 4;
                String fourCC = new String(reader.getBytes(pos, 4));
                pos += 4;
                if (qtContainerHandler.shouldAcceptContainer(fourCC)) {
                    pos += qtContainerHandler.processContainer(fourCC, size, reader, pos);
                    processAtoms(reader, size, directory, pos);
                } else if (qtAtomHandler.shouldAcceptAtom(fourCC)){
                    qtAtomHandler.processAtom(fourCC, reader.getBytes(pos, (int)size - 8), directory);
                    pos += size - 8;
                } else {
                    pos += size - 8;
                }
                System.out.println(fourCC);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
