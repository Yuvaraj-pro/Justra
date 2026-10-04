package com.justra.app

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
import com.justra.app.ui.navigation.JustraNavGraph
import com.justra.app.ui.theme.JustraTheme
import com.justra.app.ui.theme.WarmIvorySurface
import com.justra.app.ui.viewmodel.JustraViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: JustraViewModel by viewModels()

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
            JustraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmIvorySurface
                ) {
                    val navController = rememberNavController()

                    JustraNavGraph(
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

