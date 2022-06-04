package io.github.epi155.recfm.type;

/**
 * control algorithms for alphanumeric fields
 */
public enum CheckChar {
    /**
     * No check
     */
    None,
    /**
     * tests for ASCII char
     */
    Ascii,
    /**
     * tests for ISO-8859-1 (Latin1) char
     */
    Latin1,
    /**
     * tests for valid UTF-8 char
     */
    Valid
}
