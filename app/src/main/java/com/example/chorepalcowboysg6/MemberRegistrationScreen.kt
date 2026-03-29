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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddChildMemberScreen(
    onSave: (
        String, String, String, String, String,
        String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    errorMessage: String? = null
) {
    MemberRegistrationScreen(
        memberTypeTitle = "Add Child Member",
        onSave = onSave,
        onBack = onBack,
        onLogout = onLogout,
        errorMessage = errorMessage
    )
}

@Composable
fun AddAdultMemberScreen(
    onSave: (
        String, String, String, String, String,
        String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    errorMessage: String? = null
) {
    MemberRegistrationScreen(
        memberTypeTitle = "Add Adult Member",
        onSave = onSave,
        onBack = onBack,
        onLogout = onLogout,
        errorMessage = errorMessage
    )
}

@Composable
private fun MemberRegistrationScreen(
    memberTypeTitle: String,
    onSave: (
        String, String, String, String, String,
        String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    errorMessage: String? = null
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }

    var streetAddress by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("") }
    var zip by rememberSaveable { mutableStateOf("") }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

    val black = Color.Black
    val white = Color.White

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
                dob.trim().isNotBlank() &&
                streetAddress.trim().isNotBlank() &&
                city.trim().isNotBlank() &&
                state.trim().isNotBlank() &&
                zip.trim().isNotBlank() &&
                email.trim().isNotBlank() &&
                password.isNotBlank() &&
                acceptedTerms

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
                    text = memberTypeTitle,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
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
                }

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
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("DOB (MM/DD/YYYY)") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = streetAddress,
                    onValueChange = { streetAddress = it },
                    label = { Text("Address") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth()) {
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
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = black,
                            uncheckedColor = black,
                            checkmarkColor = white
                        )
                    )
                    Text("I agree to the Terms and Conditions")
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSave(
                            firstName.trim(),
                            lastName.trim(),
                            dob.trim(),
                            streetAddress.trim(),
                            city.trim(),
                            state.trim(),
                            zip.trim(),
                            email.trim(),
                            password
                        )
                    },
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = black,
                        contentColor = white
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Member")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}