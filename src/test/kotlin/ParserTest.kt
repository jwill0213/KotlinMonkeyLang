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
        assertTrue(expStmt.expression is IntegerLiteral)
        val intExp = expStmt.expression as IntegerLiteral
        assertIntegerLiteral(intExp, 5)
    }

    @Test
    fun test_parsePrefixExpression() {
        data class PrefixTestCase(val input: String, val operator: String, val right: Int)

        val prefixTests = listOf(
            PrefixTestCase("!5;", "!", 5),
            PrefixTestCase("-15;", "-", 15)
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
            assertIntegerLiteral(prefixExpr.right as IntegerLiteral, test.right)
        }
    }

    @Test
    fun test_parseInfixExpression() {
        data class InfixTestCase(val input: String, val left: Int, val operator: String, val right: Int)

        val infixTests = listOf(
            InfixTestCase("5 + 5;", 5, "+", 5),
            InfixTestCase("5 - 5;", 5, "-", 5),
            InfixTestCase("5 * 5;", 5, "*", 5),
            InfixTestCase("5 / 5;", 5, "/", 5),
            InfixTestCase("5 > 5;", 5, ">", 5),
            InfixTestCase("5 < 5;", 5, "<", 5),
            InfixTestCase("5 == 5;", 5, "==", 5),
            InfixTestCase("5 != 5;", 5, "!=", 5)
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
            assertTrue(expStmt.expression is InfixExpression)
            val infixExpr = expStmt.expression as InfixExpression
            assertIntegerLiteral(infixExpr.left as IntegerLiteral, test.left)
            assertEquals(test.operator, infixExpr.operator)
            assertIntegerLiteral(infixExpr.right as IntegerLiteral, test.right)
        }
    }

    @Test
    fun test_parseExpressionTests() {
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
        )

        for (test in exprTests) {
            val parser = Parser(Lexer(test.first))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

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

    private fun assertIntegerLiteral(exp: Expression, value: Int) {
        assertTrue(exp is IntegerLiteral)
        val intLit = exp as IntegerLiteral
        assertEquals(value, intLit.value)
        assertEquals("$value", intLit.getTokenLiteral())
    }
}