package org.example.`object`

class MonkeyReturn(var value: MonkeyObject) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.RETURN_VALUE_OBJ
    }

    override fun inspect(): String {
        return value.toString()
    }
}