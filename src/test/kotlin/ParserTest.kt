import org.example.Lexer
import org.example.Parser
import org.example.ast.expressions.Expression
import org.example.ast.expressions.Identifier
import org.example.ast.expressions.IntegerLiteral
import org.example.ast.expressions.PrefixExpression
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
        val prefixTests = listOf(
            Triple("!5;", "!", 5),
            Triple("-15;", "-", 15)
        )

        for (test in prefixTests) {
            val parser = Parser(Lexer(test.first))
            val program = parser.parseProgram()
            assertNoParserErrors(parser)
            assertNotNull(program)

            val statements = program.statements

            assertEquals(1, statements.size, "Should be 1 statement")
            assertTrue(statements[0] is ExpressionStatement)
            val expStmt = statements[0] as ExpressionStatement
            assertTrue(expStmt.expression is PrefixExpression)
            assertIntegerLiteral(expStmt.expression as PrefixExpression, test.third)
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