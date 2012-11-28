package org.languager.maven;

/**
 *
 */
public class ExtractSearch extends BaseSearch {
    private String negativeRegex;

    public String getNegativeRegex() {
        return negativeRegex;
    }

    public void setNegativeRegex(String negativeRegex) {
        this.negativeRegex = negativeRegex;
    }
}
