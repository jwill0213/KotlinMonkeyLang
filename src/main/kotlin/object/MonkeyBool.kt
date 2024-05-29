package org.example.`object`

class MonkeyBool(var value: Boolean) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.BOOLEAN
    }

    override fun inspect(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MonkeyBool

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        val TRUE = MonkeyBool(true)
        val FALSE = MonkeyBool(false)

        fun parseNativeBool(bool: Boolean): MonkeyBool {
            return if (bool) TRUE else FALSE
        }

        fun fromMonkeyObj(obj: MonkeyObject): MonkeyBool {
            return when (obj) {
                is MonkeyBool -> obj
                is MonkeyInt -> fromInt(obj)
                is MonkeyNull -> FALSE
                else -> FALSE
            }
        }

        fun negate(b: MonkeyBool?): MonkeyBool {
            return if (b?.value == true) FALSE else TRUE
        }

        fun fromInt(b: MonkeyInt?): MonkeyBool {
            return if (b == null || b.value == 0) FALSE else TRUE
        }
    }
}