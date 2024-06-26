import org.example.lexer.Lexer
import org.example.parser.Parser
import org.example.parser.ast.expressions.*
import org.example.parser.ast.statements.ExpressionStatement
import org.example.parser.ast.statements.LetStatement
import org.example.parser.ast.statements.ReturnStatement
import org.example.parser.ast.statements.Statement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.fail

class ParserTest {

    @Test
    fun test_parseLetStatements() {
        data class LetStmtTest(val input: String, val expectedIdent: String, val expectedValue: Any)

        val tests = listOf(
            LetStmtTest("let x = 5;", "x", 5),
            LetStmtTest("let y = true;", "y", true),
            LetStmtTest("let foobar = y;", "foobar", "y")
        )

        for (testCase in tests) {
            val parser = Parser(Lexer(testCase.input))

            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertLetStatement(statements[0], testCase.expectedIdent, testCase.expectedValue)
        }
    }

    @Test
    fun test_parseReturnStatements() {
        data class ReturnStmtTest(val input: String, val expectedValue: Any)

        val tests = listOf(
            ReturnStmtTest("return 5;", 5),
            ReturnStmtTest("return y;", "y"),
            ReturnStmtTest("return false;", false)
        )

        for (testCase in tests) {
            val parser = Parser(Lexer(testCase.input))

            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")

            assertReturnStatement(statements[0], testCase.expectedValue)
        }
    }

    @Test
    fun test_parseIdentifierExpression() {
        val input = "foobar;"

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        val expStmt = statements[0] as ExpressionStatement
        assertTrue(expStmt.expression is Identifier)
        val ident = expStmt.expression as Identifier
        assertEquals(ident.getTokenLiteral(), "foobar")
    }

    @Test
    fun test_parseIntegerExpression() {
        val input = "5;"

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        val expStmt = statements[0] as ExpressionStatement
        assertIntegerLiteral(expStmt.expression, 5)
    }

    @Test
    fun test_parseStringExpression() {
        val input = "\"Hello World!\";"

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        val expStmt = statements[0] as ExpressionStatement
        assertTrue(expStmt.expression is StringLiteral)
        val strLit = expStmt.expression as StringLiteral
        assertEquals("Hello World!", strLit.value)
    }

    @Test
    fun test_parseBooleanExpression() {
        val input = "true;false;"

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(2, statements.size, "Should be 2 statements")
        assertTrue(statements[0] is ExpressionStatement)
        var expStmt = statements[0] as ExpressionStatement
        assertTrue(expStmt.expression is BoolLiteral)
        var boolExpr = expStmt.expression as BoolLiteral
        assertBoolean(boolExpr, true)

        assertTrue(statements[1] is ExpressionStatement)
        expStmt = statements[1] as ExpressionStatement
        assertTrue(expStmt.expression is BoolLiteral)
        boolExpr = expStmt.expression as BoolLiteral
        assertBoolean(boolExpr, false)
    }

    @Test
    fun test_parsePrefixExpression() {
        data class PrefixTestCase(val input: String, val operator: String, val right: Any)

        val prefixTests = listOf(
            PrefixTestCase("!5;", "!", 5),
            PrefixTestCase("-15;", "-", 15),
            PrefixTestCase("!true;", "!", true),
            PrefixTestCase("!false;", "!", false)
        )

        for (test in prefixTests) {
            val parser = Parser(Lexer(test.input))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertTrue(statements[0] is ExpressionStatement)
            val expStmt = statements[0] as ExpressionStatement
            assertTrue(expStmt.expression is PrefixExpression)
            val prefixExpr = expStmt.expression as PrefixExpression
            assertEquals(test.operator, prefixExpr.operator)
            assertLiteralExpression(prefixExpr.right, test.right)
        }
    }

    @Test
    fun test_parseInfixExpression() {
        data class InfixTestCase(val input: String, val left: Any, val operator: String, val right: Any)

        val infixTests = listOf(
            InfixTestCase("5 + 5;", 5, "+", 5),
            InfixTestCase("5 - 5;", 5, "-", 5),
            InfixTestCase("5 * 5;", 5, "*", 5),
            InfixTestCase("5 / 5;", 5, "/", 5),
            InfixTestCase("5 > 5;", 5, ">", 5),
            InfixTestCase("5 < 5;", 5, "<", 5),
            InfixTestCase("5 == 5;", 5, "==", 5),
            InfixTestCase("5 != 5;", 5, "!=", 5),
            InfixTestCase("true == true;", true, "==", true),
            InfixTestCase("true != false;", true, "!=", false),
            InfixTestCase("false == false;", false, "==", false)
        )

        for (test in infixTests) {
            val parser = Parser(Lexer(test.input))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertTrue(statements[0] is ExpressionStatement)
            val expStmt = statements[0] as ExpressionStatement

            assertNotNull(expStmt.expression)
            expStmt.expression?.let { assertInfixExpression(it, test.left, test.operator, test.right) }
        }
    }

    @Test
    fun test_operatorPrecedenceParsing() {
        val exprTests = listOf(
            Pair("-a * b", "((-a) * b)"),
            Pair("!-a", "(!(-a))"),
            Pair("a + b + c", "((a + b) + c)"),
            Pair("a + b - c", "((a + b) - c)"),
            Pair("a * b * c", "((a * b) * c)"),
            Pair("a * b / c", "((a * b) / c)"),
            Pair("a + b / c", "(a + (b / c))"),
            Pair("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
            Pair("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
            Pair("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
            Pair("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
            Pair("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
            Pair("true", "true"),
            Pair("false", "false"),
            Pair("3 > 5 == false", "((3 > 5) == false)"),
            Pair("3 < 5 == true", "((3 < 5) == true)"),
            Pair("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
            Pair("(5 + 5) * 2", "((5 + 5) * 2)"),
            Pair("2 / (5 + 5)", "(2 / (5 + 5))"),
            Pair("-(5 + 5)", "(-(5 + 5))"),
            Pair("!(true == true)", "(!(true == true))"),
            Pair("a + add(b * c) + d", "((a + add((b * c))) + d)"),
            Pair("add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))", "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"),
            Pair("add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))"),
            Pair("a * [1, 2, 3, 4][b * c] * d", "((a * ([1, 2, 3, 4][(b * c)])) * d)"),
            Pair("add(a * b[2], b[1], 2 * [1, 2][1])", "add((a * (b[2])), (b[1]), (2 * ([1, 2][1])))"),
        )

        for (test in exprTests) {
            val parser = Parser(Lexer(test.first))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            assertEquals(test.second, program.toString())
        }
    }

    @Test
    fun test_parseIfExpression() {
        val testInput = "if (x < y) { x }"

        val parser = Parser(Lexer(testInput))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is IfExpression)
        val ifExpr = (statements[0] as ExpressionStatement).expression as IfExpression

        assertNotNull(ifExpr.condition)
        ifExpr.condition?.let { assertInfixExpression(it, "x", "<", "y") }

        assertNotNull(ifExpr.consequence)
        assertEquals(1, ifExpr.consequence!!.statements.size, "Should be 1 statement in consequence")
        val consStatement = ifExpr.consequence!!.statements[0]
        assertTrue(consStatement is ExpressionStatement)
        val consExpr = (consStatement as ExpressionStatement).expression
        assertIdentifier(consExpr, "x")

        assertNull(ifExpr.alternative)
    }

    @Test
    fun test_parseIfElseExpression() {
        val testInput = "if (x > y) { x } else { y }"

        val parser = Parser(Lexer(testInput))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is IfExpression)
        val ifExpr = (statements[0] as ExpressionStatement).expression as IfExpression

        assertNotNull(ifExpr.condition)
        ifExpr.condition?.let { assertInfixExpression(it, "x", ">", "y") }

        assertNotNull(ifExpr.consequence)
        assertEquals(1, ifExpr.consequence!!.statements.size, "Should be 1 statement in consequence")
        val consStatement = ifExpr.consequence!!.statements[0]
        assertTrue(consStatement is ExpressionStatement)
        val consExpr = (consStatement as ExpressionStatement).expression
        assertIdentifier(consExpr, "x")

        assertNotNull(ifExpr.alternative)
        assertEquals(1, ifExpr.alternative!!.statements.size, "Should be 1 statement in alternative")
        val altStatement = ifExpr.alternative!!.statements[0]
        assertTrue(altStatement is ExpressionStatement)
        val altExpr = (altStatement as ExpressionStatement).expression
        assertIdentifier(altExpr, "y")
    }

    @Test
    fun test_parseFunctionExpression() {
        val testInput = "fn(x, y) { x + y; }"

        val parser = Parser(Lexer(testInput))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is FunctionExpression)
        val fnExpr = (statements[0] as ExpressionStatement).expression as FunctionExpression

        assertEquals(2, fnExpr.params.size, "Should have 2 parameters")
        assertLiteralExpression(fnExpr.params[0], "x")
        assertLiteralExpression(fnExpr.params[1], "y")

        assertEquals(1, fnExpr.body.statements.size, "Function body should have 1 statement")

        val bodyStmt = fnExpr.body.statements[0]
        assertTrue(bodyStmt is ExpressionStatement)
        val expr = (bodyStmt as ExpressionStatement).expression
        assertNotNull(expr)
        if (expr != null) {
            assertInfixExpression(expr, "x", "+", "y")
        }
    }

    @Test
    fun test_parseFunctionParameters() {
        data class FnParamTest(val input: String, val expectedParams: List<String>)

        val tests = listOf(
            FnParamTest("fn() {};", listOf()),
            FnParamTest("fn(x) {};", listOf("x")),
            FnParamTest("fn(x, y, z) {};", listOf("x", "y", "z")),
        )

        for (test in tests) {
            val parser = Parser(Lexer(test.input))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertTrue(statements[0] is ExpressionStatement)
            assertTrue((statements[0] as ExpressionStatement).expression is FunctionExpression)
            val fnExpr = (statements[0] as ExpressionStatement).expression as FunctionExpression

            assertEquals(test.expectedParams.size, fnExpr.params.size, "Expected size doesn't match")

            for (i in 0..<test.expectedParams.size) {
                assertLiteralExpression(fnExpr.params[i], test.expectedParams[i])
            }
        }
    }

    @Test
    fun test_parseCallExpression() {
        val testInput = "add(1, 2 * 3, 4 + 5);"

        val parser = Parser(Lexer(testInput))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is CallExpression)
        val callExpr = (statements[0] as ExpressionStatement).expression as CallExpression

        assertIdentifier(callExpr.fn, "add")

        assertEquals(3, callExpr.args.size, "Should have 3 params")
        assertLiteralExpression(callExpr.args[0], 1)
        assertInfixExpression(callExpr.args[1], 2, "*", 3)
        assertInfixExpression(callExpr.args[2], 4, "+", 5)
    }

    @Test
    fun test_parseCallParameters() {
        data class CallParamTest(val input: String, val expectedIdent: String, val expectedArgs: List<String>)

        val tests = listOf(
            CallParamTest("println();", "println", listOf()),
            CallParamTest("add(1);", "add", listOf("1")),
            CallParamTest("add(1, 5 * 3, -2);", "add", listOf("1", "(5 * 3)", "(-2)")),
        )

        for (test in tests) {
            val parser = Parser(Lexer(test.input))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertTrue(statements[0] is ExpressionStatement)
            assertTrue((statements[0] as ExpressionStatement).expression is CallExpression)
            val callExpr = (statements[0] as ExpressionStatement).expression as CallExpression

            assertEquals(test.expectedArgs.size, callExpr.args.size, "Expected size doesn't match")
            assertEquals(test.expectedIdent, callExpr.fn.toString())

            for (i in 0..<test.expectedArgs.size) {
                assertEquals(callExpr.args[i].toString(), test.expectedArgs[i])
            }
        }
    }

    @Test
    fun test_parseArrayLiteral() {
        val test = "[1, 2 * 2, 3 + 3]"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is ArrayLiteral)
        val arrayLiteral = (statements[0] as ExpressionStatement).expression as ArrayLiteral

        assertEquals(3, arrayLiteral.elements.size)
        assertIntegerLiteral(arrayLiteral.elements[0], 1)
        assertInfixExpression(arrayLiteral.elements[1], 2, "*", 2)
        assertInfixExpression(arrayLiteral.elements[2], 3, "+", 3)
    }

    @Test
    fun test_parseIndexExpression() {
        val test = "myArray[1 + 1]"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is IndexExpression)
        val indexExpr = (statements[0] as ExpressionStatement).expression as IndexExpression

        assertIdentifier(indexExpr.left, "myArray")
        assertInfixExpression(indexExpr.index, 1, "+", 1)
    }

    @Test
    fun test_parseHashLiteralStringKey() {
        val test = "{\"one\": 1, \"two\": 2, \"three\": 3}"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is HashLiteral)
        val hashLiteral = (statements[0] as ExpressionStatement).expression as HashLiteral

        val expectedValues = mapOf(Pair("one", 1), Pair("two", 2), Pair("three", 3))

        assertEquals(3, hashLiteral.expressionMap.size)

        hashLiteral.expressionMap.forEach { entry ->
            assertTrue(entry.key is StringLiteral)
            val expectedVal = expectedValues[entry.key.toString()]

            assertIntegerLiteral(entry.value, expectedVal!!)
        }
    }

    @Test
    fun test_parseHashLiteralIntKey() {
        val test = "{1: 3, 2: 1, 3: 2}"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is HashLiteral)
        val hashLiteral = (statements[0] as ExpressionStatement).expression as HashLiteral

        val expectedValues = mapOf(Pair(1, 3), Pair(2, 1), Pair(3, 2))

        assertEquals(3, hashLiteral.expressionMap.size)

        hashLiteral.expressionMap.forEach { entry ->
            assertTrue(entry.key is IntegerLiteral)
            val expectedVal = expectedValues[(entry.key as IntegerLiteral).value]

            assertIntegerLiteral(entry.value, expectedVal!!)
        }
    }

    @Test
    fun test_parseHashLiteralBooleanKey() {
        val test = "{true: \"false\", false: \"true\"}"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is HashLiteral)
        val hashLiteral = (statements[0] as ExpressionStatement).expression as HashLiteral

        val expectedValues = mapOf(Pair(true, "false"), Pair(false, "true"))

        assertEquals(2, hashLiteral.expressionMap.size)

        hashLiteral.expressionMap.forEach { entry ->
            assertTrue(entry.key is BoolLiteral)
            val expectedVal = expectedValues[(entry.key as BoolLiteral).value]

            assertTrue(entry.value is StringLiteral)
            assertEquals(expectedVal, (entry.value as StringLiteral).value)
        }
    }

    @Test
    fun test_parseHashLiteralWithExpressions() {
        val test = "{\"add\": 3 + 6, \"sub\": 10-7}"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is HashLiteral)
        val hashLiteral = (statements[0] as ExpressionStatement).expression as HashLiteral

        assertEquals(2, hashLiteral.expressionMap.size)

        hashLiteral.expressionMap.forEach { entry ->
            assertTrue(entry.key is StringLiteral)

            if ((entry.key as StringLiteral).value == "add") {
                assertInfixExpression(entry.value, 3, "+", 6)
            } else if ((entry.key as StringLiteral).value == "sub") {
                assertInfixExpression(entry.value, 10, "-", 7)
            }
        }
    }

    @Test
    fun test_parseEmptyHashLiteral() {
        val test = "{}"

        val parser = Parser(Lexer(test))
        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(1, statements.size, "Should be 1 statement")
        assertTrue(statements[0] is ExpressionStatement)
        assertTrue((statements[0] as ExpressionStatement).expression is HashLiteral)
        val hashLiteral = (statements[0] as ExpressionStatement).expression as HashLiteral

        assertEquals(0, hashLiteral.expressionMap.size)
    }

    private fun assertLetStatement(statement: Statement, name: String, value: Any) {
        assertTrue(statement is LetStatement)
        val letStatement: LetStatement = statement as LetStatement
        assertEquals(statement.getTokenLiteral(), "let")
        assertEquals(letStatement.name.getTokenLiteral(), name)
        assertLiteralExpression(letStatement.value, value)
    }

    private fun assertReturnStatement(statement: Statement, value: Any) {
        assertTrue(statement is ReturnStatement)
        assertEquals(statement.getTokenLiteral(), "return")
        assertLiteralExpression((statement as ReturnStatement).value, value)
    }

    private fun assertNoParserErrors(p: Parser) {
        if (p.errors.isNotEmpty()) {
            val errMsg = p.errors.joinToString(separator = "\n")
            fail("The following parser errors were found: \n $errMsg")
        }
    }

    private fun assertInfixExpression(expr: Expression?, left: Any, operator: String, right: Any) {
        assertTrue(expr is InfixExpression)
        val infixExpression = expr as InfixExpression

        assertNotNull(infixExpression.left)
        infixExpression.left?.let { assertLiteralExpression(it, left) }

        assertEquals(operator, infixExpression.operator)

        assertNotNull(infixExpression.right)
        infixExpression.right?.let { assertLiteralExpression(it, right) }
    }

    private fun assertLiteralExpression(expr: Expression?, value: Any) {
        when (value) {
            is Int -> assertIntegerLiteral(expr, value)
            is String -> assertIdentifier(expr, value)
            is Boolean -> assertBoolean(expr, value)
        }
    }

    private fun assertIntegerLiteral(expr: Expression?, value: Int) {
        assertTrue(expr is IntegerLiteral)
        val intLit = expr as IntegerLiteral
        assertEquals(value, intLit.value)
        assertEquals("$value", intLit.getTokenLiteral())
    }

    private fun assertIdentifier(expr: Expression?, value: String) {
        assertTrue(expr is Identifier)
        val ident = expr as Identifier
        assertEquals(value, ident.getTokenLiteral())
    }

    private fun assertBoolean(expr: Expression?, value: Boolean) {
        assertTrue(expr is BoolLiteral)
        val boolExpr = expr as BoolLiteral
        assertEquals(value, boolExpr.value)
        assertEquals("$value", boolExpr.getTokenLiteral())
    }
}