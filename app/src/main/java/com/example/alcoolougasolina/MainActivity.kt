package com.example.alcoolougasolina

import EditScreen
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("Permissão", "Permissão concedida! Obtendo localização...")
                PermissionManager.getCurrentLocation(this) { latitude, longitude ->
                    if (latitude != null && longitude != null) {
                        Log.d("Localização", "Latitude: $latitude, Longitude: $longitude")
                    } else {
                        Log.d("Localização", "Não foi possível obter a localização.")
                    }
                }
            } else {
                Log.d("Permissão", "Permissão negada!")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AlcoolOuGasolinaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(requestPermissionLauncher)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(requestPermissionLauncher: ActivityResultLauncher<String>) {
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .background(color = tertiaryColor)
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.drawer_calculate)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screens.Calcular.screens) { popUpTo(0) }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(text = stringResource(id = R.string.drawer_list)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screens.List.screens) { popUpTo(0) }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = tertiaryColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavHost(navController = navController, startDestination = Screens.Calcular.screens) {
                    composable(Screens.List.screens) { ListScreen(navController) }
                    composable(Screens.Calcular.screens) { Calcular(requestPermissionLauncher = requestPermissionLauncher) }

                    // Rota para EditScreen (adicione esta linha)
                    composable(
                        route = "edit/{stationId}",
                        arguments = listOf(navArgument("stationId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val stationId = backStackEntry.arguments?.getString("stationId") ?: ""
                        EditScreen(navController, stationId)
                    }

                    composable(
                        "details/{stationId}",
                        arguments = listOf(navArgument("stationId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val stationId = backStackEntry.arguments?.getString("stationId") ?: ""
                        DetailsScreen(navController, stationId)
                    }
                }
            }
        }
    }
}

// ... outras funções ...