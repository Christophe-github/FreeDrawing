package com.example.freedrawing.network.bluetooth

import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.network.MultiPlayerConfig
import com.example.freedrawing.network.MultiPlayerState
import com.example.freedrawing.network.MultiPlayerService
import com.example.freedrawing.ui.main.InstructionsReceiver
import kotlinx.coroutines.flow.Flow

class BluetoothMultiplayer : MultiPlayerService {

    override var ir: InstructionsReceiver?
        get() = TODO("Not yet implemented")
        set(value) {}

    override val connectionState: Flow<MultiPlayerState>
        get() = TODO("Not yet implemented")

    override val connectionErrorMsg: Flow<String?>
        get() = TODO("Not yet implemented")


    override fun getConnectionState(): MultiPlayerState {
        TODO("Not yet implemented")
    }


    override fun getErrorMsg(): String? {
        TODO("Not yet implemented")
    }

    override fun canHandleConfig(config: MultiPlayerConfig): Boolean {
        TODO("Not yet implemented")
    }


    override suspend fun connect(config: MultiPlayerConfig) {
        TODO("Not yet implemented")
    }


    override fun disconnect() {
        TODO("Not yet implemented")
    }


    override fun dispatchInstructions(
        userId: String,
        newFigure: Figure?,
        updateFigure: Offset?,
        removeLastFigure: Boolean?,
        removeAllFigures: Boolean?
    ) {
        TODO("Not yet implemented")
    }
}