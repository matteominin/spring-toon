package com.caselli_minin.springtoon.toon;

import com.caselli_minin.springtoon.toon.converter.ToonConverter;
import com.caselli_minin.springtoon.toon.converter.ToonException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ToonConverterTest {

    @Test
    public void testEncode() throws ToonException {
        String jsonString = "{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

        String toonEncoded = ToonConverter.encode(jsonString);
        assertNotNull(toonEncoded);
        System.out.println("Encoded TOON: " + toonEncoded);
        assert toonEncoded.contains("title: example glossary");
    }

    @Test
    public void testDecode() throws ToonException {
        String toonString = """
                users[2]{id,name,role}:
                  1,Alice,admin
                  2,Bob,user""";
        String jsonDecoded = ToonConverter.decode(toonString);
        assertNotNull(jsonDecoded);
        System.out.println("Decoded JSON: " + jsonDecoded);
    }
}
