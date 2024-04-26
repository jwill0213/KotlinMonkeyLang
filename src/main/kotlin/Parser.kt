package org.example

import org.example.ast.Program
import org.example.ast.expressions.Identifier
import org.example.ast.statements.LetStatement
import org.example.ast.statements.Statement

class Parser(private val lexer: Lexer) {
    private var currToken: Token = Token(TokenType.ILLEGAL)
    private var peekToken: Token = Token(TokenType.ILLEGAL)

    // For init call nextToken twice to ensure both tokens are set
    init {
        nextToken()
        nextToken()
    }

    private fun nextToken() {
        currToken = peekToken
        peekToken = lexer.nextToken()
    }

    fun parseProgram(): Program {
        val program = Program()

        while (!currTokenIs(TokenType.EOF)) {
            val statement = parseStatement()
            if (statement != null) {
                program.statements.add(statement)
            }
            nextToken()
        }

        return program
    }

    private fun parseStatement(): Statement? {
        return when (currToken.tokenType) {
            TokenType.LET -> parseLetStatement()
            else -> null
        }
    }

    private fun parseLetStatement(): Statement? {
        val stmt = LetStatement(currToken)

        if (!expectPeek(TokenType.IDENT)) {
            return null
        }

        stmt.name = Identifier(currToken)

        if (!expectPeek(TokenType.ASSIGN)) {
            return null
        }

        // TODO skip expression for now, read until semicolon
        while (currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun currTokenIs(type:TokenType): Boolean {
        return currToken.tokenType == type
    }

    private fun peekTokenIs(type:TokenType): Boolean {
        return peekToken.tokenType == type
    }

    private fun expectPeek(type: TokenType): Boolean {
        if (peekTokenIs(type)) {
            nextToken()
            return true
        } else {
            return false
        }
    }
}