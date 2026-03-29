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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
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

enum class AdultAction {
    HOME,
    CREATE_CHORE,
    VIEW_CHORES,
    EDIT_CHORES,
    APPROVALS,
    REWARDS,
    PROFILE
}

@Composable
fun AdultDashboardScreen(
    firstName: String,
    selectedAction: AdultAction = AdultAction.HOME,
    onActionSelected: (AdultAction) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AdultBottomMenuBar(
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
                    onClick = { onActionSelected(AdultAction.PROFILE) },
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
                    AdultTile("Create New Chore", Icons.Filled.Add, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.CREATE_CHORE)
                    }
                    AdultTile("View Chores", Icons.AutoMirrored.Filled.List, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.VIEW_CHORES)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdultTile("Edit Chores", Icons.Filled.Edit, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.EDIT_CHORES)
                    }
                    AdultTile("Chore Approvals", Icons.Filled.CheckCircle, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.APPROVALS)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdultTile("Rewards", Icons.Filled.Star, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.REWARDS)
                    }
                    AdultTile("Profile", Icons.Filled.Person, Modifier.weight(1f)) {
                        onActionSelected(AdultAction.PROFILE)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdultTile(
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
private fun AdultBottomMenuBar(
    selectedAction: AdultAction,
    onActionSelected: (AdultAction) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedAction == AdultAction.HOME,
            onClick = { onActionSelected(AdultAction.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == AdultAction.CREATE_CHORE,
            onClick = { onActionSelected(AdultAction.CREATE_CHORE) },
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == AdultAction.EDIT_CHORES,
            onClick = { onActionSelected(AdultAction.EDIT_CHORES) },
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Edit") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == AdultAction.APPROVALS,
            onClick = { onActionSelected(AdultAction.APPROVALS) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Approvals") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == AdultAction.REWARDS,
            onClick = { onActionSelected(AdultAction.REWARDS) },
            icon = { Icon(Icons.Filled.Star, contentDescription = "Rewards") },
            label = null
        )
    }
}