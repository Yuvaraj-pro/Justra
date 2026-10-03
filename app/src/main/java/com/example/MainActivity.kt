package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.NyayaMateNavGraph
import com.example.ui.theme.NyayaMateTheme
import com.example.ui.theme.WarmIvorySurface
import com.example.ui.viewmodel.NyayaMateViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: NyayaMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Observe lifecycle to detect when app is backgrounded and re-opened (> 5 minutes threshold)
        lifecycle.addObserver(LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    viewModel.handleAppBackgrounded()
                }
                Lifecycle.Event.ON_START -> {
                    viewModel.handleAppForegrounded()
                }
                else -> Unit
            }
        })

        setContent {
            NyayaMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmIvorySurface
                ) {
                    val navController = rememberNavController()

                    NyayaMateNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

