package org.languager.maven;

/**
 *
 */
public class ExtractSearch extends BaseSearch {
    private String negativeRegex;
    private String ignoreRegex;

    public String getNegativeRegex() {
        return negativeRegex;
    }

    public void setNegativeRegex(String negativeRegex) {
        this.negativeRegex = negativeRegex;
    }

    public String getIgnoreRegex() {
        return ignoreRegex;
    }

    public void setIgnoreRegex(String ignoreRegex) {
        this.ignoreRegex = ignoreRegex;
    }
}
