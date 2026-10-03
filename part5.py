import os

with open('app/src/main/java/com/example/ui/navigation/Navigation.kt', 'r', encoding='utf-8') as f:
    nav_code = f.read()

# Add imports if missing
imports_to_add = '''
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.LandingPageScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.NewGrievanceScreen
import com.example.ui.screens.SmartComplaintScreen
import com.example.ui.screens.LimitationRemindersScreen
'''

if 'import com.example.ui.screens.SplashScreen' not in nav_code:
    nav_code = nav_code.replace('import com.example.ui.screens.HomeScreen', 'import com.example.ui.screens.HomeScreen\n' + imports_to_add)

# Add start destination & startup routes
if 'Routes.Splash' not in nav_code:
    # Update startDestination
    nav_code = nav_code.replace(
        'val startDestination = when {',
        'val startDestination = Routes.Splash\n    val legacyStartDestination = when {'
    )

    startup_composables = '''
        // Startup Flow: Splash -> Onboarding -> Landing -> Auth -> MainDashboard
        composable(Routes.Splash) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.Onboarding) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Onboarding) {
            OnboardingScreen(
                onOnboardingFinished = {
                    navController.navigate(Routes.Landing) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Landing) {
            LandingPageScreen(
                onProceedToVault = {
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Landing) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Auth) {
            AuthScreen(
                currentLanguage = language,
                onAuthenticated = {
                    navController.navigate(NyayaMateDestinations.HOME) {
                        popUpTo(Routes.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.NewGrievance) {
            NewGrievanceScreen(
                viewModel = viewModel,
                currentLanguage = language,
                onBackClick = { navController.popBackStack() },
                onCreated = { caseId ->
                    navController.navigate("smart_complaint/")
                }
            )
        }

        composable("smart_complaint/{caseId}") { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "new"
            SmartComplaintScreen(
                caseId = caseId,
                currentLanguage = language,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("limitation_reminders") {
            LimitationRemindersScreen(
                viewModel = viewModel,
                currentLanguage = language,
                onBackClick = { navController.popBackStack() }
            )
        }
'''
    nav_code = nav_code.replace(
        'NavHost(\n            navController = navController,',
        startup_composables + '\n        NavHost(\n            navController = navController,'
    )

with open('app/src/main/java/com/example/ui/navigation/Navigation.kt', 'w', encoding='utf-8') as f:
    f.write(nav_code)

print("Navigation.kt updated with startup flow routes!")
