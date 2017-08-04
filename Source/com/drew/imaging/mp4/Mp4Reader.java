package com.drew.imaging.mp4;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.Box;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class Mp4Reader
{
    private StreamReader reader;

    public void extract(Metadata metadata, InputStream inputStream, Mp4Handler handler) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        processBoxes(reader, -1, handler);
    }

    private void processBoxes(StreamReader reader, long atomEnd, Mp4Handler mp4Handler)
    {
        try {
            long startPos = reader.getPosition();
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (mp4Handler.shouldAcceptContainer(box)) {

                    processBoxes(reader, box.size + startPos, mp4Handler.processContainer(box));

                } else if (mp4Handler.shouldAcceptBox(box)) {

                    mp4Handler = mp4Handler.processBox(box, reader.getBytes((int)box.size - 8));

                } else {

                    if (box.size > 1) {
                        reader.skip(box.size - 8);
                    } else if (box.size == -1) {
                        break;
                    }

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}
