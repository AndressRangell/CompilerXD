package andres.rangel.compilerxd

import andres.rangel.compilerxd.ui.screens.AppBottomNavigation
import andres.rangel.compilerxd.ui.theme.CompilerXDTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompilerXDTheme {
                AppBottomNavigation()
            }
        }
    }
}