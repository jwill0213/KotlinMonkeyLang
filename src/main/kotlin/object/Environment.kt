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

        // If object still isn't found, check builtin functions
        if (foundObj == null) {
            foundObj = builtinFunctions[name]
        }
        return Pair(foundObj, foundObj != null)
    }

    fun set(name: String, obj: MonkeyObject): MonkeyObject {
        objectMap[name] = obj
        return obj
    }

    companion object {
        private val builtinFunctions = mapOf(
            Pair("len", MonkeyBuiltin { args: List<MonkeyObject> -> MonkeyBuiltin.checkLen(args) }),
            Pair("first", MonkeyBuiltin { args: List<MonkeyObject> -> MonkeyBuiltin.getFirst(args) }),
            Pair("rest", MonkeyBuiltin { args: List<MonkeyObject> -> MonkeyBuiltin.getRest(args) }),
            Pair("push", MonkeyBuiltin { args: List<MonkeyObject> -> MonkeyBuiltin.push(args) }),
            Pair("puts", MonkeyBuiltin { args: List<MonkeyObject> -> MonkeyBuiltin.put(args) })
        )
    }
}