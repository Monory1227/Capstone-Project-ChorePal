package com.example.chorepalcowboysg6

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditChoreScreen(
    chore: ChoreRow?,
    childOptions: List<ChildOption>,
    onSave: (String, String, String, String, String, String) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    var title by rememberSaveable(chore?.id) { mutableStateOf(chore?.title.orEmpty()) }
    var description by rememberSaveable(chore?.id) { mutableStateOf(chore?.description.orEmpty()) }
    var dueDate by rememberSaveable(chore?.id) { mutableStateOf(chore?.dueDate.orEmpty()) }
    var rewardAmount by rememberSaveable(chore?.id) { mutableStateOf(chore?.rewardAmount.orEmpty()) }
    var assignedChildUid by rememberSaveable(chore?.id) { mutableStateOf(chore?.assignedChildUid.orEmpty()) }
    var assignedChildName by rememberSaveable(chore?.id) { mutableStateOf(chore?.assignedChildName.orEmpty()) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(chore?.id) {
        title = chore?.title.orEmpty()
        description = chore?.description.orEmpty()
        dueDate = chore?.dueDate.orEmpty()
        rewardAmount = chore?.rewardAmount.orEmpty()
        assignedChildUid = chore?.assignedChildUid.orEmpty()
        assignedChildName = chore?.assignedChildName.orEmpty()
    }

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
        title.isNotBlank() &&
                description.isNotBlank() &&
                dueDate.isNotBlank() &&
                rewardAmount.isNotBlank() &&
                assignedChildUid.isNotBlank()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

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
                    .padding(bottom = 18.dp)
            ) {
                Text(
                    text = "Edit Chore",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Chore Title") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 3,
                    maxLines = 5,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date") },
                    placeholder = { Text("MM/DD/YYYY") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = rewardAmount,
                    onValueChange = { rewardAmount = it },
                    label = { Text("Reward Amount") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = assignedChildName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign To Child") },
                        placeholder = { Text("Select a child") },
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = black,
                            contentColor = white
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                    ) {
                        Text("Select")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        childOptions.forEach { child ->
                            DropdownMenuItem(
                                text = { Text(child.displayName) },
                                onClick = {
                                    assignedChildUid = child.uid
                                    assignedChildName = child.displayName
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onSave(
                            title.trim(),
                            description.trim(),
                            dueDate.trim(),
                            rewardAmount.trim(),
                            assignedChildUid.trim(),
                            assignedChildName.trim()
                        )
                    },
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = black,
                        contentColor = white
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                Spacer(modifier = Modifier.height(8.dp))

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

