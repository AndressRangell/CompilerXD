package andres.rangel.compilerxd

import andres.rangel.compilerxd.ui.components.AppBottomNavigation
import andres.rangel.compilerxd.ui.theme.CompilerXDTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi

@ExperimentalFoundationApi
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