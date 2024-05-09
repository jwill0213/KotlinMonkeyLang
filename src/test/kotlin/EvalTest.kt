import org.example.lexer.Lexer
import org.example.`object`.MonkeyBool
import org.example.`object`.MonkeyInt
import org.example.`object`.MonkeyObject
import org.example.parser.Parser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EvalTest {

    @Test
    fun test_evalIntExpression() {
        val tests = listOf(
            Pair("5", 5),
            Pair("10", 10),
            Pair("5000", 5000)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertIntegerObject(evaluatedValue, testCase.second)
        }
    }

    @Test
    fun test_evalBoolExpression() {
        val tests = listOf(
            Pair("true", true),
            Pair("false", false)
        )

        for (testCase in tests) {
            val evaluatedValue = evalProgramForTest(testCase.first)

            assertBooleanObject(evaluatedValue, testCase.second)
        }
    }

    private fun evalProgramForTest(input: String): MonkeyObject {
        val program = Parser(Lexer(input)).parseProgram()
        assertNotNull(program)
        val evaluatedValue = program.eval()
        assertNotNull(evaluatedValue)
        return evaluatedValue
    }

    private fun assertIntegerObject(obj: MonkeyObject?, expected: Int) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyInt)
        assertEquals(obj.value, expected)
    }

    private fun assertBooleanObject(obj: MonkeyObject?, expected: Boolean) {
        assertNotNull(obj)
        assertTrue(obj is MonkeyBool)
        assertEquals(obj.value, expected)
    }
}