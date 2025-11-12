use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use serde_json::Value;

/// Encodes a JSON string into TOON format
///
/// # Safety
/// This function is intended to be called from Java via JNA.
/// The caller must ensure the input is a valid null-terminated C string.
/// The returned pointer must be freed using `toon_free_string`.
#[no_mangle]
pub unsafe extern "C" fn toon_encode(json_input: *const c_char) -> *mut c_char {
    if json_input.is_null() {
        return std::ptr::null_mut();
    }

    let c_str = match CStr::from_ptr(json_input).to_str() {
        Ok(s) => s,
        Err(_) => return create_error_string("Invalid UTF-8 input"),
    };

    // Parse JSON
    let json_value: Value = match serde_json::from_str(c_str) {
        Ok(v) => v,
        Err(e) => return create_error_string(&format!("JSON parse error: {}", e)),
    };

    // Encode to TOON format
    match toon_format::encode(&json_value, &toon_format::EncodeOptions::default()) {
        Ok(toon_string) => match CString::new(toon_string) {
            Ok(c_string) => c_string.into_raw(),
            Err(_) => create_error_string("Failed to create C string"),
        },
        Err(e) => create_error_string(&format!("TOON encode error: {}", e)),
    }
}

/// Decodes a TOON format string into JSON
///
/// # Safety
/// This function is intended to be called from Java via JNA.
/// The caller must ensure the input is a valid null-terminated C string.
/// The returned pointer must be freed using `toon_free_string`.
#[no_mangle]
pub unsafe extern "C" fn toon_decode(toon_input: *const c_char) -> *mut c_char {
    if toon_input.is_null() {
        return std::ptr::null_mut();
    }

    let c_str = match CStr::from_ptr(toon_input).to_str() {
        Ok(s) => s,
        Err(_) => return create_error_string("Invalid UTF-8 input"),
    };

    // Decode from TOON format
    match toon_format::decode(c_str, &toon_format::DecodeOptions::default()) {
        Ok(json_value) => {
            // Convert to JSON string
            match serde_json::to_string_pretty(&json_value) {
                Ok(json_string) => match CString::new(json_string) {
                    Ok(c_string) => c_string.into_raw(),
                    Err(_) => create_error_string("Failed to create C string"),
                },
                Err(e) => create_error_string(&format!("JSON serialize error: {}", e)),
            }
        }
        Err(e) => create_error_string(&format!("TOON decode error: {}", e)),
    }
}

/// Frees a string that was allocated by Rust and returned to Java
///
/// # Safety
/// This function must only be called with pointers that were returned
/// by `toon_encode` or `toon_decode`. The pointer must not be used after this call.
#[no_mangle]
pub unsafe extern "C" fn toon_free_string(s: *mut c_char) {
    if !s.is_null() {
        let _ = CString::from_raw(s);
    }
}

/// Helper function to create an error message as a C string
fn create_error_string(msg: &str) -> *mut c_char {
    let error_msg = format!("ERROR: {}", msg);
    match CString::new(error_msg) {
        Ok(c_string) => c_string.into_raw(),
        Err(_) => std::ptr::null_mut(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::ffi::CString;

    #[test]
    fn test_encode_decode_roundtrip() {
        let json = r#"{"name": "test", "value": 42}"#;
        let json_cstring = CString::new(json).unwrap();

        unsafe {
            // Encode
            let toon_ptr = toon_encode(json_cstring.as_ptr());
            assert!(!toon_ptr.is_null());

            let toon_str = CStr::from_ptr(toon_ptr).to_str().unwrap();
            println!("Encoded TOON: {}", toon_str);

            // Decode
            let json_ptr = toon_decode(toon_ptr);
            assert!(!json_ptr.is_null());

            let json_result = CStr::from_ptr(json_ptr).to_str().unwrap();
            println!("Decoded JSON: {}", json_result);

            // Cleanup
            toon_free_string(toon_ptr);
            toon_free_string(json_ptr);
        }
    }
}