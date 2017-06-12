package com.drew.metadata.mov;

import com.drew.lang.ByteUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QtAtomTree {
    private QtDataSource source;
    private QtAtom root;

    public QtAtomTree(QtDataSource source) throws IOException
    {
        this.source = source;
        root = new QtAtom(buildAtomTree(0, source.getLength()));
    }

    public QtAtomTree(List<QtAtom> atoms)
    {
        root = new QtAtom(atoms);
    }

    private List<QtAtom> buildAtomTree(long startIndex, long stopIndex) throws IOException
    {
        ArrayList<QtAtom> atoms = new ArrayList<QtAtom>();
        long offset = startIndex;
        byte[] buffer = new byte[4];


        while(offset < stopIndex)
        {
            source.reset();
            source.skip(offset);

            source.read(buffer);
            long atomSize = ByteUtil.getUnsignedInt32(buffer, 0, true);

            if (offset + 4 == stopIndex)
            {
                break;
            }

            source.read(buffer);
            String atomType = new String(buffer);

            // if atomSize is 1, this means an extended (64 bit) size is stored immediately following atomType
            if (atomSize == 1)
            {
                byte[] extendedBuffer = new byte[8];
                source.read(buffer);
                atomSize = ByteUtil.getLong64(extendedBuffer, 0, true);
            }

            QtAtom newAtom = null;

            if (QtAtomTypes.CONTAINER_TYPES.contains(atomType))
            {
                List<QtAtom> children = buildAtomTree(offset + 8, offset + atomSize);
                newAtom = QtAtomFactory.createAtom(atomSize, atomType, offset, children);
            }
            else
            {
                newAtom = QtAtomFactory.createAtom(atomSize, atomType, offset, new ArrayList<QtAtom>());
            }


            atoms.add(newAtom);

            if (atomSize == 0)
            {
                offset = stopIndex;
            }
            else
            {
                offset = offset + atomSize;
            }
//            if (atomType.equals(QtAtomTypes.USER_DATA_ATOM))
//            {
//                offset = offset + 4;
//            }

        }
        return atoms;
    }

    // returns all occurrences of atomType in tree
    public List<QtAtom> getAtoms(String atomType)
    {
        return getAtoms(root.getChildren(), atomType);
    }

    public List<QtAtom> getAtoms(List<QtAtom> atomTree, String atomType)
    {
        List<QtAtom> atoms = new ArrayList<QtAtom>();

        for (QtAtom atom : atomTree) {
            if (atomType.equals(atom.getType()))
            {
                atoms.add(atom);
            }
            if (!atom.isLeafNode())
            {
                List<QtAtom> children = atom.getChildren();
                atoms.addAll(getAtoms(children, atomType));
            }
        }
        return atoms;
    }

    // returns first occurrence of atomType in tree
    public QtAtom getAtom(String atomType)
    {
        return getAtom(root.getChildren(), atomType);
    }

    public QtAtom getAtom(List<QtAtom> atomTree, String atomType)
    {
        QtAtom targetAtom = null;

        for (QtAtom atom: atomTree)
        {
            if (atomType.equals(atom.getType()))
            {
                return atom;
            }
            if (!atom.isLeafNode())
            {
                targetAtom = getAtom(atom.getChildren(), atomType);
            }
        }
        return targetAtom;
    }

    public boolean contains(String atomType)
    {
        return (getAtoms(atomType).size() != 0);
    }

    public void printTree()
    {
        printTree(root.getChildren(), "");
    }

    private void printTree(List<QtAtom> atoms, String indent)
    {
        for (QtAtom atom : atoms)
        {
            System.out.println(indent + atom.toString());
            if (!atom.isLeafNode())
            {
                List<QtAtom> children = atom.getChildren();
                printTree(children, indent + "  ");
            }
        }
    }

}
