package andres.rangel.compilerxd.data.model

sealed class Screen(val route: String) {
    object SourceCodeScreen : Screen("source_code_fragment")
    object TokensScreen : Screen("tokens_fragment")
    object SyntaxScreen : Screen("syntax_fragment")
    object OutputScreen : Screen("output_fragment")
}
