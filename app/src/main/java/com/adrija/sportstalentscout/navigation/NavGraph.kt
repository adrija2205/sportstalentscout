package com.adrija.sportstalentscout.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adrija.sportstalentscout.ui.screens.*
import com.adrija.sportstalentscout.viewmodel.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onTestSelected = { testType ->
                    viewModel.setSelectedTest(testType)
                    navController.navigate("instruction")
                },
                onProfileClick = {
                    navController.navigate("profile")
                }

            )
        }

        composable("instruction") {
            InstructionScreen(
                viewModel = viewModel,
                onStartTest = {
                    navController.navigate("recording")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("recording") {
            RecordingScreen(
                viewModel = viewModel,
                onRecordingComplete = {
                    navController.navigate("result")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("result") {
            ResultScreen(
                viewModel = viewModel,
                onBackToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onRetakeTest = {
                    navController.navigate("recording") {
                        popUpTo("recording") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
