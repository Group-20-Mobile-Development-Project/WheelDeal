package com.example.wheeldeal.ui.screens

import android.content.Intent
import android.widget.Toast
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wheeldeal.ui.theme.AppTypography
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.example.wheeldeal.viewmodel.AuthViewModel
import com.example.wheeldeal.viewmodel.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.Locale

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
    val showEditProfileDialog = remember { mutableStateOf(false) }
    val showChangePasswordDialog = remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${user.firstName} ${user.lastName}",
                style = AppTypography.headlineLarge.copy(fontSize = 25.sp),
                color = Color(0xFF003049)
            )

            Spacer(modifier = Modifier.height(35.dp))

            InfoCard(text = user.email, icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Edit Profile", icon = Icons.Default.Edit) {
                showEditProfileDialog.value = true
            }
            Spacer(modifier = Modifier.height(16.dp))

            InfoCard(text = "Change Password", icon = Icons.Default.Lock) {
                showChangePasswordDialog.value = true
            }
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

            InfoCard(text = "Contact Us", icon = Icons.Default.Phone) {
                showContactUsDialog.value = true
            }

            // ─── Change Password Dialog ──────────────────────────────────────────
            if (showChangePasswordDialog.value) {
                var oldPw by remember { mutableStateOf("") }
                var newPw by remember { mutableStateOf("") }
                var confirmPw by remember { mutableStateOf("") }
                var error by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showChangePasswordDialog.value = false },
                    title = { Text("Change Password", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            if (error != null) {
                                Text(error!!, color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(8.dp))
                            }
                            OutlinedTextField(
                                value = oldPw,
                                onValueChange = { oldPw = it },
                                label = { Text("Current Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPw,
                                onValueChange = { newPw = it },
                                label = { Text("New Password (≥6 chars)") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmPw,
                                onValueChange = { confirmPw = it },
                                label = { Text("Confirm New Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            error = when {
                                oldPw.isBlank() || newPw.isBlank() || confirmPw.isBlank() ->
                                    "All fields are required"
                                newPw.length < 6 ->
                                    "New password must be at least 6 characters"
                                newPw != confirmPw ->
                                    "Passwords do not match"
                                else -> null
                            }
                            if (error == null) {
                                viewModel.changePassword(oldPw, newPw)
                                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                showChangePasswordDialog.value = false
                            }
                        }) {
                            Text("Submit")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showChangePasswordDialog.value = false }) {
                            Text("Cancel")
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // ─── Edit Profile Dialog ─────────────────────────────────────────────
            if (showEditProfileDialog.value) {
                var firstName by remember { mutableStateOf(user.firstName) }
                var lastName  by remember { mutableStateOf(user.lastName) }
                var errorMsg  by remember { mutableStateOf<String?>(null) }

                AlertDialog(
                    onDismissRequest = { showEditProfileDialog.value = false },
                    title = {
                        Text(
                            "Edit Profile",
                            style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                            color = Color(0xFF003049)
                        )
                    },
                    text = {
                        Column {
                            if (errorMsg != null) {
                                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                                Spacer(Modifier.height(8.dp))
                            }
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            errorMsg = when {
                                firstName.isBlank() || lastName.isBlank() ->
                                    "Name fields cannot be empty"
                                else -> null
                            }
                            if (errorMsg == null) {
                                viewModel.updateUserData(firstName, lastName)
                                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                showEditProfileDialog.value = false
                            }
                        }) {
                            Text("Save", color = FontIconColor, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditProfileDialog.value = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            // ─── Logout Dialog ────────────────────────────────────────────────────
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

            // ─── Delete Account Dialog ────────────────────────────────────────────
            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog.value = false
                            scope.launch {
                                firestore.collection("users").document(uid!!).delete()
                                FirebaseAuth.getInstance().currentUser
                                    ?.delete()
                                    ?.addOnSuccessListener {
                                        viewModel.logout()
                                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                        onBackToMain()
                                    }
                                    ?.addOnFailureListener {
                                        Toast.makeText(
                                            context,
                                            "Failed to delete: ${it.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
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

            // ─── Feedback Dialog ──────────────────────────────────────────────────
            if (showFeedbackDialog.value) {
                FeedbackDialogContent {
                    showFeedbackDialog.value = false
                }
            }

            // ─── Contact Us Dialog ────────────────────────────────────────────────
            if (showContactUsDialog.value) {
                AlertDialog(
                    onDismissRequest = { showContactUsDialog.value = false },
                    confirmButton = {
                        TextButton(onClick = { showContactUsDialog.value = false }) {
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
                                        data = "tel:+358444333222".toUri()
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
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
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

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
                if (feedbackText.isNotBlank()) {
                    val currentTime = System.currentTimeMillis()
                    val formattedTime = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(currentTime)

                    val feedback = hashMapOf(
                        "message" to feedbackText,
                        "timestamp" to formattedTime
                    )
                    firestore.collection("feedback")
                        .add(feedback)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Feedback submitted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            feedbackText = ""
                            onSubmit()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Failed to submit feedback: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(context, "Feedback cannot be empty", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun EditProfileDialog(
    user: UserData,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName  by remember { mutableStateOf(user.lastName) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Profile",
                style = AppTypography.headlineLarge.copy(fontSize = 20.sp),
                color = Color(0xFF003049)
            )
        },
        text = {
            Column {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    errorMessage = when {
                        firstName.isBlank() || lastName.isBlank() ->
                            "Name fields cannot be empty"
                        else -> null
                    }
                    if (errorMessage == null) {
                        onSave(firstName, lastName)
                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                }
            ) {
                Text("Save", color = FontIconColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
