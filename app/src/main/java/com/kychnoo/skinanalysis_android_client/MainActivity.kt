package com.kychnoo.skinanalysis_android_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.util.CoilUtils
import com.kychnoo.skinanalysis_android_client.data.manager.snackbar.SnackbarManager
import com.kychnoo.skinanalysis_android_client.data.model.types.SnackbarType
import com.kychnoo.skinanalysis_android_client.data.remote.ApiInterceptor
import com.kychnoo.skinanalysis_android_client.provider.AndroidResourceProvider
import com.kychnoo.skinanalysis_android_client.ui.screens.ConnectionScreen
import com.kychnoo.skinanalysis_android_client.ui.screens.ConnectionScreenRoute
import com.kychnoo.skinanalysis_android_client.ui.screens.MainScreen
import com.kychnoo.skinanalysis_android_client.ui.screens.MainScreenRoute
import com.kychnoo.skinanalysis_android_client.ui.snackbar.CustomSnackbar
import com.kychnoo.skinanalysis_android_client.ui.theme.SkinAnalysis_Android_ClientTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var apiInterceptor: ApiInterceptor

    @Inject
    lateinit var snackbarManager: SnackbarManager

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        SingletonImageLoader.setSafe { imageLoader }

        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            var currentSnackbarType by remember { mutableStateOf(SnackbarType.INFO) }

            LaunchedEffect(Unit) {
                snackbarManager.snackbarEvents.collect { event ->
                    currentSnackbarType = event.type
                    snackbarHostState.showSnackbar(message = event.message)
                }
            }

            val navController = rememberNavController()
            apiInterceptor.setAuthListener {
                lifecycleScope.launch(Dispatchers.Main) { // User main thread for navigate to connection screen.
                    if (navController.currentDestination?.hasRoute<ConnectionScreenRoute>() == false) {
                        navController.navigate(ConnectionScreenRoute) {
                            popUpTo(navController.graph.startDestinationId) {  // Clear stack.
                                inclusive = true
                            }
                        }
                    }
                }
            }

            SkinAnalysis_Android_ClientTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            CustomSnackbar(data, currentSnackbarType)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ConnectionScreenRoute, // Start screen is connection screen.
                        popEnterTransition = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400)) + fadeIn(animationSpec = tween(400))
                        },
                        popExitTransition = {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, animationSpec = tween(400)) + fadeOut(animationSpec = tween(400))
                        }
                    ) {
                        composable<ConnectionScreenRoute> { // Route for navigation to connection screen.
                            ConnectionScreen(
                                modifier = Modifier.padding(innerPadding),
                                onSuccess = { navController.navigate(MainScreenRoute) }
                            )
                        }
                        composable<MainScreenRoute> { // Route for navigation to main screen.
                            MainScreen(
                                onAuthExpired = {
                                    navController.navigateUp()
                                },
                                innerPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}