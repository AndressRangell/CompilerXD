package andres.rangel.compilerxd.ui.screens

import andres.rangel.compilerxd.data.model.FileItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@ExperimentalMaterialApi
@Composable
fun ProjectsFragment() {
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    val fileList = List(5) { index -> FileItem("File $index") }

    Column {
        TopAppBar(
            title = { Text(text = "Projects", fontSize = 20.sp) }
        )

        LazyColumn {
            items(fileList.size) { index ->
                val isSelected = index in selectedItems

                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) Color.LightGray else Color.White)
                        .selectable(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    selectedItems -= index
                                } else {
                                    selectedItems += index
                                }
                            }
                        ),
                    icon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected"
                            )
                        }
                    },
                    text = { Text(text = fileList[index].name) },
                    trailing = {
                        if (isSelected) {
                            IconButton(
                                onClick = {
                                    // Handle delete or rename action here
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}