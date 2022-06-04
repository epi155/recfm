package io.github.epi155.recfm.type;

public enum SpaceMan {
    /**
     * il campo deve essere strettamente numerico
     */
    Deny,
    /**
     * il campo può essere anche SPACES, viene letto come null, viene inizializzato a ZEROES
     */
    Null,
    /**
     * il campo può essere anche SPACES, viene letto come null, viene inizializzato a SPACES
     */
    Init
}
