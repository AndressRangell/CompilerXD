package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.data.model.TokenType
import java.util.regex.Pattern

class Lexer(private var source: String) {
    private var character: String = ""
    private var readPosition: Int = 0
    private var position: Int = 0

    init {
        readCharacter()
    }

    fun nextToken(): Token {
        skipWhitespace()
        val token: Token
        when {
            "^=$".toRegex().matches(character) -> {
                token = if (peekCharacter() == "=") {
                    makeTwoCharacterToken(TokenType.EQUALS)
                } else {
                    Token(TokenType.ASSIGN, character)
                }
            }
            "^[+]$".toRegex().matches(character) -> {
                token = Token(TokenType.PLUS, character)
            }
            "^\\s*$".toRegex().matches(character) -> {
                token = Token(TokenType.END_OF_FUNCTION, character)
            }
            "^[()]$".toRegex().matches(character) -> {
                token = when (character) {
                    "(" -> Token(TokenType.LEFT_PAREN, character)
                    ")" -> Token(TokenType.RIGHT_PAREN, character)
                    else -> Token(TokenType.ILLEGAL, character)
                }
            }
            "^[{}]$".toRegex().matches(character) -> {
                token = when (character) {
                    "{" -> Token(TokenType.LEFT_BRACE, character)
                    "}" -> Token(TokenType.RIGHT_BRACE, character)
                    else -> Token(TokenType.ILLEGAL, character)
                }
            }
            "^[,]$".toRegex().matches(character) -> {
                token = Token(TokenType.COMMA, character)
            }
            "^[;]$".toRegex().matches(character) -> {
                token = Token(TokenType.SEMI_COLON, character)
            }
            "^[\\-]$".toRegex().matches(character) -> {
                token = Token(TokenType.MINUS, character)
            }
            "^[/]$".toRegex().matches(character) -> {
                token = Token(TokenType.DIVISION, character)
            }
            "^[*]$".toRegex().matches(character) -> {
                token = Token(TokenType.MULTIPLICATION, character)
            }
            "^[<]$".toRegex().matches(character) -> {
                token = Token(TokenType.LESS_THAN, character)
            }
            "^[>]$".toRegex().matches(character) -> {
                token = Token(TokenType.GREATER_THAN, character)
            }
            "^[!]$".toRegex().matches(character) -> {
                token = if (peekCharacter() == "=") {
                    makeTwoCharacterToken(TokenType.NOT_EQUALS)
                } else {
                    Token(TokenType.NEGATION, character)
                }
            }
            isLetter(character) -> {
                val literal = readIdentifier()
                val tokenType = lookupTokenType(literal)

                return Token(tokenType, literal)
            }
            isNumber(character) -> {
                val literal = readNumber()
                return Token(TokenType.INTEGER, literal)
            }
            "^\"$".toRegex().matches(character) -> {
                val literal = readString()
                return Token(TokenType.STRING, literal)
            }
            else -> {
                token = Token(TokenType.ILLEGAL, character)
            }
        }
        readCharacter()
        return token
    }

    private fun readCharacter() {
        character = if (readPosition >= source.length) {
            ""
        } else {
            source[readPosition].toString()
        }

        position = readPosition
        readPosition++
    }

    private fun isLetter(character: String): Boolean {
        return Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ_]$", character)
    }

    private fun isNumber(character: String): Boolean {
        return Pattern.matches("^\\d$", character)
    }

    private fun makeTwoCharacterToken(tokenType: TokenType): Token {
        val prefix = character
        readCharacter()
        val suffix = character

        return Token(tokenType, "$prefix$suffix")
    }

    private fun readIdentifier(): String {
        val initialPosition = position
        var isFirstLetter = true

        while (isLetter(character) || (!isFirstLetter && isNumber(character))) {
            readCharacter()
            isFirstLetter = false
        }

        return source.substring(initialPosition, position)
    }

    private fun readNumber(): String {
        val initialPosition = position

        while (isNumber(character)) {
            readCharacter()
        }

        return source.substring(initialPosition, position)
    }

    private fun readString(): String {
        readCharacter()
        val initialPosition = position

        while (character != "\"" && readPosition <= source.length) {
            readCharacter()
        }

        val string = source.substring(initialPosition, position)
        readCharacter()
        return string
    }

    private fun peekCharacter(): String {
        return if (readPosition >= source.length) {
            ""
        } else {
            source[readPosition].toString()
        }
    }

    private fun skipWhitespace() {
        while (Pattern.matches("^\\s$", character)) {
            readCharacter()
        }
    }
}