package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
fun RewardsScreen(
    currentRole: String,
    currentUserUid: String,
    chores: List<ChoreRow>,
    children: List<HouseholdMemberRow>,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isKidsMode: Boolean = false
) {

    val role = currentRole.uppercase()

    var selectedChildUid by remember(
        currentUserUid,
        children,
        role
    ) {
        mutableStateOf(
            if (role == "CHILD") {
                currentUserUid
            } else {
                children.firstOrNull()?.uid.orEmpty()
            }
        )
    }

    /*
    =========================================================
    SELECT CHORES FOR CURRENT CHILD
    =========================================================
    */

    val selectedChores =
        if (role == "CHILD") {

            chores.filter {
                it.assignedChildUid == currentUserUid
            }

        } else {

            chores.filter {
                it.assignedChildUid == selectedChildUid
            }
        }


    /*
    =========================================================
    ONLY APPROVED CHORES EARN MONEY
    =========================================================

    COMPLETED does NOT count as money earned.

    APPROVED means the parent has approved the completed chore,
    so the reward can now be added to Total Earned.
    */

    val approvedChores =
        selectedChores.filter {
            it.status
                .trim()
                .uppercase() == "APPROVED"
        }


    /*
    =========================================================
    TOTAL EARNED
    =========================================================
    */

    val totalEarned =
        approvedChores.sumOf { chore ->
            chore.rewardAmount.toDoubleOrNull() ?: 0.0
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

                    /*
                    BACK BUTTON
                    */

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

                            contentDescription =
                                "Back",

                            modifier =
                                Modifier.size(34.dp)
                        )
                    }


                    /*
                    CHOREPAL LOGO
                    */

                    Image(
                        painter =
                            painterResource(
                                id = R.drawable.chorepal_logo
                            ),

                        contentDescription =
                            "ChorePal Logo",

                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .width(280.dp)
                                .height(110.dp),

                        contentScale =
                            ContentScale.Fit
                    )


                    /*
                    LOGOUT BUTTON
                    */

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

                            contentDescription =
                                "Logout",

                            modifier =
                                Modifier.size(34.dp)
                        )
                    }
                }


                /*
                =====================================================
                TITLE
                =====================================================
                */

                Text(
                    text = "Rewards",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )

                Spacer(
                    Modifier.height(16.dp)
                )


                /*
                =====================================================
                PARENT / ADULT CHILD SELECTOR
                =====================================================
                */

                if (
                    role == "PARENT" ||
                    role == "ADULT"
                ) {

                    Text(
                        text = "Select Child",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Row(
                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        children.forEach { child ->

                            Card(
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .clickable {

                                            selectedChildUid =
                                                child.uid
                                        },

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            if (
                                                selectedChildUid ==
                                                child.uid
                                            ) {

                                                MaterialTheme
                                                    .colorScheme
                                                    .primaryContainer

                                            } else {

                                                MaterialTheme
                                                    .colorScheme
                                                    .surface
                                            }
                                    )
                            ) {

                                Text(
                                    text =
                                        "${child.firstName} ${child.lastName}",

                                    modifier =
                                        Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(
                        Modifier.height(16.dp)
                    )
                }


                /*
                =====================================================
                TOTAL EARNED CARD
                =====================================================
                */

                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(22.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                if (isKidsMode) {

                                    Color.White.copy(
                                        alpha = 0.92f
                                    )

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .surface
                                }
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(20.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        if (isKidsMode) {

                            Text(
                                text = "⭐",
                                fontSize = 36.sp
                            )
                        }

                        Text(
                            text = "Total Earned",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "$%.2f".format(totalEarned),

                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(
                    Modifier.height(16.dp)
                )


                /*
                =====================================================
                APPROVED CHORES
                =====================================================
                */

                Text(
                    text = "Approved Chores",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    Modifier.height(8.dp)
                )


                /*
                =====================================================
                NO APPROVED CHORES
                =====================================================
                */

                if (approvedChores.isEmpty()) {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

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

                        Text(
                            text =
                                "No approved chores yet.",

                            modifier =
                                Modifier.padding(20.dp)
                        )
                    }


                } else {


                    /*
                    =================================================
                    APPROVED CHORE LIST
                    =================================================
                    */

                    LazyColumn(
                        modifier =
                            Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        items(
                            items = approvedChores,
                            key = { chore -> chore.id }
                        ) { chore ->

                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),

                                shape =
                                    RoundedCornerShape(18.dp),

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
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                ) {

                                    /*
                                    CHORE TITLE
                                    */

                                    Text(
                                        text = chore.title,
                                        fontSize = 17.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )


                                    Spacer(
                                        Modifier.height(4.dp)
                                    )


                                    /*
                                    REWARD
                                    */

                                    Text(
                                        text =
                                            "Reward: $${chore.rewardAmount}"
                                    )


                                    /*
                                    STATUS
                                    */

                                    Text(
                                        text =
                                            "Status: ${chore.status}"
                                    )
                                }
                            }
                        }


                        /*
                        BOTTOM SPACING
                        */

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

