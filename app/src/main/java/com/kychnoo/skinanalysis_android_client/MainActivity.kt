package com.kychnoo.skinanalysis_android_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kychnoo.skinanalysis_android_client.data.DataStoreManager
import com.kychnoo.skinanalysis_android_client.data.remote.ApiService
import com.kychnoo.skinanalysis_android_client.data.remote.RetrofitClient
import com.kychnoo.skinanalysis_android_client.data.repository.ConnectionRepository
import com.kychnoo.skinanalysis_android_client.data.repository.SkinAnalysisRepository
import com.kychnoo.skinanalysis_android_client.provider.AndroidResourceProvider
import com.kychnoo.skinanalysis_android_client.ui.screens.ConnectionScreen
import com.kychnoo.skinanalysis_android_client.ui.screens.MainScreen
import com.kychnoo.skinanalysis_android_client.ui.theme.SkinAnalysis_Android_ClientTheme
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.AnalysisViewModel
import com.kychnoo.skinanalysis_android_client.ui.viewmodel.ConnectionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resourceProvider = AndroidResourceProvider(applicationContext)

        val apiService: ApiService = RetrofitClient.getApiService(applicationContext)
        val dataStoreManager: DataStoreManager = DataStoreManager(applicationContext)
        val connectionRepository: ConnectionRepository = ConnectionRepository(dataStoreManager)
        val skinRepository: SkinAnalysisRepository = SkinAnalysisRepository(
            apiService,
            resourceProvider
        )

        val connectionViewModel: ConnectionViewModel = ConnectionViewModel(
            connectionRepository,
            skinRepository,
            resourceProvider
        )

        val analysisViewModel: AnalysisViewModel = AnalysisViewModel(dataStoreManager, skinRepository)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            RetrofitClient.interceptor?.setAuthListener {
                lifecycleScope.launch(Dispatchers.Main) { // User main thread for navigate to connection screen.
                    if (navController.currentDestination?.route != "connection_screen") {
                        navController.navigate("connection_screen") {
                            popUpTo(navController.graph.startDestinationId) {  // Clear stack.
                                inclusive = true
                            }
                        }
                    }
                }
            }

            SkinAnalysis_Android_ClientTheme {
                NavHost(
                    navController = navController,
                    startDestination = "connection_screen" // Start screen is connection screen.
                ) {
                    composable("connection_screen") {
                        ConnectionScreen(connectionViewModel) {
                            navController.navigate("main_screen") // Route for navigation to connection screen.
                        }
                    }
                    composable("main_screen") {
                        MainScreen(analysisViewModel) // Route for navigation to main screen.
                    }
                }
            }
        }
    }
}