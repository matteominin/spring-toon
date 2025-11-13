package com.caselli_minin.springtoon.toon.converter;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * JNA interface to the native TOON Rust library.
 * This interface maps directly to the C functions exported by the Rust library.
 */
interface ToonNativeLibrary extends Library {

    ToonNativeLibrary INSTANCE = Native.load("toon_java", ToonNativeLibrary.class);

    /**
     * Encodes a JSON string into TOON format.
     * @param jsonInput JSON string to encode
     * @return Pointer to the TOON-encoded string (must be freed with toon_free_string)
     */
    Pointer toon_encode(String jsonInput);

    /**
     * Decodes a TOON format string into JSON.
     * @param toonInput TOON format string to decode
     * @return Pointer to the JSON string (must be freed with toon_free_string)
     */
    Pointer toon_decode(String toonInput);

    /**
     * Frees a string allocated by the Rust library.
     * @param ptr Pointer to free
     */
    void toon_free_string(Pointer ptr);
}