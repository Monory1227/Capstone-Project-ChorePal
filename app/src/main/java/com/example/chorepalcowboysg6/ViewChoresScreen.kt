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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ViewChoresScreen(
    title: String,
    chores: List<ChoreRow>,
    canEdit: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onEditChore: (String) -> Unit = {}
) {
    val black = Color.Black
    val white = Color.White

    val sortedChores = chores.sortedWith(
        compareBy<ChoreRow> {
            when (it.status.uppercase()) {
                "REJECTED" -> 0
                "OPEN" -> 1
                "IN PROGRESS" -> 2
                "COMPLETED" -> 3
                "APPROVED" -> 4
                else -> 5
            }
        }.thenBy { it.dueDate }
            .thenBy { it.title.uppercase() }
    )

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
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (sortedChores.isEmpty()) {
                Text(
                    text = "No chores found.",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(sortedChores, key = { it.id }) { chore ->
                        val isApproved = chore.status.uppercase() == "APPROVED"
                        val showEditButton = canEdit && !isApproved

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    text = chore.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Assigned To: ${chore.assignedChildName}")
                                Text("Due Date: ${chore.dueDate}")
                                Text("Reward: $${chore.rewardAmount}")
                                Text("Created By: ${chore.createdByName}")
                                Text("Status: ${chore.status}")

                                if (chore.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(chore.description)
                                }

                                if (showEditButton) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onEditChore(chore.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = black,
                                            contentColor = white
                                        )
                                    ) {
                                        Text("Edit")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


