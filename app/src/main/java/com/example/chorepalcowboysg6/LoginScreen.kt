package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLogin: (username: String, password: String) -> Unit,
    onForgotCredentials: () -> Unit,
    onRegister: () -> Unit
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    val black = Color.Black
    val white = Color.White

    val trimmedUsername = username.trim()
    val canSubmit = trimmedUsername.isNotBlank() && password.isNotBlank()

    fun submit() {
        val u = trimmedUsername
        val p = password
        error = when {
            u.isBlank() || p.isBlank() -> "Please enter username and password."
            else -> null
        }
        if (error == null) onLogin(u, p)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.chorepal_logo),
            contentDescription = "ChorePal Logo",
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(150.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Sign in with your ChorePal account",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                if (error != null) error = null
            },
            label = { Text("Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = black,
                unfocusedBorderColor = black,
                focusedLabelColor = black,
                cursorColor = black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (error != null) error = null
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { if (canSubmit) submit() }),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Hide" else "Show")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = black,
                unfocusedBorderColor = black,
                focusedLabelColor = black,
                cursorColor = black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onForgotCredentials,
            colors = ButtonDefaults.textButtonColors(contentColor = black),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "Forgot username or password?",
                fontWeight = FontWeight.Normal
            )
        }

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(4.dp))

        Button(
            onClick = { submit() },
            enabled = canSubmit,
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white,
                disabledContainerColor = black.copy(alpha = 0.5f),
                disabledContentColor = white.copy(alpha = 0.9f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Don't have an account?",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Register to manage kids' chores!",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onRegister,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = black),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}