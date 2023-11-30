package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.data.model.TokenType
import org.junit.Assert.assertEquals
import org.junit.Test
class LexerTest {
    @Test
    fun testIllegal() {
        val source = "¡¿@"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in source.indices) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.ILLEGAL, "¡"),
            Token(TokenType.ILLEGAL, "¿"),
            Token(TokenType.ILLEGAL, "@")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testOneCharacterOperator() {
        val source = "=+-/*<>!"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in source.indices) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.PLUS, "+"),
            Token(TokenType.MINUS, "-"),
            Token(TokenType.DIVISION, "/"),
            Token(TokenType.MULTIPLICATION, "*"),
            Token(TokenType.LESS_THAN, "<"),
            Token(TokenType.GREATER_THAN, ">"),
            Token(TokenType.NEGATION, "!")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testEof() {
        val source = "+"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until source.length + 1) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.PLUS, "+"),
            Token(TokenType.END_OF_FUNCTION, "")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testDelimiters() {
        val source = "(){},;"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in source.indices) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.LEFT_BRACE, "{"),
            Token(TokenType.RIGHT_BRACE, "}"),
            Token(TokenType.COMMA, ","),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testAssignment() {
        val source = "var cinco = 5;"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 5) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.VAR, "var"),
            Token(TokenType.IDENTIFIER, "cinco"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.INTEGER, "5"),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testFunctionDeclaration() {
        val source = """
        var suma = function(x, y) {
            x + y;
        };
    """.trimIndent()
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 16) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.VAR, "var"),
            Token(TokenType.IDENTIFIER, "suma"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.FUNCTION, "function"),
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.IDENTIFIER, "x"),
            Token(TokenType.COMMA, ","),
            Token(TokenType.IDENTIFIER, "y"),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.LEFT_BRACE, "{"),
            Token(TokenType.IDENTIFIER, "x"),
            Token(TokenType.PLUS, "+"),
            Token(TokenType.IDENTIFIER, "y"),
            Token(TokenType.SEMI_COLON, ";"),
            Token(TokenType.RIGHT_BRACE, "}"),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testFunctionCall() {
        val source = "var resultado = suma(dos, tres);"
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 10) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.VAR, "var"),
            Token(TokenType.IDENTIFIER, "resultado"),
            Token(TokenType.ASSIGN, "="),
            Token(TokenType.IDENTIFIER, "suma"),
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.IDENTIFIER, "dos"),
            Token(TokenType.COMMA, ","),
            Token(TokenType.IDENTIFIER, "tres"),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testControlStatement() {
        val source = """
        if (5 < 10) {
            return true;
        } else {
            return false;
        }
    """.trimIndent()
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 17) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.IF, "if"),
            Token(TokenType.LEFT_PAREN, "("),
            Token(TokenType.INTEGER, "5"),
            Token(TokenType.LESS_THAN, "<"),
            Token(TokenType.INTEGER, "10"),
            Token(TokenType.RIGHT_PAREN, ")"),
            Token(TokenType.LEFT_BRACE, "{"),
            Token(TokenType.RETURN, "return"),
            Token(TokenType.TRUE, "true"),
            Token(TokenType.SEMI_COLON, ";"),
            Token(TokenType.RIGHT_BRACE, "}"),
            Token(TokenType.ELSE, "else"),
            Token(TokenType.LEFT_BRACE, "{"),
            Token(TokenType.RETURN, "return"),
            Token(TokenType.FALSE, "false"),
            Token(TokenType.SEMI_COLON, ";"),
            Token(TokenType.RIGHT_BRACE, "}")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testTwoCharacterOperator() {
        val source = """
        10 == 10;
        10 != 9;
    """.trimIndent()
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 8) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.INTEGER, "10"),
            Token(TokenType.EQUALS, "=="),
            Token(TokenType.INTEGER, "10"),
            Token(TokenType.SEMI_COLON, ";"),
            Token(TokenType.INTEGER, "10"),
            Token(TokenType.NOT_EQUALS, "!="),
            Token(TokenType.INTEGER, "9"),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }

    @Test
    fun testString() {
        val source = """
        "foo";
        "Prueba de texto";
    """.trimIndent()
        val lexer = Lexer(source)

        val tokens = mutableListOf<Token>()
        for (i in 0 until 4) {
            tokens.add(lexer.nextToken())
        }

        val expectedTokens = listOf(
            Token(TokenType.STRING, "foo"),
            Token(TokenType.SEMI_COLON, ";"),
            Token(TokenType.STRING, "Prueba de texto"),
            Token(TokenType.SEMI_COLON, ";")
        )

        assertEquals(expectedTokens, tokens)
    }
}