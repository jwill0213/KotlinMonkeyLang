package org.example.`object`

class MonkeyHash(var objMap: HashMap<Int, MonkeyObject>) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.HASH
    }

    override fun inspect(): String {
        val pairs = objMap.entries.map { "${it.key}:${it.value}" }
        return "{${pairs.joinToString { it }}}"
    }
}