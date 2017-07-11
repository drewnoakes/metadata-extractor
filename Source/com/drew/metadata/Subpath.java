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
    private ArrayList<Knot> _knots;
    private String _type;

    public Subpath()
    {
        _knots = new ArrayList<Knot>();
        _type = "";
    }

    public Subpath(String type)
    {
        _knots = new ArrayList<Knot>();
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
        for (int i = 0; i < _knots.size(); i++) {
            copy.insert(_knots.get(i));
        }
        return copy;
    }

    @Override
    public String toString()
    {
        String description = _type + "(" + _knots.size() + " knots): ";
        for (int i = 0; i < _knots.size(); i++) {
            description += _knots.get(i).getType() + "[ ";
            description += "(" + _knots.get(i).getPoint(0) + ", " + _knots.get(i).getPoint(1) + ")(";
            description += "(" + _knots.get(i).getPoint(2) + ", " + _knots.get(i).getPoint(3) + ")(";
            description += "(" + _knots.get(i).getPoint(4) + ", " + _knots.get(i).getPoint(5) + ") ]";
            description += " ";
        }
        return description;
    }
}
