package com.drew.metadata.mov;

import java.util.ArrayList;
import java.util.List;

public class QtContainerAtom
{
    private long size;
    private String fourCC;
    private List<QtAtom> leafChildren;
    private List<QtContainerAtom> containerChildren;

    public QtContainerAtom(long size, String fourCC) {
        this.size = size;
        this.fourCC = fourCC;
        leafChildren = new ArrayList<QtAtom>();
        containerChildren = new ArrayList<QtContainerAtom>();
    }

    public void addLeaf(QtAtom qtAtom) {
        leafChildren.add(qtAtom);
    }

    public void addContainer(QtContainerAtom qtContainerAtom) {
        containerChildren.add(qtContainerAtom);
    }
}
