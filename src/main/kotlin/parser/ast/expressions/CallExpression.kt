package org.example.parser.ast.expressions

import org.example.lexer.Token
import org.example.`object`.*

class CallExpression(private val token: Token, val fn: Expression) : Expression() {
    var args: List<Expression> = listOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        return "${fn.toString()}(${args.joinToString { it.toString() }})"
    }

    override fun eval(env: Environment): MonkeyObject? {
        val func = fn.eval(env)
        if (func is MonkeyError) {
            return func
        }

        val argObjects = mutableListOf<MonkeyObject>()

        for (arg in args) {
            val evalArg = arg.eval(env)
            if (evalArg is MonkeyError) {
                return evalArg
            }
            argObjects.add(evalArg!!)
        }

        if (argObjects.size == 1 && argObjects[0] is MonkeyError) {
            return argObjects[0]
        }

        when (func) {
            is MonkeyFunction -> {
                val funcEnv = Environment(env)

                // Add all arguments to the environment
                func.params.forEachIndexed { index, param -> funcEnv.set(param.getTokenLiteral(), argObjects[index]) }

                // Evaluate the function
                val functionReturn = func.body.eval(funcEnv)
                // Unwrap a return value, so it doesn't stop execution of the parent code
                return if (functionReturn is MonkeyReturn) {
                    functionReturn.value
                } else {
                    functionReturn
                }
            }

            is MonkeyBuiltin -> {
                return func.func(argObjects)
            }

            else -> return MonkeyError("not a function: ${func?.getType()}")
        }
    }
}