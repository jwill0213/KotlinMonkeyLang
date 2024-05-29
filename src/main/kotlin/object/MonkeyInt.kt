package org.example.`object`

class MonkeyInt(var value: Int) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonkeyInt

        return value == other.value
    }

    override fun hashCode(): Int {
        return value
    }
}