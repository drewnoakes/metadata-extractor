package com.drew.metadata;


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
     * Insert a knot (set of 3 points) into the list
     */
    public void insert(Knot knot)
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

    /**
     * Create a copy of this Subpath
     *
     * @return a new, duplicate Subpath
     */
    public Subpath copy()
    {
        Subpath copy = new Subpath(_type);
        for (Knot knot : _knots) {
            copy.insert(knot);
        }
        return copy;
    }

    @Override
    public String toString()
    {
        String description = _type + "(" + _knots.size() + " knots): ";
        for (Knot knot : _knots) {
            description += knot.getType() + "[ ";
            description += "(" + knot.getPoint(0) + ", " + knot.getPoint(1) + ")(";
            description += "(" + knot.getPoint(2) + ", " + knot.getPoint(3) + ")(";
            description += "(" + knot.getPoint(4) + ", " + knot.getPoint(5) + ") ]";
            description += " ";
        }
        return description;
    }
}
