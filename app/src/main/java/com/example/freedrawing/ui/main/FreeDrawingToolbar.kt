package com.example.freedrawing.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.freedrawing.R
import com.example.freedrawing.drawing.drawingtool.DrawingToolValue
import com.example.freedrawing.ui.theme.FreeDrawingTheme

private val cornerRadius = 16.dp
private val iconSize = 48.dp


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun FreeDrawingToolbar(
    userState: UserState,
    modifier: Modifier = Modifier,
    isMultiplayerEnabled: Boolean = false,
    onToolSelected: (DrawingToolValue) -> Unit = {},
    onResetScale: () -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onClear: () -> Unit = {},
    onMultiplayer: () -> Unit = {}
) {

    var showAdditionalToolsBox by remember { mutableStateOf(false) }

    Box(modifier) {
        ///////////////////////////////////////////////////////////////////////////
        // Row of buttons with global actions like cancel, etc
        ///////////////////////////////////////////////////////////////////////////

        Row(
            Modifier.background(
                MaterialTheme.colors.surface.copy(alpha = 0.98f),
                RoundedCornerShape(cornerRadius)
            )
        ) {
            Spacer(Modifier.weight(1f))

            ToolbarButton(onClick = onResetScale) {
                Text("1:1", fontWeight = FontWeight.Black)
            }

            ToolbarButton(onClick = onPreviousStep) {
                Icon(painterResource(R.drawable.ic_undo_24), "Undo")
            }

            ToolbarButton(onClick = onClear) {
                Icon(painterResource(R.drawable.ic_eraser_24), "Clean")
            }

            ToolbarButton(onClick = onMultiplayer) {
                Box {
                    Icon(
                        painter = painterResource(R.drawable.ic_people_24),
                        contentDescription = "Multiplayer",
                        tint =
                        if (isMultiplayerEnabled)
                            MaterialTheme.colors.primary
                        else
                            LocalContentColor.current
                    )

                }
            }

        }


        ///////////////////////////////////////////////////////////////////////////
        // Row of buttons for choosing drawing tools
        ///////////////////////////////////////////////////////////////////////////

        Row(
            Modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(MaterialTheme.colors.surface.copy(alpha = 0.98f))
                .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
        ) {

            val toolsToShow =
                if (showAdditionalToolsBox) userState.tools
                else userState.tools.subList(0, 2)


            LazyVerticalGrid(
                GridCells.Fixed(2),
                Modifier.width(iconSize.times(2)).animateContentSize()
            ) {

                items(toolsToShow) { tool ->
                    DrawingToolButton(
                        tool = tool.first,
                        checked = tool.first == userState.currentTool,
                        onClick = { onToolSelected(tool.first) })
                }
            }


            if (userState.tools.size >= 2) {
                ToolbarButton(
                    onClick = { showAdditionalToolsBox = !showAdditionalToolsBox },
                ) {
                    Icon(painterResource(R.drawable.ic_expand_more_24), "Expand tools")
                }
            }


        }


    } //End of global box


}

/**
 * Toggleable button used for choosing a drawing tool. The color of the icon changes
 * when [checked] is `true`
 */
@Composable
fun DrawingToolButton(
    tool: DrawingToolValue,
    checked: Boolean,
    onClick: (Boolean) -> Unit
) {

    val iconTint by animateColorAsState(
        if (checked) MaterialTheme.colors.primary else Color.Black
    )

    Box(
        Modifier
            .size(iconSize)
            .clip(RoundedCornerShape(cornerRadius))
            .toggleable(
                value = checked,
                role = Role.RadioButton,
                onValueChange = onClick
            )
            .padding(12.dp)
    ) {
        Icon(
            painterResource(tool.icon()),
            tool.description(),
            tint = iconTint
        )
    }
}

/**
 * Clickable button for the toolbar including the basic rounded corner style
 */
@Composable
fun ToolbarButton(
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) =
    Box(
        Modifier
            .size(iconSize)
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(12.dp),
        content = content
    )


@Preview
@Composable
private fun FreeDrawingToolbarPreview() {
    FreeDrawingTheme {
        FreeDrawingToolbar(UserState.default, isMultiplayerEnabled = true)
    }
}



