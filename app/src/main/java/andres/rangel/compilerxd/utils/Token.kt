package andres.rangel.compilerxd.utils

import andres.rangel.compilerxd.data.model.TokenType

data class Token(val tokenType: TokenType, val literal: String) {
    override fun toString(): String {
        return "Type: $tokenType, Literal: $literal"
    }

    companion object {
        val TOKEN_LIST = arrayListOf<Token>()
        val ERROR_LIST = arrayListOf<String>()
        var OUTPUT = ""
        var SOURCE_CODE = ""
    }
}

fun lookupTokenType(literal: String): TokenType {
    val keywords: Map<String, TokenType> = mapOf(
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "function" to TokenType.FUNCTION,
        "return" to TokenType.RETURN,
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "var" to TokenType.VAR
    )

    return keywords[literal] ?: TokenType.IDENTIFIER
}