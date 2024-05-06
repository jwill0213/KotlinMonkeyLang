import org.example.Lexer
import org.example.Parser
import org.example.ast.expressions.*
import org.example.ast.statements.ExpressionStatement
import org.example.ast.statements.LetStatement
import org.example.ast.statements.ReturnStatement
import org.example.ast.statements.Statement
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.fail

class ParserTest {

    @Test
    fun test_parseLetStatements() {
        val input = """
            let x = 5;
            let y = 10;
            let foobar = 838383;
        """.trimIndent()

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(3, statements.size, "Should be 3 statements")

        assertLetStatement(statements[0], "x")
        assertLetStatement(statements[1], "y")
        assertLetStatement(statements[2], "foobar")
    }

    @Test
    fun test_parseReturnStatements() {
        val input = """
            return 5;
            return 10;
            return 993322;
        """.trimIndent()

        val parser = Parser(Lexer(input))

        val program = parser.parseProgram()
        assertNoParserErrors(parser)
        assertNotNull(program)

        val statements = program.statements

        assertEquals(3, statements.size, "Should be 3 statements")

        for (stmt in statements) {
            assertReturnStatement(stmt)
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
        assertTrue(expStmt.expression is BoolExpression)
        var boolExpr = expStmt.expression as BoolExpression
        assertBoolean(boolExpr, true)

        assertTrue(statements[1] is ExpressionStatement)
        expStmt = statements[1] as ExpressionStatement
        assertTrue(expStmt.expression is BoolExpression)
        boolExpr = expStmt.expression as BoolExpression
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
        )

        for (test in exprTests) {
            val parser = Parser(Lexer(test.first))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            assertEquals(test.second, program.toString())
        }
    }

    private fun assertLetStatement(statement: Statement, name: String) {
        assertTrue(statement is LetStatement)
        val letStatement: LetStatement = statement as LetStatement
        assertEquals(statement.getTokenLiteral(), "let")
        assertEquals(letStatement.name?.getTokenLiteral(), name)
    }

    private fun assertReturnStatement(statement: Statement) {
        assertTrue(statement is ReturnStatement)
        assertEquals(statement.getTokenLiteral(), "return")
    }

    private fun assertNoParserErrors(p: Parser) {
        if (p.errors.isNotEmpty()) {
            val errMsg = p.errors.joinToString(separator = "\n")
            fail("The following parser errors were found: \n $errMsg")
        }
    }

    private fun assertInfixExpression(expr: Expression, left: Any, operator: String, right: Any) {
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
        assertTrue(expr is BoolExpression)
        val boolExpr = expr as BoolExpression
        assertEquals(value, boolExpr.value)
        assertEquals("$value", boolExpr.getTokenLiteral())
    }
}