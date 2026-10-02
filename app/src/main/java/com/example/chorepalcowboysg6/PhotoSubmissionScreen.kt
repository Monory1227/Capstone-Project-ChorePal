package com.example.chorepalcowboysg6

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
fun PhotoSubmissionScreen(
    chore: ChoreRow?,
    isSubmitting: Boolean,
    errorMessage: String?,
    onSubmitPhoto: (Uri) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {

    var selectedPhoto by remember {
        mutableStateOf<Uri?>(null)
    }

    /*
    Purple used for the photo buttons.
    */

    val purple =
        Color(0xFF7E57C2)

    val disabledPurple =
        Color(0xFFB39DDB)

    val white =
        Color.White


    /*
    =========================================================
    PHOTO PICKER
    =========================================================
    */

    val photoPicker =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.PickVisualMedia()
        ) { uri ->

            if (uri != null) {
                selectedPhoto = uri
            }
        }


    ChorePalScreenBackground(
        isKidsMode = true
    ) {

        Scaffold(
            containerColor =
                Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = 16.dp
                    )
            ) {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                /*
                =====================================================
                HEADER
                =====================================================
                */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {

                    /*
                    BACK
                    */

                    IconButton(
                        onClick = onBack,
                        modifier =
                            Modifier.align(
                                Alignment.CenterStart
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons
                                    .AutoMirrored
                                    .Filled
                                    .ArrowBack,

                            contentDescription =
                                "Back",

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
                                    R.drawable
                                        .chorepal_logo
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
                    LOGOUT
                    */

                    IconButton(
                        onClick = onLogout,
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


                /*
                =====================================================
                TITLE
                =====================================================
                */

                Text(
                    text =
                        "Submit Chore Photo",

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    modifier =
                        Modifier.align(
                            Alignment.CenterHorizontally
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                /*
                =====================================================
                CHORE INFORMATION
                =====================================================
                */

                if (chore != null) {

                    Text(
                        text =
                            chore.title,

                        style =
                            MaterialTheme
                                .typography
                                .titleLarge
                    )


                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )


                    Text(
                        text =
                            chore.description
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                /*
                =====================================================
                PHOTO PREVIEW
                =====================================================
                */

                if (selectedPhoto != null) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        colors = CardDefaults.cardColors(containerColor = white)
                    ) {

                        AsyncImage(
                            model =
                                selectedPhoto,

                            contentDescription =
                                "Selected chore photo",

                            modifier =
                                Modifier.fillMaxSize(),

                            contentScale =
                                ContentScale.Crop
                        )
                    }

                } else {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        colors = CardDefaults.cardColors(containerColor = white)
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally,

                            verticalArrangement =
                                Arrangement.Center
                        ) {

                            Text(
                                text = "📷",

                                style =
                                    MaterialTheme
                                        .typography
                                        .displayMedium
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )


                            Text(
                                text =
                                    "Upload a photo showing your completed chore."
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                /*
                =====================================================
                CHOOSE PHOTO / CHOOSE DIFFERENT PHOTO

                PURPLE BACKGROUND
                =====================================================
                */

                Button(
                    onClick = {

                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts
                                    .PickVisualMedia
                                    .ImageOnly
                            )
                        )
                    },

                    enabled =
                        !isSubmitting,

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                purple,

                            contentColor =
                                white,

                            disabledContainerColor =
                                disabledPurple,

                            disabledContentColor =
                                white
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            if (selectedPhoto == null) {
                                "Choose Photo"
                            } else {
                                "Choose Different Photo"
                            }
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                /*
                =====================================================
                SUBMIT CHORE

                PURPLE BACKGROUND
                =====================================================
                */

                Button(
                    onClick = {

                        selectedPhoto?.let { uri ->

                            onSubmitPhoto(uri)
                        }
                    },

                    enabled =
                        selectedPhoto != null &&
                                !isSubmitting,

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                purple,

                            contentColor =
                                white,

                            /*
                            Keep disabled Submit Chore visible
                            with a lighter purple background.
                            */

                            disabledContainerColor =
                                disabledPurple,

                            disabledContentColor =
                                white
                        ),

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    if (isSubmitting) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),

                            color =
                                white,

                            strokeWidth =
                                2.dp
                        )


                        Spacer(
                            modifier =
                                Modifier.width(10.dp)
                        )


                        Text(
                            text =
                                "Analyzing Photo..."
                        )

                    } else {

                        Text(
                            text =
                                "Submit Chore"
                        )
                    }
                }


                /*
                =====================================================
                ERROR MESSAGE
                =====================================================
                */

                if (errorMessage != null) {

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )


                    Text(
                        text =
                            errorMessage,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )
                }
            }
        }
    }
}
