package com.example.freedrawing.network

import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.ui.main.InstructionsReceiver
import kotlinx.coroutines.flow.Flow


interface MultiPlayerService {

    //Raw data are converted to instructions and given to instructionsReceiver
    //Could be a Flow of instructions maybe?
    var ir: InstructionsReceiver?

    val connectionState: Flow<MultiPlayerState>
    val connectionErrorMsg: Flow<String?>

    fun getConnectionState() : MultiPlayerState
    fun getErrorMsg() : String?

    fun canHandleConfig(config: MultiPlayerConfig) : Boolean
    suspend fun connect(config: MultiPlayerConfig)
    fun disconnect()


    fun dispatchInstructions(
        userId: String,
        newFigure: Figure? = null,
        updateFigure: Offset? = null,
        removeLastFigure: Boolean? = null,
        removeAllFigures: Boolean? = null
    )

}
