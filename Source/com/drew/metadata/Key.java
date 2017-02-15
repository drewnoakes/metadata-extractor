package com.drew.metadata;

/**
 * @author Anthony Mandra
 * @author Drew Noakes https://drewnoakes.com
 */
public interface Key
{
    String getName();       // New, string rep. of enum
    String getSummary();    // previously tag.getName
    String getDescription(Directory directory); //previously directory.getDescription
    Object getValue();      // metadata key value
    int getInt();           // backwards compat int for lookup
}
