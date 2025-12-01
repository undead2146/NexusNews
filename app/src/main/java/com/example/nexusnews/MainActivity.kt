package com.example.nexusnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.nexusnews.presentation.navigation.nexusNewsNavGraph
import com.example.nexusnews.ui.theme.nexusNewsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the NexusNews application.
 * Entry point that sets up navigation and theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            nexusNewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val navController = rememberNavController()
                    nexusNewsNavGraph(navController = navController)
                }
            }
        }
    }
}
