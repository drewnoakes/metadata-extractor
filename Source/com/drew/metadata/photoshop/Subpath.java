package com.drew.metadata.photoshop;

import java.util.ArrayList;

/**
 * Represents a subpath created by Photoshop:
 * <ul>
 *   <li>Closed Bezier knot, linked</li>
 *   <li>Closed Bezier knot, unlinked</li>
 *   <li>Open Bezier knot, linked</li>
 *   <li>Open Bezier knot, unlinked</li>
 * </ul>
 *
 * @author Payton Garland
 */
public class Subpath
{
    private final ArrayList<Knot> _knots = new ArrayList<Knot>();
    private final String _type;

    public Subpath()
    {
        this("");
    }

    public Subpath(String type)
    {
        _type = type;
    }

    /**
     * Appends a knot (set of 3 points) into the list
     */
    public void add(Knot knot)
    {
        _knots.add(knot);
    }

    /**
     * Gets size of knots list
     *
     * @return size of knots ArrayList
     */
    public int size()
    {
        return _knots.size();
    }

    public Iterable<Knot> getKnots()
    {
        return _knots;
    }

    public String getType()
    {
        return _type;
    }
}
