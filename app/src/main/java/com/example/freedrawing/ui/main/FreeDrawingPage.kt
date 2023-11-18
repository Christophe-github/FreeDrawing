package com.example.freedrawing.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.freedrawing.network.MultiPlayerState
import com.example.freedrawing.ui.common.FreeDrawingAlertDialog
import com.example.freedrawing.ui.main.networkconfig.NetworkConfig
import com.example.freedrawing.ui.theme.FreeDrawingTheme
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalGraphicsApi::class, ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun FreeDrawingPage() {
    val viewModel = viewModel(modelClass = FreeDrawingViewModel::class.java)
    val id = remember { viewModel.localPlayerId }

    val globalState by viewModel.globalState.collectAsState(listOf())
    val myState = globalState.firstOrNull { it.userId == id } ?: UserState.default
    val connectionState by viewModel.connectionState.collectAsState(MultiPlayerState.NOT_CONNECTED)
    val connectionErrorMsg by viewModel.connectionErrorMsg.collectAsState(null)


    var showNetworkConfig by remember { mutableStateOf(false) }

    //Alert dialog handling
    var mustConfirmClear by remember { mutableStateOf(false) }
    if (mustConfirmClear) {
        FreeDrawingAlertDialog(
            title = "Erase everything?",
            subtitle = "All drawings will be cleared",
            positiveText = "Erase",
            onDismissRequest = { mustConfirmClear = false },
            onPositiveAction = { viewModel.removeAllFigures(id) })
    }


    //Panning and zooming handling
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        if (scale < 0.5f) scale = 0.5f
        else if (scale > 2f) scale = 2f
        offset += offsetChange
    }


    //Transformable is used to detect panning / scaling with two fingers
    //We detect these gestures on the whole Box but only apply the modifications
    //to the DrawingZone
    Box(Modifier.transformable(state = state)) {


        DrawingZone(
            onDragStart = { viewModel.canvasMotionHandler(id, dragStart = it.times(1 / scale)) },
            onDrag = { viewModel.canvasMotionHandler(id, dragContinue = it.times(1 / scale)) },
            onDragEnd = { viewModel.canvasMotionHandler(id, dragEnd = it.times(1 / scale)) },
            state = globalState,
            modifier = Modifier.graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
            )
        )

        FreeDrawingToolbar(
            userState = myState,
            modifier = Modifier.padding(8.dp),
            isMultiplayerEnabled = connectionState == MultiPlayerState.CONNECTED,
            onToolSelected = { viewModel.changeCurrentTool(id, it) },
            onResetScale = { scale = 1f; offset = Offset.Zero },
            onPreviousStep = { viewModel.removeLastFigure(id) },
            onClear = { mustConfirmClear = true; },
            onMultiplayer = { showNetworkConfig = true }
        )


        val enterTransition = remember { drawingToolsEnter() }
        val exitTransition = remember { drawingToolsExit() }

        //Displaying the drawing options for the current tool
        myState.tools.forEach { tool ->
            AnimatedVisibility(
                visible = myState.currentTool == tool.first,
                enter = enterTransition,
                exit = exitTransition,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                val swipeableState = rememberSwipeableState(false)


                DrawingToolOptions(
                    tool.first,
                    tool.second,
                    onStyleChanged = { viewModel.changeCurrentToolStyle(id, it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .offset(y = 190.dp)
                        .offset(y = swipeableState.offset.value.dp)
                        //swipe gesture will counter the initial 290.dp y offset and
                        //push the view up
                        .swipeable(
                            swipeableState,
                            anchors = mapOf(
                                0f to false,
                                with(LocalDensity.current) { -40.dp.toPx() } to true),
                            orientation = Orientation.Vertical,
                            thresholds = { _, _ -> FractionalThreshold(0.2f) }
                        )
                )

            }
        }


        val scope = rememberCoroutineScope()
        LaunchedEffect(true) {
            scope.launch {
                viewModel.connectionState.collect {
                    if (it == MultiPlayerState.CONNECTED)
                        showNetworkConfig = false
                }
            }
        }

        AnimatedVisibility(
            showNetworkConfig,
            enter = scaleIn(initialScale = 0.9f) + fadeIn(),
            exit = scaleOut(targetScale = 0.9f) + fadeOut()
        ) {
            BackHandler(showNetworkConfig) { showNetworkConfig = false }
            NetworkConfig(
                onWebServerConnect = {
                    viewModel.connectToWebServerMultiplayer(
                        it.roomID,
                        it.createRoom,
                        it.serverIpv4,
                        it.serverPort
                    )
                },
                onDisconnect = { viewModel.disconnectFromMultiplayer() },
                connectionState = connectionState,
                connectionErrorMsg = connectionErrorMsg,
                modifier = Modifier.fillMaxSize()
            )
        }


    }
}

@Stable
private fun drawingToolsEnter() = fadeIn(
    animationSpec = tween(200, delayMillis = 40)
) + slideInVertically(
    animationSpec = tween(200, delayMillis = 40)
) { it / 3 }

@Stable
private fun drawingToolsExit() = fadeOut()


@Preview
@Composable
private fun FreeDrawingPagePreview() {
    FreeDrawingTheme {
        FreeDrawingPage()
    }
}