package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UpdateChoreStatusScreen(
    chores: List<ChoreRow>,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUpdateStatus: (String, String) -> Unit,
    isKidsMode: Boolean = false
) {

    var selectedStatus by remember {
        mutableStateOf("All Statuses")
    }

    var statusMenuExpanded by remember {
        mutableStateOf(false)
    }

    val statusOptions = listOf(
        "All Statuses",
        "Assigned",
        "In Progress",
        "Completed",
        "Approved",
        "Rejected"
    )

    val filteredChores =
        if (selectedStatus == "All Statuses") {
            chores
        } else {
            chores.filter { chore ->

                val normalized =
                    chore.status
                        .trim()
                        .replace("_", " ")
                        .lowercase()

                normalized ==
                        selectedStatus.lowercase()
            }
        }


    ChorePalScreenBackground(
        isKidsMode = isKidsMode
    ) {

        Scaffold(
            containerColor =
                if (isKidsMode) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.background
                }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(
                    Modifier.height(8.dp)
                )

                /*
                =====================================================
                HEADER
                =====================================================
                */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(
                            Alignment.CenterStart
                        )
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Image(
                        painter = painterResource(
                            id = R.drawable.chorepal_logo
                        ),
                        contentDescription = "ChorePal Logo",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .width(280.dp)
                            .height(110.dp),
                        contentScale = ContentScale.Fit
                    )

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.align(
                            Alignment.CenterEnd
                        )
                    ) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                /*
                =====================================================
                TITLE
                =====================================================
                */

                Text(
                    text = "Update Chore Status",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )

                Spacer(
                    Modifier.height(14.dp)
                )

                /*
                =====================================================
                STATUS FILTER
                =====================================================
                */

                Text(
                    text = "Filter by Status",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    Modifier.height(6.dp)
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            statusMenuExpanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedStatus)
                    }

                    DropdownMenu(
                        expanded = statusMenuExpanded,
                        onDismissRequest = {
                            statusMenuExpanded = false
                        }
                    ) {

                        statusOptions.forEach { status ->

                            DropdownMenuItem(
                                text = {
                                    Text(status)
                                },
                                onClick = {
                                    selectedStatus = status
                                    statusMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(
                    Modifier.height(16.dp)
                )

                /*
                =====================================================
                EMPTY STATE
                =====================================================
                */

                if (filteredChores.isEmpty()) {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (isKidsMode) {
                                    Color.White.copy(alpha = 0.90f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                        )
                    ) {

                        Text(
                            text =
                                if (chores.isEmpty()) {
                                    "No chores available."
                                } else {
                                    "No chores found with this status."
                                },
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                } else {

                    /*
                    =====================================================
                    CHORE LIST
                    =====================================================
                    */

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = filteredChores,
                            key = { chore -> chore.id }
                        ) { chore ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        if (isKidsMode) {
                                            Color.White.copy(
                                                alpha = 0.90f
                                            )
                                        } else {
                                            MaterialTheme
                                                .colorScheme
                                                .surface
                                        }
                                )
                            ) {

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {

                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = chore.title,
                                            fontSize = 18.sp,
                                            fontWeight =
                                                FontWeight.Bold,
                                            modifier =
                                                Modifier.weight(1f)
                                        )

                                        if (isKidsMode) {
                                            Text(
                                                text = "⭐",
                                                fontSize = 22.sp
                                            )
                                        }
                                    }

                                    Spacer(
                                        Modifier.height(6.dp)
                                    )

                                    Text(
                                        text =
                                            "Current Status: ${
                                                formatStatus(
                                                    chore.status
                                                )
                                            }"
                                    )

                                    Spacer(
                                        Modifier.height(12.dp)
                                    )

                                    /*
                                    =====================================
                                    IN PROGRESS BUTTON
                                    =====================================
                                    */

                                    Button(
                                        onClick = {
                                            onUpdateStatus(
                                                chore.id,
                                                "IN_PROGRESS"
                                            )
                                        },
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor =
                                                    Color(0xFFF59E0B),
                                                contentColor =
                                                    Color.White
                                            ),
                                        modifier =
                                            Modifier.fillMaxWidth()
                                    ) {
                                        Text("In Progress")
                                    }

                                    Spacer(
                                        Modifier.height(8.dp)
                                    )

                                    /*
                                    =====================================
                                    COMPLETED BUTTON
                                    =====================================
                                    */

                                    Button(
                                        onClick = {
                                            onUpdateStatus(
                                                chore.id,
                                                "COMPLETED"
                                            )
                                        },
                                        colors =
                                            ButtonDefaults.buttonColors(
                                                containerColor =
                                                    Color(0xFF2E7D32),
                                                contentColor =
                                                    Color.White
                                            ),
                                        modifier =
                                            Modifier.fillMaxWidth()
                                    ) {
                                        Text("Completed")
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(
                                modifier =
                                    Modifier.height(170.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun formatStatus(
    status: String
): String {

    return when (
        status
            .trim()
            .uppercase()
    ) {

        "IN_PROGRESS" ->
            "In Progress"

        "COMPLETED" ->
            "Completed"

        "ASSIGNED" ->
            "Assigned"

        "APPROVED" ->
            "Approved"

        "REJECTED" ->
            "Rejected"

        else ->
            status
    }
}

