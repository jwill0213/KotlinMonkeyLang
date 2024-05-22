package org.example.`object`

class MonkeyArray(var elements: List<MonkeyObject>) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.ARRAY
    }

    override fun inspect(): String {
        return "[${elements.joinToString { it.toString() }}]"
    }
}