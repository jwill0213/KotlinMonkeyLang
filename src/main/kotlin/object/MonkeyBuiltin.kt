package org.example.`object`

class MonkeyBuiltin(val func: (List<MonkeyObject>) -> MonkeyObject) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.BUILTIN
    }

    override fun inspect(): String {
        return "builtin function"
    }
}