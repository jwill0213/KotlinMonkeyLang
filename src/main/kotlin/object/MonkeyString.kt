package org.example.`object`

class MonkeyString(var value: String) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.STRING
    }

    override fun inspect(): String {
        return value
    }
}