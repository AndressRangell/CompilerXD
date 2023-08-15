package andres.rangel.compilerxd.data.model

sealed class Screen(val title: String) {
    object ProjectsScreen : Screen("Projects Fragment")
    object SourceCodeScreen : Screen("Source Code Fragment")
    object OutputScreen : Screen("Output Fragment")
    object HelpScreen : Screen("Help Fragment")
}
