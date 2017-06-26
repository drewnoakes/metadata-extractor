package com.drew.metadata.mov;

import com.drew.lang.RandomAccessStreamReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QtContainerHandler
{
    public boolean shouldAcceptContainer(@NotNull String fourCC)
    {
        return QtContainerTypes._containerList.contains(fourCC);
    }

    public long processContainer(@NotNull String fourCC, @NotNull StreamReader reader) throws IOException
    {
        return 0;
    }
}
