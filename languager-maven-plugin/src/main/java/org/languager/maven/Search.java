package org.languager.maven;

/**
 *
 */
public class Search {
    private SearchPattern pattern;
    private String regex;
    private String negativeRegex;

    public SearchPattern getPattern() {
        return pattern;
    }

    public void setPattern(SearchPattern pattern) {
        this.pattern = pattern;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getNegativeRegex() {
        return negativeRegex;
    }

    public void setNegativeRegex(String negativeRegex) {
        this.negativeRegex = negativeRegex;
    }
}
