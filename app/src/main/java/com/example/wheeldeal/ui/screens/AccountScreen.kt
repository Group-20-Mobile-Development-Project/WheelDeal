// ✅ AccountScreen.kt
package com.example.wheeldeal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.wheeldeal.ui.theme.FontIconColor
import com.example.wheeldeal.ui.theme.PrimaryColor
import com.example.wheeldeal.ui.theme.WhiteColor
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    onLoginSuccess: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val gradientBrush = Brush.verticalGradient(listOf(PrimaryColor, WhiteColor))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhiteColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSignUp) "Create an Account" else "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = FontIconColor
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = FontIconColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FontIconColor,
                        unfocusedTextColor = FontIconColor,
                        focusedContainerColor = WhiteColor,
                        unfocusedContainerColor = WhiteColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = FontIconColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FontIconColor,
                        unfocusedTextColor = FontIconColor,
                        focusedContainerColor = WhiteColor,
                        unfocusedContainerColor = WhiteColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isSignUp) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Sign up successful! Please login.", Toast.LENGTH_SHORT).show()
                                        isSignUp = false
                                        email = ""
                                        password = ""
                                    } else {
                                        val error = task.exception?.message ?: "Sign up failed"
                                        scope.launch { snackbarHostState.showSnackbar(error) }
                                    }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                                        email = ""
                                        password = ""
                                        onLoginSuccess()
                                    } else {
                                        val error = task.exception?.message ?: "Login failed"
                                        scope.launch { snackbarHostState.showSnackbar(error) }
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FontIconColor,
                        contentColor = WhiteColor
                    )
                ) {
                    Text(if (isSignUp) "Sign Up" else "Login")
                }

                TextButton(onClick = {
                    isSignUp = !isSignUp
                    email = ""
                    password = ""
                }) {
                    Text(
                        text = if (isSignUp)
                            "Already have an account? Login"
                        else
                            "Don’t have an account? Sign Up",
                        color = FontIconColor
                    )
                }

                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}
