package com.justra.app

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.justra.app.ui.navigation.JustraNavGraph
import com.justra.app.ui.theme.JustraTheme
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
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.handleAppForegrounded()
                }
                else -> Unit
            }
        })

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()

            JustraTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        viewModel.recordUserActivity()
        return super.dispatchTouchEvent(ev)
    }
}
