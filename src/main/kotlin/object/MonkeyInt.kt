package org.example.`object`

class MonkeyInt(var value: Int) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.INTEGER
    }

    override fun inspect(): String {
        return value.toString()
    }
}