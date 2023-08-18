package andres.rangel.compilerxd.data.model

sealed class Screen(val route: String) {
    object SourceCodeScreen : Screen("source_code_fragment")
    object OutputScreen : Screen("output_fragment")
    object TokensScreen : Screen("tokens_fragment")
    object HelpScreen : Screen("help_fragment")
}
