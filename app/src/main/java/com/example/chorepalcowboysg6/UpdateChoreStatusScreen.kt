package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun UpdateChoreStatusScreen(
    chores: List<ChoreRow>,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    val black = Color.Black
    val white = Color.White
    val darkGreen = Color(0xFF1B5E20)
    val orange = Color(0xFFEF6C00)
    val red = Color(0xFFC62828)

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
                text = "Update Chore Status",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (chores.isEmpty()) {
                Text(
                    text = "No chores assigned to you.",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chores, key = { it.id }) { chore ->
                        val status = chore.status.uppercase()

                        val statusColor = when (status) {
                            "COMPLETED" -> darkGreen
                            "IN PROGRESS" -> orange
                            "REJECTED" -> red
                            else -> black
                        }

                        val canUpdateStatus =
                            status != "APPROVED"

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(text = chore.title)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Due Date: ${chore.dueDate}")
                                Text(text = "Reward: $${chore.rewardAmount}")
                                Text(
                                    text = "Current Status: $status",
                                    color = statusColor
                                )

                                if (canUpdateStatus) {
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = { onUpdateStatus(chore.id, "IN PROGRESS") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = orange,
                                            contentColor = white
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Mark In Progress")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = { onUpdateStatus(chore.id, "COMPLETED") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = darkGreen,
                                            contentColor = white
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Mark Completed")
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


