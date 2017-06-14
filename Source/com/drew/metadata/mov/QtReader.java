package com.drew.metadata.mov;

import com.drew.lang.RandomAccessReader;
import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

public class QtReader {
    private QtDataSource source;
    private Metadata metadata;
    private Directory directory;

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        this.metadata = metadata;
        this.source = new QtDataSource(inputStream);
        this.directory = new QtDirectory();
        metadata.addDirectory(directory);

        try
        {
            QtAtomTree atomTree = new QtAtomTree(source);

            if (atomTree.contains(QtAtomTypes.COMPRESSED_MOVIE_ATOM))
            {
                QtDataSource compressedSource = new QtCompressedDataSource(source);
                source.close();
                source = compressedSource;
                atomTree = new QtAtomTree(source);
            }

            List<QtAtom> metadataAtoms = new ArrayList<QtAtom>();

            QtAtom soundTrack = getTrack(atomTree, QtAtomTypes.SOUND_HANDLER_TYPE);
            if (atomExists(soundTrack))
            {
                metadataAtoms.add(getTrackDescTable(soundTrack, QtAtomTypes.SOUND_HANDLER_TYPE));
            }

            QtAtom videoTrack = getTrack(atomTree, QtAtomTypes.VIDEO_HANDLER_TYPE);
            if (atomExists(videoTrack))
            {
                metadataAtoms.add(getTrackDescTable(videoTrack, QtAtomTypes.VIDEO_HANDLER_TYPE));
                QtMediaHeaderAtom mediaHeader = (QtMediaHeaderAtom)getTrackMediaHeader(videoTrack);
                if (atomExists(mediaHeader))
                {
                    mediaHeader.getMetadata(source);
                    mediaHeader.populateMetadata(directory);
                }
                QtTimeToSampleAtom videoTimeSampleTable = (QtTimeToSampleAtom)getTrackTimeToSampleTable(videoTrack);
                if (atomExists(videoTimeSampleTable))
                {
                    videoTimeSampleTable.getMetadata(source);
                    videoTimeSampleTable.populateMetadata(directory);
                }
            }

            QtAtom metadataAtom = atomTree.getAtom(QtAtomTypes.META_ATOM);
            if (atomExists(metadataAtom)) {
                metadataAtoms.add(metadataAtom);
            }

            metadataAtoms.addAll(atomTree.getAtoms(QtAtomTypes.MOVIE_HEADER_ATOM));

            getMetadata(metadataAtoms);

            populateMetadata(metadataAtoms);

			//atomTree.printTree();

            source.close();

        }
        catch (IOException ioe)
        {
            source.close();
            throw new IOException("Error reading quicktime file: " + ioe.getMessage());
        }
        catch (MetadataException e)
        {

        }
    }


    private void getMetadata(List<QtAtom> atoms) throws IOException
    {
        for (Iterator<QtAtom> iterator = atoms.iterator(); iterator.hasNext();) {
            QtLeafAtom atom = (QtLeafAtom)iterator.next();
            if (atom != null)
            {
                atom.getMetadata(source);
            }
        }
    }

    private void populateMetadata(List<QtAtom> atoms) throws MetadataException
    {
        for (Iterator<QtAtom> iterator = atoms.iterator(); iterator.hasNext();) {
            QtLeafAtom atom = (QtLeafAtom)iterator.next();
            if (atom != null)
            {
                atom.populateMetadata(directory);
            }
        }
    }

    private boolean atomExists(QtAtom atom)
    {
        return atom != null;
    }

    public QtAtom getTrack(QtAtomTree atomTree, String trackType) throws IOException
    {
        List<QtAtom> tracks = atomTree.getAtoms(QtAtomTypes.TRACK_ATOM);

        for (QtAtom track : tracks)
        {
            QtAtomTree trackTree = new QtAtomTree(track.getChildren());
            List<QtAtom> handlers = trackTree.getAtoms(QtAtomTypes.HANDLER_REFERENCE_ATOM);
            for (QtAtom handler : handlers)
            {
                QtMediaHandlerAtom qtmHandler = (QtMediaHandlerAtom)handler;
                qtmHandler.getMetadata(source);
                if (trackType.equals(qtmHandler.getHandlerType()))
                {
                    return track;
                }
            }
        }
        return null;
    }

    public QtAtom getTrackDescTable(QtAtom track, String trackType)
    {
        QtAtomTree trackTree = new QtAtomTree(track.getChildren());
        QtSampleDescriptionAtom stsdAtom = (QtSampleDescriptionAtom)trackTree.getAtom(QtAtomTypes.SAMPLE_DESCRIPTION_ATOM);
        if (atomExists(stsdAtom))
        {
            return QtAtomFactory.createAtom(stsdAtom, trackType);
        }
        return null;
    }

    public QtAtom getTrackMediaHeader(QtAtom track)
    {
        QtAtomTree trackTree = new QtAtomTree(track.getChildren());
        QtMediaHeaderAtom mhAtom = (QtMediaHeaderAtom)trackTree.getAtom(QtAtomTypes.MEDIA_HEADER_ATOM);
        if (atomExists(mhAtom))
        {
            return mhAtom;
        }
        return null;
    }

    public QtAtom getTrackTimeToSampleTable(QtAtom track)
    {
        QtAtomTree trackTree = new QtAtomTree(track.getChildren());
        QtTimeToSampleAtom ttsAtom = (QtTimeToSampleAtom)trackTree.getAtom(QtAtomTypes.TIME_TO_SAMPLE_ATOM);
        if (atomExists(ttsAtom))
        {
            return ttsAtom;
        }
        return null;
    }
}
