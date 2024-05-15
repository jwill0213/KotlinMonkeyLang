package org.example.`object`

class MonkeyError(val message: String) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "ERROR: $message"
    }
}