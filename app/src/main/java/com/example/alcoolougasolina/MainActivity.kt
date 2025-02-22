package com.example.alcoolougasolina
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alcoolougasolina.ui.theme.AlcoolOuGasolinaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            AlcoolOuGasolinaTheme {
                Surface (
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Main(){
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
                Box(modifier = Modifier
                    .background(color = tertiaryColor)
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.bodyLarge,
                        )
            }
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.drawer_calculate)) },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(Screens.Calcular.screens) {
                        popUpTo(0)
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = stringResource(id = R.string.drawer_list)) },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigate(Screens.List.screens) {
                        popUpTo(0)
                    }
                })
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
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "MenuButton")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) { // 🔹 Aplica o padding no conteúdo
                NavHost(navController = navController, startDestination = Screens.Calcular.screens) {
                    composable(Screens.List.screens) { List() }
                    composable(Screens.Calcular.screens) { Calcular() }
                }
            }
        }
    }
}
