package org.example

fun main() {
    val prompt = ">> "

    print(prompt)

    var input = readln()
    while (input != "exit") {
        val lexer = Lexer(input)

        var tok = lexer.nextToken()
        while (tok.tokenType != TokenType.EOF) {
            println("Token ${tok.tokenType} Literal ${tok.literal}")
            tok = lexer.nextToken()
        }
        println()
        print(prompt)
        input = readln()
    }
}