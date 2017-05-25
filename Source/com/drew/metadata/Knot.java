package com.drew.metadata;


/**
 * Represents a knot created by Photoshop:
 *  Linked knot
 *  Unlinked knot
 *
 * @author Payton Garland
 */
public class Knot
{
    private double[] points;
    private String type;

    public Knot(String type)
    {
        this.points = new double[6];
        this.type = type;
    }

    /**
     * Add an individual coordinate value (x or y) to
     * points array (6 points per knot)
     *
     * @param index location of point to be added in points
     * @param point coordinate value to be added to points
     */
    public void insert(int index, double point) {
        points[index] = point;
    }

    /**
     * Get an individual coordinate value (x or y)
     *
     * @return an individual coordinate value
     */
    public double getPoint(int index) {
        return points[index];
    }

    /**
     * Get the type of knot (linked or unlinked)
     *
     * @return the type of knot
     */
    public String getType() {
        return this.type;
    }
}
