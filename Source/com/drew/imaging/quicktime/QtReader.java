package com.drew.imaging.quicktime;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class QtReader {
    private StreamReader reader;
    private int tabCount;

    public void extract(Metadata metadata, InputStream inputStream, QtHandler handler) throws IOException, DataFormatException
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);

        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);
        tabCount = 0;

        processAtoms(reader, -1, handler);
    }

    private void processAtoms(StreamReader reader, long atomEnd, QtHandler qtHandler)
    {
        try {
            long startPos = reader.getPosition();
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (qtHandler.shouldAcceptContainer(atom)) {

                    processAtoms(reader, atom.size + startPos, qtHandler.processContainer(atom));

                } else if (qtHandler.shouldAcceptAtom(atom)) {

                    qtHandler = qtHandler.processAtom(atom, reader.getBytes((int)atom.size - 8));

                } else {

                    if (atom.size > 1) {
                        reader.skip(atom.size - 8);
                    } else if (atom.size == -1) {
                        break;
                    }

                }
            }
        } catch (IOException ignored) {

        }
    }

}
