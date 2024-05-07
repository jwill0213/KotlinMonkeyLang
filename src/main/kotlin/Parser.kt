package org.example

import org.example.ast.Program
import org.example.ast.expressions.*
import org.example.ast.statements.*

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
        Pair(TokenType.IF) { parseIfExpression() },
        Pair(TokenType.FUNCTION) { parseFunctionExpression() },
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
        Pair(TokenType.LPAREN) { function: Expression -> parseCallExpression(function) },
    )

    // For init call nextToken twice to prime the parser where both tokens are set
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

        // Parse all statements until we hit EOF token
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

    // Parse a let statement in the form of `let foo = <expr>`
    private fun parseLetStatement(): Statement? {
        val stmt = LetStatement(currToken)

        // If the next token isn't IDENT there is an error parsing
        if (!expectPeek(TokenType.IDENT)) {
            return null
        }

        // Create the Identifier using the current token. currToken.literal will be the identifier value
        stmt.name = Identifier(currToken)

        // If the next token isn't ASSIGN there is an error parsing.
        if (!expectPeek(TokenType.ASSIGN)) {
            return null
        }

        // Advance token to first token of the expression
        nextToken()

        // Parse expression to assign to the identifier
        stmt.value = parseExpression(Precedence.LOWEST)

        // If peek token is SEMICOLON, advance tokens again
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    // Parse a return statement in the form of `return <expr>`
    private fun parseReturnStatement(): Statement {
        val stmt = ReturnStatement(currToken)

        nextToken()

        // Parse expression to assign to the identifier
        stmt.value = parseExpression(Precedence.LOWEST)

        // If peek token is SEMICOLON, advance tokens again
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    // Parse an expression statement that contains one expression
    private fun parseExpressionStatement(): Statement {
        val stmt = ExpressionStatement(currToken)

        stmt.expression = parseExpression(Precedence.LOWEST)

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken()
        }

        return stmt
    }

    // Parse an expression. Precedence is passed in to handle grouping of expressions
    private fun parseExpression(precedence: Precedence): Expression? {
        // See if there is a prefix matching the current token. Integer and Ident have a prefix
        val prefix = prefixParseFnMap[currToken.tokenType]
        if (prefix == null) {
            errors.add("No prefix parse function for $currToken found")
            return null
        }

        // Call the parse method associated with the prefix
        var leftExpr = prefix.invoke()

        // While the current precedence is less than the next precedence, and we haven't reached a semicolon,
        // recursively parse each infix operator
        while (!peekTokenIs(TokenType.SEMICOLON) && precedence.rank < peekPrecedence().rank) {
            // See if there is an infix matching the current token. Integer and Ident have a prefix
            val infix = infixParseFnMap[peekToken.tokenType]
            if (infix == null) {
                errors.add("No infix parse function for $currToken found")
                return null
            }

            // Advance tokens. If the leftExpr isn't set yet invoke the infix function
            nextToken()
            if (leftExpr != null) {
                leftExpr = infix(leftExpr)
            }
        }

        return leftExpr
    }

    /**
     * Create Identifier from current token
     */
    private fun parseIdentifier(): Identifier {
        return Identifier(currToken)
    }

    /**
     * Create IntegerLiteral from current token
     */
    private fun parseIntegerLiteral(): IntegerLiteral {
        return IntegerLiteral(currToken)
    }

    /**
     * Create BoolExpression from current token
     */
    private fun parseBoolean(): BoolExpression {
        return BoolExpression(currToken)
    }

    /**
     * Create PrefixExpression and call parseExpression to recursively parse the right side of the prefix expression
     */
    private fun parsePrefixExpression(): PrefixExpression {
        val expr = PrefixExpression(currToken)

        nextToken()

        expr.right = parseExpression(Precedence.PREFIX)

        return expr
    }

    /**
     * Create InfixExpression and call parseExpression to recursively parse the right side of the infix expression
     * passing in the current precedence
     */
    private fun parseInfixExpression(leftExpr: Expression): InfixExpression {
        val expr = InfixExpression(currToken, leftExpr)

        val precedence = currPrecedence()
        nextToken()
        expr.right = parseExpression(precedence)

        return expr
    }

    /**
     * Create InfixExpression and call parseExpression to recursively parse the right side of the infix expression
     * passing in the current precedence
     */
    private fun parseCallExpression(fn: Expression): CallExpression {
        val expr = CallExpression(currToken, fn)

        expr.args = parseCallArguments()

        return expr
    }

    /**
     * Parse comma separated list of parameters for a function.
     */
    private fun parseCallArguments(): List<Expression> {
        val argList = mutableListOf<Expression>()

        if (peekTokenIs(TokenType.RPAREN)) {
            // No arguments. Advance tokens and return
            nextToken()
            return argList
        }

        // We are currently on LPAREN. Advance to next token
        nextToken()
        // Parse the first argument
        var argExpr = parseExpression(Precedence.LOWEST)
        if (argExpr != null) {
            argList.add(argExpr)
        }

        // While the token after the expression is a comma, keep parsing expressions
        while (peekTokenIs(TokenType.COMMA)) {
            // Advance twice to skip the comma
            nextToken()
            nextToken()

            // Parse expression and add it to the argList if not null
            argExpr = parseExpression(Precedence.LOWEST)
            if (argExpr != null) {
                argList.add(argExpr)
            }
        }

        // Next token should be the RPAREN at the end of the argument list
        if (!expectPeek(TokenType.RPAREN)) {
            return argList
        }

        return argList
    }

    /**
     * Parse expressions that are grouped parenthesis. To keep grouped we pass LOWEST as precedence in parseExpression
     */
    private fun parseGroupedExpression(): Expression? {
        nextToken()

        // Pass LOWEST so the whole expression surrounded by parenthesis is grouped together.
        val expr = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RPAREN)) {
            return null
        }

        return expr
    }

    /**
     * Parse if expression in the form of `if (<expr>) <consequence> else <alternative>
     * consequence and alternative are both groups of statements surrounded by braces
     */
    private fun parseIfExpression(): IfExpression? {
        // If expression should have a next token of (
        val expr = IfExpression(currToken)
        if (!expectPeek(TokenType.LPAREN)) {
            return null
        }

        // Expect peek advances token so currToken is LPAREN. Skip to next and start parsing an expression
        nextToken()
        expr.condition = parseExpression(Precedence.LOWEST)

        // After parsing the expression we should be at the RPAREN
        if (!expectPeek(TokenType.RPAREN)) {
            return null
        }

        // Next token should now be LBRACE which starts a BlockStatement
        if (!expectPeek(TokenType.LBRACE)) {
            return null
        }

        // BlockStatement should start on the LBRACE
        expr.consequence = parseBlockStatement()

        // After parsing the BlockStatement we should be on RBRACE. If statements aren't required to have an else so peek
        // next token to see if there is an alternative
        if (peekTokenIs(TokenType.ELSE)) {
            // Advance so current token is ELSE and next token should be LBRACE
            nextToken()
            if (!expectPeek(TokenType.LBRACE)) {
                return null
            }

            expr.alternative = parseBlockStatement()
        }

        return expr
    }

    private fun parseBlockStatement(): BlockStatement {
        // Current token should be LBRACE. Create block and advance tokens to start parsing the statements
        val block = BlockStatement(currToken)
        nextToken()

        // Keep parsing statements in a loop until we reach the RBRACE or EOF
        while (!currTokenIs(TokenType.RBRACE) && !currTokenIs(TokenType.EOF)) {
            val stmt = parseStatement()
            if (stmt != null) {
                block.statements.add(stmt)
            }
            nextToken()
        }

        return block
    }

    /**
     * Parse function expression in the form of `fn (<paramList>) <body>
     */
    private fun parseFunctionExpression(): FunctionExpression? {
        // Current token should be FUNCTION.
        val fn = FunctionExpression(currToken)

        // After FUNCTION token we should have LPAREN
        if (!expectPeek(TokenType.LPAREN)) {
            return null
        }

        // Parse all parameters for the function
        fn.params = parseFunctionParameters()

        // After parsing the parameters we should be on RPAREN and expect the next token to be LBRACE
        if (!expectPeek(TokenType.LBRACE)) {
            return null
        }

        // Parse the body of the function
        fn.body = parseBlockStatement()

        return fn
    }

    /**
     * Parse comma separated list of parameters for a function.
     */
    private fun parseFunctionParameters(): List<Identifier> {
        val paramList = mutableListOf<Identifier>()

        // We are currently on LPAREN. Advance to next token
        nextToken()

        // Parse the comma separated list of params until we hit the RPAREN token or EOF
        while (!currTokenIs(TokenType.RPAREN) && !currTokenIs(TokenType.EOF)) {
            paramList.add(parseIdentifier())

            // If the next token is comma, advance the token twice to be on the next param
            if (peekTokenIs(TokenType.COMMA)) {
                nextToken()
            }
            nextToken()
        }

        return paramList
    }

    /**
     * Precedence of currToken
     */
    private fun currPrecedence(): Precedence {
        return Precedence.findPrecedence(currToken.tokenType) ?: Precedence.LOWEST
    }


    /**
     * Precedence of peekToken
     */
    private fun peekPrecedence(): Precedence {
        return Precedence.findPrecedence(peekToken.tokenType) ?: Precedence.LOWEST
    }

    /**
     * Check if the currToken.tokenType matches the passed in TokenType
     */
    private fun currTokenIs(type: TokenType): Boolean {
        return currToken.tokenType == type
    }


    /**
     * Check if the peekToken.tokenType matches the passed in TokenType
     */
    private fun peekTokenIs(type: TokenType): Boolean {
        return peekToken.tokenType == type
    }


    /**
     * Check if the peekToken matches the passed in TokenType. If it does advance the tokens so the currToken is now
     * that expected TokenType and return true. If it doesn't match add an error and return false
     */
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
