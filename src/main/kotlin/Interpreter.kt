package org.example

import org.example.lexer.Lexer
import org.example.`object`.Environment
import org.example.parser.Parser

fun main() {
    val prompt = ">> "
    val MONKEY_FACE = """
            __,__
   .--.  .-"     "-.  .--.
  / .. \/  .-. .-.  \/ .. \
 | |  '|  /   Y   \  |'  | |
 | \   \  \ 0 | 0 /  /   / |
  \ '- ,\.-""${'"'}${'"'}${'"'}${'"'}${'"'}-./, -' /
   ''-' /_   ^ ^   _\ '-''
       |  \._   _./  |
       \   \ '~' /   /
        '._ '-=-' _.'
           '-----'
    """.trimIndent()

    print(prompt)
    var input = readln()
    while (input != "exit") {
        val lexer = Lexer(input)
        val parser = Parser(lexer)

        val program = parser.parseProgram()
        if (parser.errors.isNotEmpty()) {
            println(MONKEY_FACE)
            println("Woops! We ran into some monkey business here!\n")
            println("parser errors:\n")
            for (e in parser.errors) {
                println("\t $e \n")
            }
        } else {
            val evaluated = program.eval(Environment.globalEnv)
            if (evaluated != null) {
                println(evaluated.inspect())
            }
        }

        print(prompt)
        input = readln()
    }
}