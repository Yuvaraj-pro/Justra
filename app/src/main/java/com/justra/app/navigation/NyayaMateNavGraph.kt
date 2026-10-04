package com.justra.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.justra.app.ui.viewmodel.NyayaMateViewModel

/**
 * NyayaMate Navigation Graph delegator for com.justra.app.navigation package.
 */
@Composable
fun NyayaMateNavGraph(
    navController: NavHostController,
    viewModel: NyayaMateViewModel,
    modifier: Modifier = Modifier
) {
    com.justra.app.ui.navigation.NyayaMateNavGraph(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}
