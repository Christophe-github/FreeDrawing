package com.example.freedrawing.ui.main

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freedrawing.drawing.PathStyle
import com.example.freedrawing.drawing.drawingtool.*
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ConcurrentHashMap


data class ToolAndStyle(var tool: DrawingToolValue, var style: PathStyle) {
    fun toPair() = tool to style
}


class FreeDrawingViewModel : ViewModel(), InstructionsReceiver {

    private var lastRefreshTime = System.currentTimeMillis()
    private val __globalState = ConcurrentHashMap<String, MutableUserState>()

    private val _globalState = MutableStateFlow(listOf<UserState>())
    val globalState: Flow<List<UserState>> = _globalState

    val localPlayerId = UUID.randomUUID().toString().run { replace("-", "") }
    private var multi: MultiPlayerService = MultiPlayerServiceProxy()


    val connectionState: Flow<MultiPlayerState> = multi.connectionState.stateIn(
        viewModelScope,
        initialValue = multi.getConnectionState(),
        started = SharingStarted.WhileSubscribed(5000)
    )

    val connectionErrorMsg: Flow<String?> = multi.connectionErrorMsg.stateIn(
        viewModelScope,
        initialValue = multi.getErrorMsg(),
        started = SharingStarted.WhileSubscribed(5000)
    )


    private fun updateUI(forceRefresh: Boolean = false) {
        synchronized(this) {
            val newTime = System.currentTimeMillis()
            if (newTime - lastRefreshTime < 16 && !forceRefresh)
                return

            lastRefreshTime = newTime

            _globalState.value = __globalState.values.map { it.toUserState() }
        }
    }

    private fun createMutableUserState(userId: String): MutableUserState {
        val isLocal = userId == localPlayerId

        val userHistory = mutableListOf<Figure>()
        val userTools = DrawingToolValue.values().map {
            ToolAndStyle(it, PathStyle.default)
        }.toMutableList()

        val currentTool = FreePathTool(userId).apply {
            if (isLocal)
                setInstructionsReceiver(this@FreeDrawingViewModel)
        }

        return MutableUserState(
            userId,
            "",
            isLocal,
            userHistory,
            userTools,
            currentTool
        )
    }


    private fun getOrPutUserState(userId: String): MutableUserState =
        __globalState.getOrPut(userId) { createMutableUserState(userId) }


    /**
     * A function to make the link between the canvas element in the UI and the drawing tool
     * which will decide what kind of figure to create / update.
     * This is useful in order to hide the actual tool implementation to the UI
     */
    fun canvasMotionHandler(
        userId: String,
        dragStart: Offset? = null,
        dragContinue: Offset? = null,
        dragEnd: Offset? = null,
    ) {
        getOrPutUserState(userId).let { ustate ->
            dragStart?.let { ustate.currentTool.dragStart(it) }
            dragContinue?.let { ustate.currentTool.dragContinue(it) }
            dragEnd?.let { ustate.currentTool.dragEnd(it) }
        }

    }


    override fun changeCurrentTool(userId: String, tool: DrawingToolValue) {
        getOrPutUserState(userId).let { ustate ->
            ustate.currentTool.removeInstructionsReceiver()
            ustate.currentTool = tool
                .toDrawingTool(userId)
                .apply {
                    if (ustate.isLocal)
                        setInstructionsReceiver(this@FreeDrawingViewModel)
                }
        }
        updateUI()
    }


    override fun changeCurrentToolStyle(userId: String, style: PathStyle) {
        getOrPutUserState(userId).let { ustate ->
            //Saving style first
            val currentTool = ustate.currentTool.toDrawingToolValue()
            ustate.tools.first { it.tool == currentTool }.style = style
            //updating current style
            ustate.currentTool.style = style
        }
        updateUI()
    }


    override fun newFigure(userId: String, figure: Figure) {
        getOrPutUserState(userId).let { ustate ->
            if (ustate.isLocal) multi.dispatchInstructions(userId, newFigure = figure)
            ustate.history.add(figure)
        }
        updateUI()
    }

    override fun updateFigure(userId: String, offset: Offset) {
        getOrPutUserState(userId).let { ustate ->
            if (ustate.isLocal) multi.dispatchInstructions(userId, updateFigure = offset)
            ustate.history.lastOrNull()?.update(offset)
        }
        updateUI()
    }

    override fun removeLastFigure(userId: String) {
        getOrPutUserState(userId).let { ustate ->
            if (ustate.isLocal) multi.dispatchInstructions(userId, removeLastFigure = true)
            ustate.history.removeLastOrNull()
        }
        updateUI()
    }

    override fun removeAllFigures(userId: String) {
        getOrPutUserState(userId).let { ustate ->
            if (ustate.isLocal) multi.dispatchInstructions(userId, removeAllFigures = true)
            ustate.history.clear()
        }
        updateUI()
    }


    fun connectToWebServerMultiplayer(
        roomID: String,
        createRoom: Boolean,
        serverIPv4: String? = null,
        serverPort: Int? = null
    ) {

        val config = if (serverIPv4 == null || serverPort == null)
            WebServerConfig(roomID, createRoom, localPlayerId)
        else
            WebServerConfig(roomID, createRoom, localPlayerId, serverIPv4, serverPort)

        connectToMultiplayer(config)
    }


    fun connectToMultiplayer(config: MultiPlayerConfig) {
        multi.ir = this
        viewModelScope.launch {
            multi.connect(config)
        }
    }


    fun disconnectFromMultiplayer() {
        multi.ir = null
        multi.disconnect()
    }
}




