# TOON Java Bindings

This directory contains the Rust code that provides native bindings for the TOON format library to be used in Java via JNA.

## Building

### Prerequisites

- Rust (install from https://rust-lang.org/tools/install/)

### Build Instructions

1. **Navigate to the Rust project directory:**

```bash
cd toon-rust
```

2. **Compile the Rust library in release mode:**

```bash
cargo build --release
```

This will download dependencies and compile the library. The first build may take a few minutes.

3. **Copy the compiled library to Java resources:**

**On macOS:**
```bash
mkdir -p ../src/main/resources/darwin
cp target/release/libtoon_java.dylib ../src/main/resources/darwin/
```

**On Linux:**
```bash
mkdir -p ../src/main/resources/linux
cp target/release/libtoon_java.so ../src/main/resources/linux/
```

**On Windows:**
```bash
mkdir -p ../src/main/resources/win32
cp target/release/toon_java.dll ../src/main/resources/win32/
```

The compiled library files are located in `target/release/`:
- macOS: `libtoon_java.dylib`
- Linux: `libtoon_java.so`
- Windows: `toon_java.dll`

## Dependencies

The main dependency is the TOON format library from:
https://github.com/toon-format/toon-rust