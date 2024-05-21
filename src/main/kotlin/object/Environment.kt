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
        private fun checkLen(args: List<MonkeyObject>): MonkeyObject {
            if (args.size != 1) {
                //error bad arguments
                return MonkeyError("wrong number of arguments. got=${args.size}, want=1")
            }

            val argument = args[0]
            if (argument !is MonkeyString) {
                //error wrong type
                return MonkeyError("argument to 'len' not supported, got ${argument.getType()}")
            }

            return MonkeyInt(argument.value.length)
        }

        private val builtinFunctions = mapOf(Pair("len", MonkeyBuiltin { args: List<MonkeyObject> -> checkLen(args) }))
    }
}