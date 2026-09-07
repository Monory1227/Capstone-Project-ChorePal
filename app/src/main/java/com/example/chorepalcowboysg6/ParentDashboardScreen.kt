package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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


/*
=========================================================
PARENT ACTIONS
=========================================================
*/

enum class ParentAction {

    HOME,

    CREATE_CHORE,

    VIEW_CHORES,

    EDIT_CHORES,

    APPROVALS,

    REWARDS,

    AI_INSIGHTS,

    HOUSEHOLD
}


/*
=========================================================
PARENT DASHBOARD
=========================================================
*/

@Composable
fun ParentDashboardScreen(

    firstName: String,

    selectedAction: ParentAction =
        ParentAction.HOME,

    onActionSelected:
        (ParentAction) -> Unit,

    onLogout: () -> Unit,

    successMessage: String? =
        null,

    onClearSuccessMessage:
        () -> Unit = {}

) {

    Scaffold(

        bottomBar = {

            BottomMenuBar(

                selectedAction =
                    selectedAction,

                onActionSelected =
                    onActionSelected
            )
        }

    ) { padding ->


        /*
        =====================================================
        MAIN DASHBOARD CONTENT
        =====================================================
        */

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = 16.dp
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )

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

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)

            ) {


                /*
                HOUSEHOLD / PROFILE BUTTON
                */

                IconButton(

                    onClick = {

                        onActionSelected(
                            ParentAction.HOUSEHOLD
                        )
                    },

                    modifier =
                        Modifier
                            .align(
                                Alignment.CenterStart
                            )
                            .padding(
                                start = 8.dp
                            )

                ) {

                    Icon(

                        imageVector =
                            Icons.Filled.Person,

                        contentDescription =
                            "Profile",

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
                            .width(280.dp)
                            .height(110.dp),

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
                            Modifier.size(34.dp)
                    )
                }
            }


            Spacer(
                Modifier.height(8.dp)
            )


            /*
            =====================================================
            WELCOME MESSAGE
            =====================================================
            */

            Text(

                text =
                    "Welcome, $firstName!",

                fontSize =
                    16.sp,

                modifier =
                    Modifier.align(
                        Alignment.CenterHorizontally
                    )
            )


            /*
            =====================================================
            SUCCESS MESSAGE
            =====================================================
            */

            if (
                successMessage != null
            ) {

                Spacer(
                    Modifier.height(10.dp)
                )


                val green =
                    Color(0xFF2E7D32)


                Surface(

                    tonalElevation =
                        2.dp,

                    shape =
                        RoundedCornerShape(12.dp),

                    modifier =
                        Modifier.fillMaxWidth()

                ) {

                    Row(

                        modifier =
                            Modifier.padding(12.dp),

                        verticalAlignment =
                            Alignment.CenterVertically

                    ) {

                        Icon(

                            imageVector =
                                Icons.Filled.CheckCircle,

                            contentDescription =
                                "Success",

                            tint =
                                green
                        )


                        Spacer(
                            Modifier.width(8.dp)
                        )


                        Text(

                            text =
                                successMessage,

                            color =
                                green
                        )


                        Spacer(
                            Modifier.weight(1f)
                        )


                        TextButton(

                            onClick =
                                onClearSuccessMessage

                        ) {

                            Text(

                                text =
                                    "OK",

                                color =
                                    green
                            )
                        }
                    }
                }
            }


            Spacer(
                Modifier.height(20.dp)
            )


            /*
            =====================================================
            DASHBOARD BUTTONS
            =====================================================
            */

            Column(

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)

            ) {


                /*
                -------------------------------------------------
                ROW 1
                CREATE NEW CHORE / EDIT CHORES
                -------------------------------------------------
                */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {


                    DashboardTile(

                        title =
                            "Create New Chore",

                        icon =
                            Icons.Filled.Add,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.CREATE_CHORE
                        )
                    }


                    DashboardTile(

                        title =
                            "Edit Chores",

                        icon =
                            Icons.Filled.Edit,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.EDIT_CHORES
                        )
                    }
                }


                /*
                -------------------------------------------------
                ROW 2
                CHORE APPROVALS / REWARDS
                -------------------------------------------------
                */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {


                    DashboardTile(

                        title =
                            "Chore Approvals",

                        icon =
                            Icons.Filled.CheckCircle,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.APPROVALS
                        )
                    }


                    DashboardTile(

                        title =
                            "Rewards",

                        icon =
                            Icons.Filled.Star,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.REWARDS
                        )
                    }
                }


                /*
                -------------------------------------------------
                ROW 3
                AI INSIGHTS / MANAGE HOUSEHOLD
                -------------------------------------------------
                */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)

                ) {


                    DashboardTile(

                        title =
                            "AI Insights",

                        icon =
                            Icons.Filled.Info,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.AI_INSIGHTS
                        )
                    }


                    DashboardTile(

                        title =
                            "Manage Household",

                        icon =
                            Icons.Filled.Person,

                        modifier =
                            Modifier.weight(1f)

                    ) {

                        onActionSelected(
                            ParentAction.HOUSEHOLD
                        )
                    }
                }
            }


            /*
            EXTRA SPACE AT BOTTOM
            */

            Spacer(
                Modifier.height(24.dp)
            )
        }
    }
}


/*
=========================================================
DASHBOARD TILE
=========================================================
*/

@Composable
fun DashboardTile(

    title: String,

    icon: ImageVector,

    modifier: Modifier =
        Modifier,

    onClick: () -> Unit

) {

    Card(

        modifier =
            modifier
                .height(120.dp)
                .clickable(
                    onClick =
                        onClick
                ),

        shape =
            RoundedCornerShape(16.dp)

    ) {

        Column(

            modifier =
                Modifier.fillMaxSize(),

            verticalArrangement =
                Arrangement.Center,

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Icon(

                imageVector =
                    icon,

                contentDescription =
                    title,

                modifier =
                    Modifier.size(36.dp)
            )


            Spacer(
                Modifier.height(8.dp)
            )


            Text(

                text =
                    title,

                textAlign =
                    TextAlign.Center
            )
        }
    }
}


/*
=========================================================
PARENT BOTTOM MENU
=========================================================
*/

@Composable
fun BottomMenuBar(

    selectedAction:
    ParentAction,

    onActionSelected:
        (ParentAction) -> Unit

) {

    NavigationBar {


        /*
        HOME
        */

        NavigationBarItem(

            selected =
                selectedAction ==
                        ParentAction.HOME,

            onClick = {

                onActionSelected(
                    ParentAction.HOME
                )
            },

            icon = {

                Icon(

                    imageVector =
                        Icons.Filled.Home,

                    contentDescription =
                        "Home"
                )
            },

            label =
                null
        )


        /*
        CREATE CHORE
        */

        NavigationBarItem(

            selected =
                selectedAction ==
                        ParentAction.CREATE_CHORE,

            onClick = {

                onActionSelected(
                    ParentAction.CREATE_CHORE
                )
            },

            icon = {

                Icon(

                    imageVector =
                        Icons.Filled.Add,

                    contentDescription =
                        "Create"
                )
            },

            label =
                null
        )


        /*
        EDIT CHORES
        */

        NavigationBarItem(

            selected =
                selectedAction ==
                        ParentAction.EDIT_CHORES,

            onClick = {

                onActionSelected(
                    ParentAction.EDIT_CHORES
                )
            },

            icon = {

                Icon(

                    imageVector =
                        Icons.Filled.Edit,

                    contentDescription =
                        "Edit"
                )
            },

            label =
                null
        )


        /*
        CHORE APPROVALS
        */

        NavigationBarItem(

            selected =
                selectedAction ==
                        ParentAction.APPROVALS,

            onClick = {

                onActionSelected(
                    ParentAction.APPROVALS
                )
            },

            icon = {

                Icon(

                    imageVector =
                        Icons.Filled.CheckCircle,

                    contentDescription =
                        "Approvals"
                )
            },

            label =
                null
        )


        /*
        HOUSEHOLD
        */

        NavigationBarItem(

            selected =
                selectedAction ==
                        ParentAction.HOUSEHOLD,

            onClick = {

                onActionSelected(
                    ParentAction.HOUSEHOLD
                )
            },

            icon = {

                Icon(

                    imageVector =
                        Icons.Filled.Person,

                    contentDescription =
                        "Household"
                )
            },

            label =
                null
        )
    }
}

