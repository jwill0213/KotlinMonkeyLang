package org.example.`object`

/**
 * Static environment so it can be accessed throughout the code
 */
class Environment {
    private val objectMap: MutableMap<String, MonkeyObject> = mutableMapOf()

    fun get(name: String): Pair<MonkeyObject?, Boolean> {
        val foundObj = objectMap[name]
        return Pair(foundObj, foundObj != null)
    }

    fun set(name: String, obj: MonkeyObject): MonkeyObject {
        objectMap[name] = obj
        return obj
    }

    companion object {
        val globalEnv: Environment = Environment()
    }
}