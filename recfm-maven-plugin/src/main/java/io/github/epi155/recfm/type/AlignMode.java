package io.github.epi155.recfm.type;

public enum AlignMode {
    LFT("R"), RGT("L");

    /**
     * Trunc/Pad suffix.
     * Align left -> pad right
     */
    public final String code;

    AlignMode(String code) {
        this.code = code;
    }
}
