package com.example.freedrawing.ui.main.networkconfig

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.freedrawing.ui.theme.FreeDrawingTheme
import com.example.freedrawing.R
import com.example.freedrawing.network.MultiPlayerState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NetworkConfig(
    onWebServerConnect: (WebServerForm) -> Unit,
    onDisconnect: () -> Unit,
    connectionState: MultiPlayerState,
    connectionErrorMsg: String? = null,
    modifier: Modifier = Modifier,
) {
    var step by remember { mutableStateOf(1) }
    BackHandler(step > 1) { step-- }


    Column(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colors.background)
                .padding(24.dp)
        )
    ) {

        Text("Multiplayer", style = MaterialTheme.typography.h2)

        Spacer(Modifier.height(36.dp))

        AnimatedContent(
            targetState = step,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(animationSpec = tween(delayMillis = 100)) { it / 4 } + fadeIn(
                        animationSpec = tween(delayMillis = 100)
                    ) with
                            slideOutHorizontally(animationSpec = tween()) { -it / 4 } + fadeOut()
                } else {
                    slideInHorizontally(animationSpec = tween(delayMillis = 100)) { -it / 4 } + fadeIn(
                        animationSpec = tween(delayMillis = 100)
                    ) with
                            slideOutHorizontally(animationSpec = tween()) { it / 4 } + fadeOut()
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )

            }) {
            when (it) {
                1 -> NetworkSelection(
                    connectionState = connectionState,
                    onDisconnect = onDisconnect,
                    onNetworkSelected = { step++ }
                )
                2 -> WebserverConfig(
                    connectionState = connectionState,
                    connectionErrorMsg = connectionErrorMsg,
                    onWebServerConnect = onWebServerConnect
                )
            }
        }

    }

}

@JvmInline
value class NetworkChoice private constructor(private val n: Int) {

    companion object {
        val INTERNET = NetworkChoice(1)
        val BLUETOOTH = NetworkChoice(2)
    }
}


@Composable
private fun NetworkSelection(
    connectionState: MultiPlayerState,
    onDisconnect: () -> Unit,
    onNetworkSelected: (NetworkChoice) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {

        val connected = connectionState == MultiPlayerState.CONNECTED

        val connectionStatus =
            if (connected)
                "You are connected to multiplayer"
            else
                "You are not connected to multiplayer"


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    BorderStroke(1.dp, MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                connectionStatus,
                style = MaterialTheme.typography.h6,
                fontWeight = if (connected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (connected) {
                Spacer(Modifier.width(12.dp))
                Button(onClick = onDisconnect) {
                    Text("Disconnect")
                }
            }

        }

        Spacer(Modifier.height(60.dp))

        val connectionChoice =
            if (connected)
                "Choose another connection"
            else
                "Choose a connection"


        Text(connectionChoice, style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(40.dp))
        OutlinedButton(
            onClick = { onNetworkSelected(NetworkChoice.INTERNET) },
            modifier = Modifier.fillMaxWidth(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painterResource(R.drawable.ic_network_cell_24),
                    contentDescription = "Internet",
                )
                Spacer(Modifier.width(32.dp))
                Text(
                    "Internet",
                    style = MaterialTheme.typography.button.copy(fontSize = 20.sp)
                )
            }

        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { onNetworkSelected(NetworkChoice.BLUETOOTH) },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {

                Icon(
                    painterResource(R.drawable.ic_bluetooth_24),
                    contentDescription = "Bluetooth",
                )
                Spacer(Modifier.width(32.dp))
                Text(
                    "Bluetooth",
                    style = MaterialTheme.typography.button.copy(fontSize = 20.sp)
                )
            }

        }


    }

}

data class WebServerForm(
    val createRoom: Boolean,
    val roomID: String,
    val serverIpv4: String? = null,
    val serverPort: Int? = null,
)

@Composable
private fun WebserverConfig(
    connectionState: MultiPlayerState,
    connectionErrorMsg: String? = null,
    onWebServerConnect: (WebServerForm) -> Unit,
) {



    var createRoom by remember { mutableStateOf(false) }

    var roomName by remember { mutableStateOf("") }
    var trackRoomNameError by remember { mutableStateOf(false) }
    val roomNameError =
        if (roomName.isEmpty()) "Room name cannot be empty" else null

    fun roomNameIsValid() = roomNameError == null


    var advancedOptions by remember { mutableStateOf(false) }
    var serverIp by remember { mutableStateOf("10.0.2.2") }
    var serverPort by remember { mutableStateOf("8080") }


    fun formIsValid() = roomNameIsValid()
    fun trackErrors() {
        trackRoomNameError = true
    }

    Box(Modifier.fillMaxSize()) {

        Column(Modifier.padding(horizontal = 4.dp)) {
            Spacer(Modifier.height(24.dp))

            val focusManager = LocalFocusManager.current


            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Join room")
                    RadioButton(selected = !createRoom, onClick = { createRoom = false })
                }
                Spacer(Modifier.width(48.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Create room")
                    RadioButton(selected = createRoom, onClick = { createRoom = true })
                }
            }

            Spacer(Modifier.height(12.dp))

            TextField(
                value = roomName,
                onValueChange = { trackRoomNameError = true; roomName = it },
                modifier = Modifier.fillMaxWidth(),
                isError = trackRoomNameError && !roomNameIsValid(),
                label = { Text("Room name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(autoCorrect = false, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = TextFieldDefaults.textFieldColors(
                    focusedLabelColor = MaterialTheme.colors.onSurface.copy(
                        ContentAlpha.medium
                    )
                )

            )


            if (trackRoomNameError && !roomNameIsValid()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    roomNameError!!,
                    color = MaterialTheme.colors.error,
                    textAlign = TextAlign.Start
                )
            }


            Spacer(Modifier.height(32.dp))


            Column(
                Modifier
                    .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 20.dp)

            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Advanced options")
                    Checkbox(checked = advancedOptions, onCheckedChange = { advancedOptions = it })
                }


                AnimatedVisibility(advancedOptions) {
                    Column {
                        Spacer(Modifier.height(24.dp))

                        TextField(
                            value = serverIp,
                            onValueChange = { serverIp = it },
                            label = { Text("Server IPv4 address ") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colors.onSurface.copy(
                                    ContentAlpha.medium
                                )
                            )

                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = serverPort,
                            onValueChange = { serverPort = it },
                            label = { Text("Port") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Number
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedLabelColor = MaterialTheme.colors.onSurface.copy(
                                    ContentAlpha.medium
                                )
                            )

                        )
                    }

                }
            }

            Spacer(Modifier.height(48.dp))


//            if (connectionState == MultiPlayerConnection.NOT_CONNECTED) {
                connectionErrorMsg?.let {
                    Text(it, color = MaterialTheme.colors.error)
                    Spacer(Modifier.height(16.dp))
                }
//            }

            Button(
                onClick = {
                    trackErrors()
                    if (!formIsValid()) return@Button

                    val formData =
                        if (!advancedOptions) WebServerForm(createRoom, roomName)
                        else WebServerForm(createRoom, roomName, serverIp, serverPort.toIntOrNull())

                    onWebServerConnect(formData)

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = connectionState != MultiPlayerState.CONNECTING
            ) {
                Text("Connect", Modifier.padding(8.dp), fontSize = 20.sp)
            }

            Spacer(Modifier.height(60.dp))
        }


        //Waiting screen when state is connecting
        AnimatedVisibility(
            connectionState == MultiPlayerState.CONNECTING,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .clickable(enabled = false) { } // Used to prevent click behind
                    .background(MaterialTheme.colors.background.copy(alpha = 0.90f))
            ) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                Text(
                    "Connecting...",
                    modifier = Modifier.align(BiasAlignment(0f, 0.3f)),
                    color = MaterialTheme.colors.primary,
                    fontSize = 22.sp
                )
            }
        }

    }

}


@Preview(showSystemUi = true)
@Composable
private fun NetworkConfigPreview() {
    FreeDrawingTheme {
        NetworkConfig(connectionState = MultiPlayerState.NOT_CONNECTED,
            onWebServerConnect = {},
            onDisconnect = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun NetworkSelectionPreview() {
    FreeDrawingTheme {
        NetworkSelection(
            MultiPlayerState.CONNECTED,
            onDisconnect = {},
            onNetworkSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WebserverConfigPreview() {
    FreeDrawingTheme {
        WebserverConfig(connectionState = MultiPlayerState.NOT_CONNECTED) { }
    }
}