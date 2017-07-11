package com.drew.metadata.photoshop;


/**
 * Represents a knot created by Photoshop:
 *
 * <ul>
 *   <li>Linked knot</li>
 *   <li>Unlinked knot</li>
 * </ul>
 *
 * @author Payton Garland
 */
public class Knot
{
    private final double[] _points = new double[6];
    private final String _type;

    public Knot(String type)
    {
        _type = type;
    }

    /**
     * Add an individual coordinate value (x or y) to
     * points array (6 points per knot)
     *
     * @param index location of point to be added in points
     * @param point coordinate value to be added to points
     */
    public void setPoint(int index, double point)
    {
        _points[index] = point;
    }

    /**
     * Get an individual coordinate value (x or y)
     *
     * @return an individual coordinate value
     */
    public double getPoint(int index)
    {
        return _points[index];
    }

    /**
     * Get the type of knot (linked or unlinked)
     *
     * @return the type of knot
     */
    public String getType()
    {
        return this._type;
    }
}
