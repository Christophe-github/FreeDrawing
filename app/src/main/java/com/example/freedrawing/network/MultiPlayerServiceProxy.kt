package com.example.freedrawing.network

import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.network.webserver.WebserverMultiplayer
import com.example.freedrawing.ui.main.InstructionsReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * A proxy between the viewmodel and the actual [MultiPlayerService] implementation.
 * The actual implementation changes according to the config that is sent with the [connect] method
 */
class MultiPlayerServiceProxy : MultiPlayerService {

    /** The actual [MultiPlayerService] implementation **/
    private var currentService: MultiPlayerService? = null

    /** Raw data are converted to instructions and given to instructionsReceiver */
    override var ir: InstructionsReceiver? = null


    private val _connectionState = MutableStateFlow(MultiPlayerState.NOT_CONNECTED)
    override val connectionState: Flow<MultiPlayerState> = _connectionState

    private val _connectionErrorMsg = MutableStateFlow<String?>(null)
    override val connectionErrorMsg: Flow<String?> = _connectionErrorMsg

    override fun getConnectionState(): MultiPlayerState =
        currentService?.getConnectionState() ?: MultiPlayerState.NOT_CONNECTED

    override fun getErrorMsg(): String? = currentService?.getErrorMsg()

    var scope: CoroutineScope? = CoroutineScope(Dispatchers.Main)


    override suspend fun connect(config: MultiPlayerConfig) {

        val service = currentService

        if (service == null || !service.canHandleConfig(config)) {
            disconnect()

            val newService = createService(config)
            attachEvents(newService)
            currentService = newService
        }

        currentService?.connect(config)
    }

    private fun createService(config: MultiPlayerConfig): MultiPlayerService =
        when (config) {
            is WebServerConfig -> WebserverMultiplayer()
//            else -> throw Exception("Can't create a service with this config $config")
        }

    private fun attachEvents(service: MultiPlayerService) {
        //Collecting service flows and forwarding them to this object flows
        scope?.cancel()
        scope = CoroutineScope(Dispatchers.Default).apply {
            launch { service.connectionErrorMsg.collect { s -> _connectionErrorMsg.value = s } }
            launch { service.connectionState.collect { s -> _connectionState.value = s } }
        }

        //Linking the instructions receiver to the service
        service.ir = ir
    }

    private fun detachEvents(service: MultiPlayerService) {
        scope?.cancel()
        _connectionState.value = MultiPlayerState.NOT_CONNECTED
        _connectionErrorMsg.value = null
        service.ir = null
    }

    override fun canHandleConfig(config: MultiPlayerConfig): Boolean = true

    override fun disconnect() {
        currentService?.let {
            detachEvents(it)
            it.disconnect()
        }
        currentService = null
    }


    override fun dispatchInstructions(
        userId: String,
        newFigure: Figure?,
        updateFigure: Offset?,
        removeLastFigure: Boolean?,
        removeAllFigures: Boolean?
    ) {
        currentService?.dispatchInstructions(
            userId,
            newFigure,
            updateFigure,
            removeLastFigure,
            removeAllFigures
        )
    }


}



