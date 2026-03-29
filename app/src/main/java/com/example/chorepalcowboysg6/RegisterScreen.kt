package com.example.chorepalcowboysg6

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun RegisterScreen(
    onRegister: (
        String, String, String, String, String,
        String, String, String, String
    ) -> Unit,
    onBackToLogin: () -> Unit,
    onViewTerms: () -> Unit,
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

    val canRegister =
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 72.dp, bottom = 24.dp)
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Register to manage kids' chores",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Create a primary parent account here. Child and additional adult accounts can be added after signing in.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 12.dp)
        )


        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("DOB (MM/DD/YYYY)") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = streetAddress,
            onValueChange = { streetAddress = it },
            label = { Text("Address") },
            singleLine = true,
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = state,
                onValueChange = { state = it },
                label = { Text("State") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = zip,
            onValueChange = { zip = it },
            label = { Text("Zip Code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        if (!errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
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

        Spacer(modifier = Modifier.height(10.dp))

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

            Text(
                text = "I agree to the Terms and Conditions",
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                onRegister(
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
            enabled = canRegister,
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white,
                disabledContainerColor = black.copy(alpha = 0.5f),
                disabledContentColor = white
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Register",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBackToLogin,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = black
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Back to Login",
                fontSize = 16.sp,
                color = black
            )
        }
    }
}


