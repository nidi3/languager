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
package guru.nidi.languager.maven;

import guru.nidi.languager.crawl.Escape;

import java.util.List;

/**
 *
 */
public class ReplaceSearch extends BaseSearch {
    private String replacement;
    private String parameterMarker;
    private String parameterSeparator;
    private List<Escape> escapes;

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getParameterMarker() {
        return parameterMarker;
    }

    public void setParameterMarker(String parameterMarker) {
        this.parameterMarker = parameterMarker;
    }

    public String getParameterSeparator() {
        return parameterSeparator;
    }

    public void setParameterSeparator(String parameterSeparator) {
        this.parameterSeparator = parameterSeparator;
    }

    public List<Escape> getEscapes() {
        return escapes;
    }

    public void setEscapes(List<Escape> escapes) {
        this.escapes = escapes;
    }
}
