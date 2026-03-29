package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onForgotCredentials: () -> Unit,
    onRegister: () -> Unit,
    errorMessage: String? = null
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val black = Color.Black
    val white = Color.White

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = black,
        unfocusedBorderColor = black,
        focusedLabelColor = black,
        unfocusedLabelColor = black,
        cursorColor = black
    )

    val canLogin = email.trim().isNotBlank() && password.isNotBlank()

    val isSuccessMessage = errorMessage?.contains("successfully", ignoreCase = true) == true
    val messageColor = if (isSuccessMessage) Color(0xFF1B5E20) else MaterialTheme.colorScheme.error
    val messageBackground = if (isSuccessMessage) Color(0xFFE8F5E9) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.chorepal_logo),
            contentDescription = "ChorePal Logo",
            modifier = Modifier
                .width(260.dp)
                .height(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Sign in with your ChorePal account",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

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
                color = messageBackground,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = messageColor,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot email or password?",
            modifier = Modifier.clickable { onForgotCredentials() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLogin(email.trim(), password) },
            enabled = canLogin,
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Don’t have a primary parent account?")

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRegister,
            colors = ButtonDefaults.buttonColors(
                containerColor = black,
                contentColor = white
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Primary Parent Account")
        }
    }
}

