package org.example.`object`

/**
 * Static environment so it can be accessed throughout the code
 */
class Environment(private val enclosingEnv: Environment? = null) {
    private val objectMap: MutableMap<String, MonkeyObject> = mutableMapOf()

    fun get(name: String): Pair<MonkeyObject?, Boolean> {
        var foundObj = objectMap[name]
        // If object wasn't found, check the enclosing environment
        if (foundObj == null && enclosingEnv != null) {
            foundObj = enclosingEnv.get(name).first
        }
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