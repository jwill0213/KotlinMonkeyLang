package org.example.`object`

class MonkeyNull : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.NULL_OBJ
    }

    override fun inspect(): String {
        return "null"
    }

    companion object {
        val NULL = MonkeyNull()
    }
}