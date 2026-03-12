package com.example.chorepalcowboysg6

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegister: (
        String, String, String, String, String,
        String, String, String, String, String
    ) -> Unit,
    onBackToLogin: () -> Unit,
    onViewTerms: () -> Unit
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

    val smallSpace = 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 42.dp, bottom = 18.dp) // ⭐ lowered title
    ) {

        Text(
            "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            "Register to manage kids' chores",
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

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("DOB (MM/DD/YYYY)") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = streetAddress,
            onValueChange = { streetAddress = it },
            label = { Text("Address") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

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

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = zip,
            onValueChange = { zip = it },
            label = { Text("Zip Code") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(smallSpace))

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

            val termsText = buildAnnotatedString {
                append("I agree to the ")
                withStyle(
                    SpanStyle(
                        color = black,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Terms and Conditions")
                }
            }

            Text(
                termsText,
                modifier = Modifier.clickable { onViewTerms() }
            )
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                onRegister(
                    firstName,
                    lastName,
                    username,
                    dob,
                    streetAddress,
                    city,
                    state,
                    zip,
                    email,
                    password
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBackToLogin,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = black),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In")
        }
    }
}