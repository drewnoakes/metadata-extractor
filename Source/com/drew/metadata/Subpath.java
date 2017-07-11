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

    /**
     * Create a copy of this Subpath
     *
     * @return a new, duplicate Subpath
     */
    public Subpath copy()
    {
        Subpath copy = new Subpath(_type);
        for (Knot knot : _knots) {
            copy.add(knot);
        }
        return copy;
    }

    @Override
    public String toString()
    {
        StringBuilder description = new StringBuilder();

        description.append(_type).append("(").append(_knots.size()).append(" knots):");

        for (Knot knot : _knots) {
            description.append(" ");
            description.append(knot.getType()).append("[ ");
            description.append("(").append(knot.getPoint(0)).append(", ").append(knot.getPoint(1)).append(") ");
            description.append("(").append(knot.getPoint(2)).append(", ").append(knot.getPoint(3)).append(") ");
            description.append("(").append(knot.getPoint(4)).append(", ").append(knot.getPoint(5)).append(") ]");
        }

        return description.toString();
    }
}
