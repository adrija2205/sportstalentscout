package com.adrija.sportstalentscout.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adrija.sportstalentscout.ui.screens.*
import com.adrija.sportstalentscout.viewmodel.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    when (val isCompleted = isOnboardingCompleted) {
        null -> {
            // Display splash / loading indicator while reading initial DataStore preference
            // to completely prevent any brief flash or flickering between screens.
            KridaSplashScreen()
        }
        else -> {
            // Remember the resolved initial startDestination so that NavHost doesn't re-initialize
            // when onboarding is completed later during the active session.
            val startDestination = remember {
                if (isCompleted) "home" else "onboarding"
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable("onboarding") {
                    OnboardingScreen(
                        onOnboardingComplete = {
                            viewModel.completeOnboarding()
                            navController.navigate("home") {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        },
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
                        },
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
                        },
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
                        },
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
                        },
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        },
                    )
                }
            }
        }
    }
}
