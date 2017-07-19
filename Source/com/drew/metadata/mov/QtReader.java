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
    private Metadata metadata;
    private RandomAccessStreamReader reader;
    private QtDirectory directory;
    private QtContainerHandler qtContainerHandler;
    private QtAtomHandler qtAtomHandler;
    private QtAtomTree qtAtomTree;

    private QtContainerAtom current;
    private boolean shouldContain;

    public ArrayList<QtContainerAtom> atomsToHandle = new ArrayList<QtContainerAtom>();

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
        shouldContain = false;

        processAtoms(reader, reader.getLength(), directory, 0);
        System.out.println("here");
    }

    public void processAtoms(RandomAccessStreamReader reader, long atomSize, QtDirectory directory, int pos)
    {
        try {
            while (pos < atomSize) {
                long size = reader.getInt32(pos);
                pos += 4;
                String fourCC = new String(reader.getBytes(pos, 4));
                pos += 4;
                if (qtContainerHandler.shouldAcceptContainer(fourCC)) {
                    pos += qtContainerHandler.processContainer(fourCC, size, reader, pos);
                    if (true) {
                        shouldContain = true;
                        current = new QtContainerAtom(size, fourCC);
                        processAtoms(reader, size + pos, directory, pos);
                        atomsToHandle.add(current);
                        current = null;
                        shouldContain = false;
                    } else {
                        if (shouldContain == true) {
                            current.addContainer(new QtContainerAtom(size, fourCC));
                        }
                        processAtoms(reader, size + pos, directory, pos);
                    }
                } else if (qtAtomHandler.shouldAcceptAtom(fourCC)){
                    byte[] payload = reader.getBytes(pos, (int)size - 8);
                    if (shouldContain == true) {
                        current.addLeaf(new QtAtom(size, fourCC, payload));
                    }
                    qtAtomHandler.processAtom(fourCC, payload, directory);
                    pos += size - 8;
                } else {
                    pos += size - 8;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
    }

}
