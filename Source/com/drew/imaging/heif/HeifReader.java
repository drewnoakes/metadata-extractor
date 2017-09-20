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

        boolean printVisited = false;
        tabCount = 0;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }
        processBoxes(reader, -1, handler, printVisited);
    }

    private void processBoxes(StreamReader reader, long atomEnd, HeifHandler heifHandler, boolean printVisited)
    {
        try {
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (heifHandler.shouldAcceptContainer(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + box.type + "]");
                        tabCount++;
                    }

                    heifHandler.processContainer(box, reader);
                    processBoxes(reader, box.size + reader.getPosition() - 8, heifHandler, printVisited);

                    if (printVisited) {
                        tabCount--;
                    }


                } else if (heifHandler.shouldAcceptBox(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + box.type);
                    }

                    heifHandler = heifHandler.processBox(box, reader.getBytes((int)box.size - 8));

                } else {

                    if (box.size > 1) {
                        reader.skip(box.size - 8);
                    } else if (box.size == -1) {
                        break;
                    }

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + box.type + "}");
                    }

                }
            }
        } catch (IOException ignored) {
            // Currently, reader relies on IOException to end
        }
    }
}
