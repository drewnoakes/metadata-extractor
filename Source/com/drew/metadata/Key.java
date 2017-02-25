package com.drew.metadata;

/**
 * @author Anthony Mandra
 * @author Drew Noakes https://drewnoakes.com
 */
public interface Key
{
    String getName();       // New, string rep. of enum
    String getType();       // TODO: getType == getValue, previously tag.getType
    String getSummary();    // previously tag.getName
    String getDescription(Directory directory); //previously directory.getDescription
    Object getValue();      // more logical name for underlying key value
}
