package com.example.freedrawing.ui.main

import androidx.compose.runtime.Immutable
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.drawingtool.DrawingTool
import com.example.freedrawing.drawing.drawingtool.DrawingToolValue
import com.example.freedrawing.drawing.figure.Figure


class MutableUserState(
    val userId: String,
    var userName: String,
    val isLocal: Boolean,
    val history: MutableList<Figure>,
    val tools: MutableList<ToolAndStyle>,
    var currentTool: DrawingTool
) {
    fun toUserState(): UserState = UserState(
        userId,
        userName,
        isLocal,
        history,
        tools.map { tool -> tool.toPair() },
        currentTool.toDrawingToolValue()
    )
}

@Immutable
class UserState(
    val userId: String,
    val userName: String,
    val isLocal: Boolean,
    val history: List<Figure>,
    val tools: List<Pair<DrawingToolValue, PathStyle>>,
    val currentTool: DrawingToolValue
) {
    companion object {
        val default = run {
            val id = "USER_DEFAULT"
            val tools = DrawingToolValue.values().map {
                it to PathStyle.default
            }
            UserState(id, "default", true, listOf(), tools, DrawingToolValue.FreePathTool)
        }
    }
}