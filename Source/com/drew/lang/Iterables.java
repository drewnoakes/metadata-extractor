package com.drew.lang;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public class Iterables
{
    public static <E> List<E> toList(Iterable<E> iterable)
    {
        ArrayList<E> list = new ArrayList<E>();
        for (E item : iterable) {
            list.add(item);
        }
        return list;
    }

    public static <E> Set<E> toSet(Iterable<E> iterable)
    {
        HashSet<E> set = new HashSet<E>();
        for (E item : iterable) {
            set.add(item);
        }
        return set;
    }
}
