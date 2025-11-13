package com.caselli_minin.springtoon.toon.converter;

import com.sun.jna.Pointer;

/**
 * High-level Java wrapper for TOON format conversion.
 * Provides easy-to-use methods for encoding JSON to TOON and decoding TOON to JSON.
 */
public class ToonConverter {

    private static final ToonNativeLibrary library = ToonNativeLibrary.INSTANCE;

    /**
     * Encodes a JSON string into TOON format.
     *
     * @param jsonString The JSON string to encode
     * @return The TOON-encoded string
     * @throws ToonException if encoding fails or the result is an error
     */
    public static String encode(String jsonString) throws ToonException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new ToonException("Input JSON string cannot be null or empty");
        }

        Pointer resultPtr = null;
        try {
            resultPtr = library.toon_encode(jsonString);

            if (resultPtr == null) {
                throw new ToonException("Native library returned null pointer");
            }

            String result = resultPtr.getString(0, "UTF-8");

            // Check if the result is an error message
            if (result.startsWith("ERROR:")) {
                throw new ToonException(result.substring(7).trim());
            }

            return result;

        } catch (Exception e) {
            if (e instanceof ToonException) {
                throw (ToonException) e;
            }
            throw new ToonException("Failed to encode JSON to TOON: " + e.getMessage(), e);
        } finally {
            if (resultPtr != null) {
                library.toon_free_string(resultPtr);
            }
        }
    }

    /**
     * Decodes a TOON format string into JSON.
     *
     * @param toonString The TOON format string to decode
     * @return The decoded JSON string
     * @throws ToonException if decoding fails or the result is an error
     */
    public static String decode(String toonString) throws ToonException {
        if (toonString == null || toonString.trim().isEmpty()) {
            throw new ToonException("Input TOON string cannot be null or empty");
        }

        Pointer resultPtr = null;
        try {
            resultPtr = library.toon_decode(toonString);

            if (resultPtr == null) {
                throw new ToonException("Native library returned null pointer");
            }

            String result = resultPtr.getString(0, "UTF-8");

            // Check if the result is an error message
            if (result.startsWith("ERROR:")) {
                throw new ToonException(result.substring(7).trim());
            }

            return result;

        } catch (Exception e) {
            if (e instanceof ToonException) {
                throw (ToonException) e;
            }
            throw new ToonException("Failed to decode TOON to JSON: " + e.getMessage(), e);
        } finally {
            if (resultPtr != null) {
                library.toon_free_string(resultPtr);
            }
        }
    }
}