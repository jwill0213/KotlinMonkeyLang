package org.example.ast.expressions

import org.example.ast.Node

abstract class Expression : Node {
    override fun getNodeType(): String {
        return "EXPRESSION"
    }

    abstract override fun getTokenLiteral(): String

    abstract override fun toString(): String
}