package org.example.ast

import org.example.ast.statements.Statement

class Program : Node {
    var statements: MutableList<Statement> = mutableListOf()

    override fun getNodeType(): String {
        return "ROOT"
    }

    override fun getTokenLiteral(): String {
        return if (statements.isNotEmpty()) {
            statements[0].getTokenLiteral()
        } else {
            ""
        }
    }

    override fun toString(): String {
        val allStatements = StringBuilder()
        for (stmt in statements) {
            allStatements.append(stmt.toString())
        }
        return allStatements.toString()
    }
}