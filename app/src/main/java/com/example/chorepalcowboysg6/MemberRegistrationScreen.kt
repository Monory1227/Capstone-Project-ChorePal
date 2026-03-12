package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddChildMemberScreen(
    onSave: (
        String, String, String, String, String,
        String, String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    MemberRegistrationScreen(
        memberTypeTitle = "Add Child Member",
        requireEmail = false, // ✅ child has no email field
        onSave = onSave,
        onBack = onBack,
        onLogout = onLogout
    )
}

@Composable
fun AddAdultMemberScreen(
    onSave: (
        String, String, String, String, String,
        String, String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    MemberRegistrationScreen(
        memberTypeTitle = "Add Adult Member",
        requireEmail = true, // ✅ adult keeps email field
        onSave = onSave,
        onBack = onBack,
        onLogout = onLogout
    )
}

@Composable
private fun MemberRegistrationScreen(
    memberTypeTitle: String,
    requireEmail: Boolean,
    onSave: (
        String, String, String, String, String,
        String, String, String, String, String
    ) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
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
        cursorColor = black
    )

    val requiredFilled =
        firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                username.isNotBlank() &&
                dob.isNotBlank() &&
                streetAddress.isNotBlank() &&
                city.isNotBlank() &&
                state.isNotBlank() &&
                zip.isNotBlank() &&
                password.isNotBlank() &&
                (!requireEmail || email.isNotBlank())

    val canSave = requiredFilled && acceptedTerms

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // ---------- HEADER (MATCHES DASHBOARD) ----------
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

            // ---------- FORM ----------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 18.dp)
            ) {
                Text(
                    text = memberTypeTitle,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") },
                    singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(lastName, { lastName = it }, label = { Text("Last Name") },
                    singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(username, { username = it }, label = { Text("Username") },
                    singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("DOB (MM/DD/YYYY)") },
                    singleLine = true,
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(streetAddress, { streetAddress = it }, label = { Text("Address") },
                    singleLine = true, colors = fieldColors, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(city, { city = it }, label = { Text("City") },
                        singleLine = true, colors = fieldColors, modifier = Modifier.weight(1f))

                    Spacer(Modifier.width(8.dp))

                    OutlinedTextField(state, { state = it }, label = { Text("State") },
                        singleLine = true, colors = fieldColors, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = zip,
                    onValueChange = { zip = it },
                    label = { Text("Zip Code") },
                    singleLine = true,
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // ✅ Email only for Adult
                if (requireEmail) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        colors = fieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Child: always blank email
                    email = ""
                }

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

                // ✅ Terms checkbox for BOTH
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
                            username.trim(),
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
                        contentColor = white,
                        disabledContainerColor = black.copy(alpha = 0.5f),
                        disabledContentColor = white
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Member")
                }

                Spacer(Modifier.height(8.dp))

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