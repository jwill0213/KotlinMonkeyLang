package org.example.`object`

class MonkeyBuiltin(val func: (List<MonkeyObject>) -> MonkeyObject) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.BUILTIN
    }

    override fun inspect(): String {
        return "builtin function"
    }

    companion object {
        fun checkLen(args: List<MonkeyObject>): MonkeyObject {
            if (args.size != 1) {
                //error bad arguments
                return MonkeyError("wrong number of arguments for 'len'. got=${args.size}, want=1")
            }

            return when (val arg = args[0]) {
                is MonkeyString -> MonkeyInt(arg.value.length)
                is MonkeyArray -> MonkeyInt(arg.elements.size)
                else -> MonkeyError("argument to 'len' not supported, got ${arg.getType()}")
            }
        }

        fun getFirst(args: List<MonkeyObject>): MonkeyObject {
            if (args.size != 1) {
                //error bad arguments
                return MonkeyError("wrong number of arguments for 'first'. got=${args.size}, want=1")
            }

            val arg = args[0]

            if (arg !is MonkeyArray) {
                return MonkeyError("argument to 'first' not supported, got ${arg.getType()}")
            }

            if (arg.elements.isNotEmpty()) {
                return arg.elements[0]
            }

            return MonkeyNull.NULL
        }

        fun getRest(args: List<MonkeyObject>): MonkeyObject {
            if (args.size != 1) {
                //error bad arguments
                return MonkeyError("wrong number of arguments for 'rest'. got=${args.size}, want=1")
            }

            val arg = args[0]

            if (arg !is MonkeyArray) {
                return MonkeyError("argument to 'rest' not supported, got ${arg.getType()}")
            }

            if (arg.elements.isNotEmpty()) {
                return MonkeyArray(arg.elements.subList(1, arg.elements.size))
            }

            return MonkeyNull.NULL
        }

        fun push(args: List<MonkeyObject>): MonkeyObject {
            if (args.size != 2) {
                //error bad arguments
                return MonkeyError("wrong number of arguments for 'push'. got=${args.size}, want=2")
            }

            val arg = args[0]

            if (arg !is MonkeyArray) {
                return MonkeyError("first argument to 'push' must be ${ObjectType.ARRAY}, got ${arg.getType()}")
            }

            return MonkeyArray(listOf(*arg.elements.toTypedArray(), args[1]))
        }

        fun put(args: List<MonkeyObject>): MonkeyObject {
            for (arg in args) {
                println(arg.inspect())
            }

            return MonkeyNull.NULL
        }
    }
}