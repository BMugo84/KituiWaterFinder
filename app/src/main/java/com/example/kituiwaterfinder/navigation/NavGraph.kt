package com.example.kituiwaterfinder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kituiwaterfinder.ui.screens.ReportScreen
import com.example.kituiwaterfinder.ui.screens.WaterSourceDetailScreen
import com.example.kituiwaterfinder.ui.screens.WaterSourceListScreen
import com.example.kituiwaterfinder.viewmodel.WaterSourceViewModel

/**
 * Screen Routes
 *
 * These are like addresses for each screen.
 * When we want to navigate somewhere, we use these names.
 * Think of them like page names in a website.
 */
object Routes {
    const val LIST = "list"                    // Main list screen
    const val DETAIL = "detail/{sourceId}"     // Detail screen (needs a water source ID)
    const val REPORT = "report/{sourceName}"   // Report screen (needs a source name)

    // Helper functions to build routes with actual values
    fun detailRoute(sourceId: String) = "detail/$sourceId"
    fun reportRoute(sourceName: String) = "report/$sourceName"
}

/**
 * NavGraph
 *
 * This sets up all the navigation in our app.
 * It defines:
 * - Which screen is shown first (startDestination)
 * - How to get from one screen to another
 * - What information to pass between screens
 *
 * @param viewModel: Shared ViewModel used across all screens
 */
@Composable
fun NavGraph(viewModel: WaterSourceViewModel) {

    // NavController manages navigation between screens
    val navController = rememberNavController()

    // NavHost is the container that shows the current screen
    NavHost(
        navController = navController,
        startDestination = Routes.LIST  // First screen to show
    ) {

        // ===== SCREEN 1: LIST SCREEN =====
        composable(route = Routes.LIST) {
            WaterSourceListScreen(
                viewModel = viewModel,
                onItemClick = { waterSource ->
                    // When user taps a water source, go to detail screen
                    // We pass the water source ID so the detail screen knows what to show
                    navController.navigate(Routes.detailRoute(waterSource.id))
                }
            )
        }

        // ===== SCREEN 2: DETAIL SCREEN =====
        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                // Tell navigation that sourceId is a String argument
                navArgument("sourceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Get the sourceId that was passed when navigating here
            val sourceId = backStackEntry.arguments?.getString("sourceId") ?: ""

            // Find the water source with this ID from our ViewModel
            val waterSource = viewModel.getWaterSourceById(sourceId)

            // Only show the screen if we found the water source
            if (waterSource != null) {
                WaterSourceDetailScreen(
                    waterSource = waterSource,
                    onBackClick = {
                        // Go back to list screen
                        navController.popBackStack()
                    },
                    onReportClick = {
                        // Go to report screen, passing the water source name
                        // We encode the name to handle spaces and special characters
                        navController.navigate(
                            Routes.reportRoute(
                                java.net.URLEncoder.encode(waterSource.name, "UTF-8")
                            )
                        )
                    }
                )
            }
        }

        // ===== SCREEN 3: REPORT SCREEN =====
        composable(
            route = Routes.REPORT,
            arguments = listOf(
                // Tell navigation that sourceName is a String argument
                navArgument("sourceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Get the sourceName that was passed when navigating here
            val encodedName = backStackEntry.arguments?.getString("sourceName") ?: ""

            // Decode the name (reverse the encoding we did above)
            val sourceName = java.net.URLDecoder.decode(encodedName, "UTF-8")

            ReportScreen(
                waterSourceName = sourceName,
                viewModel = viewModel,
                onBackClick = {
                    // Go back to detail screen
                    navController.popBackStack()
                },
                onReportSubmitted = {
                    // After submitting, go all the way back to list screen
                    navController.popBackStack(Routes.LIST, inclusive = false)
                }
            )
        }
    }
}