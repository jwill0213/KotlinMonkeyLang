package org.example

import org.example.ast.Program
import org.example.ast.expressions.*
import org.example.ast.statements.ExpressionStatement
import org.example.ast.statements.LetStatement
import org.example.ast.statements.ReturnStatement
import org.example.ast.statements.Statement

class Parser(private val lexer: Lexer) {
    var errors: MutableList<String> = mutableListOf()

    private var currToken: Token = Token(TokenType.ILLEGAL)
    private var peekToken: Token = Token(TokenType.ILLEGAL)

    // Map of token type to prefix parse function
    private val prefixParseFnMap = mapOf(
        Pair(TokenType.IDENT) { parseIdentifier() },
        Pair(TokenType.INT) { parseIntegerLiteral() },
        Pair(TokenType.TRUE) { parseBoolean() },
        Pair(TokenType.FALSE) { parseBoolean() },
        Pair(TokenType.BANG) { parsePrefixExpression() },
        Pair(TokenType.MINUS) { parsePrefixExpression() },
        Pair(TokenType.LPAREN) { parseGroupedExpression() },
    )

    // Map of token type to infix parse function
    private val infixParseFnMap = mapOf(
        Pair(TokenType.PLUS) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.MINUS) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.SLASH) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.ASTERISK) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.EQ) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.NOT_EQ) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.LT) { left: Expression -> parseInfixExpression(left) },
        Pair(TokenType.GT) { left: Expression -> parseInfixExpression(left) },
    )

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
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
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
        while (!currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseReturnStatement(): Statement {
        val stmt = ReturnStatement(currToken)

        nextToken()

        // TODO skip expression for now, read until semicolon
        while (!currTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseExpressionStatement(): Statement {
        val stmt = ExpressionStatement(currToken)

        stmt.expression = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        val prefix = prefixParseFnMap[currToken.tokenType]

        if (prefix == null) {
            errors.add("No prefix parse function for $currToken found")
            return null
        }

        var leftExp = prefix.invoke()

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.rank < peekPrecedence().rank) {
            val infix = infixParseFnMap[peekToken.tokenType]

            if (infix == null) {
                errors.add("No infix parse function for $currToken found")
                return null
            }

            nextToken()
            if (leftExp != null) {
                leftExp = infix(leftExp)
            }
        }

        return leftExp
    }

    private fun parseIdentifier(): Expression {
        return Identifier(currToken)
    }

    private fun parseIntegerLiteral(): Expression {
        return IntegerLiteral(currToken)
    }

    private fun parseBoolean(): Expression {
        return BoolExpression(currToken)
    }

    private fun parsePrefixExpression(): Expression {
        val expr = PrefixExpression(currToken)

        nextToken()

        expr.right = parseExpression(Precedence.PREFIX)

        return expr
    }

    private fun parseInfixExpression(leftExpr: Expression): Expression {
        val expr = InfixExpression(currToken, leftExpr)

        val precedence = currPrecedence()
        nextToken()
        expr.right = parseExpression(precedence)

        return expr
    }

    private fun parseGroupedExpression(): Expression? {
        nextToken()

        val expr = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RPAREN)) {
            return null
        }

        return expr
    }

    private fun currPrecedence(): Precedence {
        return Precedence.findPrecedence(currToken.tokenType) ?: Precedence.LOWEST
    }

    private fun peekPrecedence(): Precedence {
        return Precedence.findPrecedence(peekToken.tokenType) ?: Precedence.LOWEST
    }

    private fun currTokenIs(type: TokenType): Boolean {
        return currToken.tokenType == type
    }

    private fun peekTokenIs(type: TokenType): Boolean {
        return peekToken.tokenType == type
    }

    private fun expectPeek(type: TokenType): Boolean {
        if (peekTokenIs(type)) {
            nextToken()
            return true
        } else {
            errors.add("Expected next token to be $type but was ${peekToken.tokenType}")
            return false
        }
    }
}
