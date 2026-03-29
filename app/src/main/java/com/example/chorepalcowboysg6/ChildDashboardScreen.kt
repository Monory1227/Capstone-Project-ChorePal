package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ChildAction {
    HOME,
    VIEW_CHORES,
    UPDATE_STATUS,
    REWARDS,
    PROFILE
}

@Composable
fun ChildDashboardScreen(
    firstName: String,
    selectedAction: ChildAction = ChildAction.HOME,
    onActionSelected: (ChildAction) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            ChildBottomMenuBar(
                selectedAction = selectedAction,
                onActionSelected = onActionSelected
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                IconButton(
                    onClick = { onActionSelected(ChildAction.PROFILE) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(34.dp)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.chorepal_logo),
                    contentDescription = "ChorePal Logo",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(280.dp)
                        .height(110.dp),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Welcome, $firstName!",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ChildTile("View Chores", Icons.Filled.List, Modifier.weight(1f)) {
                        onActionSelected(ChildAction.VIEW_CHORES)
                    }
                    ChildTile("Update Status", Icons.Filled.CheckCircle, Modifier.weight(1f)) {
                        onActionSelected(ChildAction.UPDATE_STATUS)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ChildTile("Rewards", Icons.Filled.Star, Modifier.weight(1f)) {
                        onActionSelected(ChildAction.REWARDS)
                    }
                    ChildTile("Profile", Icons.Filled.Person, Modifier.weight(1f)) {
                        onActionSelected(ChildAction.PROFILE)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(text = title, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ChildBottomMenuBar(
    selectedAction: ChildAction,
    onActionSelected: (ChildAction) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedAction == ChildAction.HOME,
            onClick = { onActionSelected(ChildAction.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ChildAction.VIEW_CHORES,
            onClick = { onActionSelected(ChildAction.VIEW_CHORES) },
            icon = { Icon(Icons.Filled.List, contentDescription = "View Chores") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ChildAction.UPDATE_STATUS,
            onClick = { onActionSelected(ChildAction.UPDATE_STATUS) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Update Status") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ChildAction.REWARDS,
            onClick = { onActionSelected(ChildAction.REWARDS) },
            icon = { Icon(Icons.Filled.Star, contentDescription = "Rewards") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ChildAction.PROFILE,
            onClick = { onActionSelected(ChildAction.PROFILE) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = null
        )
    }
}