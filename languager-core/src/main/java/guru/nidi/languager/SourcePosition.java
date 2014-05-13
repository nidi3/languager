/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.languager;

import java.io.File;

/**
 *
 */
public class SourcePosition implements Comparable<SourcePosition> {
    private final File source;
    private final int start;
    private final int end;
    private final int line;
    private final int column;

    public SourcePosition(File source, int line, int column) {
        this(source, 0, 0, line, column);
    }

    public SourcePosition(File source, int start, int end, int line, int column) {
        this.source = source;
        this.start = start;
        this.end = end;
        this.line = line;
        this.column = column;
    }

    public File getSource() {
        return source;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return getSource().getAbsolutePath() + ":[" + getLine() + "," + getColumn() + "]";
    }

    @Override
    public int compareTo(SourcePosition o) {
        return source.getName().compareTo(o.source.getName());
    }
}
