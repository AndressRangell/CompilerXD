package andres.rangel.compilerxd.ui.components

import andres.rangel.compilerxd.R
import andres.rangel.compilerxd.data.model.Screen
import andres.rangel.compilerxd.ui.screens.OutputFragment
import andres.rangel.compilerxd.ui.screens.SourceCodeFragment
import andres.rangel.compilerxd.ui.screens.SyntaxFragment
import andres.rangel.compilerxd.ui.screens.TokensFragment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AppBottomNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry = navController.currentBackStackEntryAsState().value
                val currentScreen = navBackStackEntry?.destination?.route

                BottomNavigationItem(
                    selected = currentScreen == Screen.SourceCodeScreen.route,
                    onClick = {
                        navController.navigate(Screen.SourceCodeScreen.route)
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.icon_code),
                            contentDescription = "Source Code Fragment"
                        )
                    },
                    label = { Text("Source code") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.TokensScreen.route,
                    onClick = {
                        navController.navigate(Screen.TokensScreen.route)
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.icon_tokens),
                            contentDescription = "Tokens Fragment"
                        )
                    },
                    label = { Text("Tokens") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.SyntaxScreen.route,
                    onClick = {
                        navController.navigate(Screen.SyntaxScreen.route)
                    },
                    icon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.sintaxis),
                            contentDescription = "Syntax Fragment"
                        )
                    },
                    label = { Text("Syntax") }
                )
                BottomNavigationItem(
                    selected = currentScreen == Screen.OutputScreen.route,
                    onClick = {
                        navController.navigate(Screen.OutputScreen.route)
                    },
                    icon = {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.icon_output),
                            contentDescription = "Output Fragment"
                        )
                    },
                    label = { Text("Output") }
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = Screen.SourceCodeScreen.route) {
            composable(Screen.SourceCodeScreen.route) { SourceCodeFragment(navController) }
            composable(Screen.TokensScreen.route) { TokensFragment() }
            composable(Screen.SyntaxScreen.route) { SyntaxFragment() }
            composable(Screen.OutputScreen.route) { OutputFragment() }
        }
    }
}