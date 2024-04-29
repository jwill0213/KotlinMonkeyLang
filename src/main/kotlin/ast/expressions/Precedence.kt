package org.example.ast.expressions

enum class Precedence(val rank: Int) {
    LOWEST(1),
    EQUALS(2),
    LESSGREATER(3),
    SUM(4),
    PRODUCT(5),
    PREFIX(6),
    CALL(7)
}