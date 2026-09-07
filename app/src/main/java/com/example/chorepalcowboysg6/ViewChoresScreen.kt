package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewChoresScreen(
    title: String,
    chores: List<ChoreRow>,
    canEdit: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onEditChore: (String) -> Unit = {},
    isKidsMode: Boolean = false
) {

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

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {

                    IconButton(
                        onClick = onBack,
                        modifier =
                            Modifier.align(
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
                        modifier =
                            Modifier.align(
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

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )

                Spacer(Modifier.height(16.dp))

                if (chores.isEmpty()) {

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
                            text = "No chores available.",
                            modifier =
                                Modifier.padding(20.dp)
                        )
                    }

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(
                            items = chores,
                            key = { chore -> chore.id }
                        ) { chore ->

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(20.dp),
                                colors =
                                    CardDefaults.cardColors(
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

                                    if (
                                        chore.description
                                            .isNotBlank()
                                    ) {

                                        Spacer(
                                            Modifier.height(6.dp)
                                        )

                                        Text(
                                            text =
                                                chore.description
                                        )
                                    }

                                    Spacer(
                                        Modifier.height(8.dp)
                                    )

                                    Text(
                                        text =
                                            "Due: ${chore.dueDate}"
                                    )

                                    Text(
                                        text =
                                            "Reward: $${chore.rewardAmount}"
                                    )

                                    Text(
                                        text =
                                            "Status: ${chore.status}"
                                    )

                                    if (canEdit) {

                                        Spacer(
                                            Modifier.height(12.dp)
                                        )

                                        Button(
                                            onClick = {
                                                onEditChore(
                                                    chore.id
                                                )
                                            },
                                            modifier =
                                                Modifier.fillMaxWidth()
                                        ) {

                                            Icon(
                                                imageVector =
                                                    Icons.Filled.Edit,
                                                contentDescription =
                                                    "Edit"
                                            )

                                            Spacer(
                                                Modifier.width(8.dp)
                                            )

                                            Text("Edit Chore")
                                        }
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


