/*
 * Copyright 2002-2017 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.lang;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Reads lines and words from a reader sequentially
 * 
 * @author Kevin Mott https://github.com/kwhopper
 * @author Drew Noakes https://drewnoakes.com
 */
public class IterableWordReader implements Iterable<String>, Iterator<String>
{
    private final ReaderInfo _reader;
    private final LinkedList<String> _stringLinkedList = new LinkedList<String>();
    
    public IterableWordReader(ReaderInfo reader)
    {
        _reader = reader;
    }
    
    // the next three methods implement Iterator
    @Override
    public boolean hasNext()
    {
        if(_stringLinkedList.isEmpty())
        {
            while(true)
            {
                String line = null;
                try
                {
                   line = _reader.readLine();
                }
                catch(IOException ignore)
                {
                    return false;
                }

                if (line != null)
                {
                    int commentFromIndex = line.indexOf('#');
                    if (commentFromIndex != -1)
                        line = line.substring(0, commentFromIndex);

                    String[] words = line.split("\\s+");
                    for (String word : words)
                    {
                        String wordtrim = word.trim();
                        if(wordtrim.length() > 0)
                            _stringLinkedList.add(wordtrim);
                    }
                }
                
                // it's possible for line to be a zero-length string instead
                // of null. In that case, keep reading lines until the list
                // already contains a string or finally get a null line
                if(line == null || !_stringLinkedList.isEmpty())
                    break;
            }
        }
        
        return !_stringLinkedList.isEmpty();
    }
    
    @Override
    public String next()
    {
        if(hasNext())
            return _stringLinkedList.pop();
        return "";
    }
    
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
    
    // this method implements Iterable
    @Override
    public Iterator<String> iterator() {
        return this;
    }
}
