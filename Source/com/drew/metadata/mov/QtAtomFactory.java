package com.drew.metadata.mov;

import java.util.List;

public class QtAtomFactory {

    public static QtAtom createAtom(long size, String type, long offset, List<QtAtom> children)
    {

        if(QtAtomTypes.MOVIE_HEADER_ATOM.equals(type))
        {
            return new QtMovieHeaderAtom(size, type, offset);
        }
        else if (QtAtomTypes.SAMPLE_DESCRIPTION_ATOM.equals(type))
        {
            return new QtSampleDescriptionAtom(size, type, offset);
        }
        else if (QtAtomTypes.HANDLER_REFERENCE_ATOM.equals(type))
        {
            return new QtMediaHandlerAtom(size, type, offset);
        }
        else if (QtAtomTypes.TIME_TO_SAMPLE_ATOM.equals(type))
        {
            return new QtTimeToSampleAtom(size, type, offset);
        }
        else if (QtAtomTypes.MEDIA_HEADER_ATOM.equals(type))
        {
            return new QtMediaHeaderAtom(size, type, offset);
        }
        else if (QtAtomTypes.CONTAINER_TYPES.contains(type))
        {
            return new QtAtom(size, type, offset, children);
        }

        return new QtAtom(size, type, offset);
    }

    public static QtAtom createAtom(QtSampleDescriptionAtom atom, String type)
    {
        if (QtAtomTypes.SOUND_HANDLER_TYPE.equals(type))
        {
            return new QtAudioSampleDescriptionAtom(atom);
        }
        else if (QtAtomTypes.VIDEO_HANDLER_TYPE.equals(type))
        {
            return new QtVideoSampleDescriptionAtom(atom);
        }
        return new QtAtom(atom);
    }

}
