package com.example.freedrawing.network.webserver

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.example.freedrawing.drawing.figure.Figure
import com.example.freedrawing.network.*
import com.example.freedrawing.network.entities.FreeDrawingSocketStatus
import com.example.freedrawing.network.entities.PlayerInstructions
import com.example.freedrawing.ui.main.InstructionsReceiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ServerHandshake
import retrofit2.HttpException
import java.net.URI

class WebserverMultiplayer : MultiPlayerService {


    var restApi : FreeDrawingRestAPI? = null

    /** Raw data are converted to instructions and given to instructionsReceiver */
    override var ir: InstructionsReceiver? = null


    /** The socket where we send data to / receive data from */
    private var socket: FreeDrawingWebSocket? = null

    /** Useful to track if the server has accepted the connection by responding with a [FreeDrawingSocketStatus]
     *  with `accepted` set to `true` .
     *  We can't just rely on the [socket].[ReadyState] because the server has to open the [WebSocketClient] first
     *  in order to send an the status.
     * */
    private var hasServerAcceptedSocketRequest = false

    private val _connectionState = MutableStateFlow(MultiPlayerState.NOT_CONNECTED)
    override val connectionState: Flow<MultiPlayerState> = _connectionState

    private val _connectionErrorMsg = MutableStateFlow<String?>(null)
    override val connectionErrorMsg: Flow<String?> = _connectionErrorMsg

    override fun getConnectionState(): MultiPlayerState = _connectionState.value
    override fun getErrorMsg(): String? = _connectionErrorMsg.value

    override fun canHandleConfig(config: MultiPlayerConfig) = config is WebServerConfig


    override suspend fun connect(config: MultiPlayerConfig) {
        disconnect()
        webserverConnect(config as WebServerConfig)
    }

    override fun disconnect() {
        socket?.close()
        socket = null
        hasServerAcceptedSocketRequest = false
        _connectionState.value = MultiPlayerState.NOT_CONNECTED
    }



    private suspend fun webserverConnect(config: WebServerConfig) {
        try {
            _connectionState.value = MultiPlayerState.CONNECTING

            restApi = FreeDrawingRestAPIImpl.get(config.serverIPv4,config.serverPort)

            //Creating the room if requested
            if (config.createRoom) {
                restApi?.createRoom(config.roomID)
            }


            //Establishing the socket makes the player join the room
            socket =
                FreeDrawingWebSocket(
                    createSocketUrl(
                        config.serverIPv4,
                        config.serverPort,
                        config.roomID,
                        config.userID
                    )
                ).also {
                    it.connect()
                }

        } catch (e: HttpException) {
            _connectionState.value = MultiPlayerState.NOT_CONNECTED
            _connectionErrorMsg.value = " ${e.message} : ${e.errorBody()}"

        } catch (e: Exception) {
            _connectionState.value = MultiPlayerState.NOT_CONNECTED
            _connectionErrorMsg.value = e.message
        }
    }

    override fun dispatchInstructions(
        userId: String,
        newFigure: Figure?,
        updateFigure: Offset?,
        removeLastFigure: Boolean?,
        removeAllFigures: Boolean?
    ) {
        socket?.let {
            if (it.readyState != ReadyState.OPEN) {
                Log.d("###########","Socket is not open, can't dispatch instructions")
                return@let
            }

            val playerInstructions = PlayerInstructions(
                userId,
                newFigure = newFigure,
                updateFigure = updateFigure,
                removeLastFigure = removeLastFigure,
                removeAllFigures = removeAllFigures
            )
            it.send(Json.encodeToString(playerInstructions))


        }
    }



    private fun createSocketUrl(
        serverIPv4: String,
        serverPort: Int,
        roomID: String,
        userId: String
    ) =
        URI.create("ws://$serverIPv4:$serverPort/room/join?userID=$userId&roomID=$roomID")


    /**
     * Should be called when data from the socket has been received.
     * Will translate the data to instructions and send it to the [InstructionsReceiver]
     */
    private fun onSocketData(data: String) {

        //The first message is the connection status
        if (!hasServerAcceptedSocketRequest) {
            val status = Json.decodeFromString<FreeDrawingSocketStatus>(data)

            if (status.accepted) {
                hasServerAcceptedSocketRequest = true
                _connectionState.value = MultiPlayerState.CONNECTED
                Log.d("###########", "Connection accepted : $status")
            } else {
                Log.d("###########", "Connection rejected : $status")
                _connectionErrorMsg.value = status.error
            }

            return
        }


        //All the following messages will be instructions
        Json.decodeFromString<PlayerInstructions>(data).run {
            newFigure?.let { ir?.newFigure(playerId, it) }
            updateFigure?.let { ir?.updateFigure(playerId, it) }
            removeLastFigure?.let { ir?.removeLastFigure(playerId) }
            removeAllFigures?.let { ir?.removeAllFigures(playerId) }
        }
    }

    private inner class FreeDrawingWebSocket(uri: URI) :
        WebSocketClient(uri, Draft_6455(), null, 5000) {

        private fun isActiveSocket() = this == this@WebserverMultiplayer.socket

        override fun onOpen(handshakedata: ServerHandshake?) {
            Log.d(
                "###########",
                "socket opened, waiting to receive a status to confirm connection..."
            )
        }

        override fun onMessage(message: String?) {
//            Log.d("###########", "message : $message")
            if (isActiveSocket()) {
                message?.let { onSocketData(it) }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d("###########", "close : $reason")
            if (isActiveSocket()) {
                _connectionErrorMsg.value = reason
                _connectionState.value = MultiPlayerState.NOT_CONNECTED
                this@WebserverMultiplayer.socket = null
            }
        }

        override fun onError(ex: Exception?) {
            Log.d("###########", "error ${ex?.message}")
            if (isActiveSocket()) {
                _connectionErrorMsg.value = ex?.message
                _connectionState.value = MultiPlayerState.NOT_CONNECTED
                this@WebserverMultiplayer.socket = null
            }
        }
    }
}