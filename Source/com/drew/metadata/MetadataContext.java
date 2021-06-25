package com.drew.metadata;

import com.drew.lang.annotations.NotNull;

import java.util.Locale;

/**
 * Context class for metadata. Contains settings used while extracting and formatting metadata.
 *
 * @author Roel van Dijk
 */
public class MetadataContext
{
    /**
     * Locale used to format metadata.
     */
    private Locale _locale;

    /**
     * Initialize a context using the system default {@link Locale}.
     */
    public MetadataContext()
    {
        _locale = Locale.getDefault();
    }

    /**
     * Gets the configured {@link Locale}.
     *
     * @return the configured locale.
     */
    public Locale locale()
    {
        return _locale;
    }

    /**
     * Configure the {@link Locale} to use for extracting and formatting metadata.
     *
     * @param locale the locale to use
     * @return this context
     */
    public MetadataContext locale(@NotNull final Locale locale)
    {
        _locale = locale;
        return this;
    }

}
