package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AddMemberScreen(
    navController: NavController,
    onLogout: () -> Unit = {
        navController.navigate(Routes.LOGIN) { popUpTo(0) }
    }
) {
    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            /* ---------- HEADER ---------- */

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {

                IconButton(
                    onClick = { navController.popBackStack() },
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Manage your household members.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- VIEW HOUSEHOLD ---------- */
            AddMemberTile(
                title = "View Household",
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "View Household",
                        modifier = Modifier.size(56.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // ✅ NOW IT NAVIGATES
                    navController.navigate(Routes.VIEW_HOUSEHOLD)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            /* ---------- BIG OR ---------- */
            Text(
                text = "OR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select the type of household member you’d like to add.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ---------- ADD MEMBER TILES ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AddMemberTile(
                    title = "Add Child",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Add Child",
                            modifier = Modifier.size(56.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Routes.ADD_CHILD_MEMBER) }
                )

                AddMemberTile(
                    title = "Add Adult",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Add Adult",
                            modifier = Modifier.size(56.dp)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Routes.ADD_ADULT_MEMBER) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You’ll enter details on the next screen.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddMemberTile(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}