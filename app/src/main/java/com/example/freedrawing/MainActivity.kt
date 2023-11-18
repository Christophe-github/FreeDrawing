package com.example.freedrawing

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.freedrawing.network.MultiPlayerState
import com.example.freedrawing.ui.main.FreeDrawingPage
import com.example.freedrawing.ui.theme.FreeDrawingTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.system.measureTimeMillis


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreeDrawingTheme {
                MainActivityCompose()
            }
        }
    }
}

@Composable
fun MainActivityCompose() {

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val colorSurface = MaterialTheme.colors.surface

    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = colorSurface,
            darkIcons = useDarkIcons
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        FreeDrawingPage()
    }
}




@Preview(showBackground = true )
@Composable
fun DefaultPreview() {
    FreeDrawingTheme {
        MainActivityCompose()
    }
}





