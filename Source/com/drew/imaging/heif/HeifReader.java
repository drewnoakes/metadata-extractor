package com.drew.imaging.heif;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.heif.boxes.Box;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class HeifReader
{
    private StreamReader reader;
    private int tabCount;


    public void extract(Metadata metadata, InputStream inputStream, HeifHandler handler) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        processBoxes(reader, -1, handler);
    }

    private void processBoxes(StreamReader reader, long atomEnd, HeifHandler heifHandler)
    {
        try {
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (heifHandler.shouldAcceptContainer(box)) {
                    heifHandler.processContainer(box, reader);
                    processBoxes(reader, box.size + reader.getPosition() - 8, heifHandler);
                } else if (heifHandler.shouldAcceptBox(box)) {
                    heifHandler = heifHandler.processBox(box, reader.getBytes((int)box.size - 8));
                } else {
                    if (box.size > 1) {
                        reader.skip(box.size - 8);
                    } else if (box.size == -1) {
                        break;
                    }
                }
            }
        } catch (IOException ignored) {
            // Currently, reader relies on IOException to end
        }
    }
}
