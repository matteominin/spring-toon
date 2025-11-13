package com.caselli_minin.springtoon.toon.spring;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ToonOutputConverterTest {

    record User(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("active") boolean active
    ) {}

    record Product(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("price") double price
    ) {}

    @Test
    public void testGetFormat_WithClass() {
        ToonOutputConverter<User> converter = new ToonOutputConverter<>(User.class);
        String format = converter.getFormat();

        assertNotNull(format);
        assertTrue(format.contains("TOON format"));
        assertTrue(format.contains("Target structure schema"));
        assertTrue(format.contains("id"));
        assertTrue(format.contains("name"));
        assertTrue(format.contains("active"));
    }

    @Test
    public void testGetFormat_WithTypeReference() {
        ToonOutputConverter<List<Product>> converter = new ToonOutputConverter<>(new TypeReference<>() {});
        String format = converter.getFormat();

        assertNotNull(format);
        assertTrue(format.contains("TOON format"));
        assertTrue(format.contains("Target structure schema"));
    }

    @Test
    public void testConvert_SingleObject() {
        ToonOutputConverter<User> converter = new ToonOutputConverter<>(User.class);

        String toonOutput = """
                id: 1
                name: Alice
                active: true
                """;

        User user = converter.convert(toonOutput);

        assertNotNull(user);
        assertEquals(1, user.id());
        assertEquals("Alice", user.name());
        assertTrue(user.active());
    }

    @Test
    public void testConvert_ArrayOfObjects() {
        ToonOutputConverter<List<User>> converter = new ToonOutputConverter<>(new TypeReference<>() {});

        String toonOutput = """
                [2]{id,name,active}:
                  1,Alice,true
                  2,Bob,false
                """;

        List<User> users = converter.convert(toonOutput);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).name());
        assertEquals("Bob", users.get(1).name());
        assertTrue(users.get(0).active());
        assertFalse(users.get(1).active());
    }

    @Test
    public void testConvert_ComplexObject() {
        ToonOutputConverter<Product> converter = new ToonOutputConverter<>(Product.class);

        String toonOutput = """
                id: 42
                name: Laptop
                price: 999.99
                """;

        Product product = converter.convert(toonOutput);

        assertNotNull(product);
        assertEquals(42, product.id());
        assertEquals("Laptop", product.name());
        assertEquals(999.99, product.price(), 0.01);
    }

    @Test
    public void testConvert_InvalidToon_ThrowsException() {
        ToonOutputConverter<User> converter = new ToonOutputConverter<>(User.class);

        String invalidToon = "this is not valid TOON format {{{}}}";

        assertThrows(RuntimeException.class, () -> converter.convert(invalidToon));
    }

    @Test
    public void testConvert_EmptyString_ThrowsException() {
        ToonOutputConverter<User> converter = new ToonOutputConverter<>(User.class);

        assertThrows(RuntimeException.class, () -> converter.convert(""));
    }
}
