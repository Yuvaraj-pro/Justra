package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.ui.viewmodel.NyayaMateViewModel

/**
 * NyayaMate Navigation Graph delegator for com.example.navigation package.
 */
@Composable
fun NyayaMateNavGraph(
    navController: NavHostController,
    viewModel: NyayaMateViewModel,
    modifier: Modifier = Modifier
) {
    com.example.ui.navigation.NyayaMateNavGraph(
        navController = navController,
        viewModel = viewModel,
        modifier = modifier
    )
}
