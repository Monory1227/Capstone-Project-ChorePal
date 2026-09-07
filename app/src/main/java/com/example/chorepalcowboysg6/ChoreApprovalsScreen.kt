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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun ChoreApprovalsScreen(

    chores: List<ChoreRow>,

    onBack: () -> Unit,

    onLogout: () -> Unit,

    onApprove: (String) -> Unit,

    onReject: (String) -> Unit

) {

    val black =
        Color.Black

    val white =
        Color.White

    /*
    =========================================================
    BUTTON COLORS
    =========================================================
    */

    val approveGreen =
        Color(0xFF1B5E20)

    val rejectRed =
        Color(0xFFC62828)


    /*
    =========================================================
    ONLY SHOW CHORES WAITING FOR PARENT REVIEW
    =========================================================
    */

    val completedChores =
        chores.filter {

            it.status.uppercase() ==
                    "COMPLETED"
        }


    Scaffold { padding ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = 16.dp
                    )
        ) {


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            /*
            =================================================
            HEADER
            =================================================
            */

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            120.dp
                        )
            ) {


                /*
                BACK BUTTON
                */

                IconButton(

                    onClick =
                        onBack,

                    modifier =
                        Modifier.align(
                            Alignment.CenterStart
                        )
                ) {

                    Icon(

                        imageVector =
                            Icons
                                .AutoMirrored
                                .Filled
                                .ArrowBack,

                        contentDescription =
                            "Back",

                        modifier =
                            Modifier.size(
                                34.dp
                            )
                    )
                }


                /*
                EXISTING CHOREPAL LOGO
                */

                Image(

                    painter =
                        painterResource(
                            id =
                                R.drawable.chorepal_logo
                        ),

                    contentDescription =
                        "ChorePal Logo",

                    modifier =
                        Modifier
                            .align(
                                Alignment.Center
                            )
                            .width(
                                280.dp
                            )
                            .height(
                                110.dp
                            ),

                    contentScale =
                        ContentScale.Fit
                )


                /*
                LOGOUT BUTTON
                */

                IconButton(

                    onClick =
                        onLogout,

                    modifier =
                        Modifier.align(
                            Alignment.CenterEnd
                        )
                ) {

                    Icon(

                        imageVector =
                            Icons
                                .AutoMirrored
                                .Filled
                                .ExitToApp,

                        contentDescription =
                            "Logout",

                        modifier =
                            Modifier.size(
                                34.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text =
                    "Chore Approvals",

                modifier =
                    Modifier.align(
                        Alignment.CenterHorizontally
                    ),

                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            /*
            =================================================
            NO CHORES
            =================================================
            */

            if (
                completedChores.isEmpty()
            ) {

                Text(

                    text =
                        "No completed chores waiting for review.",

                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )

            } else {


                /*
                =================================================
                COMPLETED CHORE LIST
                =================================================
                */

                LazyColumn(

                    verticalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        ),

                    contentPadding =
                        PaddingValues(
                            bottom = 12.dp
                        )

                ) {


                    items(

                        items =
                            completedChores,

                        key = {
                            it.id
                        }

                    ) { chore ->


                        Card(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                        ) {


                            Column(

                                modifier =
                                    Modifier.padding(
                                        14.dp
                                    )
                            ) {


                                /*
                                =========================================
                                BASIC CHORE INFORMATION
                                =========================================
                                */

                                Text(

                                    text =
                                        chore.title,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            4.dp
                                        )
                                )


                                Text(
                                    text =
                                        "Assigned To: ${chore.assignedChildName}"
                                )


                                Text(
                                    text =
                                        "Due Date: ${chore.dueDate}"
                                )


                                Text(
                                    text =
                                        "Reward: \$${chore.rewardAmount}"
                                )


                                Text(
                                    text =
                                        "Status: ${chore.status}"
                                )


                                if (
                                    chore.description
                                        .isNotBlank()
                                ) {

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                6.dp
                                            )
                                    )


                                    Text(

                                        text =
                                            chore.description
                                    )
                                }


                                /*
                                =========================================
                                CHILD SUBMITTED PHOTO
                                =========================================
                                */

                                if (
                                    chore.photoUrl
                                        .isNotBlank()
                                ) {

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                14.dp
                                            )
                                    )


                                    Text(

                                        text =
                                            "Submitted Photo",

                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleMedium
                                    )


                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                8.dp
                                            )
                                    )


                                    AsyncImage(

                                        model =
                                            chore.photoUrl,

                                        contentDescription =
                                            "Submitted chore photo",

                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(
                                                    250.dp
                                                ),

                                        contentScale =
                                            ContentScale.Crop
                                    )

                                } else {

                                    /*
                                    Older completed chores may not have
                                    a photo because they were completed
                                    before AI verification was added.
                                    */

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                14.dp
                                            )
                                    )


                                    Text(

                                        text =
                                            "No chore photo was submitted."
                                    )
                                }


                                /*
                                =========================================
                                AI ANALYSIS
                                =========================================
                                */

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            16.dp
                                        )
                                )


                                Text(

                                    text =
                                        "AI Verification",

                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            8.dp
                                        )
                                )


                                when (
                                    chore.aiStatus
                                        .uppercase()
                                ) {


                                    /*
                                    =====================================
                                    AI CURRENTLY ANALYZING
                                    =====================================
                                    */

                                    "ANALYZING" -> {

                                        Text(

                                            text =
                                                "AI is analyzing the submitted photo..."
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(
                                                    8.dp
                                                )
                                        )


                                        LinearProgressIndicator(

                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                        )
                                    }


                                    /*
                                    =====================================
                                    AI THINKS JOB IS COMPLETE
                                    =====================================
                                    */

                                    "COMPLETE" -> {

                                        Text(

                                            text =
                                                "AI Result: Job appears COMPLETE",

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .titleMedium
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(
                                                    4.dp
                                                )
                                        )


                                        Text(

                                            text =
                                                "Confidence: ${
                                                    (
                                                            chore.aiConfidence *
                                                                    100
                                                            )
                                                        .toInt()
                                                }%"
                                        )


                                        if (
                                            chore.aiAnalysis
                                                .isNotBlank()
                                        ) {

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        6.dp
                                                    )
                                            )


                                            Text(

                                                text =
                                                    chore.aiAnalysis
                                            )
                                        }


                                        if (
                                            chore.aiIssues
                                                .isNotEmpty()
                                        ) {

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        8.dp
                                                    )
                                            )


                                            Text(
                                                text =
                                                    "AI noticed:"
                                            )


                                            chore.aiIssues
                                                .forEach { issue ->

                                                    Text(

                                                        text =
                                                            "• $issue"
                                                    )
                                                }
                                        }
                                    }


                                    /*
                                    =====================================
                                    AI THINKS JOB IS INCOMPLETE
                                    =====================================
                                    */

                                    "INCOMPLETE" -> {

                                        Text(

                                            text =
                                                "AI Result: Job appears INCOMPLETE",

                                            style =
                                                MaterialTheme
                                                    .typography
                                                    .titleMedium
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(
                                                    4.dp
                                                )
                                        )


                                        Text(

                                            text =
                                                "Confidence: ${
                                                    (
                                                            chore.aiConfidence *
                                                                    100
                                                            )
                                                        .toInt()
                                                }%"
                                        )


                                        if (
                                            chore.aiAnalysis
                                                .isNotBlank()
                                        ) {

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        6.dp
                                                    )
                                            )


                                            Text(

                                                text =
                                                    chore.aiAnalysis
                                            )
                                        }


                                        if (
                                            chore.aiIssues
                                                .isNotEmpty()
                                        ) {

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        8.dp
                                                    )
                                            )


                                            Text(
                                                text =
                                                    "AI noticed:"
                                            )


                                            chore.aiIssues
                                                .forEach { issue ->

                                                    Text(

                                                        text =
                                                            "• $issue"
                                                    )
                                                }
                                        }
                                    }


                                    /*
                                    =====================================
                                    AI FAILED
                                    =====================================
                                    */

                                    "ERROR" -> {

                                        Text(

                                            text =
                                                "AI analysis could not be completed."
                                        )


                                        if (
                                            chore.aiAnalysis
                                                .isNotBlank()
                                        ) {

                                            Spacer(
                                                modifier =
                                                    Modifier.height(
                                                        6.dp
                                                    )
                                            )


                                            Text(

                                                text =
                                                    chore.aiAnalysis
                                            )
                                        }
                                    }


                                    /*
                                    =====================================
                                    NO AI RESULT

                                    This allows older completed chores
                                    to continue working.
                                    =====================================
                                    */

                                    else -> {

                                        if (
                                            chore.photoUrl
                                                .isNotBlank()
                                        ) {

                                            Text(

                                                text =
                                                    "AI result is not available yet."
                                            )

                                        } else {

                                            Text(

                                                text =
                                                    "AI verification was not used for this chore."
                                            )
                                        }
                                    }
                                }


                                /*
                                =========================================
                                PARENT REMAINS FINAL DECISION MAKER
                                =========================================
                                */

                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            12.dp
                                        )
                                )


                                Text(

                                    text =
                                        "AI analysis is only a recommendation. You make the final approval decision."
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            12.dp
                                        )
                                )


                                /*
                                =========================================
                                APPROVE BUTTON - DARK GREEN
                                =========================================
                                */

                                Button(

                                    onClick = {

                                        onApprove(
                                            chore.id
                                        )
                                    },

                                    colors =
                                        ButtonDefaults
                                            .buttonColors(

                                                containerColor =
                                                    approveGreen,

                                                contentColor =
                                                    white
                                            ),

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                ) {

                                    Text(
                                        "Approve"
                                    )
                                }


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            8.dp
                                        )
                                )


                                /*
                                =========================================
                                REJECT BUTTON - RED
                                =========================================
                                */

                                Button(

                                    onClick = {

                                        onReject(
                                            chore.id
                                        )
                                    },

                                    colors =
                                        ButtonDefaults
                                            .buttonColors(

                                                containerColor =
                                                    rejectRed,

                                                contentColor =
                                                    white
                                            ),

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                ) {

                                    Text(
                                        "Reject"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

