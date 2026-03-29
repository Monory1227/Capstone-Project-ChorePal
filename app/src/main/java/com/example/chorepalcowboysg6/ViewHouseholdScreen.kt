package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ViewHouseholdScreen(
    members: List<HouseholdMemberRow> = emptyList(),
    selectedAction: ParentAction = ParentAction.HOUSEHOLD,
    onActionSelected: (ParentAction) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onMemberSelected: (String) -> Unit
) {
    val sortedMembers = members.sortedWith(
        compareBy<HouseholdMemberRow> {
            when (it.userType.uppercase()) {
                "PARENT" -> 0
                "ADULT" -> 1
                "CHILD" -> 2
                else -> 3
            }
        }.thenBy { it.firstName.uppercase() }
            .thenBy { it.lastName.uppercase() }
    )

    val black = Color.Black
    val white = Color.White

    Scaffold(
        bottomBar = {
            BottomMenuBar(
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
                text = "Household Members",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeaderCell("First Name", Modifier.weight(1f))
                    TableHeaderCell("Last Name", Modifier.weight(1f))
                    TableHeaderCell("Type", Modifier.weight(0.8f))
                    TableHeaderCell("Action", Modifier.width(76.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (sortedMembers.isEmpty()) {
                Surface(
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No household members yet.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(
                        items = sortedMembers,
                        key = { it.uid.ifBlank { "${it.firstName}_${it.lastName}_${it.userType}" } }
                    ) { member ->
                        Surface(
                            tonalElevation = 1.dp,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableBodyCell(member.firstName, Modifier.weight(1f))
                                TableBodyCell(member.lastName, Modifier.weight(1f))
                                TableBodyCell(formatRole(member.userType), Modifier.weight(0.8f))

                                Button(
                                    onClick = { onMemberSelected(member.uid) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = black,
                                        contentColor = white
                                    ),
                                    modifier = Modifier.width(76.dp)
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

@Composable
private fun TableHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@Composable
private fun TableBodyCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 14.sp,
        modifier = modifier
    )
}

