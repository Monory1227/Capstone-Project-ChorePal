package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RewardsScreen(
    currentRole: String,
    currentUserUid: String,
    chores: List<ChoreRow>,
    children: List<HouseholdMemberRow>,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val black = Color.Black
    val white = Color.White

    val approvedChores = chores.filter { it.status.uppercase() == "APPROVED" }

    var selectedChildUid by rememberSaveable {
        mutableStateOf(
            if (currentRole == "CHILD") currentUserUid else ""
        )
    }
    var selectedChildName by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    if (currentRole == "CHILD" && selectedChildUid.isBlank()) {
        selectedChildUid = currentUserUid
    }

    val displayedChores = if (currentRole == "CHILD") {
        approvedChores.filter { it.assignedChildUid == currentUserUid }
    } else {
        approvedChores.filter { it.assignedChildUid == selectedChildUid }
    }

    val totalEarned = displayedChores.sumOf { it.rewardAmount.toDoubleOrNull() ?: 0.0 }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rewards",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (currentRole == "PARENT" || currentRole == "ADULT") {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedChildName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Child") },
                        placeholder = { Text("Choose a child") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = black,
                            unfocusedBorderColor = black,
                            focusedLabelColor = black,
                            unfocusedLabelColor = black,
                            cursorColor = black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = black,
                            contentColor = white
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Text("Select")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        children.forEach { child ->
                            DropdownMenuItem(
                                text = { Text("${child.firstName} ${child.lastName}") },
                                onClick = {
                                    selectedChildUid = child.uid
                                    selectedChildName = "${child.firstName} ${child.lastName}"
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "Total Earned: $${"%.2f".format(totalEarned)}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (displayedChores.isEmpty()) {
                Text(
                    text = if (currentRole == "CHILD") {
                        "No approved chores yet."
                    } else {
                        "No approved chores for the selected child."
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(displayedChores, key = { it.id }) { chore ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(text = chore.title)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Reward Earned: $${chore.rewardAmount}")
                                Text(text = "Status: ${chore.status}")
                            }
                        }
                    }
                }
            }
        }
    }
}

