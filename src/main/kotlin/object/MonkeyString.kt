package org.example.`object`

class MonkeyString(var value: String) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.STRING
    }

    override fun inspect(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonkeyString

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }


}