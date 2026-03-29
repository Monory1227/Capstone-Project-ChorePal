package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditMemberScreen(
    member: HouseholdMemberRow?,
    onSave: (String, String, String, String, String, String, String) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    errorMessage: String? = null
) {
    var firstName by rememberSaveable(member?.uid) { mutableStateOf(member?.firstName.orEmpty()) }
    var lastName by rememberSaveable(member?.uid) { mutableStateOf(member?.lastName.orEmpty()) }
    var address by rememberSaveable(member?.uid) { mutableStateOf(member?.address.orEmpty()) }
    var city by rememberSaveable(member?.uid) { mutableStateOf(member?.city.orEmpty()) }
    var state by rememberSaveable(member?.uid) { mutableStateOf(member?.state.orEmpty()) }
    var zip by rememberSaveable(member?.uid) { mutableStateOf(member?.zip.orEmpty()) }
    var email by rememberSaveable(member?.uid) { mutableStateOf(member?.email.orEmpty()) }

    val black = Color.Black
    val white = Color.White
    val isParent = member?.userType?.equals("PARENT", ignoreCase = true) == true

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = black,
        unfocusedBorderColor = black,
        focusedLabelColor = black,
        unfocusedLabelColor = black,
        cursorColor = black
    )

    val canSave =
        firstName.trim().isNotBlank() &&
                lastName.trim().isNotBlank() &&
                address.trim().isNotBlank() &&
                city.trim().isNotBlank() &&
                state.trim().isNotBlank() &&
                zip.trim().isNotBlank() &&
                email.trim().isNotBlank()

    Scaffold { padding ->
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 18.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Edit Member",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = formatRole(member?.userType.orEmpty()),
                    onValueChange = {},
                    label = { Text("Member Type") },
                    singleLine = true,
                    enabled = false,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = zip,
                    onValueChange = { zip = it },
                    label = { Text("Zip Code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                if (!errorMessage.isNullOrBlank()) {
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        onSave(
                            firstName.trim(),
                            lastName.trim(),
                            address.trim(),
                            city.trim(),
                            state.trim(),
                            zip.trim(),
                            email.trim()
                        )
                    },
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = black,
                        contentColor = white,
                        disabledContainerColor = black.copy(alpha = 0.5f),
                        disabledContentColor = white
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                Spacer(Modifier.height(8.dp))

                if (!isParent) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = white
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Member")
                    }

                    Spacer(Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}