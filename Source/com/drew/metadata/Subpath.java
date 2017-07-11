package com.drew.metadata;


import java.util.ArrayList;

/**
 * Represents a subpath created by Photoshop:
 *  Closed Bezier knot, linked
 *  Closed Bezier knot, unlinked
 *  Open Bezier knot, linked
 *  Open Bezier knot, unlinked
 *
 * @author Payton Garland
 */
public class Subpath
{

    private ArrayList<Knot> knots;
    private String type;

    public Subpath()
    {
        this.knots = new ArrayList<Knot>();
        this.type = "";
    }

    public Subpath(String type)
    {
        this.knots = new ArrayList<Knot>();
        this.type = type;
    }

    /**
     * Insert a knot (set of 3 points) into the list
     */
    public void insert(Knot knot) {
        knots.add(knot);
    }

    /**
     * Gets size of knots list
     *
     * @return size of knots ArrayList
     */
    public int size() { return knots.size(); }

    /**
     * Create a copy of this Subpath
     *
     * @return a new, duplicate Subpath
     */
    public Subpath copy()
    {
        Subpath copy = new Subpath(type);
        for (int i = 0; i < knots.size(); i++) {
            copy.insert(knots.get(i));
        }
        return copy;
    }

    @Override
    public String toString()
    {
        String description = type + "(" + knots.size() + " knots): ";
        for (int i = 0; i < knots.size(); i++) {
            description += knots.get(i).getType() + "[ ";
            description += "(" + knots.get(i).getPoint(0) + ", " + knots.get(i).getPoint(1) + ")(";
            description += "(" + knots.get(i).getPoint(2) + ", " + knots.get(i).getPoint(3) + ")(";
            description += "(" + knots.get(i).getPoint(4) + ", " + knots.get(i).getPoint(5) + ") ]";
            description += " ";
        }
        return description;
    }

}



