package com.example.freedrawing.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.freedrawing.ui.theme.FreeDrawingTheme


@Composable
fun FreeDrawingAlertDialog(
    title: String,
    onDismissRequest: () -> Unit,
    onPositiveAction: () -> Unit,
    subtitle: String = "",
    negativeText : String = "Cancel",
    positiveText : String = "Yes",
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(12.dp),
        title = { Text(title, style = MaterialTheme.typography.h5) },
        text = { Text(subtitle); Spacer(Modifier.height(50.dp)) },
        buttons = {
            //Fixed height in order to ignore the bottom padding
            //created by the rounded corners of the dialog
            Row(
                modifier = Modifier.fillMaxWidth().height(55.dp)
            ) {
                Button(
                    onClick = onDismissRequest,
                    content = { Text(negativeText) },
                    shape = RectangleShape,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    modifier = Modifier.weight(1f).fillMaxHeight(1f)
                )
                Button(
                    onClick = { onPositiveAction(); onDismissRequest() },
                    content = { Text(positiveText) },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f).fillMaxHeight(1f)
                )
            }

        }
    )
}


@Preview
@Composable
private fun MyAlertDialogPreview() {
    FreeDrawingTheme {
        FreeDrawingAlertDialog(
            title = "Alert title",
            subtitle = "Subtitle example",
            positiveText = "Confirm",
            onDismissRequest = {},
            onPositiveAction = {})
    }
}