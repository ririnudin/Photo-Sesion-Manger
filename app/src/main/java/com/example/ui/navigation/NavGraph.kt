package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ui.screens.activesession.ActiveSessionScreen
import com.example.ui.screens.activesession.ActiveSessionViewModel
import com.example.ui.screens.camera.InAppCameraScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.newsession.NewSessionScreen
import com.example.ui.screens.newsession.NewSessionViewModel
import com.example.ui.screens.sessiondetail.SessionDetailScreen
import com.example.ui.screens.sessionlist.SessionListScreen
import com.example.ui.screens.sessionlist.SessionListViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel

object Route {
    const val HOME = "home"
    const val NEW_SESSION = "new_session"
    const val ACTIVE_SESSION = "active_session/{sessionId}"
    const val CAMERA = "camera/{sessionId}"
    const val SESSION_LIST = "session_list"
    const val SESSION_DETAIL = "session_detail/{sessionId}"
    const val SETTINGS = "settings"

    fun activeSession(sessionId: Long) = "active_session/$sessionId"
    fun camera(sessionId: Long) = "camera/$sessionId"
    fun sessionDetail(sessionId: Long) = "session_detail/$sessionId"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.HOME
    ) {
        composable(Route.HOME) {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToNewSession = { navController.navigate(Route.NEW_SESSION) },
                onNavigateToSessionList = { navController.navigate(Route.SESSION_LIST) },
                onNavigateToSettings = { navController.navigate(Route.SETTINGS) },
                onNavigateToActiveSession = { sessionId -> navController.navigate(Route.activeSession(sessionId)) },
                onNavigateToCamera = { sessionId -> navController.navigate(Route.camera(sessionId)) },
                onNavigateToSessionDetail = { sessionId -> navController.navigate(Route.sessionDetail(sessionId)) }
            )
        }

        composable(Route.NEW_SESSION) {
            val newSessionViewModel: NewSessionViewModel = viewModel()
            NewSessionScreen(
                viewModel = newSessionViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSessionCreated = { sessionId ->
                    navController.navigate(Route.activeSession(sessionId)) {
                        popUpTo(Route.HOME)
                    }
                }
            )
        }

        composable(
            route = Route.ACTIVE_SESSION,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            val context = androidx.compose.ui.platform.LocalContext.current
            val application = context.applicationContext as android.app.Application
            val activeViewModel: ActiveSessionViewModel = viewModel(
                factory = ActiveSessionViewModel.Factory(application, sessionId)
            )

            ActiveSessionScreen(
                viewModel = activeViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCamera = { id -> navController.navigate(Route.camera(id)) }
            )
        }

        composable(
            route = Route.CAMERA,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            val context = androidx.compose.ui.platform.LocalContext.current
            val application = context.applicationContext as android.app.Application
            val activeViewModel: ActiveSessionViewModel = viewModel(
                factory = ActiveSessionViewModel.Factory(application, sessionId)
            )

            InAppCameraScreen(
                viewModel = activeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.SESSION_LIST) {
            val sessionListViewModel: SessionListViewModel = viewModel()
            SessionListScreen(
                viewModel = sessionListViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSelectSession = { sessionId ->
                    navController.navigate(Route.sessionDetail(sessionId))
                }
            )
        }

        composable(
            route = Route.SESSION_DETAIL,
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            val context = androidx.compose.ui.platform.LocalContext.current
            val application = context.applicationContext as android.app.Application
            val activeViewModel: ActiveSessionViewModel = viewModel(
                factory = ActiveSessionViewModel.Factory(application, sessionId)
            )
            SessionDetailScreen(
                viewModel = activeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Route.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
