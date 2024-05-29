package org.example.`object`

enum class ObjectType(val value: String) {
    INTEGER("INTEGER_OBJ"),
    BOOLEAN("BOOLEAN_OBJ"),
    NULL("NULL_OBJ"),
    RETURN_VALUE("RETURN_VALUE_OBJ"),
    ERROR("ERROR_OBJ"),
    FUNCTION("FUNCTION_OBJ"),
    BUILTIN("BUILTIN_OBJ"),
    STRING("STRING_OBJ"),
    ARRAY("ARRAY_OBJ"),
    HASH("HASH_OBJ");

    fun isHashable(): Boolean {
        return this == INTEGER || this == BOOLEAN || this == STRING
    }
}