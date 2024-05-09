package org.example.parser.ast.expressions

import org.example.parser.ast.Node

abstract class Expression : Node {
    override fun getNodeType(): String {
        return "EXPRESSION"
    }

    abstract override fun getTokenLiteral(): String

    abstract override fun toString(): String
}