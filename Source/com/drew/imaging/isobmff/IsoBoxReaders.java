/*
 * Copyright 2002-2019 Drew Noakes and contributors
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
 *    https://github.com/drewnoakes/metadata-extractor/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.isobmff;

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Walks a box sequence and calls the {@link IsoBoxReader}s registered for each box's path.
 * Build it once and reuse it: it holds no per-file state.
 * <p>
 * A path names box types from the top level down, separated by {@code /}, such as
 * {@code moov/trak/mdia/hdlr}. {@code *} matches any one box, and {@code uuid:} followed by a
 * usertype, such as {@code uuid:85c0b687-820f-11e0-8111-f4ce462b6a48}, matches a {@code uuid}
 * box with that usertype; a plain {@code uuid} matches every {@code uuid} box. The walk
 * descends only into containers on the way to a registered path and skips everything else
 * unread.
 * <p>
 * Readers registered for the same box run in the order they were passed, after the layout.
 * A box with one reader and nothing registered below it is streamed to that reader. A box with
 * several readers, or with a reader and registered paths below it, is read into memory once
 * (up to {@code maxBufferedPayload}) so each reader and the descent start from the beginning of
 * the payload.
 * <p>
 * A problem in one box is reported to the file context's error handler and the walk goes on
 * with the next box. When the input ends inside a box, that is reported once, as a
 * {@link TruncatedInputException}, and the walk stops.
 */
public final class IsoBoxReaders
{
    /** Reads one box; the functional form of {@link IsoBoxReader}. */
    public interface Callback
    {
        void read(@NotNull IsoBoxContext box) throws IOException;
    }

    /** A reader that calls {@code callback} for the boxes at {@code paths}. */
    @NotNull
    public static IsoBoxReader on(@NotNull final Callback callback, @NotNull final String... paths)
    {
        return new IsoBoxReader()
        {
            @NotNull
            @Override
            public Iterable<String> getBoxPaths()
            {
                return Arrays.asList(paths);
            }

            @Override
            public void readBox(@NotNull IsoBoxContext box) throws IOException
            {
                callback.read(box);
            }
        };
    }

    /** The default of {@code maxBufferedPayload}: 64 MiB. */
    public static final long DEFAULT_MAX_BUFFERED_PAYLOAD = 64L * 1024 * 1024;

    @NotNull
    private final IsoBoxLayout _layout;
    private final long _maxBufferedPayload;
    private final Node _root = new Node();
    /** Every reader, in the order the readers of one box run: the layout's first, then as passed. */
    private final List<IsoBoxReader> _order = new ArrayList<IsoBoxReader>();

    /** A walk with {@link #DEFAULT_MAX_BUFFERED_PAYLOAD}. */
    public IsoBoxReaders(@NotNull IsoBoxLayout layout, @NotNull Iterable<? extends IsoBoxReader> readers)
    {
        this(layout, readers, DEFAULT_MAX_BUFFERED_PAYLOAD);
    }

    /**
     * @param maxBufferedPayload the largest payload read into memory for several readers of one
     *                           box
     */
    public IsoBoxReaders(@NotNull IsoBoxLayout layout, @NotNull Iterable<? extends IsoBoxReader> readers, long maxBufferedPayload)
    {
        _layout = layout;
        _maxBufferedPayload = maxBufferedPayload;
        for (IsoBoxReader reader : readers)
            register(reader);
        if (layout instanceof IsoBoxReader)
            registerLayout((IsoBoxReader) layout);
    }

    private void register(IsoBoxReader reader)
    {
        if (!_order.contains(reader))
            _order.add(reader);
        for (String path : reader.getBoxPaths()) {
            Node node = _root;
            for (String segment : path.split("/"))
                node = node.child(segment);
            if (!node.readers.contains(reader))
                node.readers.add(reader);
        }
    }

    /**
     * Reads {@code inputStream}, starting from the file context {@code root} (see
     * {@link IsoBoxContext#root}).
     */
    public void read(@NotNull InputStream inputStream, @NotNull IsoBoxContext root)
    {
        read(new IsoBoxIterator(inputStream), root);
    }

    /**
     * Reads {@code boxes}, starting from the file context {@code root} (see
     * {@link IsoBoxContext#root}).
     */
    public void read(@NotNull IsoBoxIterator boxes, @NotNull IsoBoxContext root)
    {
        new Walk().walk(boxes, root, _root.self);
    }

    private final class Walk
    {
        private boolean _ended;

        void walk(IsoBoxIterator boxes, IsoBoxContext parent, List<Node> nodes)
        {
            try {
                for (IsoBox box = boxes.next(); box != null; box = boxes.next()) {
                    List<Node> next = step(nodes, box);
                    if (next.isEmpty())
                        continue;
                    IsoBoxContext context = new IsoBoxContext(parent, box);
                    try {
                        boolean descend = false;
                        for (Node node : next)
                            descend |= node.hasChildren();
                        read(boxes, context, next, readers(next), descend);
                    } catch (IOException e) {
                        report(context, "Error reading box '" + box.type + "': " + e.getMessage(), e);
                    }
                    if (_ended)
                        return;
                }
            } catch (IOException e) {
                report(parent, String.valueOf(e.getMessage()), e);
            }
        }

        private void report(IsoBoxContext context, String message, IOException e)
        {
            if (_ended)
                return;
            if (e instanceof TruncatedInputException)
                _ended = true;
            context.addError(message, e);
        }

        private void read(IsoBoxIterator boxes, IsoBoxContext context, List<Node> path,
                          List<IsoBoxReader> readers, boolean descend) throws IOException
        {
            IsoBox box = context.box();
            if (readers.size() == 1 && !descend) {
                context.stream(boxes);
                readers.get(0).readBox(context);
                context.release();
                return;
            }
            if (readers.isEmpty()) {
                context.stream(boxes);
                IsoBoxIterator children = _layout.enterContainer(context) ? context.children() : null;
                context.release();
                if (children != null)
                    walk(children, context, path);
                return;
            }
            if (box.payloadSize == -1 || box.payloadSize > _maxBufferedPayload) {
                context.addError("Box '" + box.type + "' payload (" + box.payloadSize
                    + " bytes) is too large to read for several readers; skipped");
                return;
            }
            byte[] bytes = boxes.payloadBytes();
            for (IsoBoxReader reader : readers) {
                context.buffer(bytes);
                try {
                    reader.readBox(context);
                } catch (IOException e) {
                    report(context, "Error reading box '" + box.type + "': " + e.getMessage(), e);
                }
            }
            if (descend) {
                context.buffer(bytes);
                IsoBoxIterator children = _layout.enterContainer(context) ? context.children() : null;
                context.release();
                if (children != null)
                    walk(children, context, path);
            }
            context.release();
        }
    }

    private static List<Node> step(List<Node> nodes, IsoBox box)
    {
        if (nodes.size() == 1 && box.usertype == null) {
            Node node = nodes.get(0);
            Node exact = node.find(box.typeCode);
            if (exact == null)
                return node.wildcard == null ? Collections.<Node>emptyList() : node.wildcard.self;
            if (node.wildcard == null)
                return exact.self;
        }
        List<Node> next = new ArrayList<Node>(2);
        for (Node node : nodes) {
            Node exact = node.find(box.typeCode);
            if (exact != null)
                next.add(exact);
            if (box.usertype != null) {
                for (int i = 0; i < node.usertypes.size(); i++) {
                    if (Arrays.equals(node.usertypes.get(i), box.usertype))
                        next.add(node.uuidChildren.get(i));
                }
            }
            if (node.wildcard != null)
                next.add(node.wildcard);
        }
        return next;
    }

    /**
     * Registers the layout's paths that lie in containers the walk enters for the other
     * readers, ahead of those readers.
     */
    private void registerLayout(IsoBoxReader layout)
    {
        _order.add(0, layout);
        for (String path : layout.getBoxPaths()) {
            String[] segments = path.split("/");
            Node node = _root;
            for (int i = 0; i < segments.length - 1 && node != null; i++)
                node = node.existing(segments[i]);
            if (node == null)
                continue;
            Node last = node.child(segments[segments.length - 1]);
            if (!last.readers.contains(layout))
                last.readers.add(0, layout);
        }
    }

    /** The readers of the boxes at {@code nodes}, in {@link #_order}. */
    private List<IsoBoxReader> readers(List<Node> nodes)
    {
        if (nodes.size() == 1)
            return nodes.get(0).readers;
        List<IsoBoxReader> readers = new ArrayList<IsoBoxReader>();
        for (IsoBoxReader reader : _order) {
            for (Node node : nodes) {
                if (node.readers.contains(reader)) {
                    readers.add(reader);
                    break;
                }
            }
        }
        return readers;
    }

    /** One step of the registered paths: the readers registered there and where the paths continue. */
    private static final class Node
    {
        final List<IsoBoxReader> readers = new ArrayList<IsoBoxReader>(1);
        final List<Node> self = Collections.singletonList(this);
        private int[] _keys = new int[0];
        private Node[] _children = new Node[0];
        final List<byte[]> usertypes = new ArrayList<byte[]>(0);
        final List<Node> uuidChildren = new ArrayList<Node>(0);
        @Nullable
        Node wildcard;

        Node child(String segment)
        {
            if (segment.equals("*")) {
                if (wildcard == null)
                    wildcard = new Node();
                return wildcard;
            }
            if (segment.startsWith("uuid:")) {
                byte[] usertype = usertype(segment.substring(5));
                for (int i = 0; i < usertypes.size(); i++) {
                    if (Arrays.equals(usertypes.get(i), usertype))
                        return uuidChildren.get(i);
                }
                Node node = new Node();
                usertypes.add(usertype);
                uuidChildren.add(node);
                return node;
            }
            int type = typeCode(segment);
            Node node = find(type);
            if (node == null) {
                node = new Node();
                _keys = Arrays.copyOf(_keys, _keys.length + 1);
                _children = Arrays.copyOf(_children, _children.length + 1);
                _keys[_keys.length - 1] = type;
                _children[_children.length - 1] = node;
            }
            return node;
        }

        /** The node for {@code segment} if a path was registered through it, else null. */
        @Nullable
        Node existing(String segment)
        {
            if (segment.equals("*"))
                return wildcard;
            if (segment.startsWith("uuid:")) {
                byte[] usertype = usertype(segment.substring(5));
                for (int i = 0; i < usertypes.size(); i++) {
                    if (Arrays.equals(usertypes.get(i), usertype))
                        return uuidChildren.get(i);
                }
                return null;
            }
            return find(typeCode(segment));
        }

        @Nullable
        Node find(int type)
        {
            int[] keys = _keys;
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] == type)
                    return _children[i];
            }
            return null;
        }

        boolean hasChildren()
        {
            return _keys.length > 0 || !uuidChildren.isEmpty() || wildcard != null;
        }

        private static int typeCode(String type)
        {
            if (type.length() != 4)
                throw new IllegalArgumentException("Box type must have four characters: " + type);
            int code = 0;
            for (int i = 0; i < 4; i++) {
                char c = type.charAt(i);
                if (c > 0xFF)
                    throw new IllegalArgumentException("Box type must be ISO-8859-1: " + type);
                code = (code << 8) | c;
            }
            return code;
        }

        private static byte[] usertype(String uuid)
        {
            String hex = uuid.replace("-", "");
            if (hex.length() != 32)
                throw new IllegalArgumentException("Invalid usertype: " + uuid);
            byte[] bytes = new byte[16];
            for (int i = 0; i < 16; i++)
                bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
            return bytes;
        }
    }
}
