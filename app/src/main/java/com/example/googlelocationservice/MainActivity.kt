package com.example.googlelocationservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.googlelocationservice.map.presentation.view.BottomNav
import com.example.googlelocationservice.map.presentation.view.LocationPermissionScreen
import com.example.googlelocationservice.map.presentation.view.MainScreen
import com.example.googlelocationservice.map.presentation.view.SecondScreen
import com.example.googlelocationservice.map.presentation.viewmodels.MainViewModel
import com.example.googlelocationservice.ui.theme.GoogleLocationServiceTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Destination(val route: String) {
    object Main : Destination("main")
    object Second : Destination("second")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainVM: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GoogleLocationServiceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // remember if we check for location permission
                    var hasLocationPermission by remember {
                        mutableStateOf(checkForPermission(this))
                    }

                    // True
                    if (hasLocationPermission) {
                        val navController = rememberNavController()
                        ContentScaffold(mainVM = mainVM, navController = navController)
                    } else { //false
                        LocationPermissionScreen {
                            hasLocationPermission = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentScaffold(navController: NavHostController, mainVM: MainViewModel) {

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { BottomNav(navController = navController) },
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp)
    ) { innerPadding ->
        Column {
            val modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        }
        NavHost(
            navController = navController,
            startDestination = Destination.Main.route,
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 55.dp)
        ) {
            composable(Destination.Main.route) {
                MainScreen(mainVM, navController)
            }
            composable(Destination.Second.route) {
                SecondScreen(navController)
            }
        }
    }

}