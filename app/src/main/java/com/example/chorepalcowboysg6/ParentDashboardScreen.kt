package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ParentAction {
    HOME, CREATE_CHORE, VIEW_CHORES, EDIT_CHORES, APPROVALS, REWARDS, HOUSEHOLD
}

@Composable
fun ParentDashboardScreen(
    firstName: String,
    selectedAction: ParentAction = ParentAction.HOME,
    onActionSelected: (ParentAction) -> Unit,
    onLogout: () -> Unit,
    successMessage: String? = null,           // ✅ new
    onClearSuccessMessage: () -> Unit = {}    // ✅ new
) {
    Scaffold(
        bottomBar = { BottomMenuBar(selectedAction, onActionSelected) }
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

            // ✅ Success message banner (green + checkmark)
            if (successMessage != null) {
                Spacer(Modifier.height(10.dp))

                val green = Color(0xFF2E7D32)

                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = green
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = successMessage,
                            color = green
                        )
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = onClearSuccessMessage) {
                            Text("OK", color = green)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardTile("Create New Chore", Icons.Filled.Add, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.CREATE_CHORE)
                    }
                    DashboardTile("View Chores", Icons.AutoMirrored.Filled.List, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.VIEW_CHORES)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardTile("Edit Chores", Icons.Filled.Edit, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.EDIT_CHORES)
                    }
                    DashboardTile("Chore Approvals", Icons.Filled.CheckCircle, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.APPROVALS)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DashboardTile("Rewards", Icons.Filled.Star, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.REWARDS)
                    }
                    DashboardTile("Manage Household", Icons.Filled.Person, Modifier.weight(1f)) {
                        onActionSelected(ParentAction.HOUSEHOLD)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardTile(
    title: String,
    icon: ImageVector,
    modifier: Modifier,
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
fun BottomMenuBar(
    selectedAction: ParentAction,
    onActionSelected: (ParentAction) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedAction == ParentAction.HOME,
            onClick = { onActionSelected(ParentAction.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ParentAction.CREATE_CHORE,
            onClick = { onActionSelected(ParentAction.CREATE_CHORE) },
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ParentAction.EDIT_CHORES,
            onClick = { onActionSelected(ParentAction.EDIT_CHORES) },
            icon = { Icon(Icons.Filled.Edit, contentDescription = "Edit") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ParentAction.APPROVALS,
            onClick = { onActionSelected(ParentAction.APPROVALS) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Approvals") },
            label = null
        )
        NavigationBarItem(
            selected = selectedAction == ParentAction.HOUSEHOLD,
            onClick = { onActionSelected(ParentAction.HOUSEHOLD) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Household") },
            label = null
        )
    }
}