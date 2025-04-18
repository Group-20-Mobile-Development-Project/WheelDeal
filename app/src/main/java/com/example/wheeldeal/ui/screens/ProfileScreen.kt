package com.example.wheeldeal.ui.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
// import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.ui.theme.AppTypography
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onBackToMain: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val user = viewModel.userData.collectAsState().value
    val firestore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    val showLogoutDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showFeedbackDialog = remember { mutableStateOf(false) }
    val showContactUsDialog = remember { mutableStateOf(false) }


    if (user == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(PrimaryColor, WhiteColor))),
            contentAlignment = Alignment.Center
        ) {
            Text("No user logged in.", style = AppTypography.bodyMedium)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PrimaryColor, WhiteColor)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003049))
                    .padding(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${user.firstName} ${user.lastName}",
                style = AppTypography.headlineLarge.copy(fontSize = 25.sp),
                color = Color(0xFF003049)
            )

            /*Text(
                text = "Upload Image",
                style = AppTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = Color(0xFF003049)
            )*/

            Spacer(modifier = Modifier.height(75.dp))

            InfoCard(text = user.email, icon = Icons.Default.Email)
            // Spacer(modifier = Modifier.height(16.dp))
            // InfoCard(text = "Phone number", icon = Icons.Default.Phone)
            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Log out", icon = Icons.AutoMirrored.Filled.Logout) {
                showLogoutDialog.value = true
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Delete Account", icon = Icons.Default.Delete) {
                showDeleteDialog.value = true
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Leave a message", icon = Icons.Default.Email) {
                showFeedbackDialog.value = true
            }

            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Contact Us", icon = Icons.Default.Phone) { // New InfoCard for Contact Us
                showContactUsDialog.value = true
            }

            // -------- Contact Us Dialog --------
            if (showContactUsDialog.value) {
                ContactUsDialog(onDismiss = { showContactUsDialog.value = false })
            }


            // -------- Confirmation Dialogs --------
            if (showLogoutDialog.value) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showLogoutDialog.value = false
                            FirebaseAuth.getInstance().signOut()
                            viewModel.logout()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            onBackToMain()
                        }) {
                            Text("Yes", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog.value = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    title = {
                        Text(
                            "Log out?",
                            style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                            color = Color(0xFF003049)
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to log out? You'll be redirected to login.",
                            style = AppTypography.bodyMedium,
                            color = Color.DarkGray
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }


            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog.value = false
                            scope.launch {
                                firestore.collection("users").document(uid!!).delete()
                                FirebaseAuth.getInstance().currentUser?.delete()
                                    ?.addOnSuccessListener {
                                        viewModel.logout()
                                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                        onBackToMain()
                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(context, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }) {
                            Text("Yes", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog.value = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    title = {
                        Text(
                            "Delete Account?",
                            style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                            color = Color(0xFF003049)
                        )
                    },
                    text = {
                        Text(
                            "This action is permanent and cannot be undone.",
                            style = AppTypography.bodyMedium,
                            color = Color.DarkGray
                        )
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            if (showFeedbackDialog.value) {
                AlertDialog(
                    onDismissRequest = { showFeedbackDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = { showFeedbackDialog.value = false }) {
                            Text("Close", color = Color.Gray)
                        }
                    },
                    title = {
                        Text(
                            "Send Feedback",
                            style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                            color = Color(0xFF003049)
                        )
                    },
                    text = {
                        FeedbackDialogContent(onSubmit = {
                            // Handle submission
                            showFeedbackDialog.value = false
                            Toast.makeText(context, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                        })
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

        }
    }
}

@Composable
fun InfoCard(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val cardModifier = modifier
        .width(297.dp)
        .height(50.dp)
        .shadow(10.dp, RoundedCornerShape(30.dp))
        .background(Color.White, RoundedCornerShape(30.dp))
        .clickable(enabled = onClick != null) { onClick?.invoke() }
        .padding(horizontal = 24.dp, vertical = 13.dp)

    Row(
        modifier = cardModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF143D59)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF143D59)
        )
    }
}

@Composable
fun FeedbackDialogContent(onSubmit: () -> Unit) {
    var feedbackText by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text("Your message") },
            placeholder = { Text("Type your feedback...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                Log.d("Feedback", "Submitted: $feedbackText")
                feedbackText = ""
                onSubmit()
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Submit")
        }
    }
}


@Composable
fun ContactUsDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        },
        title = {
            Text(
                "Contact Us",
                style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                color = Color(0xFF003049)
            )
        },
        text = {
            Column {
                Text(
                    "For any inquiries, please reach out to us at:",
                    style = AppTypography.bodyMedium,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Email: support@wheeldeal.com",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF003049)
                )
                Text(
                    "Phone: +358444333222",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF003049),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:+358444333222")
                        }
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}