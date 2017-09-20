package com.drew.tools;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Payton Garland
 */
public class TreeLogger
{
    private static List<String> containers = Arrays.asList("moov", "trak", "tapt", "edts", "mdia", "minf", "stbl", "dinf", "tref", "udta");

    private static List<String> atoms = Arrays.asList("ftyp", "wide", "mvhd", "tkhd", "mdhd",
        "hdlr", "smhd", "hdlr", "stsd", "stts", "stsc", "stsz", "stco", "clef", "prof", "enof", "dref", "elst", "vmhd", "gmhd", "tmcd", "ctts", "cslg", "stss", "sdtp");

    private static int tabCount;

    private ArrayList<String> unknowns = new ArrayList<String>();

    public void logQuickTime(SequentialReader reader)
    {
        System.out.println("_______________Beginning to Print Tree_______________");
        System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
        System.out.println("_____________________________________________________");
        processAtoms(reader, -1);

        System.out.println("Unknown atoms: ");
        for (String unknown : unknowns) {
            System.out.println("    " + unknown);
        }
    }

    private void processAtoms(SequentialReader reader, long atomEnd)
    {
        try {
            while (atomEnd == -1 || reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                if (containers.contains(atom.type)) {
                    for (int i = 0; i < tabCount; i++) {
                        System.out.print("   " + i + "   |");
                    }
                    System.out.println(" [" + atom.type + "]");
                    tabCount++;
                    processAtoms(reader, atom.size + reader.getPosition() - 8);
                    tabCount--;
                } else if (atoms.contains(atom.type)) {
                    for (int i = 0; i < tabCount; i++) {
                        System.out.print("   " + i + "   |");
                    }
                    System.out.println("  " + atom.type);
                    reader.skip(atom.size - 8);
                } else if (atom.size > 1) {
                    for (int i = 0; i < tabCount; i++) {
                        System.out.print("   " + i + "   |");
                    }
                    System.out.println(" ?" + atom.type + "?");
                    unknowns.add(atom.type);
                    reader.skip(atom.size - 8);
                } else if (atom.size == -1) {
                    break;
                }
            }
        } catch (IOException e) {

        }
    }
}
