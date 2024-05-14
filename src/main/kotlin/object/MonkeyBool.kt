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

        fun parseNativeBool(bool: Boolean): MonkeyBool {
            return if (bool) TRUE else FALSE
        }

        fun negate(b: MonkeyBool?): MonkeyBool {
            return if (b?.value == true) FALSE else TRUE
        }

        fun fromInt(b: MonkeyInt?): MonkeyBool {
            return if (b == null || b.value == 0) FALSE else TRUE
        }
    }
}