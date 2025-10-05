package org.ghost.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.ghost.expensetracker.core.utils.toHexCode

@Composable
fun ColorPickerDialog(
    color: Color,
    onDismissRequest: () -> Unit,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    // Controller to manage and remember the color picker's state
    val controller = rememberColorPickerController()

    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // The main HSV color picker
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    controller = controller,
                    initialColor = color
                    // Optionally, you can listen for color changes in real-time
                    // onColorChanged = { colorEnvelope -> /* ... */ }
                )

                Spacer(Modifier.height(16.dp))

                // A slider to control the brightness of the selected color
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    controller = controller,
                )

                Spacer(Modifier.height(16.dp))

                // A row to display the selected color and its hex code
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AlphaTile(
                        modifier = Modifier
                            .width(80.dp)
                            .height(35.dp),
                        controller = controller
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(text = "#${controller.selectedColor.value.toHexCode()}")
                }


                Spacer(Modifier.height(16.dp))

                // Action buttons for confirming or dismissing the dialog
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Dismiss")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            // Pass the selected color back and dismiss
                            onColorSelected(controller.selectedColor.value)
                            onDismissRequest()
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}