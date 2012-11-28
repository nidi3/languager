package org.languager.maven;

import java.util.List;

import stni.languager.crawl.Escape;

/**
 *
 */
public class ReplaceSearch extends BaseSearch {
    private String replacement;
    private List<Escape> escapes;

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public List<Escape> getEscapes() {
        return escapes;
    }

    public void setEscapes(List<Escape> escapes) {
        this.escapes = escapes;
    }
}
