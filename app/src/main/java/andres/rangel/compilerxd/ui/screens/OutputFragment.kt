package andres.rangel.compilerxd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun OutputFragment() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header con título y botón de eliminar
        TopAppBar(
            title = { Text(text = "Output") },
            actions = {
                IconButton(onClick = { /* Agregar lógica para eliminar */ }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        )
        TextField(
            value = "",
            onValueChange = { /* Agregar lógica para cambiar el valor de salida */ },
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.fillMaxSize(),
            maxLines = Int.MAX_VALUE,
            readOnly = true
        )
    }
}