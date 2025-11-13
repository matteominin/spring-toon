package com.caselli_minin.springtoon.toon.spring;

import com.caselli_minin.springtoon.toon.converter.ToonConverter;
import com.caselli_minin.springtoon.toon.converter.ToonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.lang.NonNull;

public class ToonOutputConverter<T> implements StructuredOutputConverter<T> {

    private static final String TOON_FORMAT_SPEC = """
            Your response should be in TOON format.
            Do not include any explanations, only provide a TOON compliant response following this format without deviation.

            TOON Syntax:
            - Key-value: key: value
            - Objects: key{field1,field2}: then values on next line
            - Arrays: key[count]{fields}: then comma-separated values
            - Indentation: 2 spaces per nesting level
            - Strings: quote only if containing special chars (: , [ ] { })

            Example:
            users[2]{id,name,active}:
              1,Alice,true
              2,Bob,false
            """;

    private final ObjectMapper objectMapper;
    private final Class<T> targetClass;
    private final TypeReference<T> typeReference;

    public ToonOutputConverter(Class<T> targetClass) {
        this(targetClass, new ObjectMapper());
    }

    public ToonOutputConverter(Class<T> targetClass, ObjectMapper objectMapper) {
        this.targetClass = targetClass;
        this.typeReference = null;
        this.objectMapper = objectMapper;
    }

    public ToonOutputConverter(TypeReference<T> typeReference) {
        this(typeReference, new ObjectMapper());
    }

    public ToonOutputConverter(TypeReference<T> typeReference, ObjectMapper objectMapper) {
        this.targetClass = null;
        this.typeReference = typeReference;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getFormat() {
        String schema = generateJsonSchema();
        return TOON_FORMAT_SPEC + "\n\nTarget structure schema (JSON Schema):\n" + schema;
    }

    @Override
    public T convert(@NonNull String toonOutput) {
        if (toonOutput.isBlank()) {
            throw new IllegalArgumentException("TOON output cannot be empty");
        }

        try {
            String jsonString = ToonConverter.decode(toonOutput);

            if (targetClass != null) {
                return objectMapper.readValue(jsonString, targetClass);
            } else if (typeReference != null) {
                return objectMapper.readValue(jsonString, typeReference);
            } else {
                throw new IllegalStateException("Neither targetClass nor typeReference is set");
            }
        } catch (ToonException e) {
            throw new ConversionException("Failed to decode TOON format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ConversionException("Failed to convert to target type: " + e.getMessage(), e);
        }
    }

    private String generateJsonSchema() {
        try {
            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);

            if (targetClass != null) {
                JsonSchema schema = schemaGen.generateSchema(targetClass);
                return objectMapper.writeValueAsString(schema);
            } else if (typeReference != null) {
                JsonSchema schema = schemaGen.generateSchema(objectMapper.constructType(typeReference.getType()));
                return objectMapper.writeValueAsString(schema);
            }

            return "{}";
        } catch (Exception e) {
            throw new ConversionException("Failed to generate JSON schema: " + e.getMessage(), e);
        }
    }
}
