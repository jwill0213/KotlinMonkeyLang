import org.example.Lexer
import org.example.Parser
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

    private fun assertLetStatement(statement: Statement, name: String) {
        assertTrue(statement is LetStatement)
        val letStatement: LetStatement = statement as LetStatement
        assertEquals(statement.getTokenLiteral(), "let")
        assertEquals(letStatement.name?.getTokenLiteral(), name)
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
}