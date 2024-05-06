package org.example.ast.statements

import org.example.Token

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
}