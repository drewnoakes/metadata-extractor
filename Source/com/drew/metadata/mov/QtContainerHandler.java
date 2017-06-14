package com.drew.metadata.mov;

import com.drew.lang.RandomAccessStreamReader;
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

    public long processContainer(@NotNull String fourCC, long atomSize, @NotNull RandomAccessStreamReader reader, int pos) throws IOException
    {
        if (shouldAcceptContainer(fourCC)) {
            return 0;
        }
        return atomSize - 8;
    }
}
