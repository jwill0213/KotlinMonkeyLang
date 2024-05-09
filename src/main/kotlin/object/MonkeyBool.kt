package org.example.`object`

class MonkeyBool(var value: Boolean) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.BOOLEAN_OBJ
    }

    override fun inspect(): String {
        return value.toString()
    }

    companion object {
        val TRUE = MonkeyBool(true)
        val FALSE = MonkeyBool(false)
    }
}