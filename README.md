# SpringToon

TOON format integration for Spring Boot - reduce LLM token usage by 30-60% with human-readable structured data.

## What is TOON?

TOON (Token-Oriented Object Notation) is a compact, YAML-like format designed for passing structured data to Large Language Models. It maintains full JSON compatibility while using significantly fewer tokens.

**Example:**
```
(JSON)                      (TON)
{                           name: Alice
  "name": "Alice",          age: 30
  "age": 30,                city: New York
  "city": "New York"        
}
```

## Quick Start

### Prerequisites
- Java 21+
- Maven

### Build and Run

In order to update the native TOON library, navigate to the `toon-rust` directory and compile the library following the instructions in [toon-rust/README.md](toon-rust/README.md).

## Using the ToonConverter

### In Java Code

```java
import com.caselli_minin.springtoon.toon.ToonConverter;
import com.caselli_minin.springtoon.toon.ToonException;

// JSON to TOON
String json = "{\"name\":\"Alice\",\"age\":30,\"hobbies\":[\"reading\",\"gaming\"]}";
String toon = ToonConverter.encode(json);
// Result: name: Alice\nage: 30\nhobbies: [reading, gaming]

// TOON to JSON
String toonData = "name: Alice\nage: 30\nhobbies: [reading, gaming]";
String jsonResult = ToonConverter.decode(toonData);
```

## Troubleshooting

**Library not found?** Follow the build instructions in [toon-rust/README.md](toon-rust/README.md) to compile and copy the native library.

**Build fails?** Ensure Rust toolchain is installed: https://rustup.rs/

## References

- [TOON Format](https://github.com/toon-format/toon-rust)
- [Spring Boot](https://spring.io/projects/spring-boot)