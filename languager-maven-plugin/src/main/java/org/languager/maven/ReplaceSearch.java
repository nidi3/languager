package org.languager.maven;

import java.util.List;

import stni.languager.crawl.Escape;

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
