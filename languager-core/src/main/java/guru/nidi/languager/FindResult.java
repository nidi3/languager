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

import java.util.Comparator;

/**
 *
 */
public class FindResult<T> {
    public static final Comparator<FindResult<?>> POSITION_COMPARATOR = new Comparator<FindResult<?>>() {
        public int compare(FindResult<?> result1, FindResult<?> result2) {
            int res = result1.getPosition().getSource().compareTo(result2.getPosition().getSource());
            if (res == 0) {
                res = result1.getPosition().getLine() - result2.getPosition().getLine();
            }
            return res;
        }
    };

    private final SourcePosition position;
    private final T finding;

    public FindResult(SourcePosition position, T finding) {
        this.position = position;
        this.finding = finding;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public T getFinding() {
        return finding;
    }
}
