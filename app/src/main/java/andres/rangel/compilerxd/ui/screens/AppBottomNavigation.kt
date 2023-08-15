package andres.rangel.compilerxd.ui.screens

import andres.rangel.compilerxd.data.model.Screen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@ExperimentalMaterialApi
@Composable
fun AppBottomNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ProjectsScreen) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = currentScreen == Screen.ProjectsScreen,
                    onClick = { currentScreen = Screen.ProjectsScreen },
                    icon = { Icon(Icons.Default.List, contentDescription = "Projects Fragment") },
                    label = { Text("Projects") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.SourceCodeScreen,
                    onClick = { currentScreen = Screen.SourceCodeScreen },
                    icon = { Icon(Icons.Default.List, contentDescription = "Source Code Fragment") },
                    label = { Text("Source code") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.OutputScreen,
                    onClick = { currentScreen = Screen.OutputScreen },
                    icon = { Icon(Icons.Default.List, contentDescription = "Output Fragment") },
                    label = { Text("Output") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.HelpScreen,
                    onClick = { currentScreen = Screen.HelpScreen },
                    icon = { Icon(Icons.Default.List, contentDescription = "Help Fragment") },
                    label = { Text("Help") }
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentScreen) {
            Screen.ProjectsScreen -> ProjectsFragment()
            Screen.SourceCodeScreen -> SourceCodeFragment(modifier)
            Screen.OutputScreen -> OutputFragment(modifier)
            Screen.HelpScreen -> HelpFragment(modifier)
        }
    }
}

@Composable
fun SourceCodeFragment(modifier: Modifier = Modifier) {
    Text("Contenido del Fragmento 2", modifier = modifier)
}

@Composable
fun OutputFragment(modifier: Modifier = Modifier) {
    Text("Contenido del Fragmento 3", modifier = modifier)
}

@Composable
fun HelpFragment(modifier: Modifier = Modifier) {
    Text("Contenido del Fragmento 4", modifier = modifier)
}