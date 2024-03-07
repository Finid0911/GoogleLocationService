package com.example.googlelocationservice.map.presentation.view

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.googlelocationservice.BottomNavText
import com.example.googlelocationservice.Destination
import com.example.googlelocationservice.R

@Composable
fun BottomNav(navController: NavHostController) {
    BottomNavigation(
        elevation = 10.dp,
        backgroundColor = Color.Gray,
        modifier = Modifier.height(55.dp).alpha(0.8f)
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination

        val iconMain = painterResource(id = R.drawable.baseline_adjust_24)
        val iconSecond = painterResource(id = R.drawable.baseline_android_24)

        BottomNavigationItem(
            selected = currentDestination?.route == Destination.Main.route,
            onClick = {
                navController.navigate(Destination.Main.route) {
                    popUpTo(Destination.Main.route)
                    launchSingleTop = true
                }
            },
            icon = { Icon(painter = iconMain, contentDescription = null) },
            label = { BottomNavText(text = Destination.Main.route) },
        )

        BottomNavigationItem(
            selected = currentDestination?.route == Destination.Second.route,
            onClick = {
                navController.navigate(Destination.Second.route) {
                    launchSingleTop = true
                }
            },
            icon = { Icon(painter = iconSecond, contentDescription = null) },
            label = { BottomNavText(text = Destination.Second.route) },
        )
    }
}