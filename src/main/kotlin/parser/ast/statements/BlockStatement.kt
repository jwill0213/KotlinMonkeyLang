package org.example.parser.ast.statements

import org.example.lexer.Token
import org.example.`object`.MonkeyObject
import org.example.`object`.MonkeyReturn

class BlockStatement(private val token: Token) : Statement() {
    var statements: MutableList<Statement> = mutableListOf()

    override fun getTokenLiteral(): String {
        return token.literal
    }

    override fun toString(): String {
        val allStatements = StringBuilder()
        for (stmt in statements) {
            allStatements.append(stmt.toString())
        }
        return allStatements.toString()
    }

    override fun eval(): MonkeyObject? {
        var result: MonkeyObject? = null

        for (stmt in statements) {
            result = stmt.eval()

            if (result is MonkeyReturn) {
                return result
            }
        }

        return result
    }
}