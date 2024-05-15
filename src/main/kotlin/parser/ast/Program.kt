package org.example.parser.ast

import org.example.`object`.MonkeyError
import org.example.`object`.MonkeyObject
import org.example.`object`.MonkeyReturn
import org.example.parser.ast.statements.Statement

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

    override fun eval(): MonkeyObject? {
        var result: MonkeyObject? = null

        for (stmt in statements) {
            result = stmt.eval()

            when (result) {
                is MonkeyReturn -> return result.value
                is MonkeyError -> return result
            }
        }

        return result
    }
}